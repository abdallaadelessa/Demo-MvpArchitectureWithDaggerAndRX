package com.abdallaadelessa.demo.view.main;

import com.abdallaadelessa.core.dagger.AppComponent;
import com.abdallaadelessa.core.presenter.BaseCorePresenter;
import com.abdallaadelessa.demo.AirlineModel;
import com.abdallaadelessa.demo.app.MyApplication;
import com.google.common.reflect.TypeToken;

import java.util.List;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Abdalla on 13/10/2016.
 */

public class MainPresenter extends BaseCorePresenter<IMainView> {
    @Override
    public void loadViewData() {
        final AppComponent appComponent = MyApplication.getAppComponent();
        Observable<List<AirlineModel>> observable = appComponent.getHttpRequestBuilder()
                .tag("aaaaaaa")
                .url("https://www.kayak.com/h/mobileapis/directory/airlines")
                .type(new TypeToken<List<AirlineModel>>(){}.getType())
                .build();
        observable.subscribe(new Action1<List<AirlineModel>>() {
            @Override
            public void call(List<AirlineModel> response) {
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
