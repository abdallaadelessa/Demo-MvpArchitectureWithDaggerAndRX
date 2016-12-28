package com.abdallaadelessa.core.dagger.cacheModule;

import android.content.SharedPreferences;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.LruCache;

import com.abdallaadelessa.core.dagger.appModule.BaseCoreAppModule;
import com.abdallaadelessa.core.dagger.cacheModule.cache.BaseCache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Abdalla on 16/10/2016.
 */
@Singleton
@Component(modules = BaseCoreCacheModule.class)
public interface BaseCoreCacheComponent {
    BaseCache<SharedPreferences, String, String> getSharedPreferenceCache();

    BaseCache<LruCache, String, Object> getLruCache();

    BaseCache<HashMap, String, Serializable> getRetainedMemoryCache();
}
