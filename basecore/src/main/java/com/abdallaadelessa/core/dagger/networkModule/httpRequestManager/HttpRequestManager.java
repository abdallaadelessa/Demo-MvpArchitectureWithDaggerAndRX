package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager;

import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.HttpRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.MultiPartRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.volley.MultiPartObservableExecutor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.volley.VolleyHttpObservableExecutor;
import com.android.volley.RequestQueue;

import java.util.concurrent.ExecutorService;

/**
 * Created by abdullah on 12/26/16.
 */

public class HttpRequestManager {
    private BaseAppLogger logger;
    private ExecutorService executorService;
    private HttpInterceptor interceptor;
    private HttpParser parser;
    private RequestQueue queue;

    //===========> Constructor

    public HttpRequestManager(HttpInterceptor interceptor, HttpParser parser, BaseAppLogger logger, ExecutorService executorService, RequestQueue queue) {
        this.interceptor = interceptor;
        this.parser = parser;
        this.logger = logger;
        this.executorService = executorService;
        this.queue = queue;
    }

    public <T> HttpRequest<T> newHttpRequest() {
        return HttpRequest.create(getInterceptor(), getParser(), getLogger(), new VolleyHttpObservableExecutor<T>(queue), getExecutorService());
    }

    public <T> MultiPartRequest<T> newMultiPartRequest() {
        return MultiPartRequest.create(getInterceptor(), getParser(), getLogger(), new MultiPartObservableExecutor<T>(), getExecutorService());
    }

    //===========> Setters and Getters

    public BaseAppLogger getLogger() {
        return logger;
    }

    public void setLogger(BaseAppLogger logger) {
        this.logger = logger;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public HttpInterceptor getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(HttpInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    public HttpParser getParser() {
        return parser;
    }

    public void setParser(HttpParser parser) {
        this.parser = parser;
    }


    //===========>
}
