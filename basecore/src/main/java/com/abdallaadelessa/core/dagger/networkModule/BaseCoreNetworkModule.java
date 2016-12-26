package com.abdallaadelessa.core.dagger.networkModule;

import com.abdallaadelessa.core.dagger.appModule.BaseCoreModule;
import com.abdallaadelessa.core.dagger.loggerModule.BaseCoreLoggerModule;
import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpObservableExecutor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.HttpInterceptor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.HttpParser;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.BaseRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.HttpRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.HttpRequestManager;
import com.abdallaadelessa.core.dagger.networkModule.volley.MultiPartObservableExecutor;
import com.abdallaadelessa.core.dagger.networkModule.volley.VolleyHttpObservableExecutor;
import com.abdallaadelessa.core.dagger.networkModule.volley.VolleyNetworkModule;
import com.abdallaadelessa.core.model.MessageError;
import com.android.volley.RequestQueue;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
                }
                catch(Exception e) {
                    //Eat it!
                }
                return throwable;
            }
        };
    }

    @Singleton
    @Provides
    public HttpParser provideHttpParser() {
        return new HttpParser() {
            @Override
            public <T> T parse(String tag, Type type, String response) throws JSONException {
                return null;
            }
        };
    }

    @Provides
    public VolleyHttpObservableExecutor provideVolleyHttpObservableExecutor(RequestQueue requestQueue) {
        return new VolleyHttpObservableExecutor(requestQueue);
    }

    @Provides
    public MultiPartObservableExecutor provideMultiPartObservableExecutor() {
        return new MultiPartObservableExecutor();
    }

    @Singleton
    @Provides
    public HttpRequestManager provideHttpRequestManager(HttpInterceptor interceptor, HttpParser parser, BaseAppLogger logger, VolleyHttpObservableExecutor volleyHttpObservableExecutor, MultiPartObservableExecutor multiPartObservableExecutor, ExecutorService executorService) {
        return new HttpRequestManager(interceptor, parser, logger, volleyHttpObservableExecutor, multiPartObservableExecutor, executorService);
    }

    //=================>

    @Singleton
    @Provides
    public Gson provideGson() {
        return new GsonBuilder().create();
    }
}
