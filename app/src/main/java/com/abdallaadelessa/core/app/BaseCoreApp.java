package com.abdallaadelessa.core.app;

import android.app.Application;

import com.abdallaadelessa.demo.BuildConfig;

import java.io.File;

public class BaseCoreApp extends Application {
    private static BaseCoreApp mInstance;
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        appComponent = DaggerAppComponent.builder().build();
    }

    // ------------------->

    public static BaseCoreApp getInstance() {
        return mInstance;
    }

    public static AppComponent getAppComponent() {
        return getInstance().appComponent;
    }

    // -------------------> Build Config Fields

    public static boolean isMockBuildType() {
        return BuildConfig.BUILD_TYPE.equalsIgnoreCase("mock");
    }

    public static boolean isReleaseBuildType() {
        return BuildConfig.BUILD_TYPE.equalsIgnoreCase("release");
    }

    public static boolean isDebugBuildType() {
        return BuildConfig.BUILD_TYPE.equalsIgnoreCase("debug");
    }

    // -------------------> Static Methods

    public static String getAppFolderPath() {
        File externalCacheDir = getAppComponent().getContext().getExternalCacheDir();
        File internalCacheDir = getAppComponent().getContext().getCacheDir();
        return externalCacheDir == null ? internalCacheDir.getPath() : externalCacheDir.getPath();
    }

    public static String getAppDownloadsPath() {
        String path = getAppFolderPath() + File.separator + "Downloads" + File.separator;
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return path;
    }

    public static String getAppMediaFolderPath() {
        String path = getAppFolderPath() + File.separator + "Media" + File.separator;
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return path;
    }

    public static String getAppTempFolderPath() {
        String path = getAppFolderPath() + File.separator + "Temp" + File.separator;
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return path;
    }

}
