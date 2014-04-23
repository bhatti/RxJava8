package com.plexobject.rx.scheduler;

import java.util.function.Consumer;

import com.plexobject.rx.Disposable;
import com.plexobject.rx.impl.SubscriptionObserver;

public interface Scheduler extends Disposable {
	<T> void scheduleTick(Consumer<SubscriptionObserver<T>> consumer,
	        SubscriptionObserver<T> subscription);

	public static Scheduler getThreadPoolScheduler() {
		return new ThreadPoolScheduler();
	}

	public static Scheduler getImmediateScheduler() {
		return new ImmediateScheduler();
	}

	public static Scheduler getThreadScheduler() {
		return new ThreadScheduler();
	}

	public static Scheduler getTimerSchedulerWithMilliInterval(long interval) {
		return new TimerScheduler(interval);
	}
}
