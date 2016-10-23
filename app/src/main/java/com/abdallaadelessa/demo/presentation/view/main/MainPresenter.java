package com.abdallaadelessa.demo.presentation.view.main;

import com.abdallaadelessa.core.dagger.AppComponent;
import com.abdallaadelessa.core.presenter.BaseCorePresenter;
import com.abdallaadelessa.demo.app.MyApplication;
import com.abdallaadelessa.demo.data.airline.model.AirlineModel;
import com.abdallaadelessa.demo.domain.airline.ListAirlinesUseCase;

import java.util.List;

import javax.inject.Inject;

import rx.functions.Action1;

/**
 * Created by Abdalla on 13/10/2016.
 */

public class MainPresenter extends BaseCorePresenter<IMainView> {
    ListAirlinesUseCase listAirlinesUseCase;

    @Inject
    public MainPresenter(ListAirlinesUseCase listAirlinesUseCase) {
        this.listAirlinesUseCase = listAirlinesUseCase;
    }

    @Override
    public void loadViewData() {
        final AppComponent appComponent = MyApplication.getAppComponent();
        listAirlinesUseCase.listAirlines().subscribe(new Action1<List<AirlineModel>>() {
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
