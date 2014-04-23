package com.plexobject.rx;

public interface Subscription extends Disposable {
	boolean isUnsubscribed();
}
