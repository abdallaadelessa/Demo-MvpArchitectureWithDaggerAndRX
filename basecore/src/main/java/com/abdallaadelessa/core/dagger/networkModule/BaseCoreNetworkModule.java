package com.abdallaadelessa.core.dagger.networkModule;

import com.abdallaadelessa.core.dagger.appModule.BaseCoreModule;
import com.abdallaadelessa.core.dagger.loggerModule.BaseCoreLoggerModule;
import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.HttpInterceptor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.HttpParser;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.BaseRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.HttpRequestManager;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.volley.VolleyNetworkModule;
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

    //=================>  HttpRequestManager

    @Singleton
    @Provides
    public HttpInterceptor provideHttpInterceptor(final BaseAppLogger logger) {
        return new HttpInterceptor() {
            @Override
            public BaseRequest interceptRequest(BaseRequest request) throws Exception {
                logger.log(request.getTag(), request.toString());
                return request;
            }

            @Override
            public String interceptResponse(BaseRequest request, String response) throws Exception {
                logger.log(request.getTag(), response);
                return response;
            }

            @Override
            public Throwable interceptError(BaseRequest request, Throwable throwable, boolean fatal) {
                try {
                    logger.logError(request.getTag(), throwable, fatal);
                } catch (Exception e) {
                    //Eat it!
                }
                return throwable;
            }
        };
    }

    @Singleton
    @Provides
    public HttpParser provideHttpParser(final Gson gson) {
        return new HttpParser() {
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
    public HttpRequestManager provideHttpRequestManager(HttpInterceptor interceptor, HttpParser parser, BaseAppLogger logger, ExecutorService executorService, RequestQueue queue) {
        return new HttpRequestManager(interceptor, parser, logger, executorService, queue);
    }

    //=================>

    @Singleton
    @Provides
    public Gson provideGson() {
        return new GsonBuilder().create();
    }
}
