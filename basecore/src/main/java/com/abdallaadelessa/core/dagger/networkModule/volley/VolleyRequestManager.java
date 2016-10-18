package com.abdallaadelessa.core.dagger.networkModule.volley;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.abdallaadelessa.core.dagger.networkModule.builders.HttpRequest;
import com.abdallaadelessa.core.model.MessageError;
import com.abdallaadelessa.core.utils.ValidationUtils;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.NetworkError;
import com.android.volley.error.NoConnectionError;
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

    // --------------------->

    public Observable<T> createObservableFrom(final HttpRequest httpRequest) {
        final Observable<T> observable = Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(final Subscriber<? super T> subscriber) {
                if (subscriber.isUnsubscribed()) return;
                // Cancel All By Tag
                canCancelIfRunning(httpRequest);
                Context context = httpRequest.contextWeakReference() != null ? httpRequest.contextWeakReference().get() : null;
                RequestQueue requestQueue = httpRequest.requestQueue();
                final String tag = httpRequest.tag();
                final String url = httpRequest.url();
                final int method = httpRequest.method();
                final Map<String, String> headers = httpRequest.headers();
                final RetryPolicy retryPolicy = httpRequest.retryPolicy();
                boolean shouldCache = httpRequest.shouldCache();
                //---------> Listeners
                Response.Listener<String> stringListener = getStringListener(subscriber, httpRequest);
                Response.ErrorListener errorListener = getErrorListener(subscriber, httpRequest);
                //---------> Request
                if (context != null && !checkIfApplicationIsConnected(context)) {
                    errorListener.onErrorResponse(new NoConnectionError());
                    return;
                }
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
                if (requestQueue != null) requestQueue.add(request);
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
    private Response.Listener<String> getStringListener(final Subscriber<? super T> subscriber, final HttpRequest httpRequest) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                final Handler handler = new Handler();
                httpRequest.executorService().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            httpRequest.baseAppLogger().log(response);
                            String json = httpRequest.responseInterceptor().interceptResponse(httpRequest.tag(), response);
                            final T t = httpRequest.responseInterceptor().parse(httpRequest.tag(), httpRequest.type(), json);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    subscriber.onNext(t);
                                    subscriber.onCompleted();
                                }
                            });
                        } catch (final Throwable e) {
                            handleError(handler, subscriber, httpRequest, e, false);
                        }
                    }
                });
            }
        };
    }

    @NonNull
    private Response.ErrorListener getErrorListener(final Subscriber<? super T> subscriber, final HttpRequest httpRequest) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError e) {
                final Handler handler = new Handler();
                httpRequest.executorService().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (e != null && e.networkResponse != null && e.networkResponse.data != null) {
                                String responseError = getJsonErrorFromVolleyError(httpRequest, e);
                                httpRequest.baseAppLogger().log("Server Response Error : " + responseError);
                            } else {
                                httpRequest.baseAppLogger().log("Volley Error Type : " + (e != null ? e.getClass().getName() : "Null"));
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    subscriber.onError(httpRequest.responseInterceptor().interceptError(httpRequest.tag(), e));
                                }
                            });
                            if (isServerError(e)) {
                                httpRequest.baseAppLogger().logError(e, true);
                            }
                        } catch (Throwable ee) {
                            handleError(handler, subscriber, httpRequest, e, true);
                        }
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

    private static void handleError(Handler handler, final Subscriber subscriber, final HttpRequest httpRequest, final Throwable e, boolean fatal) {
        httpRequest.baseAppLogger().logError(e, fatal);
        if (handler != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        subscriber.onError(e);
                    } catch (Throwable ee) {
                        httpRequest.baseAppLogger().logError(ee, true);
                    }
                }
            });
        } else {
            try {
                subscriber.onError(e);
            } catch (Throwable ee) {
                httpRequest.baseAppLogger().logError(ee, true);
            }
        }
    }

    private static boolean checkIfApplicationIsConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null) {
            return activeNetInfo.isAvailable() && activeNetInfo.isConnected();
        } else {
            return false;
        }
    }

    // ------------------------->

    private static String getJsonErrorFromVolleyError(HttpRequest httpRequest, Throwable error) {
        String errorInString = "Unknown Error";
        try {
            if (error != null && isRequestError(error) && ((VolleyError) error).networkResponse != null && ((VolleyError) error).networkResponse.data != null) {
                errorInString = new String(((VolleyError) error).networkResponse.data);
            }
        } catch (Exception e) {
            httpRequest.baseAppLogger().logError(e);
        }
        return errorInString;
    }

    public static boolean isRequestError(Throwable error) {
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
        return isRequestError(error) && (((VolleyError) error).networkResponse != null && (((VolleyError) error).networkResponse.statusCode == 400));
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
