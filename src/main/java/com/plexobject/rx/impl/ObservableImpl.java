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

    @Override
    public Subscription subscribe(Consumer<T> onNext,
            Consumer<Throwable> onError) {
        return subscribe(onNext, onError, null);
    }

    public synchronized Subscription subscribe(Consumer<T> onNext,
            Consumer<Throwable> onError, OnCompletion onCompletion) {
        Objects.requireNonNull(onNext);
        SubscriptionObserver<T> subscription = new SubscriptionImpl<T>(onNext,
                onError, onCompletion);
        it = stream.iterator();
        scheduler.scheduleTick(s -> tick(s), subscription);
        return subscription;
    }

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

    private void notifyError(SubscriptionObserver<T> subscription) {
        logger.warn("Subscription " + subscription + " failed", error);
        subscription.onError(error);
    }

    private void notifyData(SubscriptionObserver<T> subscription, T obj) {
        subscription.onNext(obj);
    }

    private void notifyCompleted(SubscriptionObserver<T> subscription) {
        subscription.onCompleted();
    }

    @Override
    public Observable<T> subscribeOn(Scheduler scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    @Override
    public Observable<T> distinct() {
        this.stream = stream.distinct();
        return this;
    }

    @Override
    public Observable<T> filter(Predicate<? super T> predicate) {
        this.stream = stream.filter(predicate);
        return this;
    }

    @Override
    public <R> Observable<R> map(Function<? super T, ? extends R> mapper) {
        Stream<R> newStream = stream.map(mapper);
        return new ObservableImpl<R>(newStream, error, scheduler);
    }

    @Override
    public <R> Observable<R> flatMap(
            Function<? super T, ? extends Stream<? extends R>> mapper) {
        Stream<R> newStream = stream.flatMap(mapper);
        return new ObservableImpl<R>(newStream, error, scheduler);
    }

    @Override
    public Observable<T> limit(long maxSize) {
        this.stream = stream.limit(maxSize);
        return this;
    }

    @Override
    public Observable<T> skip(long n) {
        this.stream = stream.skip(n);
        return this;
    }

    @Override
    public Observable<T> sorted() {
        this.stream = stream.sorted();
        return this;
    }

    @Override
    public Observable<T> sorted(Comparator<? super T> comparator) {
        this.stream = stream.sorted(comparator);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Observable<T> merge(Observable<? extends T> other) {
        this.stream = Stream.concat(this.stream,
                ((ObservableImpl<T>) other).stream);
        return this;
    }
}
