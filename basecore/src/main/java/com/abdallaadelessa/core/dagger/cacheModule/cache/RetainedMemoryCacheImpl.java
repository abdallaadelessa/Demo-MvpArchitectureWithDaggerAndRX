package com.abdallaadelessa.core.dagger.cacheModule.cache;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Abdalla on 23/10/2016.
 */

public class RetainedMemoryCacheImpl implements BaseCache<HashMap, String, Serializable>, Serializable {
    private HashMap<String, Serializable> cache;

    public RetainedMemoryCacheImpl(Context context) {
        cache = new HashMap<>();
    }

    @Override
    public void setCacheObject(HashMap cache) {
        this.cache = cache;
    }

    @Override
    public HashMap getCacheObject() {
        return cache;
    }

    @Override
    public Serializable getEntry(String key, Serializable defaultValue) {
        return cache.containsKey(key) ? cache.get(key) : defaultValue;
    }

    @Override
    public void putEntry(String key, Serializable value) {
        cache.put(key, value);
    }

    @Override
    public Serializable removeEntry(String key) {
        Serializable serializable = cache.get(key);
        cache.remove(key);
        return serializable;
    }

    @Override
    public boolean containsEntry(String key) {
        return cache.containsKey(key);
    }

    @Override
    public void clearCache() {
        cache.clear();
    }
}
