package com.plexobject.rx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.plexobject.rx.scheduler.Scheduler;

public class ObservableCreateTest extends BaseObservableTest {
    @Test
    public void testSubscribeCreate() throws Exception {
        Observable<String> observable = Observable.create(observer -> {
            for (String name : names) {
                observer.onNext(name);
            }
            observer.onCompleted();
        });
        initLatch(names.size() + 1); // N*onNext + onCompleted
        //
        setupCallback(observable, null, true);
        latch.await(100, TimeUnit.MILLISECONDS);
        //
        assertEquals(names.size(), onNext.get());
        assertNull(onError.get());
        assertEquals(1, onCompleted.get());
    }

    @Test
    public void testSubscribeCreateAsync() throws Exception {
        Observable<String> observable = Observable.create(observer -> {
            Scheduler.newNewThreadScheduler().scheduleBackgroundTask((o) -> {
                for (String name : names) {
                    o.onNext(name);
                }
                o.onCompleted();
            }, observer);
        });
        initLatch(names.size() + 1); // N*onNext + onCompleted
        //
        setupCallback(observable, null, true);
        latch.await(100, TimeUnit.MILLISECONDS);
        //
        assertEquals(names.size(), onNext.get());
        assertNull(onError.get());
        assertEquals(1, onCompleted.get());
    }
}
