package com.abdallaadelessa.core.module.loggerModule;

import com.abdallaadelessa.core.app.BaseCoreApp;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by abdullah on 12/10/16.
 */

@Module
public class BaseCoreLoggerModule {
    private static final boolean ENABLE_LOGS = !BaseCoreApp.isReleaseBuildType();
    private static final String DEFAULT_TAG = "TimberLoggerImpl";

    @Provides
    @Singleton
    public AppLogger.IReporter provideILogger() {
        return new AppLogger.IReporter() {
            @Override
            public void reportError(Throwable throwable) {
                //Eat it
            }

            @Override
            public void reportError(String title, String message, Throwable throwable) {
                //Eat it
            }
        };
    }

    @Provides
    @Singleton
    public AppLogger provideAppLogger(AppLogger.IReporter iReporter) {
        return new TimberLoggerImpl(DEFAULT_TAG, ENABLE_LOGS, BaseCoreApp.getAppFolderPath(), iReporter);
    }
}
