package com.abdallaadelessa.core.dagger.networkModule;

import com.abdallaadelessa.core.dagger.appModule.BaseCoreModule;
import com.abdallaadelessa.core.dagger.networkModule.builders.HttpRequestBuilder;
import com.abdallaadelessa.core.dagger.networkModule.builders.MultipartRequestBuilder;
import com.abdallaadelessa.core.dagger.networkModule.volley.VolleyNetworkModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Abdalla on 16/10/2016.
 */
@Module(includes = {VolleyNetworkModule.class})
public class BaseCoreNetworkModule {

    @Singleton
    @Provides
    public HttpRequestBuilder provideHttpRequestBuilder() {
        return HttpRequestBuilder.builder();
    }

    @Singleton
    @Provides
    public MultipartRequestBuilder provideMultipartRequestBuilder() {
        return MultipartRequestBuilder.builder();
    }

    @Singleton
    @Provides
    public Gson provideGson() {
        return new GsonBuilder().create();
    }
}
