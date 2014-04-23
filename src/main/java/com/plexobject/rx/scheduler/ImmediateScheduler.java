package com.plexobject.rx.scheduler;

import java.util.function.Consumer;

import com.plexobject.rx.Disposable;
import com.plexobject.rx.impl.SubscriptionObserver;

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
