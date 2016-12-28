package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager;

import android.os.Handler;

import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.BaseRequest;
import com.abdallaadelessa.core.model.BaseCoreError;
import com.abdallaadelessa.core.utils.ValidationUtils;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;

/**
 * Created by abdullah on 12/26/16.
 */

public abstract class BaseHttpExecutor<M, R extends BaseRequest> {
    private final Handler handler = new Handler();
    //================>

    public final Observable<M> toObservable(R request) {
        return getRequest(request).flatMap(new Func1<R, Observable<M>>() {
            @Override
            public Observable<M> call(final R r) {
                try {
                    return buildObservable(r).doOnUnsubscribe(new Action0() {
                        @Override
                        public void call() {
                            cancelRequestOnSubscribe(r);
                        }
                    });
                } catch (Exception e) {
                    return Observable.error(interceptError(e, r, false));
                }
            }
        });
    }

    //================>

    protected abstract Observable<M> buildObservable(R request);

    protected abstract void cancelRequest(R request);

    //================>

    public BaseCoreError getBaseCoreError(Throwable throwable) {
        return new BaseCoreError(throwable);
    }

    //================>

    protected M parse(String json, R r) throws Exception {
        json = interceptResponse(json, r);
        return r.getParser().parse(r.getTag(), r.getType(), json);
    }

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
                    Throwable error = interceptError(e, request, fatal);
                    subscriber.onError(error);
                } catch (Throwable ee) {
                    onError(request, subscriber, ee, true);
                }
            }
        });

    }

    //================>

    private Observable<R> getRequest(final R request) {
        return Observable.create(new Observable.OnSubscribe<R>() {
            @Override
            public void call(Subscriber<? super R> subscriber) {
                try {
                    // Check Subscriber
                    if (subscriber.isUnsubscribed()) return;
                    // Intercept Request
                    R httpRequest = interceptRequest(request);
                    // Cancel if Running
                    cancelIfRunning(request);
                    // Complete
                    subscriber.onNext(httpRequest);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    onError(request, subscriber, e, false);
                }
            }
        });
    }

    private void cancelIfRunning(R r) {
        if (r.isCancelIfRunning() && !ValidationUtils.isStringEmpty(r.getTag())) {
            cancelRequest(r);
        }
    }

    private void cancelRequestOnSubscribe(R r) {
        if (r.isCancelOnUnSubscribe() && !ValidationUtils.isStringEmpty(r.getTag())) {
            cancelRequest(r);
        }
    }

    private R interceptRequest(R request) throws Exception {
        R httpRequest = request;
        List<HttpInterceptor> interceptors = request.getInterceptors();
        for (HttpInterceptor interceptor : interceptors) {
            httpRequest = (R) interceptor.interceptRequest(request);
        }
        return httpRequest;
    }

    private String interceptResponse(String response, R r) throws Exception {
        String json = response;
        List<HttpInterceptor> interceptors = r.getInterceptors();
        for (HttpInterceptor interceptor : interceptors) {
            json = interceptor.interceptResponse(r, json);
        }
        return json;
    }

    private Throwable interceptError(Throwable e, R request, boolean fatal) {
        Throwable error = e;
        List<HttpInterceptor> interceptors = request.getInterceptors();
        for (HttpInterceptor interceptor : interceptors) {
            error = interceptor.interceptError(request, error, fatal);
        }
        return error;
    }

    //================>
}
