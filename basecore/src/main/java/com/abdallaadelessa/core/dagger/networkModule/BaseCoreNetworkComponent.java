package com.abdallaadelessa.core.dagger.networkModule;

import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpParser;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpRequestManager;
import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Abdalla on 16/10/2016.
 */
@Singleton
@Component(modules = {BaseCoreNetworkModule.class})
public interface BaseCoreNetworkComponent {
    BaseHttpRequestManager getHttpRequestManager();

    BaseHttpParser getHttpParser();

    Gson getGson();
}
