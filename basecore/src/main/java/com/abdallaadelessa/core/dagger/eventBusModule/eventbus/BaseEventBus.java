package com.abdallaadelessa.core.dagger.eventBusModule.eventbus;

import rx.Observable;

/**
 * Created by Abdalla on 13/10/2016.
 */
public interface BaseEventBus {
    void send(Object o);

    Observable<Object> toObservable();

    boolean hasObservers();
}
