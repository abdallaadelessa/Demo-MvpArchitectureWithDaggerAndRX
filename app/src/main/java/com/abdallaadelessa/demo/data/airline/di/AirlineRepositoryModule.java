package com.abdallaadelessa.demo.data.airline.di;

import com.abdallaadelessa.demo.data.airline.repository.local.AirlinesFavRepository;
import com.abdallaadelessa.demo.data.airline.repository.local.MemoryFavImpl;
import com.abdallaadelessa.demo.data.airline.repository.remote.CloudImpl;
import com.abdallaadelessa.demo.data.airline.repository.remote.AirlinesRemoteRepository;
import com.abdallaadelessa.demo.data.airline.repository.remote.StubImpl;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Abdalla on 20/10/2016.
 */

@Module
public class AirlineRepositoryModule {

    public static final String CLOUD = "CLOUD";
    public static final String STUB = "STUB";

    // ---------->

    @Named(AirlineRepositoryModule.CLOUD)
    @Provides
    AirlinesRemoteRepository provideCloudRepository() {
        return new CloudImpl();
    }

    @Named(AirlineRepositoryModule.STUB)
    @Provides
    AirlinesRemoteRepository provideStubRepository() {
        return new StubImpl();
    }

    // ---------->

    @Provides
    AirlinesFavRepository provideMemoryFavImpl() {
        return new MemoryFavImpl();
    }
}
