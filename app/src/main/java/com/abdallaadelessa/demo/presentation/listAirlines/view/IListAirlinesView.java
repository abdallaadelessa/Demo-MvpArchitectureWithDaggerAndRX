package com.abdallaadelessa.demo.presentation.listAirlines.view;

import android.view.View;

import com.abdallaadelessa.core.view.IBaseView;
import com.abdallaadelessa.demo.data.airline.model.AirlineModel;

import java.util.List;

/**
 * Created by Abdalla on 16/10/2016.
 */

public interface IListAirlinesView extends IBaseView {
    void showProgress(boolean show);

    void showError(Throwable throwable);

    void showNoDataPlaceHolder();

    void setTitle(int title);

    void enableMenu(boolean enable);

    boolean isFavouriteOnlyOptionSelected();

    void loadData(List<AirlineModel> data);

    void refreshData();

    void showFilterAirLinesDialog();

    void goToAirlineDetails(View tvTitle, View ivIcon, AirlineModel airlineModel);
}
