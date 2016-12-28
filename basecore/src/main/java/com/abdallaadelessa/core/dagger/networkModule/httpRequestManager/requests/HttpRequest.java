package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests;

import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpExecutor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.HttpInterceptor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.HttpParser;
import com.abdallaadelessa.core.utils.ValidationUtils;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import rx.Observable;

/**
 * Created by abdullah on 12/26/16.
 */

public class HttpRequest<T> extends BaseRequest<T> {
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String PROTOCOL_CHARSET = "utf-8";
    private static final String CONTENT_TYPE_JSON = String.format("application/json; charset=%s", PROTOCOL_CHARSET);
    public static final RetryPolicy FILE_UPLOAD_RETRY_POLICY = new DefaultRetryPolicy(40000, 0, 0);
    public static final DefaultRetryPolicy DEFAULT_RETRY_POLICY = new DefaultRetryPolicy(5000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    private int method;
    private Type type;
    private Map<String, String> headers;
    private Map<String, String> params;
    private String body;
    private RetryPolicy retryPolicy;
    private boolean shouldCache;
    private boolean cancelIfRunning;
    private boolean cancelOnUnSubscribe;

    //=====> Builder

    public static <T> HttpRequest<T> create(HttpInterceptor interceptor, HttpParser parser, BaseAppLogger logger
            , BaseHttpExecutor<T, HttpRequest> observableExecutor, ExecutorService executorService) {
        // Default Values
        HttpRequest<T> httpRequest = new HttpRequest<T>(parser, logger, observableExecutor, executorService);
        httpRequest.addInterceptor(interceptor);
        httpRequest.GET();
        httpRequest.setType(String.class);
        httpRequest.setHeaders(new HashMap<String, String>());
        httpRequest.setParams(new HashMap<String, String>());
        httpRequest.setRetryPolicy(DEFAULT_RETRY_POLICY);
        httpRequest.setShouldCache(false);
        httpRequest.setCancelIfRunning(true);
        httpRequest.setCancelOnUnSubscribe(true);
        return httpRequest;
    }

    private HttpRequest(HttpParser parser, BaseAppLogger logger, BaseHttpExecutor<T, HttpRequest> observableExecutor, ExecutorService executorService) {
        super(parser, logger, observableExecutor, executorService);
    }

    //=====>

    public HttpRequest<T> GET() {
        return setMethod(Request.Method.GET);
    }

    public HttpRequest<T> POST() {
        return setMethod(Request.Method.POST);
    }

    public HttpRequest<T> contentType(String contentType) {
        return addHeader(HEADER_CONTENT_TYPE, contentType);
    }

    public HttpRequest<T> addHeader(String key, String value) {
        getHeaders().put(key, value);
        return this;
    }

    public HttpRequest<T> addParam(String key, String value) {
        setBody(null);
        contentType(contentType());
        getParams().put(key, value);
        setMethod(getMethod());
        return this;
    }

    public HttpRequest<T> addBody(String body) {
        setParams(new HashMap<String, String>());
        contentType(CONTENT_TYPE_JSON);
        setBody(body);
        POST();
        return this;
    }

    //==> Setters

    public HttpRequest<T> setMethod(int method) {
        this.method = method;
        return this;
    }

    public HttpRequest<T> setType(Type type) {
        this.type = type;
        return this;
    }

    public HttpRequest<T> setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
        return this;
    }

    public HttpRequest<T> setShouldCache(boolean shouldCache) {
        this.shouldCache = shouldCache;
        return this;
    }

    public HttpRequest<T> setCancelIfRunning(boolean cancelIfRunning) {
        this.cancelIfRunning = cancelIfRunning;
        return this;
    }

    public HttpRequest<T> setCancelOnUnSubscribe(boolean cancelOnUnSubscribe) {
        this.cancelOnUnSubscribe = cancelOnUnSubscribe;
        return this;
    }

    private HttpRequest<T> setBody(String body) {
        this.body = body;
        return this;
    }

    private HttpRequest<T> setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    private HttpRequest<T> setParams(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public HttpRequest<T> addInterceptor(HttpInterceptor interceptor) {
        getInterceptors().add(interceptor);
        return this;
    }

    public HttpRequest<T> clearInterceptors() {
        getInterceptors().clear();
        return this;
    }

    public HttpRequest<T> setParser(HttpParser parser) {
        this.parser = parser;
        return this;
    }

    public HttpRequest<T> setLogger(BaseAppLogger logger) {
        this.logger = logger;
        return this;
    }

    public HttpRequest<T> setObservableExecutor(BaseHttpExecutor observableExecutor) {
        this.observableExecutor = observableExecutor;
        return this;
    }

    public HttpRequest<T> setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public HttpRequest<T> setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public HttpRequest<T> setUrl(String url) {
        this.url = url;
        return this;
    }

    //=====> Getters

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

    public String getBody() {
        return body;
    }

    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
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

    public String contentType() {
        return getHeaders().get(HEADER_CONTENT_TYPE);
    }

    public boolean hasBody() {
        return !ValidationUtils.isStringEmpty(getBody());
    }

    public byte[] bodyToBytes() {
        byte[] bodyBytes = null;
        try {
            if (!ValidationUtils.isStringEmpty(getBody())) {
                bodyBytes = getBody().getBytes(PROTOCOL_CHARSET);
            }
        } catch (Exception ee) {
            if (getLogger() != null) getLogger().logError(ee);
        }
        return bodyBytes;
    }

    //=====>

    public Observable<T> toObservable() {
        return getObservableExecutor().toObservable(this);
    }
}
