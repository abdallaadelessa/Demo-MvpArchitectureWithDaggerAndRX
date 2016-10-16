package com.abdallaadelessa.core.dagger.loggerModule;

import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Abdalla on 16/10/2016.
 */
@Singleton
@Component(modules = {BaseCoreLoggerModule.class})
public interface BaseCoreLoggerComponent {
    BaseAppLogger getLogger();
}
