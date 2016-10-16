package com.abdallaadelessa.core.app;

import com.abdallaadelessa.core.dagger.appModule.BaseCoreComponent;
import com.abdallaadelessa.core.dagger.errorHandlerModule.BaseCoreErrorHandlerComponent;
import com.abdallaadelessa.core.dagger.eventBusModule.BaseCoreEventBusComponent;
import com.abdallaadelessa.core.dagger.loggerModule.BaseCoreLoggerComponent;
import com.abdallaadelessa.core.dagger.memoryModule.BaseCoreCacheComponent;
import com.abdallaadelessa.core.dagger.networkModule.BaseCoreNetworkComponent;

import dagger.Component;

/**
 * Created by abdullah on 13/10/16.
 */
@ApplicationScope
@Component(dependencies = {BaseCoreComponent.class, BaseCoreErrorHandlerComponent.class
        , BaseCoreEventBusComponent.class, BaseCoreLoggerComponent.class
        , BaseCoreCacheComponent.class, BaseCoreNetworkComponent.class})
public interface AppComponent
        extends BaseCoreComponent, BaseCoreErrorHandlerComponent
        , BaseCoreEventBusComponent, BaseCoreLoggerComponent
        , BaseCoreCacheComponent, BaseCoreNetworkComponent {

}
