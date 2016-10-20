package com.abdallaadelessa.demo.data.airline.repository.di;

import com.abdallaadelessa.demo.data.airline.repository.AirlinesRepository;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Abdalla on 20/10/2016.
 */
@Singleton
@Component(modules = AirlineRepositoryModule.class)
public interface AirlineRepositoryComponent {
    @Named(AirlineRepositoryModule.CLOUD)
    AirlinesRepository getCloudRepository();

    @Named(AirlineRepositoryModule.STUB)
    AirlinesRepository getStubRepository();
}
