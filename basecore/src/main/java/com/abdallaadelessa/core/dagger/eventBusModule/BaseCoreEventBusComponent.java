package com.abdallaadelessa.core.dagger.eventBusModule;

import com.abdallaadelessa.core.dagger.eventBusModule.eventbus.BaseEventBus;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Abdalla on 16/10/2016.
 */
@Singleton
@Component(modules = {BaseCoreEventBusModule.class})
public interface BaseCoreEventBusComponent {
    BaseEventBus getGlobalEventBus();
}
