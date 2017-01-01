package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests;

import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpExecutor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpParser;
import com.abdallaadelessa.core.utils.ValidationUtils;

import java.util.HashMap;

/**
 * Created by abdullah on 12/26/16.
 */

public class HttpRequest<T> extends BaseRequest<HttpRequest<T>, T> {
    private String jsonBody;
    private String downloadPath;

    public static <T> HttpRequest<T> create(BaseAppLogger logger, BaseHttpParser parser,
                                            BaseHttpExecutor<T, HttpRequest<T>> observableExecutor) {
        return new HttpRequest<>(logger, parser, observableExecutor);
    }

    private HttpRequest(BaseAppLogger logger, BaseHttpParser parser, BaseHttpExecutor<T, HttpRequest<T>> observableExecutor) {
        super(logger, parser, observableExecutor);
    }

    //=====>

    public HttpRequest<T> setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
        return this;
    }

    private HttpRequest<T> setJsonBody(String jsonBody) {
        this.jsonBody = jsonBody;
        return this;
    }

    public HttpRequest<T> addJsonBody(String jsonBody) {
        setFormParams(new HashMap<String, String>());
        setJsonBody(jsonBody);
        POST();
        setContentType(CONTENT_TYPE_JSON);
        return this;
    }

    public HttpRequest<T> addFormParam(String key, String value) {
        setJsonBody(null);
        setContentType(CONTENT_TYPE_FORM);
        return super.addFormParam(key, value);
    }

    //=====>

    public String getJsonBody() {
        return jsonBody;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public boolean hasJsonBody() {
        return !ValidationUtils.isStringEmpty(getJsonBody());
    }

    public byte[] bodyToBytes() {
        if (hasJsonBody()) {
            byte[] bodyBytes = new byte[0];
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
