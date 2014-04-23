package com.plexobject.rx.impl;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.plexobject.rx.Observable;
import com.plexobject.rx.Observer;
import com.plexobject.rx.OnCompletion;
import com.plexobject.rx.Subscription;
import com.plexobject.rx.scheduler.Scheduler;

public class ObservableDelegate<T> implements Observable<T> {
	private Consumer<Observer<T>> consumer;

	public ObservableDelegate(final Consumer<Observer<T>> consumer) {
		this.consumer = consumer;
	}

	@Override
	public Subscription subscribe(Consumer<T> onNext,
	        Consumer<Throwable> onError) {
		return subscribe(onNext, onError, null);
	}

	public synchronized Subscription subscribe(Consumer<T> onNext,
	        Consumer<Throwable> onError, OnCompletion onCompletion) {
		Objects.requireNonNull(onNext);
		SubscriptionObserver<T> subscription = new SubscriptionImpl<T>(onNext,
		        onError, onCompletion);
		consumer.accept(new Observer<T>() {
			@Override
			public void onNext(T obj) {
				if (!subscription.isUnsubscribed()) {
					onNext.accept(obj);
				}
			}

			@Override
			public void onError(Throwable error) {
				if (!subscription.isUnsubscribed()) {
					onError.accept(error);
				}
			}

			@Override
			public void onCompleted() {
				if (!subscription.isUnsubscribed()) {
					onCompletion.onCompleted();
				}
				subscription.dispose();
			}
		});
		return subscription;
	}

	@Override
	public Observable<T> subscribeOn(Scheduler scheduler) {
		return this;
	}

	@Override
	public Observable<T> distinct() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Observable<T> filter(Predicate<? super T> predicate) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <R> Observable<R> map(Function<? super T, ? extends R> mapper) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <R> Observable<R> flatMap(
	        Function<? super T, ? extends Stream<? extends R>> mapper) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Observable<T> limit(long maxSize) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Observable<T> skip(long n) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Observable<T> sorted() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Observable<T> sorted(Comparator<? super T> comparator) {
		throw new UnsupportedOperationException();
	}
}
