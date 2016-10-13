package com.abdallaadelessa.core.module.eventBusModule;

import rx.Observable;

/**
 * Created by Abdalla on 13/10/2016.
 */
public interface EventBus {
    void send(Object o);

    Observable<Object> toObservable();

    boolean hasObservers();
}
