package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.executors.volleyModule;


import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpExecutor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.BaseRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.HttpRequest;
import com.abdallaadelessa.core.model.BaseCoreError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestTickle;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.NetworkError;
import com.android.volley.error.ServerError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;

import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by abdulla on 8/12/15.
 */
public class VolleyHttpExecutor<M> extends BaseHttpExecutor<M, HttpRequest<M>> {

    private volatile RequestTickle requestTickle;

    public VolleyHttpExecutor() {
        this.requestTickle = DaggerVolleyNetworkComponent.create().getRequestTickle();
    }

    //=====================>

    public Observable<M> buildObservable(final HttpRequest httpRequest) {
        return Observable.create(new Observable.OnSubscribe<M>() {
            @Override
            public void call(final Subscriber<? super M> subscriber) {
                StringRequest stringRequest = new StringRequest(getMethod(httpRequest), httpRequest.getUrlWithQueryParams(), null, null) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return httpRequest.getFormParams();
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return httpRequest.bodyToBytes();
                    }

                    @Override
                    public String getBodyContentType() {
                        return httpRequest.getContentType();
                    }
                };
                stringRequest.setHeaders(httpRequest.getHeaderParams());
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(httpRequest.getTimeout(), httpRequest.getRetriesNumber(), 1f));
                stringRequest.setShouldCache(httpRequest.isShouldCacheResponse());
                stringRequest.setTag(httpRequest.getTag());
                try {
                    requestTickle.add(stringRequest);
                    NetworkResponse networkResponse = requestTickle.start();
                    String response = new String(networkResponse.data, BaseRequest.UTF_8);
                    onNext(subscriber, httpRequest, response);
                    onCompleted(subscriber,httpRequest);
                } catch (Exception e) {
                    onError(subscriber, httpRequest, e, false);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }

    @Override
    protected void cancelExecutor() {
        if (requestTickle != null) {
            requestTickle.cancel();
        }
    }

    public BaseCoreError convertErrorToBaseCoreError(Throwable throwable) {
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

    //=====================> Helpers

    private int getMethod(HttpRequest httpRequest) {
        switch (httpRequest.getMethod()) {
            default:
            case GET:
                return Request.Method.GET;
            case POST:
                return Request.Method.POST;
            case PUT:
                return Request.Method.PUT;
            case DELETE:
                return Request.Method.DELETE;
        }
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
