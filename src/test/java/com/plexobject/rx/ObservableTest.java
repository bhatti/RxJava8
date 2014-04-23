package com.plexobject.rx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.plexobject.rx.scheduler.Scheduler;
import com.plexobject.rx.util.NatsSpliterator;
import com.plexobject.rx.util.SpliteratorFromIterator;

public class ObservableTest {
	private Set<Object> onNext = new HashSet<>();
	private Set<Throwable> onErrors = new HashSet<>();
	private int onCompletion;
	private Subscription subscription;

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
		subscription.dispose();
	}

	@Test
	public void testSubscribeCreate() throws Exception {
		List<String> names = Arrays.asList("Erica", "Matt", "John", "Mike");
		Observable<String> observable = Observable
		        .create(new Consumer<Observer<String>>() {
			        @Override
			        public void accept(Observer<String> observer) {
				        for (String name : names) {
					        observer.onNext(name);
					        onNext.add(name);
				        }
				        onCompletion++;
			        }
		        });
		//
		setupCallbackWithCompletion(observable, false, true);
		//
		assertEquals(4, onNext.size());
		assertEquals(0, onErrors.size());
		assertEquals(1, onCompletion);
	}

	@Test
	public void testSubscribeFromWithTimer() throws Exception {
		List<String> names = Arrays.asList("Erica", "Matt", "John", "Mike");
		Observable<String> observable = Observable.from(names);
		List<Long> times = new ArrayList<>();
		observable
		        .subscribeOn(Scheduler.getTimerSchedulerWithMilliInterval(10));
		subscription = observable.subscribe(name -> {
			onNext.add(name);
			times.add(System.currentTimeMillis());
		}, error -> onErrors.add(error), () -> onCompletion++);

		Thread.sleep(100);
		for (int i = 1; i < times.size(); i++) {
			long interval = times.get(i) - times.get(i - 1);
			assertTrue("interval should be >= 10 for i " + i + ", " + times,
			        interval >= 10);
		}
		//
		assertEquals(4, onNext.size());
		assertEquals(0, onErrors.size());
		assertEquals(1, onCompletion);
	}

	@Test
	public void testSubscribeFrom() throws Exception {
		for (int i = 0; i < 8; i++) {
			List<String> names = Arrays.asList("Erica", "Matt", "John", "Mike");
			Observable<String> observable = null;
			if (i > 6) {
				observable = Observable
				        .from(new SpliteratorFromIterator<String>(names
				                .iterator()));
				observable.subscribeOn(Scheduler.getThreadPoolScheduler());
			} else if (i > 4) {
				observable = Observable.from(names);
				observable.subscribeOn(Scheduler.getThreadScheduler());
			} else if (i > 2) {
				observable = Observable.from(names.stream());
				observable.subscribeOn(Scheduler
				        .getTimerSchedulerWithMilliInterval(1));
			} else {
				observable = Observable.from(names.iterator());
			}
			//
			setupCallbackWithCompletion(observable, i % 2 == 0, true);
			//
			assertEquals("unexpected size for " + (i % 2 == 0), 4,
			        onNext.size());
			assertEquals(0, onErrors.size());
			assertEquals("i " + i, 1, onCompletion);
		}
	}

	@Test
	public void testSubscribeFromInfiniteNats() throws Exception {
		Observable<Integer> observable = Observable.from(new NatsSpliterator());
		setupCallbackWithCompletion(observable, false, false);
		Thread.sleep(1);
		subscription.dispose();
		//
		assertTrue(onNext.size() > 0);
		assertEquals("Unexpected errors " + onErrors, 0, onErrors.size());
		assertEquals(0, onCompletion);
	}

	@Test
	public void testSubscribeEmpty() throws Exception {
		for (int i = 0; i < 2; i++) {
			Observable<String> observable = Observable.empty();
			setupCallbackWithoutCompletion(observable, i % 2 == 0, true);
			assertEquals(0, onNext.size());
			assertEquals(0, onErrors.size());
		}
	}

	@Test
	public void testSubscribeJust() throws Exception {
		for (int i = 0; i < 2; i++) {
			Observable<String> observable = Observable.just("One");
			setupCallbackWithCompletion(observable, i % 2 == 0, true);
			assertEquals(1, onNext.size());
			assertEquals(0, onErrors.size());
			assertEquals(1, onCompletion);
		}
	}

	@Test
	public void testSubscribeThrowing() throws Exception {
		for (int i = 0; i < 2; i++) {
			Observable<String> observable = Observable.throwing(new Error());
			setupCallbackWithCompletion(observable, i % 2 == 0, true);
			assertEquals(0, onNext.size());
			assertEquals(1, onErrors.size());
			assertEquals(0, onCompletion);
		}
	}

	@Test
	public void testSubscribeNever() throws Exception {
		for (int i = 0; i < 2; i++) {
			Observable<String> observable = Observable.never();
			setupCallbackWithCompletion(observable, i % 2 == 0, true);
			assertEquals(0, onNext.size());
			assertEquals(0, onErrors.size());
			assertEquals(0, onCompletion);
		}
	}

	private <T> Observable<T> setupCallbackWithCompletion(
	        Observable<T> observable, boolean immediate, boolean sleep)
	        throws Exception {
		onNext.clear();
		onErrors.clear();
		onCompletion = 0;

		if (immediate) {
			observable.subscribeOn(Scheduler.getImmediateScheduler());
		}
		subscription = observable.subscribe(name -> onNext.add(name),
		        error -> onErrors.add(error), () -> onCompletion++);
		if (!immediate && sleep) {
			Thread.sleep(10);
		}
		return observable;
	}

	private <T> Observable<T> setupCallbackWithoutCompletion(
	        Observable<T> observable, boolean immediate, boolean sleep)
	        throws Exception {
		onNext.clear();
		onErrors.clear();
		onCompletion = 0;
		if (immediate) {
			observable.subscribeOn(Scheduler.getImmediateScheduler());
		}
		subscription = observable.subscribe(name -> onNext.add(name),
		        error -> onErrors.add(error));
		if (!immediate && sleep) {
			Thread.sleep(10);
		}
		return observable;
	}
}
