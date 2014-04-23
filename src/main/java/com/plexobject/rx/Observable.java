package com.plexobject.rx;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.plexobject.rx.impl.ObservableDelegate;
import com.plexobject.rx.impl.ObservableImpl;
import com.plexobject.rx.impl.ObservableNever;
import com.plexobject.rx.scheduler.Scheduler;
import com.plexobject.rx.util.SpliteratorAdapter;

public interface Observable<T> {
    public static <T> Observable<T> create(Consumer<Observer<T>> consumer) {
        Objects.requireNonNull(consumer);
        return new ObservableDelegate<T>(consumer);
    }

    public static <T> Observable<T> from(Stream<T> stream) {
        Objects.requireNonNull(stream);
        return new ObservableImpl<T>(stream, null);
    }

    public static <T> Observable<T> from(Iterator<T> it) {
        Objects.requireNonNull(it);
        return new ObservableImpl<T>(new SpliteratorAdapter<T>(it).toStream(),
                null);
    }

    public static <T> Observable<T> from(Spliterator<T> it) {
        Objects.requireNonNull(it);
        return new ObservableImpl<T>(StreamSupport.stream(it, false), null);
    }

    public static <T> Observable<T> from(Collection<T> c) {
        Objects.requireNonNull(c);
        return new ObservableImpl<T>(c.stream(), null);
    }

    public static <T> Observable<T> empty() {
        return new ObservableImpl<T>(Stream.<T> of(), null);
    }

    public static <T> Observable<T> just(T obj) {
        Objects.requireNonNull(obj);
        return new ObservableImpl<T>(Stream.<T> of(obj), null);
    }

    public static <T> Observable<T> throwing(Throwable error) {
        Objects.requireNonNull(error);
        return new ObservableImpl<T>(Stream.<T> of(), error);
    }

    /**
     * Creates range of numbers starting at from until it reaches to exclusively
     * 
     * @param from
     * @param to
     * @return
     */
    public static Observable<Integer> range(int from, int to) {
        return new ObservableImpl<Integer>(IntStream.range(from, to).boxed(),
                null);
    }

    // ignores subscription and thread get stuck in synchronous mode
    public static <T> Observable<T> never() {
        return new ObservableNever<T>();
    }

    Observable<T> distinct();

    Observable<T> filter(Predicate<? super T> predicate);

    <R> Observable<R> map(Function<? super T, ? extends R> mapper);

    <R> Observable<R> flatMap(
            Function<? super T, ? extends Stream<? extends R>> mapper);

    Observable<T> limit(long maxSize);

    Observable<T> skip(long n);

    Observable<T> sorted();

    Observable<T> sorted(Comparator<? super T> comparator);

    Observable<T> subscribeOn(Scheduler scheduler);

    Observable<T> merge(Observable<? extends T> other);

    Subscription subscribe(Consumer<T> onNext, Consumer<Throwable> onError,
            OnCompletion onCompletion);

    Subscription subscribe(Consumer<T> onNext, Consumer<Throwable> onError);
}
