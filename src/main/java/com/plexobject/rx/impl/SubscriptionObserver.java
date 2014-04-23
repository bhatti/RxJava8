package com.plexobject.rx.impl;

import com.plexobject.rx.Observer;
import com.plexobject.rx.Subscription;

public interface SubscriptionObserver<T> extends Subscription, Observer<T> {

}
