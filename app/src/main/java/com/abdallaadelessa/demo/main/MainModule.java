package com.abdallaadelessa.demo.main;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Abdalla on 13/10/2016.
 */
@Module
public class MainModule {
    @Provides
    public MainPresenter provideMainPresenter() {
        return new MainPresenter();
    }
}
