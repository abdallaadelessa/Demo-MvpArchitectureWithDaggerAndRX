package com.abdallaadelessa.demo.presentation.listAirlines.presenter;

import com.abdallaadelessa.demo.domain.airline.di.AirlineUseCasesModule;

import dagger.Component;

/**
 * Created by Abdalla on 23/10/2016.
 */
@Component(modules = AirlineUseCasesModule.class)
public interface ListAirlinesComponent {
    ListAirlinesPresenter getListAirlinesPresenter();
}
