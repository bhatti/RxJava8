package com.plexobject.rx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.Test;

import com.plexobject.rx.scheduler.Scheduler;
import com.plexobject.rx.util.NatsSpliterator;

public class ObservableTest extends BaseObservableTest {
    @Test
    public void testSubscribeFilter() throws Exception {
        Observable<String> observable = Observable.from(names).filter(
                name -> name.startsWith("M"));
        initLatch(2 + 1); // N*onNext + onCompleted

        setupCallback(observable, null, true);
        latch.await(100, TimeUnit.MILLISECONDS);
        //
        assertEquals(2, onNext.get());
        assertNull(onError.get());
        assertEquals(1, onCompleted.get());
    }

    @Test
    public void testSubscribeSkip() throws Exception {
        Observable<String> observable = Observable.from(names).skip(2);

        initLatch(names.size() - 2 + 1); // N*onNext + onCompleted

        setupCallback(observable, null, true);
        latch.await(100, TimeUnit.MILLISECONDS);
        //
        assertEquals(names.size() - 2, onNext.get());
        assertNull(onError.get());
        assertEquals(1, onCompleted.get());
    }

    @Test
    public void testSubscribeDistinct() throws Exception {
        List<String> list = Arrays.asList("one", "two", "two", "three");
        Observable<String> observable = Observable.from(list).distinct();

        initLatch(3 + 1); // N*onNext + onCompleted

        setupCallback(observable, null, true);
        latch.await(100, TimeUnit.MILLISECONDS);
        //
        assertEquals(3, onNext.get());
        assertNull(onError.get());
        assertEquals(1, onCompleted.get());
    }

    @Test
    public void testSubscribeMap() throws Exception {
        List<Integer> hashes = new ArrayList<>();

        Observable<Integer> observable = Observable.from(names).map(
                name -> name.hashCode());

        initLatch(names.size() + 1); // N*onNext + onCompleted

        setupCallback(observable, (h) -> hashes.add(h), true);
        latch.await(100, TimeUnit.MILLISECONDS);
        //
        assertEquals(names.size(), onNext.get());
        assertEquals(names.size(), hashes.size());

        assertNull(onError.get());
        assertEquals(1, onCompleted.get());
    }

    @Test
    public void testSubscribeFlatMap() throws Exception {
        List<Integer> merged = new ArrayList<>();

        Stream<List<Integer>> integerListStream = Stream.of(
                Arrays.asList(1, 2), Arrays.asList(3, 4), Arrays.asList(5));

        Observable<Integer> observable = Observable.from(integerListStream)
                .flatMap(integerList -> integerList.stream());

        initLatch(names.size() + 1); // N*onNext + onCompleted

        setupCallback(observable, (n) -> merged.add(n), true);
        latch.await(100, TimeUnit.MILLISECONDS);
        //
        assertEquals(5, onNext.get());
        assertEquals(5, merged.size());

        assertNull(onError.get());
        assertEquals(1, onCompleted.get());
    }

    @Test
    public void testSubscribeFromCollection() throws Exception {
        initLatch(names.size() + 1); // N*onNext + onCompleted

        Observable<String> observable = Observable.from(names);

        setupCallback(observable, null, true);
        latch.await(100, TimeUnit.MILLISECONDS);
        //
        assertEquals(names.size(), onNext.get());
        assertNull(onError.get());
        assertEquals(1, onCompleted.get());
    }

    @Test
    public void testSubscribeFromArray() throws Exception {
        Observable<String> observable = Observable.from("one", "two", "three",
                "four", "five");

        initLatch(5 + 1); // N*onNext + onCompleted

        setupCallback(observable, null, true);
        latch.await(100, TimeUnit.MILLISECONDS);
        //
        assertEquals(5, onNext.get());
        assertNull(onError.get());
        assertEquals(1, onCompleted.get());
    }

    @Test
    public void testSubscribeParallel() throws Exception {
        Observable<Integer> observable = Observable.range(1, 101)
                .subscribeOn(Scheduler.newNewThreadScheduler()).parallel();

        initLatch(100 + 1); // N*onNext + onCompleted
        List<Integer> processedOrder = Collections
                .synchronizedList(new ArrayList<>());
        Subscription subscription = setupCallback(observable, (v) -> {
            processedOrder.add(v);
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
    }

    @Test
    public void testSubscribeFromArrayOne() throws Exception {
        Observable<String> observable = Observable.from("one");

        initLatch(1 + 1); // N*onNext + onCompleted

        setupCallback(observable, null, true);
        latch.await(100, TimeUnit.MILLISECONDS);
        //
        assertEquals(1, onNext.get());
        assertNull(onError.get());
        assertEquals(1, onCompleted.get());
    }

    @Test
    public void testSubscribeFromArrayEmpty() throws Exception {
        Observable<String> observable = Observable.from();

        initLatch(1); // onCompleted

        setupCallback(observable, null, true);
        latch.await(100, TimeUnit.MILLISECONDS);
        //
        assertEquals(0, onNext.get());
        assertNull(onError.get());
        assertEquals(1, onCompleted.get());
    }

    @Test
    public void testSubscribeFromSpliterator() throws Exception {
        initLatch(names.size() + 1); // N*onNext + onCompleted

        Observable<String> observable = Observable.from(names.spliterator());

        setupCallback(observable, null, true);
        latch.await(100, TimeUnit.MILLISECONDS);
        //
        assertEquals(names.size(), onNext.get());
        //
        assertNull(onError.get());
        assertEquals(1, onCompleted.get());
    }

    @Test
    public void testSubscribeFromStream() throws Exception {
        initLatch(names.size() + 1); // N*onNext + onCompleted

        Observable<String> observable = Observable.from(names.stream());
        setupCallback(observable, null, true);
        latch.await(100, TimeUnit.MILLISECONDS);
        //
        assertEquals(names.size(), onNext.get());
        //
        assertNull(onError.get());
        assertEquals(1, onCompleted.get());
    }

    @Test
    public void testSubscribeFromIterator() throws Exception {
        initLatch(names.size() + 1); // N*onNext + onCompleted

        Observable<String> observable = Observable.from(names.iterator());

        setupCallback(observable, null, true);
        latch.await(100, TimeUnit.MILLISECONDS);
        //
        assertEquals(names.size(), onNext.get());
        //
        assertNull(onError.get());
        assertEquals(1, onCompleted.get());
    }

    @Test
    public void testSubscribeFromInfiniteNats() throws Exception {
        Observable<Integer> observable = Observable
                .from(new NatsSpliterator(0));
        initLatch(0); // ignore latch
        Subscription subscription = setupCallback(observable, null, true);
        Thread.sleep(10);
        subscription.dispose();
        //
        assertTrue(onNext.get() > 0);
        assertNull(onError.get());
        assertEquals(0, onCompleted.get());
    }

    @Test
    public void testSubscribeEmpty() throws Exception {
        for (Scheduler scheduler : allSchedulers) {
            Observable<String> observable = Observable.empty();
            //
            initLatch(1);
            observable.subscribeOn(scheduler);
            setupCallback(observable, null, false);
            latch.await(100, TimeUnit.MILLISECONDS);

            assertEquals(0, onNext.get());
            assertNull(onError.get());
            assertEquals(0, onCompleted.get());
        }
    }

    @Test
    public void testSubscribeJust() throws Exception {
        for (Scheduler scheduler : allSchedulers) {
            Observable<String> observable = Observable.just("One");
            //
            initLatch(1 + 1);
            observable.subscribeOn(scheduler);
            setupCallback(observable, null, false);
            latch.await(100, TimeUnit.MILLISECONDS);

            assertEquals(1, onNext.get());
            assertNull(onError.get());
            assertEquals(0, onCompleted.get());
        }
    }

    @Test
    public void testSubscribeNever() throws Exception {
        for (Scheduler scheduler : allSchedulers) {
            Observable<String> observable = Observable.never();

            initLatch(1); // just make one latch
            observable.subscribeOn(scheduler);
            setupCallback(observable, null, false);
            latch.await(100, TimeUnit.MILLISECONDS);

            assertEquals(0, onNext.get());
            assertNull(onError.get());
            assertEquals(0, onCompleted.get());
        }
    }

}
