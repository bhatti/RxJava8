package com.plexobject.rx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.plexobject.rx.scheduler.Scheduler;

public class ObservableSchedulerTest extends BaseObservableTest {
    private static final int MAX_NUMBERS = 10;

    @Test
    public void testSubscribeFromWithTimer() throws Exception {
        List<Long> times = new ArrayList<>();

        Observable<Integer> observable = Observable.integers(1).limit(
                MAX_NUMBERS);
        initLatch(MAX_NUMBERS + 1); // N*onNext + onCompleted

        observable.subscribeOn(Scheduler
                .getTimerSchedulerWithMilliInterval(MAX_NUMBERS));
        setupCallback(observable, (v) -> times.add(System.currentTimeMillis()),
                true);

        latch.await(200, TimeUnit.MILLISECONDS);

        for (int i = 1; i < times.size(); i++) {
            long interval = times.get(i) - times.get(i - 1);
            assertTrue("interval should be >= " + MAX_NUMBERS + " for i " + i
                    + ", " + times, interval >= MAX_NUMBERS);
        }
        //
        assertEquals(MAX_NUMBERS, onNext.get());
        assertNull(onError.get());
        assertEquals(1, onCompleted.get());
    }

    @Test
    public void testErrorOnNext() throws Exception {
        Observable<Integer> observable = Observable.integers(1).limit(
                MAX_NUMBERS);
        initLatch(1);

        observable.subscribeOn(Scheduler.getNewThreadScheduler());
        setupCallback(observable, (v) -> {
            throw new RuntimeException("test error");
        }, true);

        latch.await(200, TimeUnit.MILLISECONDS);

        assertEquals(0, onNext.get());
        assertNotNull(onError.get());
        assertEquals(0, onCompleted.get());
    }

    @Test
    public void testSubscribeWithDifferentSchedulers() throws Exception {
        for (int i = 0; i < 8; i++) {
            initLatch(names.size() + 1); // N*onNext + onCompleted

            Observable<String> observable = Observable.from(names);

            if (i > 6) {
                observable.subscribeOn(Scheduler.getThreadPoolScheduler());
            } else if (i > 4) {
                observable.subscribeOn(Scheduler.getNewThreadScheduler());
            } else if (i > 2) {
                observable.subscribeOn(Scheduler
                        .getTimerSchedulerWithMilliInterval(1));
            } else {
                observable.subscribeOn(Scheduler.getImmediateScheduler());
            }

            setupCallback(observable, null, true);
            latch.await(100, TimeUnit.MILLISECONDS);
            //
            assertEquals(names.size(), onNext.get());

            assertNull(onError.get());
            assertEquals(1, onCompleted.get());
        }
    }
}
