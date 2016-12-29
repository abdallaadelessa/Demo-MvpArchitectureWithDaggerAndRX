package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.executors.okhttpModule;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;

/**
 * Created by abdullah on 12/29/16.
 */
@Singleton
@Component(modules = OkHttpModule.class)
public interface OkHttpComponent {
    OkHttpClient getOkHttpClient();
}
