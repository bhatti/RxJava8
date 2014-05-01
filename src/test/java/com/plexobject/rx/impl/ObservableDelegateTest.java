package com.plexobject.rx.impl;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.plexobject.rx.Observable;

@RunWith(JMockit.class)
public class ObservableDelegateTest {
    @Mocked
    private Consumer<Observer<Integer>> defaultConsumer;
    @Tested
    private Observable<Integer> defaultInstance;

    @Before
    public void setup() {
        defaultInstance = new ObservableDelegate<>(defaultConsumer);
    }

    @After
    public void teardown() {
    }

    @Test
    public void testSubscribeWithoutOnCompletion() throws Exception {
        AtomicInteger onNext = new AtomicInteger();
        AtomicInteger onError = new AtomicInteger();
        Consumer<Observer<Integer>> consumer = new Consumer<Observer<Integer>>() {
            @Override
            public void accept(Observer<Integer> t) {
                t.onNext(1);
                t.onNext(2);
                t.onCompleted();
            }
        };
        Observable<Integer> instance = new ObservableDelegate<>(consumer);

        instance.subscribe(v -> onNext.incrementAndGet(),
                e -> onError.incrementAndGet());
        assertEquals(2, onNext.get());
        assertEquals(0, onError.get());
    }

    @Test
    public void testSubscribeWithCompletion() throws Exception {
        AtomicInteger onNext = new AtomicInteger();
        AtomicInteger onError = new AtomicInteger();
        AtomicInteger onCompleted = new AtomicInteger();
        Consumer<Observer<Integer>> consumer = new Consumer<Observer<Integer>>() {
            @Override
            public void accept(Observer<Integer> t) {
                t.onNext(1);
                t.onNext(2);
                t.onCompleted();
            }
        };
        Observable<Integer> instance = new ObservableDelegate<>(consumer);

        instance.subscribe(v -> onNext.incrementAndGet(),
                e -> onError.incrementAndGet(),
                () -> onCompleted.incrementAndGet());
        assertEquals(2, onNext.get());
        assertEquals(0, onError.get());
        assertEquals(1, onCompleted.get());
    }

    @Test
    public void testSubscribeWithErrors() throws Exception {
        AtomicInteger onNext = new AtomicInteger();
        AtomicInteger onError = new AtomicInteger();
        AtomicInteger onCompleted = new AtomicInteger();
        Consumer<Observer<Integer>> consumer = new Consumer<Observer<Integer>>() {
            @Override
            public void accept(Observer<Integer> t) {
                t.onNext(1);
                t.onNext(2);
                t.onError(new Exception());
            }
        };
        Observable<Integer> instance = new ObservableDelegate<>(consumer);

        instance.subscribe(v -> onNext.incrementAndGet(),
                e -> onError.incrementAndGet(),
                () -> onCompleted.incrementAndGet());
        assertEquals(2, onNext.get());
        assertEquals(1, onError.get());
        assertEquals(0, onCompleted.get());
    }

    @Test
    public void testSubscribeWithException() throws Exception {
        AtomicInteger onNext = new AtomicInteger();
        AtomicInteger onError = new AtomicInteger();
        AtomicInteger onCompleted = new AtomicInteger();
        Consumer<Observer<Integer>> consumer = new Consumer<Observer<Integer>>() {
            @Override
            public void accept(Observer<Integer> t) {
                t.onNext(1);
                t.onNext(2);
                t.onCompleted();
            }
        };
        Observable<Integer> instance = new ObservableDelegate<>(consumer);

        instance.subscribe(v -> {
            if (onNext.get() == 0) {
                onNext.incrementAndGet();
            } else {
                throw new RuntimeException();
            }
        }, e -> onError.incrementAndGet(), () -> onCompleted.incrementAndGet());
        assertEquals(1, onNext.get());
        assertEquals(1, onError.get());
        assertEquals(0, onCompleted.get());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSubscribeOn() throws Exception {
        defaultInstance.subscribeOn(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDistinct() throws Exception {
        defaultInstance.distinct();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testFilter() throws Exception {
        defaultInstance.filter(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testMap() throws Exception {
        defaultInstance.map(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testFlatMap() throws Exception {
        defaultInstance.flatMap(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testLimit() throws Exception {
        defaultInstance.limit(1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSkip() throws Exception {
        defaultInstance.skip(1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSorted() throws Exception {
        defaultInstance.sorted();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSortedComparator() throws Exception {
        defaultInstance.sorted(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testMerge() throws Exception {
        defaultInstance.merge(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testToList() throws Exception {
        defaultInstance.toList();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testToSet() throws Exception {
        defaultInstance.toSet();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testParallel() throws Exception {
        defaultInstance.parallel();
    }

}
