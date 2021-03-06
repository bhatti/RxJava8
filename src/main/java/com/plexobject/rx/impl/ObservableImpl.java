package com.plexobject.rx.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plexobject.rx.Observable;
import com.plexobject.rx.OnCompletion;
import com.plexobject.rx.Streamable;
import com.plexobject.rx.Subscription;
import com.plexobject.rx.scheduler.Scheduler;
import com.plexobject.rx.util.CancelableSpliterator;
import com.plexobject.rx.util.Tuple;
import com.plexobject.rx.util.TupleSpliterator;

/**
 * This is default implementation of Observable that keeps data as stream
 * 
 * @author Shahzad Bhatti
 *
 * @param <T>
 *            type of subscription data
 */
public class ObservableImpl<T> implements Observable<T>, Streamable<T> {
    private static final Logger logger = LoggerFactory
            .getLogger(ObservableImpl.class);

    private Stream<T> stream;
    private Throwable error;
    private static final Scheduler defaultScheduler = Scheduler
            .newThreadPoolScheduler(8);
    private Scheduler scheduler;
    private Spliterator<T> spliterator;

    public ObservableImpl(final Stream<T> stream, Throwable error) {
        this(stream, error, defaultScheduler);
    }

    ObservableImpl(final Stream<T> stream, Throwable error, Scheduler scheduler) {
        this.stream = stream;
        this.error = error;
        this.scheduler = scheduler;
    }

    /**
     * This method subscribes user for receiving incoming data from stream
     */
    @Override
    public Subscription subscribe(Consumer<T> onNext,
            Consumer<Throwable> onError) {
        return subscribe(onNext, onError, null);
    }

    /**
     * This method subscribes user for receiving incoming data from stream Note:
     * onNext and onError is required but onCompletion is optional This method
     * registers a callback with scheduler, which is notified asynchronously
     */
    public Subscription subscribe(Consumer<T> onNext,
            Consumer<Throwable> onError, OnCompletion onCompletion) {
        Objects.requireNonNull(onNext);
        Objects.requireNonNull(onError);

        if (error == null && stream.isParallel()) {
            CancelableSpliterator<T> cancelableSpliterator = new CancelableSpliterator<T>(
                    stream.spliterator());
            SubscriptionObserver<T> subscription = new SubscriptionImpl<T>(
                    onNext, onError, onCompletion, cancelableSpliterator);

            cancelableSpliterator.parEach(v -> parallelTick(subscription, v),
                    () -> notifyCompleted(subscription));

            return subscription;
        } else {
            SubscriptionObserver<T> subscription = new SubscriptionImpl<T>(
                    onNext, onError, onCompletion, null);

            spliterator = stream.spliterator();
            scheduler.scheduleBackgroundTask(s -> tick(s), subscription);
            return subscription;
        }
    }

    /**
     * This method is called to override default scheduler
     */
    @Override
    public Observable<T> subscribeOn(Scheduler scheduler) {
        Objects.requireNonNull(scheduler);

        this.scheduler = scheduler;
        return this;
    }

    /**
     * This method removes duplicates from internal stream
     */
    @Override
    public Observable<T> distinct() {
        this.stream = stream.distinct();
        return this;
    }

    /**
     * This method is called to filter objects from internal stream using given
     * predicate
     */
    @Override
    public Observable<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);

        this.stream = stream.filter(predicate);
        return this;
    }

    /**
     * This method is called to transform objects in internal stream and create
     * a new stream. This will also create a new Observable as types have been
     * changed.
     */
    @Override
    public <R> Observable<R> map(Function<? super T, ? extends R> mapper) {
        Objects.requireNonNull(mapper);

        Stream<R> newStream = stream.map(mapper);
        return new ObservableImpl<R>(newStream, error, scheduler);
    }

    /**
     * This method is called to transform objects in internal stream and create
     * a new stream. This will also create a new Observable as types have been
     * changed.
     */
    @Override
    public <R> Observable<R> flatMap(
            Function<? super T, ? extends Stream<? extends R>> mapper) {
        Objects.requireNonNull(mapper);

        Stream<R> newStream = stream.flatMap(mapper);
        return new ObservableImpl<R>(newStream, error, scheduler);
    }

    /**
     * This method limits number of objects that will be pushed from an internal
     * stream
     */
    @Override
    public Observable<T> limit(long maxSize) {
        this.stream = stream.limit(maxSize);
        return this;
    }

    /**
     * This method skips first N objects from internal stream
     */
    @Override
    public Observable<T> skip(long n) {
        this.stream = stream.skip(n);
        return this;
    }

    /**
     * This method sorts internal stream
     */
    @Override
    public Observable<T> sorted() {
        this.stream = stream.sorted();
        return this;
    }

    /**
     * This method sorts internal stream using given comparator
     */
    @Override
    public Observable<T> sorted(Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator);

        this.stream = stream.sorted(comparator);
        return this;
    }

    /**
     * This method merges another Observable
     */
    @SuppressWarnings("unchecked")
    @Override
    public Observable<T> merge(Observable<? extends T> other) {
        Objects.requireNonNull(other);

        if (other instanceof Streamable) {
            this.stream = Stream.concat(this.stream,
                    ((Streamable<T>) other).getStream());
            return this;
        } else {
            throw new IllegalArgumentException(
                    "Other observable is not streamable");
        }
    }

    /**
     * This method zips current stream with another streams and creates new
     * Observable of type Tuple
     * 
     * @param other
     *            other stream - if other type is of type Tuple then all
     *            elements of that tuple are added to the result tuple
     * @return instance of Observable
     */
    @SuppressWarnings("unchecked")
    @Override
    public <U> Observable<Tuple> zip(Observable<? extends U> other) {
        Objects.requireNonNull(other);

        if (other instanceof Streamable) {
            TupleSpliterator<T, U> tupleSpliterator = new TupleSpliterator<T, U>(
                    stream.spliterator(), ((Streamable<U>) other).getStream()
                            .spliterator());
            return new ObservableImpl<Tuple>(tupleSpliterator.getStream(), null);
        } else {
            throw new IllegalArgumentException(
                    "Other observable is not streamable");
        }
    }

    /**
     * This method is called asynchronously by parallel stream. Each time this
     * method is called, it checks if subscription is still valid and if it's
     * valid then it proceeds to check for data. Note: this method would be
     * called in multiple threads by parallel stream and we will call cancel
     * stream if subscription is unsubscribed.
     * 
     * @param subscription
     */
    private void parallelTick(SubscriptionObserver<T> subscription, T obj) {
        if (!subscription.isSubscribed()) {
            return;
        }
        if (error != null) {
            notifyError(subscription);
        } else {
            notifyData(subscription, obj);
        }
    }

    /**
     * This method is called asynchronously by scheduler. Each time this method
     * is called, it checks if subscription is still valid and if it's valid
     * then it proceeds to check for data. When there is more data and there are
     * no errors, it registers itself again with Scheduler, which then calls
     * back this method asynchronously.
     * 
     * @param subscription
     */
    private void tick(SubscriptionObserver<T> subscription) {
        if (!subscription.isSubscribed()) {
            return;
        }
        if (error != null) {
            notifyError(subscription);
        } else {
            AtomicBoolean tickNext = new AtomicBoolean();
            if (spliterator.tryAdvance(obj -> {
                if (notifyData(subscription, obj)) {
                    tickNext.set(true);
                }
            })) {
                if (tickNext.get()) {
                    scheduler
                            .scheduleBackgroundTask(s -> tick(s), subscription);
                }
            } else {
                notifyCompleted(subscription);
            }
        }
    }

    /**
     * This method is called to push data to the subscriber
     * 
     * @param subscription
     * @param obj
     * @return true if onNext method was called successfully, false otherwise
     */
    private boolean notifyData(SubscriptionObserver<T> subscription, T obj) {
        try {
            subscription.onNext(obj);
            return true;
        } catch (Throwable e) {
            this.error = e;
            notifyError(subscription);
            return false;
        }
    }

    /**
     * This method is called to notify an error
     * 
     * @param subscription
     */
    private void notifyError(SubscriptionObserver<T> subscription) {
        try {
            subscription.onError(error);
        } catch (Throwable e) {
            logger.error("Failed to notify subscriber for error " + error, e);
        }
    }

    /**
     * This method is called to notify subscriber that there is no more data
     * 
     * @param subscription
     */
    private void notifyCompleted(SubscriptionObserver<T> subscription) {
        try {
            subscription.onCompleted();
        } catch (Throwable e) {
            logger.error("Failed to notify subscriber for onCompletion", e);
        }
    }

    /**
     * This returns internal stream as a list
     * 
     * @return list of objects
     */
    @Override
    public List<T> toList() {
        return stream.collect(Collectors.toList());
    }

    /**
     * This returns internal stream as a set
     * 
     * @return set of objects
     */
    @Override
    public Set<T> toSet() {
        return stream.collect(Collectors.toSet());
    }

    /**
     * Converts internal stream into parallel stream (underlying stream must
     * support parallel processing)
     * 
     * @return instance of Observable that supports parallel stream
     */
    @Override
    public Observable<T> parallel() {
        stream = stream.parallel();
        return this;
    }

    /**
     * This method counts number of elements in stream and creates another
     * stream with that value that is consumed by the subscriber
     * 
     * @return
     */
    public Observable<Long> count() {
        Stream<Long> countStream = Stream.of(stream.count());
        return new ObservableImpl<Long>(countStream, error, scheduler);
    }

    /**
     * This method returns underlying stream
     * 
     * @return
     */
    @Override
    public Stream<T> getStream() {
        return stream;
    }

}
