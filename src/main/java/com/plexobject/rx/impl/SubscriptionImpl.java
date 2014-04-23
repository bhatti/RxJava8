package com.plexobject.rx.impl;

import java.util.function.Consumer;

import com.plexobject.rx.OnCompletion;

public class SubscriptionImpl<T> implements SubscriptionObserver<T> {
    private final Consumer<T> onNext;
    private final Consumer<Throwable> onError;
    private final OnCompletion onCompletion;
    private boolean subscribed;

    public SubscriptionImpl(Consumer<T> onNext, Consumer<Throwable> onError,
            OnCompletion onCompletion) {
        this.onNext = onNext;
        this.onError = onError;
        this.onCompletion = onCompletion;
        this.subscribed = true;
    }

    @Override
    public void dispose() {
        subscribed = false;
    }

    @Override
    public boolean isUnsubscribed() {
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
