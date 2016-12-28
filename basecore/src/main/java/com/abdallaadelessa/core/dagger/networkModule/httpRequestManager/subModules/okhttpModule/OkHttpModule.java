package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.subModules.okhttpModule;

import com.android.volley.toolbox.HttpStack;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

/**
 * Created by abdullah on 12/28/16.
 */
@Module
public class OkHttpModule {
    @Singleton
    @Provides
    public HttpStack provideHttpStack(OkHttpClient okHttpClient) {
        return new OkHttpStack(okHttpClient);
    }

    @Singleton
    @Provides
    public OkHttpClient provideOkHttpClient() {
        return new OkHttpClient().newBuilder().build();
    }
}
