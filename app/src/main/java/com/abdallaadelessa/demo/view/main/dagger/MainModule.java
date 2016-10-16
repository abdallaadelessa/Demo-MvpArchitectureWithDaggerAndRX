package com.abdallaadelessa.demo.view.main.dagger;

import com.abdallaadelessa.demo.view.main.MainPresenter;

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
