package com.abdallaadelessa.core.presenter;

import com.abdallaadelessa.core.app.BaseCoreApp;

import java.lang.ref.WeakReference;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by abdullah on 12/10/16.
 */

public abstract class BaseCorePresenter<V> {
    private CompositeSubscription subscriptions = new CompositeSubscription();

    private WeakReference<V> viewRef;

    public void attachView(V view) {
        viewRef = new WeakReference<V>(view);
    }

    public void detachView() {
        try {
            if (subscriptions != null) {
                if (subscriptions.hasSubscriptions()) {
                    subscriptions.unsubscribe();
                }
                subscriptions.clear();
            }
        } catch (Exception e) {
            BaseCoreApp.getInstance().getLoggerComponent().getLogger().logError(e);
        }
        try {
            if (viewRef != null) {
                viewRef.clear();
                viewRef = null;
            }
        } catch (Exception e) {
            BaseCoreApp.getInstance().getLoggerComponent().getLogger().logError(e);
        }
    }

    protected boolean isViewAttached() {
        return viewRef != null && viewRef.get() != null;
    }

    protected V getView() {
        return viewRef == null ? null : viewRef.get();
    }

    public void addSubscription(Subscription subscription) {
        if (subscriptions != null) subscriptions.add(subscription);
    }

    public void loadViewData() {

    }

    public void handleError(Throwable throwable) {

    }

}
