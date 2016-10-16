package com.abdallaadelessa.core.utils;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class RxUtils {
    public static void unSubscribeIfNotNull(Subscription subscription) {
        if(subscription != null) {
            subscription.unsubscribe();
        }
    }

    public static CompositeSubscription getNewCompositeSubIfUnsubscribed(CompositeSubscription subscription) {
        if(subscription == null || subscription.isUnsubscribed()) {
            return new CompositeSubscription();
        }

        return subscription;
    }

    public static <T> Observable.Transformer<T, T> doOnAnyCase(final Action0 action0) {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        if(action0 != null) action0.call();
                    }
                }).doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        if(action0 != null) action0.call();
                    }
                }).doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if(action0 != null) action0.call();
                    }
                }).doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        if(action0 != null) action0.call();
                    }
                });
            }
        };
    }
}
