package com.plexobject.rx;

import java.util.function.Consumer;

import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.plexobject.rx.scheduler.Scheduler;

@RunWith(JMockit.class)
public class ObservableCountTest {
    @Mocked
    private Consumer<Long> onNext;
    @Mocked
    private Consumer<Throwable> onError;
    @Mocked
    private OnCompletion onCompleted;

    @Test
    public void testSubscribeCount() throws Exception {
        Observable<Long> observable = Observable.from(1, 2, 3, 4, 5)
                .subscribeOn(Scheduler.newImmediateScheduler()).count();

        observable.subscribe(onNext, onError, onCompleted);

        new Verifications() {
            {
                onNext.accept(5L);
                onCompleted.onCompleted();
                onError.accept((Throwable) any);
                times = 0;
            }
        };
    }

    @Test
    public void testSubscribeCountEmpty() throws Exception {
        Observable<Long> observable = Observable.empty()
                .subscribeOn(Scheduler.newImmediateScheduler()).count();

        observable.subscribe(onNext, onError, onCompleted);

        new Verifications() {
            {
                onNext.accept(0L);
                onCompleted.onCompleted();
                onError.accept((Throwable) any);
                times = 0;
            }
        };
    }

    @Test
    public void testSubscribeError() throws Exception {
        Observable<Long> observable = Observable.create(observer -> {
            throw new RuntimeException("test");
        });

        observable.subscribe(onNext, onError, onCompleted);
        new Verifications() {
            {
                onNext.accept((Long) any);
                times = 0;
                onCompleted.onCompleted();
                times = 0;
                onError.accept((Throwable) any);
                times = 1;
            }
        };
    }

}
