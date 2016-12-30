package com.abdallaadelessa.demo.presentation.airlineDetails.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;

import com.abdallaadelessa.core.presenter.BaseCorePresenter;
import com.abdallaadelessa.core.view.BaseCoreActivity;
import com.abdallaadelessa.demo.R;
import com.abdallaadelessa.demo.data.airline.model.AirlineModel;
import com.abdallaadelessa.demo.presentation.managers.ViewErrorHandler;

/**
 * Created by abdullah on 01/10/16.
 */
public class AirLineDetailsActivity extends BaseCoreActivity {
    public static void startAirLineDetailsActivity(Context context, View ivTitle, View ivIcon, AirlineModel airlineModel) {
        Intent starter = new Intent(context, AirLineDetailsActivity.class);
        starter.putExtras(AirLinesDetailsFragment.prepareBundle(airlineModel));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Pair<View, String> pair1 = Pair.create(ivIcon, context.getString(R.string.trans_logo));
            Pair<View, String> pair2 = Pair.create(ivTitle, context.getString(R.string.trans_title));
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((Activity) context, pair1, pair2);
            context.startActivity(starter, options.toBundle());
        } else {
            context.startActivity(starter);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, AirLinesDetailsFragment.newInstance(getIntent().getExtras()), "AirLinesDetailsFragment").commitAllowingStateLoss();
        }
    }

    @Override
    public void handleError(Throwable throwable) {
        ViewErrorHandler.handleError(this,throwable);
    }

    @Override
    protected BaseCorePresenter initPresenter() {
        return null;
    }
}
