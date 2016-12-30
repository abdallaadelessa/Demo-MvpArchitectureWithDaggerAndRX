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
import rx.functions.Func1;

/**
 * Created by abdullah on 12/26/16.
 */

public abstract class BaseHttpExecutor<M, R extends BaseRequest> {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isCanceled;

    //================>

    public final Observable<M> toObservable(R request) {
        return getRequest(request).flatMap(new Func1<R, Observable<M>>() {
            @Override
            public Observable<M> call(final R r) {
                try {
                    isCanceled = false;
                    return buildObservable(r).doOnUnsubscribe(new Action0() {
                        @Override
                        public void call() {
                            forceCancelRequestOnUnSubscribe(r);
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

    //================>

    public void cancel() {
        isCanceled = true;
        cancelExecutor();
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    protected abstract void cancelExecutor();

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

    protected void onSuccess(final Subscriber subscriber, final R request, final String response) {
        if (isCanceled()) {
            RxUtils.unSubscribeIfNotNull(subscriber);
            return;
        }
        request.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final M m = parse(response, request);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            subscriber.onNext(m);
                            subscriber.onCompleted();
                        }
                    });
                } catch (final Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onError(subscriber, request, e, false);
                        }
                    });
                }
            }
        });

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

    //================>

    private void cancelRequestByHttpRequestManager(R request) {
        BaseCoreApp.getInstance().getNetworkComponent().getHttpRequestManager().cancelRequestByTag(request.getTag());
    }

    private void forceCancelIfRunning(R r) {
        if (r.isCancelIfWasRunning() && !ValidationUtils.isStringEmpty(r.getTag())) {
            cancelRequestByHttpRequestManager(r);
        }
    }

    private void forceCancelRequestOnUnSubscribe(R r) {
        if (r.isCancelOnUnSubscribe() && !ValidationUtils.isStringEmpty(r.getTag())) {
            cancelRequestByHttpRequestManager(r);
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
