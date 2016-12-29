package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager;

import android.support.annotation.NonNull;

import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.executors.okhttpModule.OkHttpExecutor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.BaseRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.HttpRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.MultiPartRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.executors.okhttpModule.MultiPartExecutor;
import com.abdallaadelessa.core.utils.ValidationUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

/**
 * Created by abdullah on 12/26/16.
 */

public class BaseHttpRequestManager {
    private Map<String, BaseRequest> requestsByTagMap;
    private BaseAppLogger logger;
    private ExecutorService executorService;
    private BaseHttpInterceptor interceptor;
    private BaseHttpParser parser;

    //===========> Constructor
    @Inject
    public BaseHttpRequestManager(BaseHttpParser parser, BaseAppLogger logger, ExecutorService executorService) {
        this.parser = parser;
        this.logger = logger;
        this.executorService = executorService;
        requestsByTagMap = Collections.synchronizedMap(new HashMap<String, BaseRequest>());
        this.interceptor = initHttpRequestManagerInterceptor();
    }

    protected BaseHttpInterceptor initHttpRequestManagerInterceptor() {
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

    @NonNull
    protected <T> BaseHttpExecutor<T, HttpRequest> createNewHttpExecutorInstance() {
        return new OkHttpExecutor<>();
    }

    @NonNull
    protected <T> BaseHttpExecutor<T, MultiPartRequest> createNewMultiPartExecutorInstance() {
        return new MultiPartExecutor<>();
    }

    //===========>

    public final <T> HttpRequest<T> newHttpRequest() {
        return HttpRequest.create(getInterceptor(), getParser(), getLogger(), (BaseHttpExecutor<T, HttpRequest>) createNewHttpExecutorInstance(), getExecutorService());
    }

    public final <T> MultiPartRequest<T> newMultiPartRequest() {
        return MultiPartRequest.create(getInterceptor(), getParser(), getLogger(), (BaseHttpExecutor<T, MultiPartRequest>) createNewMultiPartExecutorInstance(), getExecutorService());
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

    protected BaseAppLogger getLogger() {
        return logger;
    }

    protected ExecutorService getExecutorService() {
        return executorService;
    }

    protected BaseHttpInterceptor getInterceptor() {
        return interceptor;
    }

    protected BaseHttpParser getParser() {
        return parser;
    }

    //===========>

    public void setParser(BaseHttpParser parser) {
        this.parser = parser;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void setLogger(BaseAppLogger logger) {
        this.logger = logger;
    }

    //===========>

    private void addRequestToQueue(BaseRequest request) {
        if (ValidationUtils.isStringEmpty(request.getTag())) return;
        requestsByTagMap.put(request.getTag(), request);
    }

    private void removeRequestFromQueue(String tag) {
        if (ValidationUtils.isStringEmpty(tag)) return;
        requestsByTagMap.remove(tag);
    }

    //===========>

}
