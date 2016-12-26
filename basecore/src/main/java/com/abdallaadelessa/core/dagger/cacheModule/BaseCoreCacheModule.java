package com.abdallaadelessa.core.dagger.cacheModule;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.LruCache;

import com.abdallaadelessa.core.dagger.appModule.BaseCoreModule;
import com.abdallaadelessa.core.dagger.cacheModule.cache.BaseCache;
import com.abdallaadelessa.core.dagger.cacheModule.cache.LruBaseCacheImpl;
import com.abdallaadelessa.core.dagger.cacheModule.cache.RetainedMemoryCacheImpl;
import com.abdallaadelessa.core.dagger.cacheModule.cache.SharedPreferenceCacheImpl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Abdalla on 16/10/2016.
 */
@Module(includes = BaseCoreModule.class)
public class BaseCoreCacheModule {
    @Singleton
    @Provides
    public BaseCache<SharedPreferences, String, String> provideSharedPreferenceCache(Context context) {
        return new SharedPreferenceCacheImpl(context);
    }

    @Singleton
    @Provides
    public BaseCache<LruCache, String, Object> provideLruCache(Context context) {
        return new LruBaseCacheImpl(context);
    }

    @Singleton
    @Provides
    public BaseCache<HashMap, String, Serializable> provideRetainedMemoryCache(Context context) {
        return new RetainedMemoryCacheImpl(context);
    }

}
