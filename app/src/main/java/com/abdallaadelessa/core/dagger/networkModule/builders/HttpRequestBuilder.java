package com.abdallaadelessa.core.dagger.networkModule.builders;

import android.content.Context;
import android.support.annotation.Nullable;

import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.volley.VolleyRequestManager;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by Abdalla on 13/10/2016.
 */
public class HttpRequestBuilder {
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String PROTOCOL_CHARSET = "utf-8";
    private static final String CONTENT_TYPE_JSON = String.format("application/json; charset=%s", PROTOCOL_CHARSET);
    private static final String JSON_PARAM = "|json|_|param|";
    // ----> File Upload Parameters
    private static final int UPLOAD_FILE_SOCKET_TIMEOUT = 40000;
    public static final RetryPolicy FILE_UPLOAD_RETRY_POLICY = new DefaultRetryPolicy(UPLOAD_FILE_SOCKET_TIMEOUT, 0, 0);
    // ----> Default Parameters
    private static final int DEFAULT_SOCKET_TIMEOUT = 5000;
    private static final int DEFAULT_MAX_RETRIES = 2;
    private static final float DEFAULT_BACKOFF_MULT = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
    private static final DefaultRetryPolicy DEFAULT_RETRY_POLICY = new DefaultRetryPolicy(DEFAULT_SOCKET_TIMEOUT, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT);
    // ------------------------->
    @Inject
    BaseAppLogger baseAppLogger;
    @Inject
    Gson gson;
    @Inject
    RequestQueue requestQueue;
    @Inject
    WeakReference<Context> contextWeakReference;
    @Inject
    java.util.concurrent.ExecutorService executorService;
    // ------------------------->
    private String tag;
    private String url;
    private int method;
    private Type type;
    private Map<String, String> headers;
    private Map<String, String> bodyParams;
    private RetryPolicy retryPolicy;
    private ResponseInterceptor responseInterceptor;
    private boolean shouldCache;
    private boolean cancelIfRunning;
    private boolean cancelOnUnSubscribe;

    public static HttpRequestBuilder builder() {
        return new HttpRequestBuilder();
    }

    private HttpRequestBuilder() {
        this(null, null, Request.Method.GET, String.class, new HashMap<String, String>(), new HashMap<String, String>(), DEFAULT_RETRY_POLICY, false, true, true);
    }

    private HttpRequestBuilder(String tag, String url, int method, Type type, Map<String, String> headers, Map<String, String> bodyParams, RetryPolicy retryPolicy, boolean shouldCache, boolean cancelIfRunning, boolean cancelOnUnSubscribe) {
        this.tag = tag;
        this.url = url;
        this.method = method;
        this.type = type;
        this.headers = headers;
        this.bodyParams = bodyParams;
        this.retryPolicy = retryPolicy;
        this.shouldCache = shouldCache;
        this.cancelIfRunning = cancelIfRunning;
        this.cancelOnUnSubscribe = cancelOnUnSubscribe;
    }

    // --> Init Attrs

    public HttpRequestBuilder tag(String tag) {
        this.tag = tag;
        return this;
    }

    public HttpRequestBuilder url(String url) {
        this.url = url;
        return this;
    }

    public HttpRequestBuilder type(Type type) {
        this.type = type;
        return this;
    }

    // --> Methods

    public HttpRequestBuilder method(int method) {
        this.method = method;
        return this;
    }

    public HttpRequestBuilder GET() {
        method(Request.Method.GET);
        return this;
    }

    public HttpRequestBuilder POST() {
        method(Request.Method.POST);
        return this;
    }

    // --> Headers

    public HttpRequestBuilder addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public HttpRequestBuilder contentType(String contentType) {
        addHeader(HEADER_CONTENT_TYPE, contentType);
        return this;
    }

    // --> Parameters

    public boolean containsBodyParams() {
        return bodyParams != null && bodyParams.containsKey(JSON_PARAM);
    }

    public HttpRequestBuilder addBodyParam(String key, String value) {
        bodyParams.put(key, value);
        return this;
    }

    public HttpRequestBuilder addBody(String json) {
        contentType(CONTENT_TYPE_JSON);
        bodyParams.put(JSON_PARAM, json);
        POST();
        return this;
    }

    // --> Extras

    public HttpRequestBuilder setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
        return this;
    }

    public void interceptResponse(ResponseInterceptor responseInterceptor) {
        this.responseInterceptor = responseInterceptor;
    }

    public HttpRequestBuilder shouldCache(boolean shouldCache) {
        this.shouldCache = shouldCache;
        return this;
    }

    public HttpRequestBuilder setCancelIfRunning(boolean cancelAllByTagIfRunning) {
        this.cancelIfRunning = cancelAllByTagIfRunning;
        return this;
    }

    public HttpRequestBuilder setCancelOnUnSubscribe(boolean cancelAllByTagIfRunning) {
        this.cancelIfRunning = cancelAllByTagIfRunning;
        return this;
    }

    // -----------------> Getters

    public BaseAppLogger getBaseAppLogger() {
        return baseAppLogger;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public WeakReference<Context> getContextWeakReference() {
        return contextWeakReference;
    }

    public String getTag() {
        return tag;
    }

    public String getUrl() {
        return url;
    }

    public int getMethod() {
        return method;
    }

    public Type getType() {
        return type;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getBodyParams() {
        return bodyParams;
    }

    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    public ResponseInterceptor getResponseInterceptor() {
        return responseInterceptor;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    @Nullable
    public String getHeaderContentType() {
        return headers.get(HEADER_CONTENT_TYPE);
    }

    @Nullable
    public byte[] getRequestBodyParams() {
        if (bodyParams == null || bodyParams.isEmpty() || !containsBodyParams()) return null;
        try {
            String mRequestBody = bodyParams.get(JSON_PARAM);
            return mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
        } catch (Exception uee) {
            if (baseAppLogger != null) baseAppLogger.logError(uee);
            return null;
        }
    }

    @Nullable
    public Map<String, String> getRequestHeaderParams() {
        HashMap<String, String> paramsWithOutBodyContent = new HashMap<>(bodyParams);
        paramsWithOutBodyContent.remove(JSON_PARAM);
        return paramsWithOutBodyContent;
    }

    public Gson getGson() {
        return gson;
    }

    public boolean isShouldCache() {
        return shouldCache;
    }

    public boolean isCancelIfRunning() {
        return cancelIfRunning;
    }

    public boolean isCancelOnUnSubscribe() {
        return cancelOnUnSubscribe;
    }

    // -----------------> Build Method

    public <T> Observable<T> build() {
        if (responseInterceptor != null) {
            responseInterceptor.onBuild(this);
        }
        return new VolleyRequestManager<T>().createObservableFrom(this);
    }

    // ----------------->

    public interface ResponseInterceptor {
        String interceptResponse(String tag, String jsonResult) throws Exception;

        Throwable interceptError(String tag, Throwable throwable);

        void onBuild(HttpRequestBuilder httpRequestBuilder);
    }
}
