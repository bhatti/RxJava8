package com.plexobject.rx.scheduler;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import com.plexobject.rx.Disposable;
import com.plexobject.rx.impl.SubscriptionObserver;

public class TimerScheduler implements Scheduler, Disposable {
	private final Timer timer = new Timer();
	private final long interval;
	private boolean shutdown;

	public TimerScheduler(long interval) {
		this.interval = interval;
	}

	@Override
	public synchronized void dispose() {
		shutdown = true;
		timer.cancel();
	}

	@Override
	public <T> void scheduleTick(Consumer<SubscriptionObserver<T>> consumer,
	        SubscriptionObserver<T> subscription) {
		if (shutdown) {
			throw new IllegalStateException("Already shutdown");
		}

		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				consumer.accept(subscription);
			}
		}, interval);

	}

}
