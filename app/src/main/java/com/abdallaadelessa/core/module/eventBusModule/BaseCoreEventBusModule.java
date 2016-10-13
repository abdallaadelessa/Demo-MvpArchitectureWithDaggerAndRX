package com.abdallaadelessa.core.module.eventBusModule;

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

    @Provides
    @Singleton
    public EventBus provideGlobalEventBus() {
        return new EventBusImpl(EventsObjects.GLOBAL_SUBJECT);
    }
}
