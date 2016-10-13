package com.abdallaadelessa.core.module.networkModule.volley.volleyObservable;

import android.content.Context;
import android.support.annotation.Nullable;

import com.abdallaadelessa.core.module.loggerModule.AppLogger;
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

import rx.Observable;

/**
 * Created by Abdalla on 13/10/2016.
 */
public class VolleyObservableBuilder {
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
    private AppLogger appLogger;
    private Gson gson;
    private RequestQueue requestQueue;
    private WeakReference<Context> contextWeakReference;
    private java.util.concurrent.ExecutorService executorService;
    // ------------------------->
    private String tag;
    private String url;
    private int method;
    private Type type;
    private Map<String, String> headers;
    private Map<String, String> params;
    private RetryPolicy retryPolicy;
    private VolleyResponseInterceptor volleyResponseInterceptor;
    private boolean shouldCache;
    private boolean cancelIfRunning;
    private boolean cancelOnUnSubscribe;

    VolleyObservableBuilder() {
        this(null, null, Request.Method.GET, String.class, new HashMap<String, String>(), new HashMap<String, String>(), DEFAULT_RETRY_POLICY, false, true, true);
    }

    private VolleyObservableBuilder(String tag, String url, int method, Type type, Map<String, String> headers, Map<String, String> params, RetryPolicy retryPolicy, boolean shouldCache, boolean cancelIfRunning, boolean cancelOnUnSubscribe) {
        this.tag = tag;
        this.url = url;
        this.method = method;
        this.type = type;
        this.headers = headers;
        this.params = params;
        this.retryPolicy = retryPolicy;
        this.shouldCache = shouldCache;
        this.cancelIfRunning = cancelIfRunning;
        this.cancelOnUnSubscribe = cancelOnUnSubscribe;
    }

    // ----------------->

    public void requestQueue(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public void appLogger(AppLogger applogger) {
        this.appLogger = applogger;
    }

    public void context(Context context) {
        this.contextWeakReference = new WeakReference<Context>(context);
    }

    public void executorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void gson(Gson gson) {
        this.gson = gson;
    }

    // -----------------> Init Attrs

    public VolleyObservableBuilder tag(String tag) {
        this.tag = tag;
        return this;
    }

    public VolleyObservableBuilder url(String url) {
        this.url = url;
        return this;
    }

    public VolleyObservableBuilder type(Type type) {
        this.type = type;
        return this;
    }

    // -----------------> Methods

    public VolleyObservableBuilder method(int method) {
        this.method = method;
        return this;
    }

    public VolleyObservableBuilder GET() {
        method(Request.Method.GET);
        return this;
    }

    public VolleyObservableBuilder POST() {
        method(Request.Method.POST);
        return this;
    }

    // -----------------> Headers

    public VolleyObservableBuilder addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public VolleyObservableBuilder contentType(String contentType) {
        addHeader(HEADER_CONTENT_TYPE, contentType);
        return this;
    }

    @Nullable
    public String getHeaderContentType() {
        return headers.get(HEADER_CONTENT_TYPE);
    }

    // -----------------> Parameters

    boolean containsBodyParams() {
        return params != null && params.containsKey(JSON_PARAM);
    }

    public VolleyObservableBuilder addParam(String key, String value) {
        params.put(key, value);
        return this;
    }

    public VolleyObservableBuilder addBody(String json) {
        contentType(CONTENT_TYPE_JSON);
        params.put(JSON_PARAM, json);
        POST();
        return this;
    }

    @Nullable
    byte[] getRequestBodyParams() {
        if (params == null || params.isEmpty() || !containsBodyParams()) return null;
        try {
            String mRequestBody = params.get(JSON_PARAM);
            return mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
        } catch (Exception uee) {
            if (appLogger != null) appLogger.logError(uee);
            return null;
        }
    }

    @Nullable
    Map<String, String> getRequestHeaderParams() {
        HashMap<String, String> paramsWithOutBodyContent = new HashMap<>(params);
        paramsWithOutBodyContent.remove(JSON_PARAM);
        return paramsWithOutBodyContent;
    }

    // ----------------->

    public VolleyObservableBuilder setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
        return this;
    }

    // -----------------> Extras

    public void interceptResponse(VolleyResponseInterceptor volleyResponseInterceptor) {
        this.volleyResponseInterceptor = volleyResponseInterceptor;
    }

    public VolleyObservableBuilder shouldCache(boolean shouldCache) {
        this.shouldCache = shouldCache;
        return this;
    }

    public VolleyObservableBuilder setCancelIfRunning(boolean cancelAllByTagIfRunning) {
        this.cancelIfRunning = cancelAllByTagIfRunning;
        return this;
    }

    public VolleyObservableBuilder setCancelOnUnSubscribe(boolean cancelAllByTagIfRunning) {
        this.cancelIfRunning = cancelAllByTagIfRunning;
        return this;
    }

    // ----------------->

    public String url() {
        return url;
    }

    public String tag() {
        return tag;
    }

    // ----------------->

    public AppLogger getAppLogger() {
        return appLogger;
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

    public Map<String, String> getParams() {
        return params;
    }

    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    public VolleyResponseInterceptor getVolleyResponseInterceptor() {
        return volleyResponseInterceptor;
    }

    public ExecutorService getExecutorService() {
        return executorService;
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

    // ----------------->

    public <T> Observable<T> build() {
        if (volleyResponseInterceptor != null) {
            volleyResponseInterceptor.onBuild(this);
        }
        return new VolleyObservableRequest<T>().buildVolleyObservableRequest(this);
    }
}
