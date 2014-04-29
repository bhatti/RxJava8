package com.plexobject.rx.scheduler;

import java.util.function.Consumer;

import com.plexobject.rx.Disposable;

/**
 * This interface defines method to schedule callback function that is invoked
 * upon each tick. It also defines a number of factory methods for creating
 * different types of Schedulers.
 * 
 * @author Shahzad Bhatti
 *
 */
public interface Scheduler extends Disposable {
    /**
     * This method registers user-defined function that is invoked by scheduler
     * 
     * @param consumer
     *            - callback function to notify tick
     * @param handle
     *            to pass in with consumer
     */
    <T> void scheduleBackgroundTask(Consumer<T> consumer, T handle);

    public static Scheduler getThreadPoolScheduler() {
        return new ThreadPoolScheduler();
    }

    public static Scheduler getImmediateScheduler() {
        return new ImmediateScheduler();
    }

    public static Scheduler getNewThreadScheduler() {
        return new NewThreadScheduler();
    }

    public static Scheduler getTimerSchedulerWithMilliInterval(long interval) {
        return new TimerScheduler(interval);
    }
}
