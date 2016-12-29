package com.abdallaadelessa.core.dagger.networkModule;

import com.abdallaadelessa.core.dagger.appModule.BaseCoreAppModule;
import com.abdallaadelessa.core.dagger.loggerModule.BaseCoreLoggerModule;
import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpParser;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpRequestManager;
import com.google.gson.Gson;

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
@Module(includes = {BaseCoreAppModule.class, BaseCoreLoggerModule.class})
public class BaseCoreNetworkModule {

    //=================>  BaseHttpRequestManager

    @Singleton
    @Provides
    public BaseHttpParser provideHttpParser(final Gson gson) {
        return new BaseHttpParser() {
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
    public BaseHttpRequestManager provideHttpRequestManager(BaseHttpParser parser, BaseAppLogger logger, ExecutorService executorService) {
        return  new BaseHttpRequestManager(parser, logger, executorService);
    }

}
