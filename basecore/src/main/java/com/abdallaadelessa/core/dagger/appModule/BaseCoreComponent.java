package com.abdallaadelessa.core.dagger.appModule;

import android.content.Context;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Abdalla on 16/10/2016.
 */
@Singleton
@Component(modules = {BaseCoreModule.class})
public interface BaseCoreComponent {
    Context getContext();

    Toast getToast();

    ExecutorService getExecutorService();
}
