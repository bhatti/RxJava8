package com.plexobject.rx.scheduler;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plexobject.rx.Disposable;

/**
 * This implementation of scheduler uses same thread to notify subscriber. This
 * should not be used for large data as it doesn't allow user to unsubscribe.
 * 
 * @author Shahzad Bhatti
 *
 */
public class ImmediateScheduler implements Scheduler, Disposable {
    private static final Logger logger = LoggerFactory
            .getLogger(ImmediateScheduler.class);

    private volatile boolean shutdown;

    @Override
    public void dispose() {
        shutdown = true;
    }

    @Override
    public <T> void scheduleBackgroundTask(Consumer<T> consumer, T handle) {
        if (shutdown) {
            logger.warn("Already shutdown, cannot schedule new background task "
                    + consumer);
            return;
        }
        consumer.accept(handle);

    }
}
