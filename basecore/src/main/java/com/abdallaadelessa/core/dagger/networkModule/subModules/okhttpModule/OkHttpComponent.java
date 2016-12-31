package com.abdallaadelessa.core.dagger.networkModule.subModules.okhttpModule;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;

/**
 * Created by abdullah on 12/29/16.
 */
@Singleton
@Component(modules = OkHttpModule.class)
public interface OkHttpComponent {
    OkHttpClient.Builder getOkHttpClientBuilder();
}
