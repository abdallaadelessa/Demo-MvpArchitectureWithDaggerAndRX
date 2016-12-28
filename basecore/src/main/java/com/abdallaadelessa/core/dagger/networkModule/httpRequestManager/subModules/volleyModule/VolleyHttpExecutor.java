package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.subModules.volleyModule;


import android.support.annotation.NonNull;

import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpExecutor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.HttpInterceptor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.HttpRequest;
import com.abdallaadelessa.core.model.BaseCoreError;
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

import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;

/**
 * Created by abdulla on 8/12/15.
 */
public class VolleyHttpExecutor<M> extends BaseHttpExecutor<M, HttpRequest> {

    private RequestQueue requestQueue;

    //=====================>

    public VolleyHttpExecutor(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public Observable<M> buildObservable(final HttpRequest httpRequest) {
        return Observable.create(new Observable.OnSubscribe<M>() {
            @Override
            public void call(final Subscriber<? super M> subscriber) {
                final String tag = httpRequest.getTag();
                final String url = httpRequest.getUrl();
                final int method = httpRequest.getMethod();
                final Map headers = httpRequest.getHeaders();
                final RetryPolicy retryPolicy = httpRequest.getRetryPolicy();
                boolean shouldCache = httpRequest.isShouldCache();
                //---------> Listeners
                Response.Listener<String> stringListener = getStringListener(httpRequest, subscriber);
                Response.ErrorListener errorListener = getErrorListener(httpRequest, subscriber);
                //---------> Request
                StringRequest request1 = new StringRequest(method, url, stringListener, errorListener) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return httpRequest.getParams();
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
                request1.setHeaders(headers);
                request1.setRetryPolicy(retryPolicy);
                request1.setShouldCache(shouldCache);
                if (!ValidationUtils.isStringEmpty(tag)) request1.setTag(tag);
                requestQueue.add(request1);
            }
        });
    }

    @Override
    protected void cancelRequest(HttpRequest request) {
        if (!ValidationUtils.isStringEmpty(request.getTag())) {
            requestQueue.cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    return request != null && request.getTag().equals(request.getTag());
                }
            });
        }
    }

    //=====================>

    @NonNull
    private Response.Listener<String> getStringListener(final HttpRequest httpRequest, final Subscriber<? super M> subscriber) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                httpRequest.getExecutorService().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final M m = parse(response, httpRequest);
                            onSuccess(httpRequest, subscriber, m);
                        } catch (final Throwable e) {
                            onError(httpRequest, subscriber, e, false);
                        }
                    }
                });
            }
        };
    }

    @NonNull
    private Response.ErrorListener getErrorListener(final HttpRequest httpRequest, final Subscriber<? super M> subscriber) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError e) {
                httpRequest.getExecutorService().execute(new Runnable() {
                    @Override
                    public void run() {
                        onError(httpRequest, subscriber, getBaseCoreError(e), false);
                    }
                });
            }
        };
    }

    // ------------------------->

    @Override
    public BaseCoreError getBaseCoreError(Throwable throwable) {
        BaseCoreError baseCoreError = new BaseCoreError(throwable);
        if (isVolleyError(throwable)) {
            if (isTimeoutError(throwable)) {
                baseCoreError = new BaseCoreError(BaseCoreError.CODE_TIMEOUT_ERROR, throwable);
            } else if (isNetworkError(throwable)) {
                baseCoreError = new BaseCoreError(BaseCoreError.CODE_NETWORK_ERROR, throwable);
            } else if (isServerError(throwable)) {
                baseCoreError = new BaseCoreError(BaseCoreError.CODE_SERVER_ERROR, throwable);
            } else if (isBadRequestError(throwable)) {
                baseCoreError = new BaseCoreError(BaseCoreError.CODE_BAD_REQUEST_ERROR, throwable);
            }
        }
        return baseCoreError;
    }

    private boolean isVolleyError(Throwable error) {
        return error instanceof VolleyError;
    }

    private boolean isTimeoutError(Throwable error) {
        return error instanceof TimeoutError;
    }

    private boolean isNetworkError(Throwable error) {
        return (error instanceof NetworkError);
    }

    private boolean isServerError(Throwable error) {
        return (error instanceof ServerError) || (error instanceof AuthFailureError);
    }

    private boolean isBadRequestError(Throwable error) {
        return isVolleyError(error) && (((VolleyError) error).networkResponse != null && (((VolleyError) error).networkResponse.statusCode == 400));
    }

    // ------------------------->

}
