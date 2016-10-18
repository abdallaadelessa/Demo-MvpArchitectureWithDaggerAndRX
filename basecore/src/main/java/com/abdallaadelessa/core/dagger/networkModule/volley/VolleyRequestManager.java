package com.abdallaadelessa.core.dagger.networkModule.volley;


import android.os.Handler;
import android.support.annotation.NonNull;

import com.abdallaadelessa.core.dagger.networkModule.builders.HttpRequest;
import com.abdallaadelessa.core.utils.ValidationUtils;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.NetworkError;
import com.android.volley.error.ServerError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;

import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

/**
 * Created by abdulla on 8/12/15.
 */
public class VolleyRequestManager<T> {
    final Handler handler = new Handler();

    public Observable<T> createObservableFrom(final HttpRequest httpRequest) {
        final Observable<T> observable = Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(final Subscriber<? super T> subscriber) {
                try {
                    if (subscriber.isUnsubscribed()) return;
                    // Cancel All By Tag
                    canCancelIfRunning(httpRequest);
                    RequestQueue requestQueue = httpRequest.requestQueue();
                    final String tag = httpRequest.tag();
                    final String url = httpRequest.url();
                    final int method = httpRequest.method();
                    final Map<String, String> headers = httpRequest.headers();
                    final RetryPolicy retryPolicy = httpRequest.retryPolicy();
                    boolean shouldCache = httpRequest.shouldCache();
                    //---------> On Start
                    httpRequest.responseInterceptor().onStart(tag, url);
                    //---------> Listeners
                    Response.Listener<String> stringListener = getStringListener(httpRequest, subscriber);
                    Response.ErrorListener errorListener = getErrorListener(httpRequest, subscriber);
                    //---------> Request
                    StringRequest request = new StringRequest(method, url, stringListener, errorListener) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            return httpRequest.params();
                        }

                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            return httpRequest.hasBody() ? httpRequest.bodyToBytes() : super.getBody();
                        }

                        @Override
                        public String getBodyContentType() {
                            return !ValidationUtils.isStringEmpty(httpRequest.contentType()) ? httpRequest.contentType() : super.getBodyContentType();
                        }
                    };
                    request.setHeaders(headers);
                    request.setRetryPolicy(retryPolicy);
                    request.setShouldCache(shouldCache);
                    if (!ValidationUtils.isStringEmpty(tag)) request.setTag(tag);
                    requestQueue.add(request);
                } catch (Exception e) {
                    onError(httpRequest, subscriber, e, false);
                }
            }
        });

        //---------> OnSubscribe
        return observable.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                canCancelRequestOnSubscribe(httpRequest);
            }
        });
    }

    @NonNull
    private Response.Listener<String> getStringListener(final HttpRequest httpRequest, final Subscriber<? super T> subscriber) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                httpRequest.executorService().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String json = httpRequest.responseInterceptor().interceptResponse(httpRequest.tag(), httpRequest.url(), response);
                            final T t = httpRequest.responseInterceptor().parse(httpRequest.tag(), httpRequest.type(), json);
                            onSuccess(httpRequest, subscriber, t);
                        } catch (final Throwable e) {
                            onError(httpRequest, subscriber, e, false);
                        }
                    }
                });
            }
        };
    }

    @NonNull
    private Response.ErrorListener getErrorListener(final HttpRequest httpRequest, final Subscriber<? super T> subscriber) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError e) {
                httpRequest.executorService().execute(new Runnable() {
                    @Override
                    public void run() {
                        onError(httpRequest, subscriber, e, false);
                    }
                });
            }
        };
    }

    // -------------------------> Helpers

    private void canCancelIfRunning(HttpRequest httpRequest) {
        final String tag = httpRequest.tag();
        final boolean cancelIfRunning = httpRequest.cancelIfRunning();
        if (cancelIfRunning && !ValidationUtils.isStringEmpty(tag)) {
            cancelRequestByTag(httpRequest.requestQueue(), tag);
        }
    }

    private void canCancelRequestOnSubscribe(HttpRequest httpRequest) {
        final String tag = httpRequest.tag();
        final boolean cancelOnUnSubscribe = httpRequest.cancelOnUnSubscribe();
        if (cancelOnUnSubscribe && !ValidationUtils.isStringEmpty(tag)) {
            cancelRequestByTag(httpRequest.requestQueue(), tag);
        }
    }

    private void onSuccess(final HttpRequest httpRequest, final Subscriber<? super T> subscriber, final T t) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                subscriber.onNext(t);
                subscriber.onCompleted();
            }
        });
    }

    private void onError(final HttpRequest httpRequest, final Subscriber<? super T> subscriber, final Throwable e, final boolean fatal) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Throwable error = httpRequest.responseInterceptor().interceptError(httpRequest.tag(), httpRequest.url(), e, fatal);
                    subscriber.onError(error);
                } catch (Throwable ee) {
                    onError(httpRequest, subscriber, ee, true);
                }
            }
        });

    }

    // ------------------------->

    public static String getJsonError(Throwable throwable) {
        String errorInString = null;
        try {
            if (throwable != null && isVolleyError(throwable) && ((VolleyError) throwable).networkResponse != null && ((VolleyError) throwable).networkResponse.data != null) {
                errorInString = new String(((VolleyError) throwable).networkResponse.data);
            }
        } catch (Exception e) {
            //Eat it!
        }
        return errorInString;
    }

    public static boolean isVolleyError(Throwable error) {
        return error instanceof VolleyError;
    }

    public static boolean isTimeoutError(Throwable error) {
        return error instanceof TimeoutError;
    }

    public static boolean isNetworkError(Throwable error) {
        return (error instanceof NetworkError);
    }

    public static boolean isServerError(Throwable error) {
        return (error instanceof ServerError) || (error instanceof AuthFailureError);
    }

    public static boolean isBadRequestError(Throwable error) {
        return isVolleyError(error) && (((VolleyError) error).networkResponse != null && (((VolleyError) error).networkResponse.statusCode == 400));
    }

    // ------------------------->

    public static void cancelRequestByTag(RequestQueue requestQueue, final String tag) {
        if (tag == null) return;
        requestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return request != null && tag.equals(request.getTag());
            }
        });
    }
}
