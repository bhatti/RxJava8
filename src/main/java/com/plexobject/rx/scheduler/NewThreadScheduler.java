package com.plexobject.rx.scheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plexobject.rx.Disposable;

/**
 * This method will create a new thread for scheduling, which notifies user for
 * incoming data and errors
 * 
 * @author Shahzad Bhatti
 *
 */
public class NewThreadScheduler implements Scheduler, Disposable {
    private static final Logger logger = LoggerFactory
            .getLogger(NewThreadScheduler.class);

    final ExecutorService defaulExecutor = Executors.newSingleThreadExecutor();
    private boolean shutdown;

    @Override
    public synchronized void dispose() {
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
