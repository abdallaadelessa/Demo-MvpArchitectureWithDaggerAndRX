package com.abdallaadelessa.core.dagger;

import com.abdallaadelessa.core.dagger.appModule.BaseCoreComponent;
import com.abdallaadelessa.core.dagger.appModule.BaseCoreModule;
import com.abdallaadelessa.core.dagger.errorHandlerModule.BaseCoreErrorHandlerComponent;
import com.abdallaadelessa.core.dagger.errorHandlerModule.BaseCoreErrorHandlerModule;
import com.abdallaadelessa.core.dagger.eventBusModule.BaseCoreEventBusComponent;
import com.abdallaadelessa.core.dagger.eventBusModule.BaseCoreEventBusModule;
import com.abdallaadelessa.core.dagger.loggerModule.BaseCoreLoggerComponent;
import com.abdallaadelessa.core.dagger.loggerModule.BaseCoreLoggerModule;
import com.abdallaadelessa.core.dagger.cacheModule.BaseCoreCacheComponent;
import com.abdallaadelessa.core.dagger.cacheModule.BaseCoreCacheModule;
import com.abdallaadelessa.core.dagger.networkModule.BaseCoreNetworkComponent;
import com.abdallaadelessa.core.dagger.networkModule.BaseCoreNetworkModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by abdullah on 13/10/16.
 */
@Singleton
@Component(modules = {BaseCoreModule.class, BaseCoreErrorHandlerModule.class
        , BaseCoreEventBusModule.class, BaseCoreLoggerModule.class
        , BaseCoreCacheModule.class, BaseCoreNetworkModule.class})
public interface AppComponent
        extends BaseCoreComponent, BaseCoreErrorHandlerComponent
        , BaseCoreEventBusComponent, BaseCoreLoggerComponent
        , BaseCoreCacheComponent, BaseCoreNetworkComponent {

}
