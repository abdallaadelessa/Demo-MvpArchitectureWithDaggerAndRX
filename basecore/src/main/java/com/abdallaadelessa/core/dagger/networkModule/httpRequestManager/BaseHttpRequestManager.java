package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager;

import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.executors.FileExecutor;
import com.abdallaadelessa.core.model.FileDownloadModel;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.executors.VolleyExecutor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.BaseRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.HttpRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.MultiPartRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.executors.MultiPartExecutor;
import com.abdallaadelessa.core.utils.ValidationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by abdullah on 12/26/16.
 */

public class BaseHttpRequestManager {
    private Map<String, BaseRequest> requestsByTagMap;
    private List<BaseHttpInterceptor> interceptors;
    private BaseAppLogger logger;
    private BaseHttpParser parser;

    //===========> Constructor
    @Inject
    public BaseHttpRequestManager(BaseAppLogger logger, BaseHttpParser parser) {
        this.parser = parser;
        this.logger = logger;
        this.requestsByTagMap = Collections.synchronizedMap(new HashMap<String, BaseRequest>());
        this.interceptors = new ArrayList<>();
        this.interceptors.add(initHttpRequestManagerInterceptor());
    }

    protected final BaseHttpInterceptor initHttpRequestManagerInterceptor() {
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
        HttpRequest<T> tHttpRequest = HttpRequest.create(getLogger(), getParser(), new VolleyExecutor<T>());
        tHttpRequest.getInterceptors().addAll(getInterceptors());
        return tHttpRequest;
    }

    public <T> MultiPartRequest<T> newMultiPartRequest() {
        MultiPartRequest<T> multiPartRequest = MultiPartRequest.create(getLogger(), getParser(), new MultiPartExecutor<T>());
        multiPartRequest.getInterceptors().addAll(getInterceptors());
        return multiPartRequest;
    }

    public HttpRequest<FileDownloadModel> newFileRequest() {
        HttpRequest<FileDownloadModel> request = newHttpRequest();
        return request.setObservableExecutor(new FileExecutor());
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

    protected List<BaseHttpInterceptor> getInterceptors() {
        return interceptors;
    }

    protected BaseHttpParser getParser() {
        return parser;
    }

    //===========>

    public void setParser(BaseHttpParser parser) {
        this.parser = parser;
    }

    public void setLogger(BaseAppLogger logger) {
        this.logger = logger;
    }

    public void addInterceptor(BaseHttpInterceptor interceptor) {
        this.interceptors.add(interceptor);
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
