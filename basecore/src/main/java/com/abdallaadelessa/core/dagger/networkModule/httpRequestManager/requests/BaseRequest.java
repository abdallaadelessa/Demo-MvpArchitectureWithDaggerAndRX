package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests;

import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpExecutor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.HttpInterceptor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.HttpParser;
import com.android.volley.RetryPolicy;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Created by abdullah on 12/27/16.
 */

public abstract class BaseRequest<T> {
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String PROTOCOL_CHARSET = "utf-8";
    public static final String CONTENT_TYPE_JSON = String.format("application/json; charset=%s", PROTOCOL_CHARSET);
    protected HttpParser parser;
    protected BaseAppLogger logger;
    protected BaseHttpExecutor<T, BaseRequest> observableExecutor;
    private List<HttpInterceptor> interceptors;
    protected ExecutorService executorService;
    protected String tag;
    protected String url;
    protected Type type;
    protected Map<String, String> headers;
    protected RetryPolicy retryPolicy;
    protected Map<String, String> params;
    protected boolean cancelIfRunning;
    protected boolean cancelOnUnSubscribe;

    public BaseRequest(HttpParser parser, BaseAppLogger logger, BaseHttpExecutor observableExecutor, ExecutorService executorService) {
        this.interceptors = new ArrayList<>();
        this.parser = parser;
        this.logger = logger;
        this.observableExecutor = observableExecutor;
        this.executorService = executorService;
    }

    //=====> Setters

    public BaseRequest<T> setParser(HttpParser parser) {
        this.parser = parser;
        return this;
    }

    public BaseRequest<T> setLogger(BaseAppLogger logger) {
        this.logger = logger;
        return this;
    }

    public BaseRequest<T> setObservableExecutor(BaseHttpExecutor<T, BaseRequest> observableExecutor) {
        this.observableExecutor = observableExecutor;
        return this;
    }

    public BaseRequest<T> setInterceptors(List<HttpInterceptor> interceptors) {
        this.interceptors = interceptors;
        return this;
    }

    public BaseRequest<T> setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public BaseRequest<T> setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public BaseRequest<T> setUrl(String url) {
        this.url = url;
        return this;
    }

    public BaseRequest<T> setType(Type type) {
        this.type = type;
        return this;
    }

    public BaseRequest<T> setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
        return this;
    }

    public BaseRequest<T> setCancelIfRunning(boolean cancelIfRunning) {
        this.cancelIfRunning = cancelIfRunning;
        return this;
    }

    public BaseRequest<T> setCancelOnUnSubscribe(boolean cancelOnUnSubscribe) {
        this.cancelOnUnSubscribe = cancelOnUnSubscribe;
        return this;
    }

    protected BaseRequest<T> setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    protected BaseRequest<T> setParams(Map<String, String> params) {
        this.params = params;
        return this;
    }

    //=====> Getters

    public List<HttpInterceptor> getInterceptors() {
        return interceptors;
    }

    public HttpParser getParser() {
        return parser;
    }

    public BaseAppLogger getLogger() {
        return logger;
    }

    public BaseHttpExecutor<T, BaseRequest> getObservableExecutor() {
        return observableExecutor;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public String getTag() {
        return tag;
    }

    public String getUrl() {
        return url;
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

    public boolean isCancelIfRunning() {
        return cancelIfRunning;
    }

    public boolean isCancelOnUnSubscribe() {
        return cancelOnUnSubscribe;
    }

    //=====>

    @Override
    public String toString() {
        return "Request{" +
                "tag='" + tag + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
