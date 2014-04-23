package com.plexobject.rx.impl;

import com.plexobject.rx.OnCompletion;

/**
 * This interface is internally used for notifying subscriber
 * 
 * @author Shahzad Bhatti
 *
 * @param <T>
 */
public interface Observer<T> extends OnCompletion {
    /**
     * This method is called to push data
     * 
     * @param obj
     */
    void onNext(T obj);

    /**
     * This method is called to notify error
     * 
     * @param error
     */
    void onError(Throwable error);
}
