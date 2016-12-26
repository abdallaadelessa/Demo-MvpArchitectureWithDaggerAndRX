package com.abdallaadelessa.core.dagger.eventBusModule;

import com.abdallaadelessa.core.dagger.eventBusModule.eventbus.BaseEventBus;
import com.abdallaadelessa.core.dagger.eventBusModule.eventbus.EventBusImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by Abdalla on 13/10/2016.
 */

@Module
public class BaseCoreEventBusModule {
    public enum EventsObjects {
        ;
        private static final Subject<Object, Object> GLOBAL_SUBJECT = new SerializedSubject<>(PublishSubject.create());
    }

    @Singleton
    @Provides
    public BaseEventBus provideGlobalEventBus() {
        return new EventBusImpl(EventsObjects.GLOBAL_SUBJECT);
    }
}
