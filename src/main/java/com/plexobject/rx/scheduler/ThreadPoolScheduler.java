package com.plexobject.rx.scheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plexobject.rx.Disposable;

/**
 * This implementation of Scheduler uses thread-pool for background processing
 * that pushes incoming data to user.
 * 
 * @author Shahzad Bhatti
 *
 */
public class ThreadPoolScheduler implements Scheduler, Disposable {
    private static final Logger logger = LoggerFactory
            .getLogger(ThreadPoolScheduler.class);

    private final ExecutorService defaulExecutor = new ForkJoinPool();
    private volatile boolean shutdown;

    @Override
    public synchronized void dispose() {
        if (shutdown) return;
        shutdown = true;
        defaulExecutor.shutdown();
    }

    @Override
    public <T> void scheduleBackgroundTask(Consumer<T> consumer, T handle) {
        if (shutdown) {
            logger.warn("Already shutdown, cannot schedule new background task "
                    + consumer);
            return;
        }
        defaulExecutor.submit(() -> {
            consumer.accept(handle);
        });

    }
}
