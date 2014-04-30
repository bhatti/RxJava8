package com.plexobject.rx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.plexobject.rx.scheduler.Scheduler;

public class ObservableParallelTest extends BaseObservableTest {
    @Test
    public void testSubscribeParallelWithCancelation() throws Exception {
        Observable<Integer> observable = Observable.range(1, 101)
                .subscribeOn(Scheduler.newNewThreadScheduler()).parallel();

        initLatch(0); // ignore latch

        Subscription subscription = setupCallback(observable, (v) -> {
            try {
                Thread.sleep(1);
            } catch (Exception e) {
                Thread.interrupted();
            }
        }, true);
        Thread.sleep(10);
        subscription.dispose();

        assertEquals(0, onCompleted.get());
        assertNull(onError.get());
        assertTrue(onNext.get() > 0);
    }

    @Test
    public void testSubscribeParallelWithFailure() throws Exception {
        Observable<Integer> observable = Observable.range(1, 101)
                .subscribeOn(Scheduler.newNewThreadScheduler()).parallel();

        initLatch(1); // latch for error

        setupCallback(observable, (v) -> {
            throw new RuntimeException("test error");
        }, true);
        latch.await(100, TimeUnit.MILLISECONDS);

        assertEquals(0, onCompleted.get());
        assertNotNull(onError.get());
        assertEquals(0, onNext.get());
    }

    @Test
    public void testSubscribeParallel() throws Exception {
        Observable<Integer> observable = Observable.range(1, 101)
                .subscribeOn(Scheduler.newNewThreadScheduler()).parallel();

        initLatch(100 + 1); // N*onNext + onCompleted
        List<Integer> processedList = Collections
                .synchronizedList(new ArrayList<>());
        setupCallback(observable, (v) -> {
            processedList.add(v);
        }, true);
        latch.await(100, TimeUnit.MILLISECONDS);
        System.out.println(processedList);

        assertFalse(isSorted(processedList)); // parallel processing will
                                              // execute list in unsorted
                                              // fashion
        assertEquals(100, processedList.size());
        assertEquals(100, onNext.get());
        assertNull(onError.get());
        assertEquals(1, onCompleted.get());
    }

    @Test
    public void testSubscribeParallelSequentialStreamWithUnsubscribing()
            throws Exception {
        Observable<Integer> observable = Observable.integers(1)
                .subscribeOn(Scheduler.newNewThreadScheduler()).parallel();

        initLatch(0); // ignore latch
        List<Integer> processedList = Collections
                .synchronizedList(new ArrayList<>());
        Subscription subscription = setupCallback(observable, (v) -> {
            processedList.add(v);
        }, true);
        Thread.sleep(10);
        subscription.dispose();
        assertTrue(isSorted(processedList)); // parallel processing will
        // execute list in unsorted
        // fashion
        assertTrue(processedList.size() > 0);
        assertTrue(onNext.get() > 0);
        assertNull(onError.get());
        assertEquals(0, onCompleted.get());
    }

    @SuppressWarnings("unchecked")
    public static <T> boolean isSorted(List<? extends Comparable<T>> list) {
        boolean sorted = true;
        for (int i = 1; i < list.size(); i++) {
            Comparable<T> current = list.get(i);
            Comparable<T> prev = list.get(i - 1);
            if (prev.compareTo((T) current) > 0)
                sorted = false;
        }

        return sorted;
    }
}
