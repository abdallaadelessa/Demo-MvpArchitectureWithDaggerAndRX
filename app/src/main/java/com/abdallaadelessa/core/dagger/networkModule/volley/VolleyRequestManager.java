package com.abdallaadelessa.core.dagger.networkModule.volley;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.abdallaadelessa.core.model.MessageError;
import com.abdallaadelessa.core.dagger.networkModule.builders.HttpRequestBuilder;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Type;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

/**
 * Created by abdulla on 8/12/15.
 */
public class VolleyRequestManager<T> {

    // --------------------->

    public Observable<T> createObservableFrom(final HttpRequestBuilder httpRequestBuilder) {
        final Observable<T> observable = Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(final Subscriber<? super T> subscriber) {
                if (subscriber.isUnsubscribed()) return;
                // Validate
                String error = validateBuilder(httpRequestBuilder);
                if (!ValidationUtils.isStringEmpty(error)) {
                    subscriber.onError(new MessageError(error));
                    return;
                }
                // Cancel All By Tag
                canCancelIfRunning(httpRequestBuilder);
                Context context = httpRequestBuilder.getContextWeakReference() != null ? httpRequestBuilder.getContextWeakReference().get() : null;
                RequestQueue requestQueue = httpRequestBuilder.getRequestQueue();
                final String tag = httpRequestBuilder.getTag();
                final String url = httpRequestBuilder.getUrl();
                final int method = httpRequestBuilder.getMethod();
                final Map<String, String> headers = httpRequestBuilder.getHeaders();
                final RetryPolicy retryPolicy = httpRequestBuilder.getRetryPolicy();
                boolean shouldCache = httpRequestBuilder.isShouldCache();
                //---------> Listeners
                Response.Listener<String> stringListener = getStringListener(subscriber, httpRequestBuilder);
                Response.ErrorListener errorListener = getErrorListener(subscriber, httpRequestBuilder);
                //---------> Request
                if (context != null && !checkIfApplicationIsConnected(context)) {
                    errorListener.onErrorResponse(new NoConnectionError());
                    return;
                }
                StringRequest request = new StringRequest(method, url, stringListener, errorListener) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return httpRequestBuilder.getRequestHeaderParams();
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return httpRequestBuilder.containsBodyParams() ? httpRequestBuilder.getRequestBodyParams() : super.getBody();
                    }

                    @Override
                    public String getBodyContentType() {
                        return !ValidationUtils.isStringEmpty(httpRequestBuilder.getHeaderContentType()) ? httpRequestBuilder.getHeaderContentType() : super.getBodyContentType();
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
                canCancelRequestOnSubscribe(httpRequestBuilder);
            }
        });
    }

    @NonNull
    private Response.Listener<String> getStringListener(final Subscriber<? super T> subscriber, final HttpRequestBuilder httpRequestBuilder) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                final Handler handler = new Handler();
                httpRequestBuilder.getExecutorService().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            httpRequestBuilder.getBaseAppLogger().log(response);
                            String json = httpRequestBuilder.getResponseInterceptor() != null
                                    ? httpRequestBuilder.getResponseInterceptor().interceptResponse(httpRequestBuilder.getTag(), response) : response;
                            final T t = parseJson(json, httpRequestBuilder);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    subscriber.onNext(t);
                                    subscriber.onCompleted();
                                }
                            });
                        } catch (final Throwable e) {
                            handleError(handler, subscriber, httpRequestBuilder, e, false);
                        }
                    }
                });
            }
        };
    }

    @NonNull
    private Response.ErrorListener getErrorListener(final Subscriber<? super T> subscriber, final HttpRequestBuilder httpRequestBuilder) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError e) {
                final Handler handler = new Handler();
                httpRequestBuilder.getExecutorService().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (e != null && e.networkResponse != null && e.networkResponse.data != null) {
                                String responseError = getJsonErrorFromVolleyError(httpRequestBuilder, e);
                                httpRequestBuilder.getBaseAppLogger().log("Server Response Error : " + responseError);
                            } else {
                                httpRequestBuilder.getBaseAppLogger().log("Volley Error Type : " + (e != null ? e.getClass().getName() : "Null"));
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    subscriber.onError(httpRequestBuilder.getResponseInterceptor() != null
                                            ? httpRequestBuilder.getResponseInterceptor().interceptError(httpRequestBuilder.getTag(), e) : e);
                                }
                            });
                            if (isServerError(e)) {
                                httpRequestBuilder.getBaseAppLogger().logError(e, true);
                            }
                        } catch (Throwable ee) {
                            handleError(handler, subscriber, httpRequestBuilder, e, true);
                        }
                    }
                });
            }
        };
    }

    // -------------------------> Helpers

    private void canCancelIfRunning(HttpRequestBuilder httpRequestBuilder) {
        final String tag = httpRequestBuilder.getTag();
        final boolean cancelIfRunning = httpRequestBuilder.isCancelIfRunning();
        if (cancelIfRunning && !ValidationUtils.isStringEmpty(tag)) {
            cancelRequestByTag(httpRequestBuilder.getRequestQueue(), tag);
        }
    }

    private void canCancelRequestOnSubscribe(HttpRequestBuilder httpRequestBuilder) {
        final String tag = httpRequestBuilder.getTag();
        final boolean cancelOnUnSubscribe = httpRequestBuilder.isCancelOnUnSubscribe();
        if (cancelOnUnSubscribe && !ValidationUtils.isStringEmpty(tag)) {
            cancelRequestByTag(httpRequestBuilder.getRequestQueue(), tag);
        }
    }

    private static String validateBuilder(HttpRequestBuilder httpRequestBuilder) {
        String errorMessage = null;
        if (httpRequestBuilder == null) {
            errorMessage = "httpRequestBuilder is null";
        } else if (TextUtils.isEmpty(httpRequestBuilder.getUrl())) {
            errorMessage = "url is empty";
        } else if (httpRequestBuilder.getType() == null) {
            errorMessage = "type is null";
        } else if (httpRequestBuilder.getHeaders() == null) {
            errorMessage = "headers is null";
        } else if (httpRequestBuilder.getBodyParams() == null) {
            errorMessage = "params is null";
        } else if (httpRequestBuilder.getRetryPolicy() == null) {
            errorMessage = "retryPolicy is null";
        } else if (httpRequestBuilder.getRequestQueue() == null) {
            errorMessage = "RequestQueue is null";
        } else if (httpRequestBuilder.getGson() == null) {
            errorMessage = "Gson is null";
        } else if (httpRequestBuilder.getBaseAppLogger() == null) {
            errorMessage = "BaseAppLogger is null";
        } else if (httpRequestBuilder.getContextWeakReference() == null) {
            errorMessage = "Context is null";
        }
        return errorMessage;
    }

    private static <T> T parseJson(String json, HttpRequestBuilder httpRequestBuilder) throws JSONException {
        return parseJson(json, httpRequestBuilder.getGson(), httpRequestBuilder.getType());
    }

    private static void handleError(Handler handler, final Subscriber subscriber, final HttpRequestBuilder httpRequestBuilder, final Throwable e, boolean fatal) {
        httpRequestBuilder.getBaseAppLogger().logError(e, fatal);
        if (handler != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        subscriber.onError(e);
                    } catch (Throwable ee) {
                        httpRequestBuilder.getBaseAppLogger().logError(ee, true);
                    }
                }
            });
        } else {
            try {
                subscriber.onError(e);
            } catch (Throwable ee) {
                httpRequestBuilder.getBaseAppLogger().logError(ee, true);
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

    private static String getJsonErrorFromVolleyError(HttpRequestBuilder httpRequestBuilder, Throwable error) {
        String errorInString = "Unknown Error";
        try {
            if (error != null && isRequestError(error) && ((VolleyError) error).networkResponse != null && ((VolleyError) error).networkResponse.data != null) {
                errorInString = new String(((VolleyError) error).networkResponse.data);
            }
        } catch (Exception e) {
            httpRequestBuilder.getBaseAppLogger().logError(e);
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

    public static <T> T parseJson(String json, Gson gson, Type type) throws JSONException {
        T t = null;
        if (type == String.class) {
            t = (T) json;
        } else {
            Object parsedData = new JSONTokener(json).nextValue();
            if (parsedData instanceof JSONObject) {
                JSONObject response = new JSONObject(json);
                if (type == JSONObject.class) {
                    t = (T) response;
                } else {
                    t = (T) gson.fromJson(json, type);
                }
            } else if (parsedData instanceof JSONArray) {
                t = (T) gson.fromJson(json, type);
            }
        }
        return t;
    }

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
