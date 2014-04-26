package com.plexobject.rx.impl;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plexobject.rx.Observable;
import com.plexobject.rx.OnCompletion;
import com.plexobject.rx.Subscription;
import com.plexobject.rx.scheduler.Scheduler;

/**
 * This is default implementation of Observable that keeps data as stream
 * 
 * @author Shahzad Bhatti
 *
 * @param <T> type of subscription data
 */
public class ObservableImpl<T> implements Observable<T> {
    private static final Logger logger = LoggerFactory
            .getLogger(ObservableImpl.class);

    private Stream<T> stream;
    private Throwable error;
    private static final Scheduler defaultScheduler = Scheduler
            .getThreadPoolScheduler();
    private Scheduler scheduler;
    private Iterator<T> it;

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
    public synchronized Subscription subscribe(Consumer<T> onNext,
            Consumer<Throwable> onError, OnCompletion onCompletion) {
        Objects.requireNonNull(onNext);
        Objects.requireNonNull(onError);

        SubscriptionObserver<T> subscription = new SubscriptionImpl<T>(onNext,
                onError, onCompletion);
        it = stream.iterator();
        scheduler.scheduleTick(s -> tick(s), subscription);
        return subscription;
    }

    /**
     * This method is called to override default scheduler
     */
    @Override
    public Observable<T> subscribeOn(Scheduler scheduler) {
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
        this.stream = stream.sorted(comparator);
        return this;
    }

    /**
     * This method merges another Observable
     */
    @SuppressWarnings("unchecked")
    @Override
    public Observable<T> merge(Observable<? extends T> other) {
        this.stream = Stream.concat(this.stream,
                ((ObservableImpl<T>) other).stream);
        return this;
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
    private synchronized void tick(SubscriptionObserver<T> subscription) {
        if (!subscription.isUnsubscribed()) {
            return;
        }
        if (error != null) {
            notifyError(subscription);
        } else {
            if (it.hasNext()) {
                try {
                    T obj = it.next();
                    notifyData(subscription, obj);
                    scheduler.scheduleTick(s -> tick(s), subscription);
                } catch (Throwable e) {
                    this.error = e;
                    notifyError(subscription);
                }
            } else {
                notifyCompleted(subscription);
            }
        }
    }

    /**
     * This method is called to notify an error
     * 
     * @param subscription
     */
    private void notifyError(SubscriptionObserver<T> subscription) {
        logger.warn("Subscription " + subscription + " failed", error);
        subscription.onError(error);
    }

    /**
     * This method is called to push data to the subscriber
     * 
     * @param subscription
     * @param obj
     */
    private void notifyData(SubscriptionObserver<T> subscription, T obj) {
        subscription.onNext(obj);
    }

    /**
     * This method is called to notify subscriber that there is no more data
     * 
     * @param subscription
     */
    private void notifyCompleted(SubscriptionObserver<T> subscription) {
        subscription.onCompleted();
    }

}
