package com.plexobject.rx;

/**
 * This interface is used to unsubscribe from Observable and to check if
 * subscription is still valid
 * 
 * @author Shahzad Bhatti
 *
 */
public interface Subscription extends Disposable {
    /**
     * This method returns true if user is still subscribed and false otherwise
     * 
     * @return subscription status - true/false
     */
    boolean isUnsubscribed();
}
