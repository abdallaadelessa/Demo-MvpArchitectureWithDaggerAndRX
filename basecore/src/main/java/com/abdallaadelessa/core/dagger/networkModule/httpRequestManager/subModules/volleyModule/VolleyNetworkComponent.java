package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.subModules.volleyModule;

import com.android.volley.RequestTickle;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by abdullah on 12/29/16.
 */
@Singleton
@Component(modules = VolleyNetworkModule.class)
public interface VolleyNetworkComponent {
    RequestTickle getRequestTickle();
}
