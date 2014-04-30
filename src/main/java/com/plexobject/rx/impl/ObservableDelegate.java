package com.plexobject.rx.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.plexobject.rx.Observable;
import com.plexobject.rx.OnCompletion;
import com.plexobject.rx.Subscription;
import com.plexobject.rx.scheduler.Scheduler;

/**
 * This is implementation of Observable that uses user-specified consumer
 * function to notify subscriber for data and errors
 * 
 * @author Shahzad Bhatti
 *
 * @param <T>
 *            type of subscription data
 */
public class ObservableDelegate<T> implements Observable<T> {
    private Consumer<Observer<T>> consumer;

    public ObservableDelegate(final Consumer<Observer<T>> consumer) {
        this.consumer = consumer;
    }

    /**
     * This method subscribes user to receive data
     */
    @Override
    public Subscription subscribe(Consumer<T> onNext,
            Consumer<Throwable> onError) {
        return subscribe(onNext, onError, null);
    }

    /**
     * This method subscribes user to receive data Note: onNext and onError are
     * required but onCompletion is optional
     */
    public Subscription subscribe(Consumer<T> onNext,
            Consumer<Throwable> onError, OnCompletion onCompletion) {
        Objects.requireNonNull(onNext);
        Objects.requireNonNull(onError);
        SubscriptionObserver<T> subscription = new SubscriptionImpl<T>(onNext,
                onError, onCompletion, null);
        consumer.accept(new Observer<T>() {
            @Override
            public void onNext(T obj) {
                subscription.onNext(obj);
            }

            @Override
            public void onError(Throwable error) {
                subscription.onError(error);
            }

            @Override
            public void onCompleted() {
                subscription.onCompleted();
            }
        });
        return subscription;
    }

    /**
     * This method allows user to specify scheduler but it's not supported in
     * this implementation.
     */
    @Override
    public Observable<T> subscribeOn(Scheduler scheduler) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method removes duplicates but it's not supported in this
     * implementation.
     */
    @Override
    public Observable<T> distinct() {
        throw new UnsupportedOperationException();
    }

    /**
     * This method filters objects but it's not supported in this
     * implementation.
     */
    @Override
    public Observable<T> filter(Predicate<? super T> predicate) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method transforms objects but it's not supported in this
     * implementation.
     */
    @Override
    public <R> Observable<R> map(Function<? super T, ? extends R> mapper) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method transforms objects but it's not supported in this
     * implementation.
     */
    @Override
    public <R> Observable<R> flatMap(
            Function<? super T, ? extends Stream<? extends R>> mapper) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method limits objects that can be sent but it's not supported in
     * this implementation.
     */
    @Override
    public Observable<T> limit(long maxSize) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method skips first N objects but it's not supported in this
     * implementation.
     */
    @Override
    public Observable<T> skip(long n) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method sorts objects but it's not supported in this implementation.
     */
    @Override
    public Observable<T> sorted() {
        throw new UnsupportedOperationException();
    }

    /**
     * This method sorts objects but it's not supported in this implementation.
     */
    @Override
    public Observable<T> sorted(Comparator<? super T> comparator) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method merges other Observable but it's not supported in this
     * implementation.
     */
    @Override
    public Observable<T> merge(Observable<? extends T> other) {
        throw new UnsupportedOperationException();
    }

    /**
     * This returns internal stream as a list
     * 
     * @return list of objects
     */
    @Override
    public List<T> toList() {
        throw new UnsupportedOperationException();
    }

    /**
     * This returns internal stream as a set
     * 
     * @return set of objects
     */
    @Override
    public Set<T> toSet() {
        throw new UnsupportedOperationException();
    }

    /**
     * Converts internal stream into parallel stream (underlying stream must
     * support parallel processing)
     * 
     * @return instance of Observable that supports parallel stream
     */
    @Override
    public Observable<T> parallel() {
        throw new UnsupportedOperationException();
    }
}
