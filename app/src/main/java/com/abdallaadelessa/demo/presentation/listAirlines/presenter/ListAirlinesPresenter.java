package com.abdallaadelessa.demo.presentation.listAirlines.presenter;

import android.view.View;

import com.abdallaadelessa.core.app.BaseCoreApp;
import com.abdallaadelessa.core.presenter.BaseCorePresenter;
import com.abdallaadelessa.demo.R;
import com.abdallaadelessa.demo.app.MyApplication;
import com.abdallaadelessa.demo.data.airline.model.AirlineModel;
import com.abdallaadelessa.demo.data.airline.model.FavouriteAirLineStateChanged;
import com.abdallaadelessa.demo.domain.airline.useCases.FavouriteAirlinesUseCase;
import com.abdallaadelessa.demo.domain.airline.useCases.ListAirlinesUseCase;
import com.abdallaadelessa.demo.presentation.listAirlines.view.IListAirlinesView;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Abdalla on 13/10/2016.
 */

public class ListAirlinesPresenter extends BaseCorePresenter<IListAirlinesView> {
    private ListAirlinesUseCase listAirlinesUseCase;
    private FavouriteAirlinesUseCase favouriteAirlinesUseCase;
    private List<AirlineModel> airlineModels;

    @Inject
    public ListAirlinesPresenter(ListAirlinesUseCase listAirlinesUseCase, FavouriteAirlinesUseCase favouriteAirlinesUseCase) {
        this.listAirlinesUseCase = listAirlinesUseCase;
        this.favouriteAirlinesUseCase = favouriteAirlinesUseCase;
        registerEventBus();
    }

    // -------------------->

    private void registerEventBus() {
        Subscription subscription = BaseCoreApp.getAppComponent().getGlobalEventBus().toObservable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (!isViewAttached()) return;
                if (o instanceof FavouriteAirLineStateChanged) {
                    getView().refreshData();
                    filterAirlinesByTypeIfIsFavouriteOnlyOptionSelected(true);
                }
            }
        });
        addSubscription(subscription);
    }

    private void showData(List<AirlineModel> airlineModels) {
        if (!isViewAttached()) return;
        if (airlineModels == null || airlineModels.isEmpty()) {
            getView().showNoDataPlaceHolder();
        } else {
            getView().loadData(airlineModels);
        }
    }

    private void filterAirlinesByTypeIfIsFavouriteOnlyOptionSelected(final boolean silent) {
        if (!isViewAttached()) return;
        if (getView().isFavouriteOnlyOptionSelected()) filterAirlinesByType(silent);
    }

    // -------------------->

    @Override
    public void loadViewData() {
        if (!isViewAttached()) return;
        getView().enableMenu(false);
        getView().showProgress(true);
        Subscription subscription = listAirlinesUseCase.listAirlines().subscribe(new Action1<List<AirlineModel>>() {
            @Override
            public void call(List<AirlineModel> airlineModels) {
                ListAirlinesPresenter.this.airlineModels = airlineModels;
                if (!isViewAttached()) return;
                getView().setTitle(R.string.txt_kayak_airlines_all);
                getView().enableMenu(true);
                getView().showProgress(false);
                showData(airlineModels);
                filterAirlinesByTypeIfIsFavouriteOnlyOptionSelected(true);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                if (!isViewAttached()) return;
                getView().showProgress(false);
                getView().showError(throwable);
            }
        });
        addSubscription(subscription);
    }

    public void filterAirlinesByType(final boolean silent) {
        if (!isViewAttached()) return;
        if (!silent) getView().showProgress(true);
        final boolean favOnly = getView().isFavouriteOnlyOptionSelected();
        Subscription subscription = favouriteAirlinesUseCase.filterAirlinesAsObservable(airlineModels, favOnly)
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread()).subscribe(new Action1<List<AirlineModel>>() {
                    @Override
                    public void call(List<AirlineModel> airlineModels) {
                        if (!isViewAttached()) return;
                        if (!silent) getView().showProgress(false);
                        getView().setTitle(favOnly ? R.string.txt_kayak_airlines_fav_only : R.string.txt_kayak_airlines_all);
                        showData(airlineModels);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (!isViewAttached()) return;
                        if (!silent) getView().showProgress(false);
                        getView().showError(throwable);
                    }
                });
        addSubscription(subscription);
    }

    public boolean isFavouriteAirline(AirlineModel airlineModel) {
        return favouriteAirlinesUseCase.isFavAirline(airlineModel);
    }

    public void favouriteAirline(AirlineModel airlineModel, boolean isFav) {
        favouriteAirlinesUseCase.favAirline(airlineModel, isFav);
    }

    public void goToAirlineDetails(View tvTitle, View ivIcon, AirlineModel airlineModel) {
        if (!isViewAttached()) return;
        getView().goToAirlineDetails(tvTitle, ivIcon, airlineModel);
    }

    // -------------------->
}
