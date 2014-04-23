package com.plexobject.rx;

public interface Observer<T> extends OnCompletion {
	void onNext(T obj);

	void onError(Throwable error);
}
