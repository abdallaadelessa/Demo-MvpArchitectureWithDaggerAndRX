package com.abdallaadelessa.core.module.networkModule.volley.volleyObservable;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.abdallaadelessa.core.model.MessageError;
import com.abdallaadelessa.core.utils.ValidationUtils;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.ServerError;
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
public class VolleyObservableRequest<T> {

    public static VolleyObservableBuilder builder() {
        return new VolleyObservableBuilder();
    }

    // --------------------->

    Observable<T> buildVolleyObservableRequest(final VolleyObservableBuilder volleyObservableBuilder) {
        final Observable<T> observable = Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(final Subscriber<? super T> subscriber) {
                if (subscriber.isUnsubscribed()) return;
                // Validate
                String error = validateBuilder(volleyObservableBuilder);
                if (!ValidationUtils.isStringEmpty(error)) {
                    subscriber.onError(new MessageError(error));
                    return;
                }
                // Cancel All By Tag
                canCancelIfRunning(volleyObservableBuilder);
                Context context = volleyObservableBuilder.getContextWeakReference() != null ? volleyObservableBuilder.getContextWeakReference().get() : null;
                RequestQueue requestQueue = volleyObservableBuilder.getRequestQueue();
                final String tag = volleyObservableBuilder.getTag();
                final String url = volleyObservableBuilder.getUrl();
                final int method = volleyObservableBuilder.getMethod();
                final Map<String, String> headers = volleyObservableBuilder.getHeaders();
                final RetryPolicy retryPolicy = volleyObservableBuilder.getRetryPolicy();
                final VolleyResponseInterceptor volleyResponseInterceptor = volleyObservableBuilder.getVolleyResponseInterceptor();
                boolean shouldCache = volleyObservableBuilder.isShouldCache();
                //---------> Listeners
                Response.Listener<String> stringListener = getStringListener(subscriber, volleyObservableBuilder);
                Response.ErrorListener errorListener = getErrorListener(subscriber, volleyObservableBuilder);
                //---------> Request
                if (context != null && !checkIfApplicationIsConnected(context)) {
                    errorListener.onErrorResponse(new NoConnectionError());
                    return;
                }
                StringRequest request = new StringRequest(method, url, stringListener, errorListener) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return volleyObservableBuilder.getRequestHeaderParams();
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return volleyObservableBuilder.containsBodyParams() ? volleyObservableBuilder.getRequestBodyParams() : super.getBody();
                    }

                    @Override
                    public String getBodyContentType() {
                        return !ValidationUtils.isStringEmpty(volleyObservableBuilder.getHeaderContentType()) ? volleyObservableBuilder.getHeaderContentType() : super.getBodyContentType();
                    }
                };
                request.setHeaders(headers);
                request.setRetryPolicy(retryPolicy);
                request.setShouldCache(shouldCache);
                if (!ValidationUtils.isStringEmpty(tag)) request.setTag(tag);
                if (requestQueue != null)
                    requestQueue.add(request);
            }
        });

        //---------> OnSubscribe
        return observable.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                canCancelRequestOnSubscribe(volleyObservableBuilder);
            }
        });
    }

    @NonNull
    private Response.Listener<String> getStringListener(final Subscriber<? super T> subscriber, final VolleyObservableBuilder volleyObservableBuilder) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                final Handler handler = new Handler();
                volleyObservableBuilder.getExecutorService().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            volleyObservableBuilder.getAppLogger().log(response);
                            String json = volleyObservableBuilder.getVolleyResponseInterceptor() != null ? volleyObservableBuilder.getVolleyResponseInterceptor().interceptResponse(volleyObservableBuilder.getTag(), response) : response;
                            final T t = parseJson(json, volleyObservableBuilder);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    subscriber.onNext(t);
                                    subscriber.onCompleted();
                                }
                            });
                        } catch (final Throwable e) {
                            handleError(handler, subscriber, volleyObservableBuilder, e, false);
                        }
                    }
                });
            }
        };
    }

    @NonNull
    private Response.ErrorListener getErrorListener(final Subscriber<? super T> subscriber, final VolleyObservableBuilder volleyObservableBuilder) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError e) {
                final Handler handler = new Handler();
                volleyObservableBuilder.getExecutorService().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (e != null && e.networkResponse != null && e.networkResponse.data != null) {
                                String responseError = getJsonErrorFromVolleyError(volleyObservableBuilder, e);
                                volleyObservableBuilder.getAppLogger().log("Server Response Error : " + responseError);
                            } else {
                                volleyObservableBuilder.getAppLogger().log("Volley Error Type : " + (e != null ? e.getClass().getName() : "Null"));
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    subscriber.onError(volleyObservableBuilder.getVolleyResponseInterceptor() != null ? volleyObservableBuilder.getVolleyResponseInterceptor().interceptError(volleyObservableBuilder.getTag(), e) : e);
                                }
                            });
                            if (isServerError(e)) {
                                volleyObservableBuilder.getAppLogger().logError(e, true);
                            }
                        } catch (Throwable ee) {
                            handleError(handler, subscriber, volleyObservableBuilder, e, true);
                        }
                    }
                });
            }
        };
    }

    // -------------------------> Helpers

    private void canCancelIfRunning(VolleyObservableBuilder volleyObservableBuilder) {
        final String tag = volleyObservableBuilder.getTag();
        final boolean cancelIfRunning = volleyObservableBuilder.isCancelIfRunning();
        if (cancelIfRunning && !ValidationUtils.isStringEmpty(tag)) {
            cancelRequestByTag(volleyObservableBuilder.getRequestQueue(), tag);
        }
    }

    private void canCancelRequestOnSubscribe(VolleyObservableBuilder volleyObservableBuilder) {
        final String tag = volleyObservableBuilder.getTag();
        final boolean cancelOnUnSubscribe = volleyObservableBuilder.isCancelOnUnSubscribe();
        if (cancelOnUnSubscribe && !ValidationUtils.isStringEmpty(tag)) {
            cancelRequestByTag(volleyObservableBuilder.getRequestQueue(), tag);
        }
    }

    private static String validateBuilder(VolleyObservableBuilder volleyObservableBuilder) {
        String errorMessage = null;
        if (volleyObservableBuilder == null) {
            errorMessage = "volleyObservableBuilder is null";
        } else if (TextUtils.isEmpty(volleyObservableBuilder.getUrl())) {
            errorMessage = "url is empty";
        } else if (volleyObservableBuilder.getType() == null) {
            errorMessage = "type is null";
        } else if (volleyObservableBuilder.getHeaders() == null) {
            errorMessage = "headers is null";
        } else if (volleyObservableBuilder.getParams() == null) {
            errorMessage = "params is null";
        } else if (volleyObservableBuilder.getRetryPolicy() == null) {
            errorMessage = "retryPolicy is null";
        } else if (volleyObservableBuilder.getRequestQueue() == null) {
            errorMessage = "RequestQueue is null";
        } else if (volleyObservableBuilder.getGson() == null) {
            errorMessage = "Gson is null";
        } else if (volleyObservableBuilder.getAppLogger() == null) {
            errorMessage = "AppLogger is null";
        } else if (volleyObservableBuilder.getContextWeakReference() == null) {
            errorMessage = "ContextWeakReference is null";
        }
        return errorMessage;
    }

    private static <T> T parseJson(String json, VolleyObservableBuilder volleyObservableBuilder) throws JSONException {
        return parseJson(json, volleyObservableBuilder.getGson(), volleyObservableBuilder.getType());
    }

    private static void handleError(Handler handler, final Subscriber subscriber, final VolleyObservableBuilder volleyObservableBuilder, final Throwable e, boolean fatal) {
        volleyObservableBuilder.getAppLogger().logError(e, fatal);
        if (handler != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        subscriber.onError(e);
                    } catch (Throwable ee) {
                        volleyObservableBuilder.getAppLogger().logError(ee, true);
                    }
                }
            });
        } else {
            try {
                subscriber.onError(e);
            } catch (Throwable ee) {
                volleyObservableBuilder.getAppLogger().logError(ee, true);
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

    private static String getJsonErrorFromVolleyError(VolleyObservableBuilder volleyObservableBuilder, Throwable error) {
        String errorInString = "Unknown Error";
        try {
            if (error != null && isRequestError(error) && ((VolleyError) error).networkResponse != null && ((VolleyError) error).networkResponse.data != null) {
                errorInString = new String(((VolleyError) error).networkResponse.data);
            }
        } catch (Exception e) {
            volleyObservableBuilder.getAppLogger().logError(e);
        }
        return errorInString;
    }

    private static boolean isRequestError(Throwable error) {
        return error instanceof VolleyError;
    }

    private static boolean isServerError(Throwable error) {
        return (error instanceof ServerError) || (error instanceof AuthFailureError);
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
