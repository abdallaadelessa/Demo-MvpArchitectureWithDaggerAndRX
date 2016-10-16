package com.abdallaadelessa.core.dagger.memoryModule;

import android.content.Context;

import com.abdallaadelessa.core.dagger.appModule.BaseCoreModule;
import com.abdallaadelessa.core.dagger.memoryModule.cache.BaseCache;
import com.abdallaadelessa.core.dagger.memoryModule.cache.LruBaseCacheImpl;
import com.abdallaadelessa.core.dagger.memoryModule.cache.SharedPreferenceImpl;

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
    public BaseCache<String, String> provideDiskCache(Context context) {
        return new SharedPreferenceImpl(context);
    }

    @Singleton
    @Provides
    public BaseCache<String, Object> provideMemoryCache(Context context) {
        return new LruBaseCacheImpl(context);
    }

}
