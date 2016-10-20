package com.abdallaadelessa.demo.domain.airline.di;

import com.abdallaadelessa.demo.domain.airline.AirlineUseCases;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Abdalla on 20/10/2016.
 */

@Module
public class AirlineUseCasesModule {
    @Provides
    AirlineUseCases provideAirlineUseCases() {
        return new AirlineUseCases();
    }
}
