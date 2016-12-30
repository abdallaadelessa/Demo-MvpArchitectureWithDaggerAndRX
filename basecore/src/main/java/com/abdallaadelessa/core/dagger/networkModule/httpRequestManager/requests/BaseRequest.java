package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests;

import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpExecutor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpInterceptor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpParser;
import com.abdallaadelessa.core.utils.StringUtils;
import com.abdallaadelessa.core.utils.ValidationUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import rx.Observable;

/**
 * Created by abdullah on 12/27/16.
 */

public abstract class BaseRequest<B extends BaseRequest, T> {
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String UTF_8 = "utf-8";
    public static final String CONTENT_TYPE_JSON = String.format("application/json; charset=%s", UTF_8);
    public static final String CONTENT_TYPE_FORM = String.format("application/x-www-form-urlencoded; charset=%s", UTF_8);
    public static final int TIMEOUT_LONG_IN_MILLIS = 40000;
    public static final int TIMEOUT_MEDIUM_IN_MILLIS = 10000;
    public static final int TIMEOUT_SHORT_IN_MILLIS = 5000;
    //=>
    protected BaseHttpParser parser;
    protected BaseAppLogger logger;
    protected BaseHttpExecutor<T, BaseRequest> observableExecutor;
    protected List<BaseHttpInterceptor> interceptors;
    protected ExecutorService executorService;
    //=>
    protected String tag;
    protected String url;
    protected String urlWithQueryParams;
    protected HttpMethod method;
    protected Type type;
    //=>
    protected int retriesNumber;
    protected int timeout;
    //=>
    protected Map<String, String> headerParams;
    protected Map<String, String> queryParams;
    protected Map<String, String> formParams;
    //=>
    protected boolean shouldCacheResponse;
    protected boolean cancelIfWasRunning;
    protected boolean cancelOnUnSubscribe;

    //=====>

    protected BaseRequest() {
        // Default Values
        interceptors = new ArrayList<>();
        headerParams = new HashMap<>();
        formParams = new HashMap<>();
        queryParams = new HashMap<>();
        retriesNumber = 1;
        timeout = TIMEOUT_MEDIUM_IN_MILLIS;
        method = HttpMethod.GET;
        setContentType(CONTENT_TYPE_FORM);
        type = String.class;
        shouldCacheResponse = false;
        cancelIfWasRunning = true;
        cancelOnUnSubscribe = true;
    }

    public BaseRequest(BaseHttpParser parser, BaseAppLogger logger, BaseHttpExecutor observableExecutor, ExecutorService executorService) {
        this();
        this.parser = parser;
        this.logger = logger;
        this.observableExecutor = observableExecutor;
        this.executorService = executorService;
    }

    //=====> Custom

    public B GET() {
        return setMethod(HttpMethod.GET);
    }

    public B POST() {
        return setMethod(HttpMethod.POST);
    }

    public B addHeader(String key, String value) {
        getHeaderParams().put(key, value);
        return (B) this;
    }

    public B addQueryParam(String key, String value) {
        getQueryParams().put(key, value);
        return (B) this;
    }

    public B addFormParam(String key, String value) {
        getFormParams().put(key, value);
        return (B) this;
    }

    public boolean hasContentType() {
        return !ValidationUtils.isStringEmpty(getContentType());
    }

    public B setContentType(String contentType) {
        return addHeader(HEADER_CONTENT_TYPE, contentType);
    }

    public String getContentType() {
        return getHeaderParams().get(HEADER_CONTENT_TYPE);
    }

    public B addInterceptor(BaseHttpInterceptor interceptor) {
        getInterceptors().add(interceptor);
        return (B) this;
    }

    protected B clearInterceptors() {
        getInterceptors().clear();
        return (B) this;
    }

    //=====> Setters

    public B setParser(BaseHttpParser parser) {
        this.parser = parser;
        return (B) this;
    }

    public B setLogger(BaseAppLogger logger) {
        this.logger = logger;
        return (B) this;
    }

    public B setObservableExecutor(BaseHttpExecutor<T, BaseRequest> observableExecutor) {
        this.observableExecutor = observableExecutor;
        return (B) this;
    }

    public B setInterceptors(List<BaseHttpInterceptor> interceptors) {
        this.interceptors = interceptors;
        return (B) this;
    }

    public B setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return (B) this;
    }

    public B setTag(String tag) {
        this.tag = tag;
        return (B) this;
    }

    public B setUrl(String url) {
        this.url = url;
        return (B) this;
    }

    public B setMethod(HttpMethod method) {
        this.method = method;
        return (B) this;
    }

    public B setType(Type type) {
        this.type = type;
        return (B) this;
    }

    public B setRetriesNumber(int retriesNumber) {
        this.retriesNumber = retriesNumber;
        return (B) this;
    }

    public B setTimeout(int timeout) {
        this.timeout = timeout;
        return (B) this;
    }

    protected B setHeaderParams(Map<String, String> headerParams) {
        this.headerParams = headerParams;
        return (B) this;
    }

    protected B setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
        return (B) this;
    }

    protected B setFormParams(Map<String, String> formParams) {
        this.formParams = formParams;
        return (B) this;
    }

    public B setShouldCacheResponse(boolean shouldCacheResponse) {
        this.shouldCacheResponse = shouldCacheResponse;
        return (B) this;
    }

    public B setCancelIfWasRunning(boolean cancelIfWasRunning) {
        this.cancelIfWasRunning = cancelIfWasRunning;
        return (B) this;
    }

    public B setCancelOnUnSubscribe(boolean cancelOnUnSubscribe) {
        this.cancelOnUnSubscribe = cancelOnUnSubscribe;
        return (B) this;
    }


    //=====> Getters

    public BaseHttpParser getParser() {
        return parser;
    }

    public BaseAppLogger getLogger() {
        return logger;
    }

    public BaseHttpExecutor<T, BaseRequest> getObservableExecutor() {
        return observableExecutor;
    }

    public List<BaseHttpInterceptor> getInterceptors() {
        return interceptors;
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

    public String getUrlWithQueryParams() {
        return urlWithQueryParams;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public Type getType() {
        return type;
    }

    public int getRetriesNumber() {
        return retriesNumber;
    }

    public int getTimeout() {
        return timeout;
    }

    public Map<String, String> getHeaderParams() {
        return headerParams;
    }

    public Map<String, String> getFormParams() {
        return formParams;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public byte[] bodyToBytes() {
        return StringUtils.mapToBytes(getFormParams(), UTF_8);
    }

    public boolean isShouldCacheResponse() {
        return shouldCacheResponse;
    }

    public boolean isCancelIfWasRunning() {
        return cancelIfWasRunning;
    }

    public boolean isCancelOnUnSubscribe() {
        return cancelOnUnSubscribe;
    }

    //=====>

    protected void build() {
        try {
            urlWithQueryParams = StringUtils.addQueryParams(url, queryParams, UTF_8);
        } catch (Exception e) {
            urlWithQueryParams = url;
        }
    }

    public final Observable<T> toObservable() {
        build();
        return getObservableExecutor().toObservable(this);
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
