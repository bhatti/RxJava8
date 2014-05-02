package com.plexobject.rx.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.plexobject.rx.Observable;

public class ObservableNeverTest {
    private Observable<Integer> instance;

    @Before
    public void setup() {
        instance = new ObservableNever<>();
    }

    @After
    public void teardown() {
    }

    @Test
    public void testSubscribeWithoutOnCompletion() throws Exception {
        AtomicInteger onNext = new AtomicInteger();
        AtomicInteger onError = new AtomicInteger();
        instance.subscribe(v -> onNext.incrementAndGet(),
                e -> onError.incrementAndGet());
        assertEquals(0, onNext.get()); // it won't call any methods
        assertEquals(0, onError.get()); // it won't call any methods
    }

    @Test
    public void testSubscribeWithCompletion() throws Exception {
        AtomicInteger onNext = new AtomicInteger();
        AtomicInteger onError = new AtomicInteger();
        AtomicInteger onCompleted = new AtomicInteger();
        instance.subscribe(v -> onNext.incrementAndGet(),
                e -> onError.incrementAndGet(),
                () -> onCompleted.incrementAndGet());
        assertEquals(0, onNext.get()); // it won't call any methods
        assertEquals(0, onError.get()); // it won't call any methods
        assertEquals(0, onCompleted.get()); // it won't call any methods
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSubscribeOn() throws Exception {
        instance.subscribeOn(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDistinct() throws Exception {
        instance.distinct();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testFilter() throws Exception {
        instance.filter(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testMap() throws Exception {
        assertNotNull(instance.map(null));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testFlatMap() throws Exception {
        assertNotNull(instance.flatMap(null));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testLimit() throws Exception {
        instance.limit(1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSkip() throws Exception {
        instance.skip(1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSorted() throws Exception {
        instance.sorted();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSortedComparator() throws Exception {
        instance.sorted(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testMerge() throws Exception {
        instance.merge(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testToList() throws Exception {
        instance.toList();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testToSet() throws Exception {
        instance.toSet();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testParallel() throws Exception {
        instance.parallel();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCount() throws Exception {
        instance.count();
    }
}
