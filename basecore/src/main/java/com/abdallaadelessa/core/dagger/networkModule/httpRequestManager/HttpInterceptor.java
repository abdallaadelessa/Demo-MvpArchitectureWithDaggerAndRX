package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager;

import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.BaseRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.HttpRequest;

/**
 * Created by abdullah on 12/26/16.
 */

public interface HttpInterceptor {
    BaseRequest interceptRequest(BaseRequest request) throws Exception;

    String interceptResponse(BaseRequest request, String response) throws Exception;

    Throwable interceptError(BaseRequest request, Throwable throwable, final boolean fatal);
}
