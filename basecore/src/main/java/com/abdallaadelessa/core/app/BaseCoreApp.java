package com.abdallaadelessa.core.app;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.abdallaadelessa.core.dagger.AppComponent;
import com.abdallaadelessa.core.dagger.DaggerAppComponent;

import java.io.File;

public abstract class BaseCoreApp extends Application {
    private static BaseCoreApp mInstance;
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        appComponent = constructAppComponent();
    }

    protected AppComponent constructAppComponent() {
        return DaggerAppComponent.builder().build();
    }

    // ------------------->

    public static BaseCoreApp getInstance() {
        return mInstance;
    }

    public static AppComponent getAppComponent() {
        return getInstance().appComponent;
    }

    // -------------------> Build Config Fields

    public abstract boolean isReleaseBuildType();

    public abstract boolean isDebugBuildType();

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

    // ------------------->

    public static int getDefaultPlaceholder() {
        return 0;
    }

    public static Bitmap getDefaultPlaceholderBitmap(Context context) {
        try {
            return BitmapFactory.decodeResource(context.getResources(), getDefaultPlaceholder());
        } catch (Throwable throwable) {
            getAppComponent().getLogger().logError(throwable);
            return null;
        }
    }
}
