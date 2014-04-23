package com.plexobject.rx.impl;

import java.util.Comparator;
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
 */
public class ObservableNever<T> implements Observable<T> {
    @Override
    public Subscription subscribe(Consumer<T> onNext,
            Consumer<Throwable> onError) {
        return subscribe(onNext, onError, null);
    }

    @Override
    public synchronized Subscription subscribe(Consumer<T> onNext,
            Consumer<Throwable> onError, OnCompletion onCompletion) {
        return new SubscriptionImpl<T>(onNext, onError, onCompletion);
    }

    @Override
    public Observable<T> subscribeOn(Scheduler scheduler) {
        return this;
    }

    @Override
    public Observable<T> distinct() {
        return this;
    }

    @Override
    public Observable<T> merge(Observable<? extends T> other) {
        return this;
    }

    @Override
    public Observable<T> filter(Predicate<? super T> predicate) {
        return this;
    }

    @Override
    public <R> Observable<R> map(Function<? super T, ? extends R> mapper) {
        return new ObservableNever<R>();
    }

    @Override
    public <R> Observable<R> flatMap(
            Function<? super T, ? extends Stream<? extends R>> mapper) {
        return new ObservableNever<R>();
    }

    @Override
    public Observable<T> limit(long maxSize) {
        return this;
    }

    @Override
    public Observable<T> skip(long n) {
        return this;
    }

    @Override
    public Observable<T> sorted() {
        return this;
    }

    @Override
    public Observable<T> sorted(Comparator<? super T> comparator) {
        return this;
    }
}
