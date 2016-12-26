package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager;

import android.os.Handler;

import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.BaseRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.HttpRequest;
import com.abdallaadelessa.core.model.MessageError;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by abdullah on 12/26/16.
 */

public abstract class BaseHttpObservableExecutor<T, R extends BaseRequest> {
    private final Handler handler = new Handler();
    //================>

    public abstract Observable<T> toObservable(R request);

    public MessageError getMessageError(Throwable throwable) {
        return new MessageError(throwable);
    }

    //================>

    protected void onSuccess(final R request, final Subscriber<? super T> subscriber, final T t) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                subscriber.onNext(t);
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
                }
                catch(Throwable ee) {
                    onError(request, subscriber, ee, true);
                }
            }
        });

    }

    //================>
}
