package com.abdallaadelessa.core.module.appModule;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferenceImpl implements SimpleLocalStorage {

    public SharedPreferences getPreference(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void save(Context context, String key, String value) {
        SharedPreferences sp = getPreference(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    @Override
    public String read(Context context, String key, String defaultValue) {
        SharedPreferences sp = getPreference(context);
        return sp.getString(key, defaultValue);
    }

    @Override
    public void delete(Context context, String key) {
        SharedPreferences sp = getPreference(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }
}