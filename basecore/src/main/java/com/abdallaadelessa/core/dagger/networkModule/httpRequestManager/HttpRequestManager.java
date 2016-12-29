package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager;

import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.BaseRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.HttpRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.MultiPartRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.executors.okhttpModule.MultiPartExecutor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.executors.volleyModule.VolleyHttpExecutor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

/**
 * Created by abdullah on 12/26/16.
 */

public class HttpRequestManager {
    private Map<String, BaseRequest> requestsByTagMap;
    private BaseAppLogger logger;
    private ExecutorService executorService;
    private BaseHttpInterceptor interceptor;
    private BaseHttpParser parser;

    //===========> Constructor
    @Inject
    public HttpRequestManager(BaseHttpParser parser, BaseAppLogger logger, ExecutorService executorService) {
        this.parser = parser;
        this.logger = logger;
        this.executorService = executorService;
        requestsByTagMap = Collections.synchronizedMap(new HashMap<String, BaseRequest>());
        this.interceptor = initHttpRequestManagerInterceptor();
    }

    private BaseHttpInterceptor initHttpRequestManagerInterceptor() {
        return new BaseHttpInterceptor() {
            @Override
            public BaseRequest interceptRequest(BaseRequest request) throws Exception {
                addRequestToQueue(request);
                request.getLogger().log(request.getTag(), request.toString());
                return super.interceptRequest(request);
            }

            @Override
            public String interceptResponse(BaseRequest request, String response) throws Exception {
                removeRequestFromQueue(request.getTag());
                request.getLogger().log(request.getTag(), response);
                return super.interceptResponse(request, response);
            }

            @Override
            public Throwable interceptError(BaseRequest request, Throwable throwable, boolean fatal) {
                removeRequestFromQueue(request.getTag());
                try {
                    request.getLogger().logError(request.getTag(), throwable, fatal);
                } catch (Exception e) {
                    //Eat it!
                }
                return super.interceptError(request, throwable, fatal);
            }
        };
    }

    //===========>

    public <T> HttpRequest<T> newHttpRequest() {
        return HttpRequest.create(getInterceptor(), getParser(), getLogger(), new VolleyHttpExecutor<T>(), getExecutorService());
    }

    public <T> MultiPartRequest<T> newMultiPartRequest() {
        return MultiPartRequest.create(getInterceptor(), getParser(), getLogger(), new MultiPartExecutor<T>(), getExecutorService());
    }

    public BaseRequest getRequestByTag(String tag) {
        return requestsByTagMap.get(tag);
    }

    public boolean cancelRequestByTag(String tag) {
        BaseRequest requestByTag = getRequestByTag(tag);
        if (requestByTag != null) {
            requestByTag.getObservableExecutor().cancel();
            removeRequestFromQueue(tag);
            return true;
        }
        return false;
    }

    public void cancelAll() {
        for (String tag : requestsByTagMap.keySet()) {
            cancelRequestByTag(tag);
        }
    }

    //===========> Setters and Getters

    private BaseAppLogger getLogger() {
        return logger;
    }

    private void setLogger(BaseAppLogger logger) {
        this.logger = logger;
    }

    private ExecutorService getExecutorService() {
        return executorService;
    }

    private void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    private BaseHttpInterceptor getInterceptor() {
        return interceptor;
    }

    private void setInterceptor(BaseHttpInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    private BaseHttpParser getParser() {
        return parser;
    }

    private void setParser(BaseHttpParser parser) {
        this.parser = parser;
    }

    //===========>

    private void addRequestToQueue(BaseRequest request) {
        requestsByTagMap.put(request.getTag(), request);
    }

    private void removeRequestFromQueue(String tag) {
        requestsByTagMap.remove(tag);
    }
}
