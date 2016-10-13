package com.abdallaadelessa.core.app;

import android.content.Context;
import android.widget.Toast;

import com.abdallaadelessa.core.module.appModule.BaseCoreAppModule;
import com.abdallaadelessa.core.module.appModule.SimpleLocalStorage;
import com.abdallaadelessa.core.module.eventBusModule.BaseCoreEventBusModule;
import com.abdallaadelessa.core.module.loggerModule.AppLogger;
import com.abdallaadelessa.core.module.loggerModule.BaseCoreLoggerModule;
import com.abdallaadelessa.core.module.networkModule.VolleyNetworkModule;
import com.android.volley.RequestQueue;
import com.android.volley.RequestTickle;
import com.google.gson.Gson;

import java.util.concurrent.ExecutorService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by abdullah on 13/10/16.
 */
@Singleton
@Component(modules = {BaseCoreAppModule.class, VolleyNetworkModule.class, BaseCoreEventBusModule.class, BaseCoreLoggerModule.class})
public interface AppComponent {
    // BaseCoreAppModule
    Context getContext();

    BaseCoreApp getMyApplication();

    SimpleLocalStorage getSimpleLocalStorage();

    Toast getToast();

    ExecutorService getExecutorService();

    // VolleyNetworkModule
    RequestQueue getRequestQueue();

    RequestTickle getRequestTickle();

    Gson getGson();

    // BaseCoreLoggerModule
    AppLogger getLogger();
}
