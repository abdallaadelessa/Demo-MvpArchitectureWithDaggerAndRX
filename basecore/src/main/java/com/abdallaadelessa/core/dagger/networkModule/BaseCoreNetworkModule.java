package com.abdallaadelessa.core.dagger.networkModule;

import com.abdallaadelessa.core.dagger.appModule.BaseCoreModule;
import com.abdallaadelessa.core.dagger.loggerModule.BaseCoreLoggerModule;
import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.builders.BaseResponseInterceptor;
import com.abdallaadelessa.core.dagger.networkModule.builders.HttpRequest;
import com.abdallaadelessa.core.dagger.networkModule.builders.MultipartRequest;
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
    public BaseResponseInterceptor provideBaseResponseInterceptor(final Gson gson, BaseAppLogger baseAppLogger) {
        return new BaseResponseInterceptor(baseAppLogger) {

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
    public HttpRequest.Builder provideHttpRequestBuilder(RequestQueue requestQueue, BaseResponseInterceptor baseResponseInterceptor, ExecutorService executorService) {
        return HttpRequest.builder(requestQueue, baseResponseInterceptor, executorService);
    }

    @Singleton
    @Provides
    public MultipartRequest.Builder provideMultipartRequestBuilder(BaseResponseInterceptor baseResponseInterceptor) {
        return MultipartRequest.builder(baseResponseInterceptor);
    }

    @Singleton
    @Provides
    public Gson provideGson() {
        return new GsonBuilder().create();
    }
}
