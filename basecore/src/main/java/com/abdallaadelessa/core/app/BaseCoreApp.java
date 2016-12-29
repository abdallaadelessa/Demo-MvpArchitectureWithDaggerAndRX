package com.abdallaadelessa.core.app;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.abdallaadelessa.core.dagger.appModule.BaseCoreAppComponent;
import com.abdallaadelessa.core.dagger.appModule.BaseCoreAppModule;
import com.abdallaadelessa.core.dagger.appModule.DaggerBaseCoreAppComponent;
import com.abdallaadelessa.core.dagger.cacheModule.BaseCoreCacheComponent;
import com.abdallaadelessa.core.dagger.cacheModule.BaseCoreCacheModule;
import com.abdallaadelessa.core.dagger.cacheModule.DaggerBaseCoreCacheComponent;
import com.abdallaadelessa.core.dagger.errorHandlerModule.BaseCoreErrorHandlerComponent;
import com.abdallaadelessa.core.dagger.errorHandlerModule.BaseCoreErrorHandlerModule;
import com.abdallaadelessa.core.dagger.errorHandlerModule.DaggerBaseCoreErrorHandlerComponent;
import com.abdallaadelessa.core.dagger.eventBusModule.BaseCoreEventBusComponent;
import com.abdallaadelessa.core.dagger.eventBusModule.BaseCoreEventBusModule;
import com.abdallaadelessa.core.dagger.eventBusModule.DaggerBaseCoreEventBusComponent;
import com.abdallaadelessa.core.dagger.loggerModule.BaseCoreLoggerComponent;
import com.abdallaadelessa.core.dagger.loggerModule.BaseCoreLoggerModule;
import com.abdallaadelessa.core.dagger.loggerModule.DaggerBaseCoreLoggerComponent;
import com.abdallaadelessa.core.dagger.networkModule.BaseCoreNetworkComponent;
import com.abdallaadelessa.core.dagger.networkModule.BaseCoreNetworkModule;
import com.abdallaadelessa.core.dagger.networkModule.DaggerBaseCoreNetworkComponent;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.executors.okhttpModule.OkHttpModule;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.executors.volleyModule.VolleyNetworkModule;

import java.io.File;

public abstract class BaseCoreApp extends Application {
    private static BaseCoreApp mInstance;
    //===>
    private BaseCoreAppModule appModule;
    private BaseCoreErrorHandlerModule errorHandlerModule;
    private BaseCoreEventBusModule eventBusModule;
    private BaseCoreLoggerModule loggerModule;
    private BaseCoreCacheModule cacheModule;
    private BaseCoreNetworkModule networkModule;
    //===>
    private BaseCoreAppComponent appComponent;
    private BaseCoreErrorHandlerComponent errorHandlerComponent;
    private BaseCoreEventBusComponent eventBusComponent;
    private BaseCoreLoggerComponent loggerComponent;
    private BaseCoreCacheComponent cacheComponent;
    private BaseCoreNetworkComponent networkComponent;

    //===>

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        constructAppModules();
    }

    private void constructAppModules() {
        appModule = new BaseCoreAppModule();
        errorHandlerModule = new BaseCoreErrorHandlerModule();
        eventBusModule = new BaseCoreEventBusModule();
        loggerModule = new BaseCoreLoggerModule();
        cacheModule = new BaseCoreCacheModule();
        networkModule = new BaseCoreNetworkModule();
    }

    //=========================>

    public static BaseCoreApp getInstance() {
        return mInstance;
    }

    //=========================>

    public BaseCoreAppComponent getAppComponent() {
        if (appComponent == null) {
            appComponent = DaggerBaseCoreAppComponent
                    .builder()
                    .baseCoreAppModule(appModule)
                    .build();
        }
        return appComponent;
    }

    public BaseCoreErrorHandlerComponent getErrorHandlerComponent() {
        if (errorHandlerComponent == null) {
            errorHandlerComponent = DaggerBaseCoreErrorHandlerComponent
                    .builder()
                    .baseCoreAppModule(appModule)
                    .baseCoreErrorHandlerModule(errorHandlerModule)
                    .baseCoreLoggerModule(loggerModule)
                    .build();
        }
        return errorHandlerComponent;
    }

    public BaseCoreEventBusComponent getEventBusComponent() {
        if (eventBusComponent == null) {
            eventBusComponent = DaggerBaseCoreEventBusComponent
                    .builder()
                    .baseCoreEventBusModule(eventBusModule)
                    .build();
        }
        return eventBusComponent;
    }

    public BaseCoreLoggerComponent getLoggerComponent() {
        if (loggerComponent == null) {
            loggerComponent = DaggerBaseCoreLoggerComponent
                    .builder()
                    .baseCoreLoggerModule(loggerModule)
                    .build();
        }
        return loggerComponent;
    }

    public BaseCoreCacheComponent getCacheComponent() {
        if (cacheComponent == null) {
            cacheComponent = DaggerBaseCoreCacheComponent
                    .builder()
                    .baseCoreAppModule(appModule)
                    .baseCoreCacheModule(cacheModule)
                    .build();
        }
        return cacheComponent;
    }

    public BaseCoreNetworkComponent getNetworkComponent() {
        if (networkComponent == null) {
            networkComponent = DaggerBaseCoreNetworkComponent
                    .builder()
                    .baseCoreAppModule(appModule)
                    .baseCoreLoggerModule(loggerModule)
                    .baseCoreNetworkModule(networkModule)
                    .build();
        }
        return networkComponent;
    }

    //=========================> Build Config Fields

    public abstract boolean isReleaseBuildType();

    public abstract boolean isDebugBuildType();

    //=========================> Static Methods

    public static String getAppFolderPath() {
        File externalCacheDir = BaseCoreApp.getInstance().getAppComponent().getContext().getExternalCacheDir();
        File internalCacheDir = BaseCoreApp.getInstance().getAppComponent().getContext().getCacheDir();
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

    //=========================>

    public static int getDefaultPlaceholder() {
        return 0;
    }

    public static Bitmap getDefaultPlaceholderBitmap(Context context) {
        try {
            return BitmapFactory.decodeResource(context.getResources(), getDefaultPlaceholder());
        } catch (Throwable throwable) {
            BaseCoreApp.getInstance().getLoggerComponent().getLogger().logError(throwable);
            return null;
        }
    }
}
