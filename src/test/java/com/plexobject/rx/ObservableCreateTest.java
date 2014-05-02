package com.plexobject.rx;

import java.util.function.Consumer;

import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.plexobject.rx.scheduler.Scheduler;

@RunWith(JMockit.class)
public class ObservableCreateTest {
    @Mocked
    private Consumer<Integer> onNext;
    @Mocked
    private Consumer<Throwable> onError;
    @Mocked
    private OnCompletion onCompleted;

    @Test
    public void testSubscribeCreate() throws Exception {
        Observable<Integer> observable = Observable.create(observer -> {
            observer.onNext(1);
            observer.onNext(2);
            observer.onNext(3);
            observer.onCompleted();
        });

        observable.subscribe(onNext, onError, onCompleted);
        new Verifications() {
            {
                onNext.accept(1);
                onNext.accept(2);
                onNext.accept(3);
                onCompleted.onCompleted();
                onError.accept((Throwable) any);
                times = 0;
            }
        };
    }

    @Test
    public void testSubscribeCreateAsync() throws Exception {
        Observable<Integer> observable = Observable.create(observer -> {
            Scheduler.newNewThreadScheduler().scheduleBackgroundTask((o) -> {
                observer.onNext(1);
                observer.onNext(2);
                observer.onNext(3);
                observer.onCompleted();
            }, observer);
        });
        observable.subscribe(onNext, onError, onCompleted);
        new Verifications() {
            {
                onNext.accept(1);
                onNext.accept(2);
                onNext.accept(3);
                onCompleted.onCompleted();
                onError.accept((Throwable) any);
                times = 0;
            }
        };
    }

    @Test
    public void testSubscribeCreateWithError() throws Exception {
        Observable<Integer> observable = Observable.create(observer -> {
            observer.onNext(1);
            observer.onNext(2);
            observer.onError(new Exception());
            observer.onNext(3);
            observer.onCompleted();
        });

        observable.subscribe(onNext, onError, onCompleted);
        new Verifications() {
            {
                onNext.accept(1);
                onNext.accept(2);
                onNext.accept(3);
                times = 0;
                onError.accept((Throwable) any);
                onCompleted.onCompleted();
                times = 0;
            }
        };
    }
}
