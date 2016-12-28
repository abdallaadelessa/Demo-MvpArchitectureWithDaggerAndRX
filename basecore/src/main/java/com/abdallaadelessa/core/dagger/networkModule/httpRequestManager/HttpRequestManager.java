package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager;

import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.HttpRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.MultiPartRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.subModules.okhttpModule.MultiPartExecutor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.subModules.volleyModule.VolleyHttpExecutor;
import com.android.volley.RequestQueue;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import okhttp3.OkHttpClient;

/**
 * Created by abdullah on 12/26/16.
 */

public class HttpRequestManager {
    private BaseAppLogger logger;
    private ExecutorService executorService;
    private HttpInterceptor interceptor;
    private HttpParser parser;
    //======>
    private RequestQueue queue;
    private OkHttpClient okHttpClient;

    //===========> Constructor
    @Inject
    public HttpRequestManager(HttpInterceptor interceptor, HttpParser parser, BaseAppLogger logger, ExecutorService executorService) {
        this.interceptor = interceptor;
        this.parser = parser;
        this.logger = logger;
        this.executorService = executorService;
    }

    public <T> HttpRequest<T> newHttpRequest() {
        return HttpRequest.create(getInterceptor(), getParser(), getLogger(), new VolleyHttpExecutor<T>(queue), getExecutorService());
    }

    public <T> MultiPartRequest<T> newMultiPartRequest() {
        return MultiPartRequest.create(getInterceptor(), getParser(), getLogger(), new MultiPartExecutor<T>(okHttpClient), getExecutorService());
    }

    //===========> Setters and Getters


    public void setQueue(RequestQueue queue) {
        this.queue = queue;
    }

    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

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
