package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager;

import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.HttpRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.MultiPartRequest;

import java.util.concurrent.ExecutorService;

/**
 * Created by abdullah on 12/26/16.
 */

public class HttpRequestManager {
    private HttpInterceptor interceptor;
    private HttpParser parser;
    private BaseAppLogger logger;
    private ExecutorService executorService;
    private BaseHttpObservableExecutor httpObservableExecutor;
    private BaseHttpObservableExecutor multipartObservableExecutor;

    //===========> Constructor

    public HttpRequestManager(HttpInterceptor interceptor, HttpParser parser, BaseAppLogger logger, BaseHttpObservableExecutor httpObservableExecutor, BaseHttpObservableExecutor multipartObservableExecutor, ExecutorService executorService) {
        this.interceptor = interceptor;
        this.parser = parser;
        this.logger = logger;
        this.httpObservableExecutor = httpObservableExecutor;
        this.multipartObservableExecutor = multipartObservableExecutor;
        this.executorService = executorService;
    }

    public HttpRequest newHttpRequest() {
        return HttpRequest.create(getInterceptor(), getParser(), getLogger(), getHttpObservableExecutor(), getExecutorService());
    }

    public MultiPartRequest newMultiPartRequest() {
        return MultiPartRequest.create(getInterceptor(), getParser(), getLogger(), getMultipartObservableExecutor(), getExecutorService());
    }

    //===========> Setters and Getters

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

    public BaseAppLogger getLogger() {
        return logger;
    }

    public void setLogger(BaseAppLogger logger) {
        this.logger = logger;
    }

    public BaseHttpObservableExecutor getHttpObservableExecutor() {
        return httpObservableExecutor;
    }

    public BaseHttpObservableExecutor getMultipartObservableExecutor() {
        return multipartObservableExecutor;
    }

    public void setMultipartObservableExecutor(BaseHttpObservableExecutor multipartObservableExecutor) {
        this.multipartObservableExecutor = multipartObservableExecutor;
    }

    public void setHttpObservableExecutor(BaseHttpObservableExecutor httpObservableExecutor) {
        this.httpObservableExecutor = httpObservableExecutor;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    //===========>
}
