package com.abdallaadelessa.core.dagger.appModule;

import android.content.Context;
import android.widget.Toast;

import com.abdallaadelessa.core.app.BaseCoreApp;

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

    @Singleton
    @Provides
    public Context provideAppContext() {
        return BaseCoreApp.getInstance();
    }

    @Singleton
    @Provides
    public Toast provideAppToast(Context context) {
        return Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }

    @Singleton
    @Provides
    public ExecutorService provideAppExecutorService() {
        return Executors.newFixedThreadPool(10);
    }
}
