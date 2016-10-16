package com.abdallaadelessa.core.dagger.memoryModule.cache;

/**
 * Created by Abdalla on 16/10/2016.
 */

public interface BaseCache<K, V> {
    public V getEntry(K key, V defaultValue);

    public void putEntry(K key, V value);

    public V removeEntry(K key);

    public boolean containsEntry(K key);

    public void clearCache();
}
