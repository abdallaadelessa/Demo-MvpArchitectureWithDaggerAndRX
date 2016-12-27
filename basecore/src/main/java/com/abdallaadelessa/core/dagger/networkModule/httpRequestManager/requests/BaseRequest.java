package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests;

import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpObservableExecutor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.HttpInterceptor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.HttpParser;

import java.util.concurrent.ExecutorService;

/**
 * Created by abdullah on 12/27/16.
 */

public abstract class BaseRequest<T> {
    protected HttpInterceptor interceptor;
    protected HttpParser parser;
    protected BaseAppLogger logger;
    protected BaseHttpObservableExecutor<T, BaseRequest> observableExecutor;
    protected ExecutorService executorService;
    protected String tag;
    protected String url;

    public BaseRequest(HttpInterceptor interceptor, HttpParser parser, BaseAppLogger logger, BaseHttpObservableExecutor observableExecutor, ExecutorService executorService) {
        this.interceptor = interceptor;
        this.parser = parser;
        this.logger = logger;
        this.observableExecutor = observableExecutor;
        this.executorService = executorService;
    }

    //=====> Getters

    public HttpInterceptor getInterceptor() {
        return interceptor;
    }

    public HttpParser getParser() {
        return parser;
    }

    public BaseAppLogger getLogger() {
        return logger;
    }

    public BaseHttpObservableExecutor<T, BaseRequest> getObservableExecutor() {
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
}
