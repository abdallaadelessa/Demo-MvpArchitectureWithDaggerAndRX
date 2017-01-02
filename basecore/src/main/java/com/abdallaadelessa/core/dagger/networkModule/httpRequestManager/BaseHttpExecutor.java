package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager;

import android.os.Handler;
import android.os.Looper;

import com.abdallaadelessa.core.app.BaseCoreApp;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.BaseRequest;
import com.abdallaadelessa.core.model.BaseCoreError;
import com.abdallaadelessa.core.utils.RxUtils;
import com.abdallaadelessa.core.utils.ValidationUtils;

import java.net.SocketException;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by abdullah on 12/26/16.
 */

public abstract class BaseHttpExecutor<M, R extends BaseRequest> {
    private boolean isCanceled;
    private Observable<M> observable;

    //================>

    public final Observable<M> toObservable(R request) {
        observable = getRequest(request).flatMap(new Func1<R, Observable<M>>() {
            @Override
            public Observable<M> call(final R r) {
                try {
                    isCanceled = false;
                    return buildObservable(r).doOnUnsubscribe(new Action0() {
                        @Override
                        public void call() {
                            doOnUnSubscribe(r);
                        }
                    });
                } catch (Exception e) {
                    return Observable.error(interceptError(e, r, false));
                }
            }
        }).share();
        return observable;
    }

    //================>

    protected abstract Observable<M> buildObservable(R request);

    protected abstract void cancelExecutor();

    //================>

    public void cancel() {
        isCanceled = true;
        cancelExecutor();
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    //================>

    public BaseCoreError convertErrorToBaseCoreError(Throwable throwable) {
        BaseCoreError baseCoreError;
        if (throwable instanceof SocketException) {
            baseCoreError = new BaseCoreError(BaseCoreError.CODE_NETWORK_ERROR, throwable);
        } else {
            baseCoreError = new BaseCoreError(throwable);
        }
        return baseCoreError;
    }

    //================>

    protected void onNext(final Subscriber subscriber, final R request, final String response) {
        if (isCanceled()) {
            RxUtils.unSubscribeIfNotNull(subscriber);
            return;
        }
        try {
            final M m = parse(response, request);
            subscriber.onNext(m);
        } catch (final Exception e) {
            onError(subscriber, request, e, false);
        }

    }

    protected void onError(final Subscriber subscriber, final R request, final Throwable e, final boolean fatal) {
        if (isCanceled()) {
            RxUtils.unSubscribeIfNotNull(subscriber);
            return;
        }
        try {
            final Throwable error = interceptError(convertErrorToBaseCoreError(e), request, fatal);
            subscriber.onError(error);
        } catch (Throwable ee) {
            subscriber.onError(ee);
        }
    }

    protected void onCompleted(final Subscriber subscriber, final R request) {
        if (isCanceled()) {
            RxUtils.unSubscribeIfNotNull(subscriber);
            return;
        }
        try {
            subscriber.onCompleted();
        } catch (Throwable ee) {
            subscriber.onError(ee);
        }
    }

    //================>

    private void forceCancelIfRunning(R r) {
        if (r.isForceCancelIfWasRunning() && !ValidationUtils.isStringEmpty(r.getTag())) {
            BaseCoreApp.getInstance().getNetworkComponent().getHttpRequestManager().cancelRequestsByTag(r.getTag());
        }
    }

    private void forceCancelRequestOnUnSubscribe(R r) {
        if (r.isForceCancelOnUnSubscribe() && !ValidationUtils.isStringEmpty(r.getTag())) {
            BaseCoreApp.getInstance().getNetworkComponent().getHttpRequestManager().cancelRequestById(r.getId());
        }
    }

    //================>

    private Observable<R> getRequest(final R request) {
        return Observable.create(new Observable.OnSubscribe<R>() {
            @Override
            public void call(Subscriber<? super R> subscriber) {
                try {
                    // Check Subscriber
                    if (subscriber.isUnsubscribed()) return;
                    // Cancel if Running
                    forceCancelIfRunning(request);
                    // Intercept Request
                    R httpRequest = interceptRequest(request);
                    // Complete
                    subscriber.onNext(httpRequest);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    onError(subscriber, request, e, false);
                }
            }
        });
    }

    private M parse(String json, R r) throws Exception {
        json = interceptResponse(json, r);
        return r.getParser().parse(r.getType(), json);
    }

    private void doOnUnSubscribe(R r) {
        forceCancelRequestOnUnSubscribe(r);
        r.clearInterceptors();
        r.setParser(null);
        r.setObservableExecutor(null);
    }

    //================>

    private R interceptRequest(R request) throws Exception {
        R httpRequest = request;
        List<BaseHttpInterceptor> interceptors = request.getInterceptors();
        for (BaseHttpInterceptor interceptor : interceptors) {
            httpRequest = (R) interceptor.interceptRequest(request);
        }
        return httpRequest;
    }

    private String interceptResponse(String response, R r) throws Exception {
        String json = response;
        List<BaseHttpInterceptor> interceptors = r.getInterceptors();
        for (BaseHttpInterceptor interceptor : interceptors) {
            json = interceptor.interceptResponse(r, json);
        }
        return json;
    }

    private Throwable interceptError(Throwable e, R request, boolean fatal) {
        Throwable error = e;
        List<BaseHttpInterceptor> interceptors = request.getInterceptors();
        for (BaseHttpInterceptor interceptor : interceptors) {
            error = interceptor.interceptError(request, error, fatal);
        }
        return error;
    }

    //================>
}
