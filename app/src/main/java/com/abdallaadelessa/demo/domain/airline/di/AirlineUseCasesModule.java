package com.abdallaadelessa.demo.domain.airline.di;

import com.abdallaadelessa.demo.data.airline.di.AirlineRepositoryModule;
import com.abdallaadelessa.demo.data.airline.repository.local.AirlinesFavRepository;
import com.abdallaadelessa.demo.data.airline.repository.remote.AirlinesRemoteRepository;
import com.abdallaadelessa.demo.domain.airline.useCases.FavouriteAirlinesUseCase;
import com.abdallaadelessa.demo.domain.airline.useCases.ListAirlinesUseCase;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Abdalla on 23/10/2016.
 */
@Module(includes = AirlineRepositoryModule.class)
public class AirlineUseCasesModule {
    @Provides
    public ListAirlinesUseCase provideListAirlinesUseCase(@Named(AirlineRepositoryModule.CLOUD) AirlinesRemoteRepository airlinesRemoteRepository) {
        return new ListAirlinesUseCase(airlinesRemoteRepository);
    }

    @Provides
    public FavouriteAirlinesUseCase provideFavouriteAirlinesUseCase(AirlinesFavRepository airlinesFavRepository) {
        return new FavouriteAirlinesUseCase(airlinesFavRepository);
    }
}
