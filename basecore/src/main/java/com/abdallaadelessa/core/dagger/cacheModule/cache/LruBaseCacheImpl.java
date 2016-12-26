package com.abdallaadelessa.core.dagger.cacheModule.cache;

import android.content.Context;
import android.support.v4.util.LruCache;

/**
 * Created by Abdalla on 16/10/2016.
 */

public class LruBaseCacheImpl implements BaseCache<LruCache, String, Object> {
    private static final int MAX_SIZE = 10;
    private LruCache<String, Object> lruCache;

    // ----------------->

    public LruBaseCacheImpl(Context context) {
        lruCache = new LruCache<>(MAX_SIZE);
    }

    private LruCache<String, Object> getLruCache() {
        return lruCache;
    }

    // ----------------->

    @Override
    public void setCacheObject(LruCache lruCache) {
        this.lruCache = lruCache;
    }

    @Override
    public LruCache getCacheObject() {
        return lruCache;
    }

    @Override
    public Object getEntry(String key, Object defaultValue) {
        Object returnedValue = getLruCache().get(key);
        return returnedValue == null ? defaultValue : returnedValue;
    }

    @Override
    public void putEntry(String key, Object value) {
        if (key != null && value != null)
            getLruCache().put(key, value);
    }

    @Override
    public Object removeEntry(String key) {
        return getLruCache().remove(key);
    }

    @Override
    public boolean containsEntry(String key) {
        return getLruCache().get(key) != null;
    }

    @Override
    public void clearCache() {
        getLruCache().evictAll();
    }

    // ----------------->
}
