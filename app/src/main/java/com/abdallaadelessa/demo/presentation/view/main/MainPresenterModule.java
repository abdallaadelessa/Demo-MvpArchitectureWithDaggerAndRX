package com.abdallaadelessa.demo.presentation.view.main;

import com.abdallaadelessa.demo.domain.airline.ListAirlinesUseCase;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Abdalla on 23/10/2016.
 */
@Module
public class MainPresenterModule {
    @Provides
    public ListAirlinesUseCase provideListAirlinesUseCase() {
        return new ListAirlinesUseCase();
    }
}
