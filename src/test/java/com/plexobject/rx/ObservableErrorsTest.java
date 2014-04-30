package com.plexobject.rx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.plexobject.rx.scheduler.Scheduler;

public class ObservableErrorsTest extends BaseObservableTest {
    @Test
    public void testSubscribeThrowing() throws Exception {
        for (Scheduler scheduler : allSchedulers) {
            Observable<String> observable = Observable.throwing(new Error());

            initLatch(1);
            observable.subscribeOn(scheduler);
            setupCallback(observable, null, false);
            latch.await(100, TimeUnit.MILLISECONDS);

            assertEquals(0, onNext.get());
            assertNotNull(onError.get());
            assertEquals(0, onCompleted.get());
        }
    }

    @Test
    public void testSubscribeExceptionInOnNext() throws Exception {
        Observable<Integer> observable = Observable.from(1, 2, 3);

        initLatch(1);
        setupCallback(observable, (v) -> {
            throw new RuntimeException("test error");
        }, false);
        latch.await(100, TimeUnit.MILLISECONDS);

        assertEquals(0, onNext.get());
        assertNotNull(onError.get());
        assertEquals(0, onCompleted.get());
    }

    @Test
    public void testSubscribeExceptionInOnError() throws Exception {
        Observable<Object> observable = Observable.throwing(new Error())
                .subscribeOn(Scheduler.newImmediateScheduler());

        initLatch(1);
        observable.subscribe(v -> {
        }, error -> {
            throw new RuntimeException();
        }, () -> {
        });
        latch.await(100, TimeUnit.MILLISECONDS);

        assertEquals(0, onNext.get());
        assertNull(onError.get());
        assertEquals(0, onCompleted.get());
    }

}
