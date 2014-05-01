package com.plexobject.rx.impl;

import java.util.Comparator;
import java.util.List;
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
 * This implementation of Observable is used to ignore subscription. This won't
 * call any of callback methods of subscriber.
 * 
 * @author Shahzad Bhatti
 *
 * @param <T>
 *            type of subscription data
 */
public class ObservableNever<T> implements Observable<T> {
    @Override
    public Subscription subscribe(Consumer<T> onNext,
            Consumer<Throwable> onError) {
        return subscribe(onNext, onError, null);
    }

    @Override
    public Subscription subscribe(Consumer<T> onNext,
            Consumer<Throwable> onError, OnCompletion onCompletion) {
        return new SubscriptionImpl<T>(onNext, onError, onCompletion, null);
    }

    @Override
    public Observable<T> subscribeOn(Scheduler scheduler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<T> distinct() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<T> merge(Observable<? extends T> other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<T> filter(Predicate<? super T> predicate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <R> Observable<R> map(Function<? super T, ? extends R> mapper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <R> Observable<R> flatMap(
            Function<? super T, ? extends Stream<? extends R>> mapper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<T> limit(long maxSize) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<T> skip(long n) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<T> sorted() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<T> sorted(Comparator<? super T> comparator) {
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
