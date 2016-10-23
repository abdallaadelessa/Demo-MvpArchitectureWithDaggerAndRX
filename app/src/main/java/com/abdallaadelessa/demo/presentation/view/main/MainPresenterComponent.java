package com.abdallaadelessa.demo.presentation.view.main;

import dagger.Component;

/**
 * Created by Abdalla on 23/10/2016.
 */
@Component(modules = MainPresenterModule.class)
public interface MainPresenterComponent {
    MainPresenter getMainPresenter();
}
