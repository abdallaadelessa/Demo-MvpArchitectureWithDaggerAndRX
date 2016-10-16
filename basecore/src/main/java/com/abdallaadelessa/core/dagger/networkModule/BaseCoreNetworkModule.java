package com.abdallaadelessa.core.dagger.networkModule;

import android.content.Context;

import com.abdallaadelessa.core.dagger.appModule.BaseCoreModule;
import com.abdallaadelessa.core.dagger.loggerModule.BaseCoreLoggerModule;
import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.builders.HttpRequestBuilder;
import com.abdallaadelessa.core.dagger.networkModule.builders.MultipartRequestBuilder;
import com.abdallaadelessa.core.dagger.networkModule.volley.VolleyNetworkModule;
import com.android.volley.RequestQueue;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.ExecutorService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Abdalla on 16/10/2016.
 */
@Module(includes = {BaseCoreModule.class, BaseCoreLoggerModule.class, VolleyNetworkModule.class})
public class BaseCoreNetworkModule {

    @Singleton
    @Provides
    public HttpRequestBuilder provideHttpRequestBuilder(Context context
            , RequestQueue requestQueue, ExecutorService executorService
            , Gson gson, BaseAppLogger baseAppLogger) {
        return HttpRequestBuilder.builder(context, requestQueue, executorService, gson, baseAppLogger);
    }

    @Singleton
    @Provides
    public MultipartRequestBuilder provideMultipartRequestBuilder(Gson gson, BaseAppLogger baseAppLogger) {
        return MultipartRequestBuilder.builder(gson, baseAppLogger);
    }

    @Singleton
    @Provides
    public Gson provideGson() {
        return new GsonBuilder().create();
    }
}
