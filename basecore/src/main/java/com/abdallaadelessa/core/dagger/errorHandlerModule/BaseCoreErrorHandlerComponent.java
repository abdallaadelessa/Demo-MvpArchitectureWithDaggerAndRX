package com.abdallaadelessa.core.dagger.errorHandlerModule;

import com.abdallaadelessa.core.dagger.appModule.BaseCoreAppModule;
import com.abdallaadelessa.core.dagger.errorHandlerModule.errorHandler.BaseErrorHandler;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Abdalla on 16/10/2016.
 */

@Singleton
@Component(modules = BaseCoreErrorHandlerModule.class)
public interface BaseCoreErrorHandlerComponent {
    BaseErrorHandler getErrorHandler();
}
