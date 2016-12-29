package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager;

import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.BaseRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.HttpRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.MultiPartRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.subModules.okhttpModule.MultiPartExecutor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.subModules.volleyModule.VolleyHttpExecutor;

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
    private HttpInterceptor interceptor;
    private HttpParser parser;

    //===========> Constructor
    @Inject
    public HttpRequestManager(HttpParser parser, BaseAppLogger logger, ExecutorService executorService) {
        this.parser = parser;
        this.logger = logger;
        this.executorService = executorService;
        requestsByTagMap = Collections.synchronizedMap(new HashMap<String, BaseRequest>());
        this.interceptor = initQueueInterceptor();
    }

    private HttpInterceptor initQueueInterceptor() {
        return new HttpInterceptor() {
            @Override
            public BaseRequest interceptRequest(BaseRequest request) throws Exception {
                addRequestToQueue(request);
                logger.log(request.getTag(), request.toString());
                return super.interceptRequest(request);
            }

            @Override
            public String interceptResponse(BaseRequest request, String response) throws Exception {
                removeRequestFromQueue(request.getTag());
                logger.log(request.getTag(), response);
                return super.interceptResponse(request, response);
            }

            @Override
            public Throwable interceptError(BaseRequest request, Throwable throwable, boolean fatal) {
                removeRequestFromQueue(request.getTag());
                try {
                    logger.logError(request.getTag(), throwable, fatal);
                } catch (Exception e) {
                    //Eat it!
                }
                return super.interceptError(request, throwable, fatal);
            }
        };
    }

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

    private HttpInterceptor getInterceptor() {
        return interceptor;
    }

    private void setInterceptor(HttpInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    private HttpParser getParser() {
        return parser;
    }

    private void setParser(HttpParser parser) {
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
