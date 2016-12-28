package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager;

import android.os.Handler;

import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.BaseRequest;
import com.abdallaadelessa.core.model.BaseCoreError;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by abdullah on 12/26/16.
 */

public abstract class BaseHttpExecutor<M, R extends BaseRequest> {
    private final Handler handler = new Handler();
    //================>

    public abstract Observable<M> toObservable(R request);

    public BaseCoreError getBaseCoreError(Throwable throwable) {
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
                    Throwable error = e;
                    List<HttpInterceptor> interceptors = request.getInterceptors();
                    for (HttpInterceptor interceptor : interceptors) {
                        error = interceptor.interceptError(request, error, fatal);
                    }
                    subscriber.onError(error);
                } catch (Throwable ee) {
                    onError(request, subscriber, ee, true);
                }
            }
        });

    }

    //================>
}
