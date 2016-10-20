package com.abdallaadelessa.demo.data.airline.repository.di;

import com.abdallaadelessa.demo.data.airline.repository.AirlinesRepository;
import com.abdallaadelessa.demo.data.airline.repository.CloudImpl;
import com.abdallaadelessa.demo.data.airline.repository.StubImpl;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Abdalla on 20/10/2016.
 */

@Module
public class AirlineRepositoryModule {

    public static final String CLOUD = "CLOUD";
    public static final String STUB = "STUB";

    @Singleton
    @Named(AirlineRepositoryModule.CLOUD)
    @Provides
    AirlinesRepository provideCloudRepository() {
        return new CloudImpl();
    }

    @Singleton
    @Named(AirlineRepositoryModule.STUB)
    @Provides
    AirlinesRepository provideStubRepository() {
        return new StubImpl();
    }
}
