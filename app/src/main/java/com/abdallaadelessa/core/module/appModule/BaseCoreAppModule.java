package com.abdallaadelessa.core.module.appModule;

import android.content.Context;
import android.widget.Toast;

import com.abdallaadelessa.core.app.BaseCoreApp;
import com.abdallaadelessa.core.model.AppSingleton;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by abdullah on 12/10/16.
 */
@Module
public class BaseCoreAppModule {

    private BaseCoreApp baseCoreApp;

    public BaseCoreAppModule(BaseCoreApp baseCoreApp) {
        this.baseCoreApp = baseCoreApp;
    }

    @Provides
    @Singleton
    public Context provideAppContext() {
        return baseCoreApp;
    }

    @Provides
    @Singleton
    public BaseCoreApp provideBaseCoreApp() {
        return baseCoreApp;
    }

    @Provides
    @Singleton
    public SimpleLocalStorage provideSimpleLocalStorage() {
        return new SharedPreferenceImpl();
    }

    @Provides
    @Singleton
    public AppSingleton provideAppSingleton() {
        return new AppSingleton();
    }

    @Provides
    @Singleton
    public Toast provideAppToast(Context context) {
        return Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }

    @Provides
    @Singleton
    public ExecutorService provideAppExecutorService() {
        return Executors.newFixedThreadPool(10);
    }
}
