package com.abdallaadelessa.demo.view.main;

import com.abdallaadelessa.core.presenter.BaseCorePresenter;
import com.abdallaadelessa.demo.app.MyApplication;

/**
 * Created by Abdalla on 13/10/2016.
 */

public class MainPresenter extends BaseCorePresenter<IMainView> {
    @Override
    public void loadViewData() {
        MyApplication.getAppComponent().getLogger().log("MainPresenter loadViewData");
    }

    public void displayData(String message) {
        MyApplication.getAppComponent().getLogger().log(message);
        if (isViewAttached()) {
            getView().showToast(message);
        }
    }
}
