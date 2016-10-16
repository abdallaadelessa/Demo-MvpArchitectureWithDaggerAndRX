package com.abdallaadelessa.demo.view.main.dagger;

import com.abdallaadelessa.demo.view.main.MainFragment;

import dagger.Component;

/**
 * Created by Abdalla on 13/10/2016.
 */
@Component(modules = {MainModule.class})
public interface MainComponent {
    void inject(MainFragment fragment);
}
