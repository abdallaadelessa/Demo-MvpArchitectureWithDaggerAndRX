package com.abdallaadelessa.demo.presentation.airlineDetails.presenter;

import com.abdallaadelessa.demo.domain.airline.di.AirlineUseCasesModule;

import dagger.Component;

/**
 * Created by Abdalla on 23/10/2016.
 */
@Component(modules = AirlineUseCasesModule.class)
public interface AirlineDetailsComponent {
    AirlineDetailsPresenter getAirlineDetailsPresenter();
}
