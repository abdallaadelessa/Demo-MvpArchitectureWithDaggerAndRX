package com.abdallaadelessa.demo.domain.airline.di;

import com.abdallaadelessa.demo.domain.airline.AirlineUseCases;

import dagger.Component;

/**
 * Created by Abdalla on 20/10/2016.
 */
@Component(modules = AirlineUseCasesModule.class)
public interface AirlineUseCasesComponent {
    AirlineUseCases getAirlineUseCases();
}
