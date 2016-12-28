package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests;

import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpExecutor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.HttpInterceptor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.HttpParser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by abdullah on 12/27/16.
 */

public abstract class BaseRequest<T> {
    protected HttpParser parser;
    protected BaseAppLogger logger;
    protected BaseHttpExecutor<T, BaseRequest> observableExecutor;
    private List<HttpInterceptor> interceptors;
    protected ExecutorService executorService;
    protected String tag;
    protected String url;

    public BaseRequest(HttpParser parser, BaseAppLogger logger, BaseHttpExecutor observableExecutor, ExecutorService executorService) {
        this.interceptors = new ArrayList<>();
        this.parser = parser;
        this.logger = logger;
        this.observableExecutor = observableExecutor;
        this.executorService = executorService;
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

    @Override
    public String toString() {
        return "Request{" +
                "tag='" + tag + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
