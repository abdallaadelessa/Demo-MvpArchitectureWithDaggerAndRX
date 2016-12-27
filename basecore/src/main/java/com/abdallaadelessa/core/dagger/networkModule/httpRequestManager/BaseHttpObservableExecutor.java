package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager;

import android.os.Handler;

import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.BaseRequest;
import com.abdallaadelessa.core.model.BaseCoreError;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by abdullah on 12/26/16.
 */

public abstract class BaseHttpObservableExecutor<M, R extends BaseRequest> {
    private final Handler handler = new Handler();
    //================>

    public abstract Observable<M> toObservable(R request);

    public BaseCoreError getMessageError(Throwable throwable) {
        return new BaseCoreError(throwable);
    }

    //================>

    protected void onSuccess(final R request, final Subscriber<? super M> subscriber, final M m) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                subscriber.onNext(m);
                subscriber.onCompleted();
            }
        });
    }

    protected void onError(final R request, final Subscriber subscriber, final Throwable e, final boolean fatal) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Throwable error = request.getInterceptor().interceptError(request, e, fatal);
                    subscriber.onError(error);
                } catch (Throwable ee) {
                    onError(request, subscriber, ee, true);
                }
            }
        });

    }

    //================>
}
