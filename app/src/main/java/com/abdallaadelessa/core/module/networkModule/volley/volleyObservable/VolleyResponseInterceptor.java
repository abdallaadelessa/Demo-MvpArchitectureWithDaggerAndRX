package com.abdallaadelessa.core.module.networkModule.volley.volleyObservable;

/**
 * Created by Abdalla on 13/10/2016.
 */
public interface VolleyResponseInterceptor {
    String interceptResponse(String tag, String jsonResult) throws Exception;

    Throwable interceptError(String tag, Throwable throwable);

    void onBuild(VolleyObservableBuilder volleyObservableBuilder);
}
