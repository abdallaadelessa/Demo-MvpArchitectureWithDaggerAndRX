package com.abdallaadelessa.core.dagger.networkModule.volley;

import android.content.Context;

import com.abdallaadelessa.core.app.BaseCoreApp;
import com.abdallaadelessa.core.dagger.appModule.BaseCoreModule;
import com.android.volley.RequestQueue;
import com.android.volley.RequestTickle;
import com.android.volley.VolleyLog;
import com.android.volley.cache.DiskLruBasedCache;
import com.android.volley.cache.plus.SimpleImageLoader;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.VolleyTickle;

import dagger.Module;
import dagger.Provides;

/**
 * Created by abdullah on 12/10/16.
 */
@Module
public class VolleyNetworkModule {
    private static final boolean ENABLE_LOGS = !BaseCoreApp.isReleaseBuildType();

    public VolleyNetworkModule() {
        VolleyLog.DEBUG = ENABLE_LOGS;
    }

    //----> Volley
    @Provides

    public HttpStack provideHttpStack() {
        return new OkHttpStack();
    }

    @Provides

    public RequestQueue provideRequestQueue(Context context, HttpStack okHttpStack) {

        return Volley.newRequestQueue(context, okHttpStack);
    }

    @Provides

    public RequestTickle provideRequestTickle(Context context, HttpStack okHttpStack) {
        return VolleyTickle.newRequestTickle(context, okHttpStack);
    }

    //----> Image Loader
    @Provides

    public SimpleImageLoader provideSimpleImageLoader(Context context) {
        DiskLruBasedCache.ImageCacheParams cacheParams = new DiskLruBasedCache.ImageCacheParams(context, "CacheDirectory");
        cacheParams.setMemCacheSizePercent(0.5f);
        return new SimpleImageLoader(context, cacheParams);
    }


}
