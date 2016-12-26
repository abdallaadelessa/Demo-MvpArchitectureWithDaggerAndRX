package com.abdallaadelessa.core.dagger.loggerModule;

import com.abdallaadelessa.core.app.BaseCoreApp;
import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.loggerModule.logger.TimberLoggerImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by abdullah on 12/10/16.
 */

@Module
public class BaseCoreLoggerModule {
    private static final boolean ENABLE_LOGS = !BaseCoreApp.getInstance().isReleaseBuildType();
    private static final String DEFAULT_TAG = "TimberLoggerImpl";

    @Singleton
    @Provides
    public BaseAppLogger.IReporter provideILogger() {
        return new BaseAppLogger.IReporter() {
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

    @Singleton
    @Provides
    public BaseAppLogger provideAppLogger(BaseAppLogger.IReporter iReporter) {
        return new TimberLoggerImpl(DEFAULT_TAG, ENABLE_LOGS, BaseCoreApp.getAppFolderPath(), iReporter);
    }
}
