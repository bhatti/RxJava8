package com.plexobject.rx.scheduler;

import java.util.function.Consumer;

import com.plexobject.rx.Disposable;
import com.plexobject.rx.impl.SubscriptionObserver;

/**
 * This implementation of scheduler uses same thread to notify subscriber. This
 * should not be used for large data as it doesn't allow user to unsubscribe.
 * 
 * @author Shahzad Bhatti
 *
 */
public class ImmediateScheduler implements Scheduler, Disposable {
    private boolean shutdown;

    @Override
    public synchronized void dispose() {
        shutdown = true;
    }

    @Override
    public <T> void scheduleTick(Consumer<SubscriptionObserver<T>> consumer,
            SubscriptionObserver<T> subscription) {
        if (shutdown) {
            throw new IllegalStateException("Already shutdown");
        }
        consumer.accept(subscription);

    }
}
