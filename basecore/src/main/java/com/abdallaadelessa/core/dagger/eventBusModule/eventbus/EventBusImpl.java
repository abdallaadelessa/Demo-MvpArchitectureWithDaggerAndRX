package com.abdallaadelessa.core.dagger.eventBusModule.eventbus;

import rx.Observable;
import rx.subjects.Subject;

/**
 * Created by Abdalla on 13/10/2016.
 */
public class EventBusImpl implements BaseEventBus {

    private Subject<Object, Object> rxBus;

    public EventBusImpl(Subject<Object, Object> rxBus) {
        this.rxBus = rxBus;
    }

    @Override
    public void send(Object o) {
        if (rxBus != null) rxBus.onNext(o);
    }

    @Override
    public Observable<Object> toObservable() {
        return rxBus;
    }

    @Override
    public boolean hasObservers() {
        if (rxBus != null) {
            return rxBus.hasObservers();
        }
        return false;
    }
}
