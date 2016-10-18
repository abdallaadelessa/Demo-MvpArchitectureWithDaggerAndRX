package com.abdallaadelessa.core.dagger.networkModule;

import com.abdallaadelessa.core.dagger.networkModule.builders.HttpRequest;
import com.abdallaadelessa.core.dagger.networkModule.builders.MultipartRequest;
import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Abdalla on 16/10/2016.
 */
@Singleton
@Component(modules = {BaseCoreNetworkModule.class})
public interface BaseCoreNetworkComponent {
    HttpRequest.Builder getHttpRequestBuilder();

    MultipartRequest.Builder getMultipartRequestBuilder();

    Gson getGson();
}
