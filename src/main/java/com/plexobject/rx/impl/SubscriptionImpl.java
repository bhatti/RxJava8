package com.plexobject.rx.impl;

import java.util.function.Consumer;

import com.plexobject.rx.Cancelable;
import com.plexobject.rx.OnCompletion;

/**
 * This class keeps track of user subscription including callback functions.
 * This also allows user to unsubcribe from receiving data.
 * 
 * @author Shahzad Bhatti
 *
 * @param <T>
 *            type of subscription data
 */
public class SubscriptionImpl<T> implements SubscriptionObserver<T> {
    private final Consumer<T> onNext;
    private final Consumer<Throwable> onError;
    private final OnCompletion onCompletion;
    private final Cancelable cancelable;
    private volatile boolean subscribed;

    public SubscriptionImpl(Consumer<T> onNext, Consumer<Throwable> onError,
            OnCompletion onCompletion, Cancelable cancelable) {
        this.onNext = onNext;
        this.onError = onError;
        this.onCompletion = onCompletion;
        this.cancelable = cancelable;
        this.subscribed = true;
    }

    @Override
    public void dispose() {
        subscribed = false;
        if (cancelable != null) {
            cancelable.cancel();
        }
    }

    @Override
    public boolean isSubscribed() {
        return subscribed;
    }

    @Override
    public void onNext(T obj) {
        if (subscribed) {
            onNext.accept(obj);
        }

    }

    @Override
    public void onError(Throwable error) {
        if (subscribed && onError != null) {
            onError.accept(error);
        }
        subscribed = false;
    }

    @Override
    public void onCompleted() {
        if (subscribed && onCompletion != null) {
            onCompletion.onCompleted();
        }
        subscribed = false;
    }
}
