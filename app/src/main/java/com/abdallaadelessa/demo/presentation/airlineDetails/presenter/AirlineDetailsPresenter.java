package com.abdallaadelessa.demo.presentation.airlineDetails.presenter;

import android.app.Activity;
import android.content.Context;

import com.abdallaadelessa.core.presenter.BaseCorePresenter;
import com.abdallaadelessa.core.utils.AndroidUtils;
import com.abdallaadelessa.core.utils.UIUtils;
import com.abdallaadelessa.core.utils.ValidationUtils;
import com.abdallaadelessa.demo.R;
import com.abdallaadelessa.demo.app.MyApplication;
import com.abdallaadelessa.demo.data.airline.model.AirlineModel;
import com.abdallaadelessa.demo.domain.airline.useCases.FavouriteAirlinesUseCase;
import com.abdallaadelessa.demo.presentation.airlineDetails.view.IAirlineDetailsView;

import javax.inject.Inject;

/**
 * Created by Abdalla on 23/10/2016.
 */

public class AirlineDetailsPresenter extends BaseCorePresenter<IAirlineDetailsView> {
    private FavouriteAirlinesUseCase favouriteAirlinesUseCase;

    @Inject
    public AirlineDetailsPresenter(FavouriteAirlinesUseCase favouriteAirlinesUseCase) {
        this.favouriteAirlinesUseCase = favouriteAirlinesUseCase;
    }

    // ------------>

    @Override
    public void loadViewData() {
    }

    // ------------>

    public boolean isFavouriteAirline(AirlineModel airlineModel) {
        return favouriteAirlinesUseCase.isFavAirline(airlineModel);
    }

    public void toggleFavouriteAirline(AirlineModel airlineModel) {
        favouriteAirlinesUseCase.toggleFavAirline(airlineModel);
    }

    public void openBrowser(Context context, AirlineModel airlineModel) {
        if (context == null || airlineModel == null) return;
        if (!ValidationUtils.isStringEmpty(airlineModel.getSite())) {
            try {
                AndroidUtils.openBrowser(context, airlineModel.getSite());
            } catch (Exception e) {
                MyApplication.getAppComponent().getLogger().logError(e);
                UIUtils.showToast(context, context.getString(R.string.error_no_browser));
            }
        } else {
            UIUtils.showToast(context, R.string.txt_no_content);
        }
    }

    public void callPhone(Context context, AirlineModel airlineModel) {
        if (context == null || airlineModel == null) return;
        if (!ValidationUtils.isStringEmpty(airlineModel.getPhone())) {
            try {
                AndroidUtils.call(context, airlineModel.getPhone());
            } catch (Exception e) {
                MyApplication.getAppComponent().getLogger().logError(e);
                UIUtils.showToast(context, context.getString(R.string.error_no_dialer));
            }
        } else {
            UIUtils.showToast(context, R.string.txt_no_content);
        }
    }
}
