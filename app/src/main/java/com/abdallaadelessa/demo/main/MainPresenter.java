package com.abdallaadelessa.demo.main;

import com.abdallaadelessa.core.app.BaseCoreApp;
import com.abdallaadelessa.core.presenter.BaseCorePresenter;

/**
 * Created by Abdalla on 13/10/2016.
 */

public class MainPresenter extends BaseCorePresenter<MainFragment> {
    @Override
    public void loadViewData() {
        if (isViewAttached()) {
            getView().showMessage("Hello From Presenter");
            BaseCoreApp.getAppComponent().getLogger().log("Hello From Presenter");
        }
    }
}
