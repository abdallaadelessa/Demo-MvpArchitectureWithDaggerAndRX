package com.abdallaadelessa.demo.presentation.presenter.main;

import com.abdallaadelessa.core.dagger.AppComponent;
import com.abdallaadelessa.core.presenter.BaseCorePresenter;
import com.abdallaadelessa.demo.app.MyApplication;
import com.abdallaadelessa.demo.data.airline.model.AirlineModel;
import com.abdallaadelessa.demo.domain.airline.AirlineUseCases;
import com.abdallaadelessa.demo.domain.airline.di.DaggerAirlineUseCasesComponent;

import java.util.List;

import rx.functions.Action1;

/**
 * Created by Abdalla on 13/10/2016.
 */

public class MainPresenter extends BaseCorePresenter<IMainView> {
    private AirlineUseCases airlineUseCases;

    public MainPresenter() {
        airlineUseCases = DaggerAirlineUseCasesComponent.create().getAirlineUseCases();
    }

    @Override
    public void loadViewData() {
        final AppComponent appComponent = MyApplication.getAppComponent();
        airlineUseCases.getAirlines().subscribe(new Action1<List<AirlineModel>>() {
            @Override
            public void call(List<AirlineModel> airlineModels) {
                appComponent.getLogger().log("Success");
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                appComponent.getLogger().log("Failure");
            }
        });
    }

    public void displayData(String message) {
        MyApplication.getAppComponent().getLogger().log(message);
        if (isViewAttached()) {
            getView().showToast(message);
        }
    }
}
