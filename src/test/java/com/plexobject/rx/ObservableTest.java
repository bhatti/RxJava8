package com.plexobject.rx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.plexobject.rx.scheduler.Scheduler;
import com.plexobject.rx.util.NatsSpliterator;

public class ObservableTest {
    private final List<String> names = Arrays.asList("Erica", "Matt", "John",
            "Mike", "Scott", "Alex", "Jeff", "Brad");

    private List<Object> onNext = new ArrayList<>();
    private List<Throwable> onErrors = new ArrayList<>();
    private int onCompletion;
    private Subscription subscription;

    @Before
    public void setup() {
    }

    @After
    public void teardown() {
        if (subscription != null) {
            subscription.dispose();
        }
    }

    @Test
    public void testSubscribeCreate() throws Exception {
        Observable<String> observable = Observable.create(observer -> {
            for (String name : names) {
                observer.onNext(name);
            }
            observer.onCompleted();
        });
        //
        setupCallbackWithCompletion(observable, false, true);
        //
        assertEquals(names.size(), onNext.size());
        assertEquals(0, onErrors.size());
        assertEquals(1, onCompletion);
    }

    @Test
    public void testSubscribeFromWithTimer() throws Exception {
        Observable<String> observable = Observable.from(names);
        List<Long> times = new ArrayList<>();
        observable
                .subscribeOn(Scheduler.getTimerSchedulerWithMilliInterval(10));
        subscription = observable.subscribe(name -> {
            onNext.add(name);
            times.add(System.currentTimeMillis());
        }, error -> onErrors.add(error), () -> onCompletion++);

        Thread.sleep(200);
        for (int i = 1; i < times.size(); i++) {
            long interval = times.get(i) - times.get(i - 1);
            assertTrue("interval should be >= 10 for i " + i + ", " + times,
                    interval >= 10);
        }
        //
        assertEquals(names.size(), onNext.size());
        assertEquals(0, onErrors.size());
        assertEquals(1, onCompletion);
    }

    @Test
    public void testSubscribeMerge() throws Exception {
        Observable<Integer> observable1 = Observable.from(Stream.of(1, 2, 3));
        Observable<Integer> observable2 = Observable.from(Stream.of(4, 5, 6));

        Observable<Integer> observableMerged = observable1.merge(observable2);
        setupCallbackWithCompletion(observableMerged, false, true);

        //
        assertEquals(6, onNext.size());
        assertEquals(0, onErrors.size());
        assertEquals(1, onCompletion);
    }

    @Test
    public void testSubscribeFilter() throws Exception {
        Observable<String> observable = Observable.from(names).filter(
                name -> name.startsWith("M"));
        //
        setupCallbackWithCompletion(observable, false, true);
        //
        assertEquals(2, onNext.size());
        assertEquals(0, onErrors.size());
        assertEquals(1, onCompletion);
    }

    @Test
    public void testSubscribeSkip() throws Exception {
        Observable<String> observable = Observable.from(names).skip(2);
        //
        setupCallbackWithCompletion(observable, false, true);
        //
        assertEquals(names.size() - 2, onNext.size());
        assertEquals(0, onErrors.size());
        assertEquals(1, onCompletion);
    }

    @Test
    public void testSubscribeDistinct() throws Exception {
        List<String> list = Arrays.asList("one", "two", "two", "three");
        Observable<String> observable = Observable.from(list).distinct();
        //
        setupCallbackWithCompletion(observable, false, true);
        //
        assertEquals(3, onNext.size());
        assertEquals(0, onErrors.size());
        assertEquals(1, onCompletion);
    }

    @Test
    public void testSubscribeFromArray() throws Exception {
        Observable<String> observable = Observable.from("one", "two", "three",
                "four", "five").distinct();
        //
        setupCallbackWithCompletion(observable, false, true);
        //
        assertEquals(5, onNext.size());
        assertEquals(0, onErrors.size());
        assertEquals(1, onCompletion);
    }

    @Test
    public void testSubscribeMap() throws Exception {
        List<Integer> hashes = new ArrayList<>();

        Observable<Integer> observable = Observable.from(names).map(
                name -> name.hashCode());
        subscription = observable.subscribe(hash -> hashes.add(hash),
                error -> onErrors.add(error), () -> onCompletion++);

        Thread.sleep(200);

        assertEquals(names.size(), hashes.size());
        assertEquals(0, onErrors.size());
        assertEquals(1, onCompletion);
    }

    @Test
    public void testSubscribeFlatMap() throws Exception {
        List<Integer> merged = new ArrayList<>();

        Stream<List<Integer>> integerListStream = Stream.of(
                Arrays.asList(1, 2), Arrays.asList(3, 4), Arrays.asList(5));

        Observable
                .from(integerListStream)
                .flatMap(integerList -> integerList.stream())
                .subscribe(num -> merged.add(num),
                        error -> onErrors.add(error), () -> onCompletion++);

        Thread.sleep(200);

        assertEquals(5, merged.size());
        assertEquals(0, onErrors.size());
        assertEquals(1, onCompletion);
    }

    @Test
    public void testSubscribeManual() throws Exception {
        // Observable.from(names).subscribe(System.out::println,
        // Throwable::printStackTrace);
        //
        // Observable.from(names.stream()).subscribe(
        // name -> System.out.println(name),
        // error -> error.printStackTrace());
        //
        // Observable.from(names.iterator()).subscribe(
        // name -> System.out.println(name),
        // error -> error.printStackTrace());
        //
        // Observable.from(names.iterator()).subscribe(
        // name -> System.out.println(name),
        // error -> error.printStackTrace());

        // Observable.from(names.spliterator()).subscribe(
        // name -> System.out.println(name),
        // error -> error.printStackTrace());

        // Observable.just("value").subscribe(v -> System.out.println(v),
        // error -> error.printStackTrace());
        //
        // Observable.just(Arrays.asList(1, 2, 3)).subscribe(
        // num -> System.out.println(num),
        // error -> error.printStackTrace());
        //
        // Observable.create(observer -> {
        // for (String name : names) {
        // observer.onNext(name);
        // }
        // observer.onCompleted();
        // }).subscribe(System.out::println, Throwable::printStackTrace);
        //
        // Observable.range(4, 8).subscribe(num -> System.out.println(num),
        // error -> error.printStackTrace());
        Observable.throwing(new Error("test error")).subscribe(
                System.out::println, error -> System.err.println(error));
    }

    @Test
    public void testSubscribeFrom() throws Exception {
        for (int i = 0; i < 8; i++) {
            Observable<String> observable = null;
            if (i > 6) {
                observable = Observable.from(names.spliterator());
                observable.subscribeOn(Scheduler.getThreadPoolScheduler());
            } else if (i > 4) {
                observable = Observable.from(names);
                observable.subscribeOn(Scheduler.getNewThreadScheduler());
            } else if (i > 2) {
                observable = Observable.from(names.stream());
                observable.subscribeOn(Scheduler
                        .getTimerSchedulerWithMilliInterval(1));
            } else {
                observable = Observable.from(names.iterator());
                observable.subscribeOn(Scheduler.getThreadPoolScheduler());
            }
            //
            setupCallbackWithCompletion(observable, i % 2 == 0, true);
            //
            assertEquals("unexpected size for " + i, names.size(),
                    onNext.size());
            assertEquals(0, onErrors.size());
            assertEquals("i " + i, 1, onCompletion);
        }
    }

    @Test
    public void testSubscribeFromInfiniteNats() throws Exception {
        Observable<Integer> observable = Observable.from(new NatsSpliterator());
        setupCallbackWithCompletion(observable, false, false);
        Thread.sleep(1);
        subscription.dispose();
        //
        assertTrue(onNext.size() > 0);
        assertEquals("Unexpected errors " + onErrors, 0, onErrors.size());
        assertEquals(0, onCompletion);
    }

    @Test
    public void testSubscribeEmpty() throws Exception {
        for (int i = 0; i < 2; i++) {
            Observable<String> observable = Observable.empty();
            setupCallbackWithoutCompletion(observable, i % 2 == 0, true);
            assertEquals(0, onNext.size());
            assertEquals(0, onErrors.size());
        }
    }

    @Test
    public void testSubscribeJust() throws Exception {
        for (int i = 0; i < 2; i++) {
            Observable<String> observable = Observable.just("One");
            setupCallbackWithCompletion(observable, i % 2 == 0, true);
            assertEquals(1, onNext.size());
            assertEquals(0, onErrors.size());
            assertEquals(1, onCompletion);
        }
    }

    @Test
    public void testSubscribeThrowing() throws Exception {
        for (int i = 0; i < 2; i++) {
            Observable<String> observable = Observable.throwing(new Error());
            setupCallbackWithCompletion(observable, i % 2 == 0, true);
            assertEquals(0, onNext.size());
            assertEquals(1, onErrors.size());
            assertEquals(0, onCompletion);
        }
    }

    @Test
    public void testSubscribeNever() throws Exception {
        for (int i = 0; i < 2; i++) {
            Observable<String> observable = Observable.never();
            setupCallbackWithCompletion(observable, i % 2 == 0, true);
            assertEquals(0, onNext.size());
            assertEquals(0, onErrors.size());
            assertEquals(0, onCompletion);
        }
    }

    private <T> Observable<T> setupCallbackWithCompletion(
            Observable<T> observable, boolean immediate, boolean sleep)
            throws Exception {
        onNext.clear();
        onErrors.clear();
        onCompletion = 0;

        if (immediate) {
            observable.subscribeOn(Scheduler.getImmediateScheduler());
        }
        subscription = observable.subscribe(name -> onNext.add(name),
                error -> onErrors.add(error), () -> onCompletion++);
        if (!immediate && sleep) {
            Thread.sleep(100);
        }
        return observable;
    }

    private <T> Observable<T> setupCallbackWithoutCompletion(
            Observable<T> observable, boolean immediate, boolean sleep)
            throws Exception {
        onNext.clear();
        onErrors.clear();
        onCompletion = 0;
        if (immediate) {
            observable.subscribeOn(Scheduler.getImmediateScheduler());
        }
        subscription = observable.subscribe(name -> onNext.add(name),
                error -> onErrors.add(error));
        if (!immediate && sleep) {
            Thread.sleep(100);
        }
        return observable;
    }
}
