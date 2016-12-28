package com.abdallaadelessa.core.dagger.errorHandlerModule;

import android.content.Context;

import com.abdallaadelessa.core.dagger.appModule.BaseCoreAppModule;
import com.abdallaadelessa.core.dagger.errorHandlerModule.errorHandler.BaseErrorHandler;
import com.abdallaadelessa.core.dagger.errorHandlerModule.errorHandler.CustomErrorHandlerImpl;
import com.abdallaadelessa.core.dagger.loggerModule.BaseCoreLoggerModule;
import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Abdalla on 16/10/2016.
 */

@Module(includes = {BaseCoreAppModule.class, BaseCoreLoggerModule.class})
public class BaseCoreErrorHandlerModule {

    @Singleton
    @Provides
    public BaseErrorHandler provideErrorHandler(Context context, BaseAppLogger appLogger) {
        return new CustomErrorHandlerImpl(context, appLogger);
    }
}
