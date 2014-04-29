package com.plexobject.rx;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BaseObservableTest {
    protected final List<String> names = Arrays.asList("Erica", "Matt", "John",
            "Mike", "Scott", "Alex", "Jeff", "Brad");

    protected Subscription subscription;
    protected CountDownLatch latch;
    protected AtomicReference<Throwable> onError;
    protected AtomicInteger onNext;
    protected AtomicInteger onCompleted;

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
        // Observable.throwing(new Error("test error")).subscribe(
        // System.out::println, error -> System.err.println(error));
    }

    protected void initLatch(int maxLatch) {
        latch = new CountDownLatch(maxLatch);
        onError = new AtomicReference<Throwable>();
        onNext = new AtomicInteger();
        onCompleted = new AtomicInteger();
    }

    protected <T> Observable<T> setupCallback(Observable<T> observable,
            Consumer<T> onNextWork, boolean onComplete) throws Exception {

        if (onComplete) {
            subscription = observable.subscribe(v -> {
                if (onNextWork != null) {
                    onNextWork.accept(v);
                }
                latch.countDown();
                onNext.incrementAndGet();
            }, error -> {
                latch.countDown();
                onError.set(error);
            }, () -> {
                latch.countDown();
                onCompleted.incrementAndGet();
            });
        } else {
            subscription = observable.subscribe(v -> {
                if (onNextWork != null) {
                    onNextWork.accept(v);
                }
                latch.countDown();
                onNext.incrementAndGet();
            }, error -> {
                latch.countDown();
                onError.set(error);
            });
        }

        return observable;
    }
}
