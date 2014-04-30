package com.plexobject.rx.scheduler;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plexobject.rx.Disposable;

/**
 * This implementation of Scheduler notifies subscriber at given interval
 * 
 * @author Shahzad Bhatti
 *
 */
public class TimerScheduler implements Scheduler, Disposable {
    private static final Logger logger = LoggerFactory
            .getLogger(TimerScheduler.class);

    private final Timer timer = new Timer();
    private final long interval;
    private volatile boolean shutdown;

    public TimerScheduler(long interval) {
        this.interval = interval;
    }

    @Override
    public synchronized void dispose() {
        if (shutdown) return;
        shutdown = true;
        timer.cancel();
    }

    @Override
    public <T> void scheduleBackgroundTask(Consumer<T> consumer, T handle) {
        if (shutdown) {
            logger.warn("Already shutdown, cannot schedule new background task "
                    + consumer);
            return;
        }

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                consumer.accept(handle);
            }
        }, interval);

    }

}
