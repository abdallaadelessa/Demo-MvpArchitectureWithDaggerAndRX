package com.abdallaadelessa.core.presenter;

import java.lang.ref.WeakReference;

/**
 * Created by abdullah on 12/10/16.
 */

public abstract class BaseCorePresenter<V> {

    private WeakReference<V> viewRef;

    public void attachView(V view) {
        viewRef = new WeakReference<V>(view);
    }

    public void detachView() {
        if (viewRef != null) {
            viewRef.clear();
            viewRef = null;
        }
    }

    protected boolean isViewAttached() {
        return viewRef != null && viewRef.get() != null;
    }

    protected V getView() {
        return viewRef == null ? null : viewRef.get();
    }

    public abstract void loadViewData();

}
