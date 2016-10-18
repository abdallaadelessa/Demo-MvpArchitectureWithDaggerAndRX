package com.abdallaadelessa.core.dagger.networkModule;

import android.content.Context;

import com.abdallaadelessa.core.dagger.appModule.BaseCoreModule;
import com.abdallaadelessa.core.dagger.loggerModule.BaseCoreLoggerModule;
import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.builders.HttpRequest;
import com.abdallaadelessa.core.dagger.networkModule.builders.MultipartRequestBuilder;
import com.abdallaadelessa.core.dagger.networkModule.volley.VolleyNetworkModule;
import com.android.volley.RequestQueue;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Type;
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
    public HttpRequest.BaseResponseInterceptor provideBaseResponseInterceptor(final Gson gson) {
        return new HttpRequest.BaseResponseInterceptor() {

            @Override
            public <T> T parse(String tag, Type type, String json) throws JSONException {
                T t = null;
                if (type == String.class) {
                    t = (T) json;
                } else {
                    Object parsedData = new JSONTokener(json).nextValue();
                    if (parsedData instanceof JSONObject) {
                        JSONObject response = new JSONObject(json);
                        if (type == JSONObject.class) {
                            t = (T) response;
                        } else {
                            t = (T) gson.fromJson(json, type);
                        }
                    } else if (parsedData instanceof JSONArray) {
                        t = (T) gson.fromJson(json, type);
                    }
                }
                return t;
            }
        };
    }

    @Singleton
    @Provides
    public HttpRequest.Builder provideHttpRequestBuilder(Context context
            , RequestQueue requestQueue, HttpRequest.BaseResponseInterceptor baseResponseInterceptor, ExecutorService executorService, BaseAppLogger baseAppLogger) {
        return HttpRequest.builder(context, requestQueue, baseResponseInterceptor, executorService, baseAppLogger);
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
