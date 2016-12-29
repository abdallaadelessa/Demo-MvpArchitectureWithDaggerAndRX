package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests;

import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpExecutor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.HttpInterceptor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.HttpParser;
import com.abdallaadelessa.core.utils.ValidationUtils;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;

/**
 * Created by abdullah on 12/26/16.
 */

public class HttpRequest<T> extends BaseRequest<HttpRequest<T>, T> {
    private String jsonBody;

    private HttpRequest(HttpParser parser, BaseAppLogger logger, BaseHttpExecutor<T, HttpRequest> observableExecutor, ExecutorService executorService) {
        super(parser, logger, observableExecutor, executorService);
    }

    public static <T> HttpRequest<T> create(HttpInterceptor interceptor, HttpParser parser, BaseAppLogger logger
            , BaseHttpExecutor<T, HttpRequest> observableExecutor, ExecutorService executorService) {
        HttpRequest<T> httpRequest = new HttpRequest<>(parser, logger, observableExecutor, executorService);
        httpRequest.addInterceptor(interceptor);
        return httpRequest;
    }

    //=====>

    private HttpRequest<T> setJsonBody(String jsonBody) {
        this.jsonBody = jsonBody;
        return this;
    }

    public HttpRequest<T> addJsonBody(String jsonBody) {
        setFormParams(new HashMap<String, String>());
        setJsonBody(jsonBody);
        POST();
        contentType(CONTENT_TYPE_JSON);
        return this;
    }

    public HttpRequest<T> addFormParam(String key, String value) {
        setJsonBody(null);
        contentType(CONTENT_TYPE_FORM);
        return super.addFormParam(key, value);
    }

    //=====>

    public String getJsonBody() {
        return jsonBody;
    }

    public boolean hasJsonBody() {
        return !ValidationUtils.isStringEmpty(getJsonBody());
    }

    public byte[] bodyToBytes() {
        if (!hasJsonBody()) {
            byte[] bodyBytes = null;
            try {
                if (!ValidationUtils.isStringEmpty(getJsonBody())) {
                    bodyBytes = getJsonBody().getBytes(UTF_8);
                }
            } catch (Exception ee) {
                if (getLogger() != null) getLogger().logError(ee);
            }
            return bodyBytes;
        } else {
            return super.bodyToBytes();
        }
    }

    //=====>


}
