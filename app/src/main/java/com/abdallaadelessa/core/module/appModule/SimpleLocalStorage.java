package com.abdallaadelessa.core.module.appModule;

import android.content.Context;

/**
 * Created by Abdalla on 13/10/2016.
 */
public interface SimpleLocalStorage {
    void save(Context context, String key, String value);

    String read(Context context, String key, String defaultValue);

    void delete(Context context, String key);
}
