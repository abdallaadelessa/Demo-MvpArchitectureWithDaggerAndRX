package com.abdallaadelessa.core.dagger.cacheModule.cache;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferenceImpl implements BaseCache<String, String> {
    private SharedPreferences sharedPreferences;

    // ----------------->

    public SharedPreferenceImpl(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private SharedPreferences getPreference() {
        return sharedPreferences;
    }

    // ----------------->

    @Override
    public String getEntry(String key, String defaultValue) {
        SharedPreferences sp = getPreference();
        return sp != null ? sp.getString(key, defaultValue) : null;
    }

    @Override
    public void putEntry(String key, String value) {
        SharedPreferences sp = getPreference();
        if (sp != null) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, value);
            editor.apply();
        }
    }

    @Override
    public String removeEntry(String key) {
        String value = null;
        SharedPreferences sp = getPreference();
        if (sp != null) {
            value = getEntry(key, null);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove(key);
            editor.apply();
        }
        return value;
    }

    @Override
    public boolean containsEntry(String key) {
        return getEntry(key, null) != null;
    }

    @Override
    public void clearCache() {
        SharedPreferences sp = getPreference();
        if (sp != null) {
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.apply();
        }
    }

    // ----------------->
}