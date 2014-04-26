package com.plexobject.rx.scheduler;

import java.util.function.Consumer;

import com.plexobject.rx.Disposable;
import com.plexobject.rx.impl.SubscriptionObserver;

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
     * @param consumer - callback function to notify tick
     * @param subscription
     */
    <T> void scheduleTick(Consumer<SubscriptionObserver<T>> consumer,
            SubscriptionObserver<T> subscription);

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
