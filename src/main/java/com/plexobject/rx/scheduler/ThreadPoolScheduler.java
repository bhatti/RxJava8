package com.plexobject.rx.scheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plexobject.rx.Disposable;
import com.plexobject.rx.impl.SubscriptionObserver;

public class ThreadPoolScheduler implements Scheduler, Disposable {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory
            .getLogger(ThreadPoolScheduler.class);

    private final ExecutorService defaulExecutor = new ForkJoinPool();
    private boolean shutdown;

    @Override
    public synchronized void dispose() {
        shutdown = true;
        defaulExecutor.shutdown();
    }

    @Override
    public <T> void scheduleTick(Consumer<SubscriptionObserver<T>> consumer,
            SubscriptionObserver<T> subscription) {
        if (shutdown) {
            throw new IllegalStateException("Already shutdown");
        }
        defaulExecutor.submit(() -> {
            consumer.accept(subscription);
        });

    }
}
