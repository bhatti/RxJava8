package com.plexobject.rx.impl;

import com.plexobject.rx.Subscription;

/**
 * This is used internally to keep track of subscription and to notify
 * subscriber
 * 
 * @author Shahzad Bhatti
 *
 * @param <T> type of subscription data
 */
public interface SubscriptionObserver<T> extends Subscription, Observer<T> {

}
