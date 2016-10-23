package com.abdallaadelessa.demo.presentation.airlineDetails.view;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.abdallaadelessa.core.utils.ImageLoaderManager;
import com.abdallaadelessa.core.utils.ValidationUtils;
import com.abdallaadelessa.core.view.BaseCoreActivity;
import com.abdallaadelessa.core.view.BaseCoreFragment;
import com.abdallaadelessa.demo.R;
import com.abdallaadelessa.demo.data.airline.model.AirlineModel;
import com.abdallaadelessa.demo.data.airline.util.AirlinesUtils;
import com.abdallaadelessa.demo.presentation.airlineDetails.presenter.AirlineDetailsPresenter;
import com.abdallaadelessa.demo.presentation.airlineDetails.presenter.DaggerAirlineDetailsComponent;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by abdullah on 02/10/16.
 */
public class AirLinesDetailsFragment extends BaseCoreFragment<AirlineDetailsPresenter> {
    private static final String KEY_AIR_LINE_MODEL = "KEY_AIR_LINE_MODEL";
    @BindView(R.id.ivIcon)
    ImageView ivIcon;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvSite)
    TextView tvSite;
    @BindView(R.id.tvPhone)
    TextView tvPhone;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    //-->
    private AirlineModel airlineModel;


    public static AirLinesDetailsFragment newInstance(Bundle bundle) {
        AirLinesDetailsFragment airLinesDetailsFragment = new AirLinesDetailsFragment();
        airLinesDetailsFragment.setArguments(bundle);
        return airLinesDetailsFragment;
    }

    public static Bundle prepareBundle(AirlineModel airLineModel) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_AIR_LINE_MODEL, Parcels.wrap(airLineModel));
        return bundle;
    }

    // ------------------->

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        airlineModel = getArguments() != null && getArguments().containsKey(KEY_AIR_LINE_MODEL) ?
                (AirlineModel) Parcels.unwrap(getArguments().getParcelable(KEY_AIR_LINE_MODEL)) : null;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_air_line_details;
    }


    @Override
    protected AirlineDetailsPresenter initPresenter() {
        return DaggerAirlineDetailsComponent.create().getAirlineDetailsPresenter();
    }

    // ------------------->

    @Override
    protected Unbinder initUI(View view) {
        super.initUI(view);
        if (!isPresenterAttached()) return null;
        Unbinder unbinder = ButterKnife.bind(this, view);
        //--->
        if (getActivity() instanceof BaseCoreActivity) {
            ((BaseCoreActivity) getActivity()).setupToolbar(toolbar, "", android.R.color.white, -1);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
        }
        tvTitle.setText(airlineModel.getName());
        tvPhone.setText(!ValidationUtils.isStringEmpty(airlineModel.getPhone()) ? airlineModel.getPhone() : getString(R.string.txt_no_content));
        tvSite.setText(!ValidationUtils.isStringEmpty(airlineModel.getSite()) ? airlineModel.getSite() : getString(R.string.txt_no_content));
        int iconSize = (int) getResources().getDimension(R.dimen.air_line_details_icon_size);
        ImageLoaderManager.getDefaultBuilder().path(getActivity(), AirlinesUtils.getFullLogoUrl(airlineModel)).imageView(ivIcon).widthAndHeight(iconSize).load();
        fab.setImageResource(getPresenter().isFavouriteAirline(airlineModel) ? R.drawable.ic_star_selected : R.drawable.ic_white_star_not_selected);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivIcon.setTransitionName(getString(R.string.trans_logo));
            tvTitle.setTransitionName(getString(R.string.trans_title));
        }
        //--->
        return unbinder;
    }

    // ------------------->

    @OnClick(R.id.fab)
    public void onFabClicked() {
        if (!isPresenterAttached()) return;
        getPresenter().toggleFavouriteAirline(airlineModel);
        fab.setImageResource(getPresenter().isFavouriteAirline(airlineModel) ? R.drawable.ic_star_selected : R.drawable.ic_white_star_not_selected);
    }

    @OnClick(R.id.vgWebsite)
    public void onWebsiteClicked() {
        if (!isPresenterAttached()) return;
        getPresenter().openBrowser(getActivity(), airlineModel);
    }

    @OnClick(R.id.vgPhone)
    public void onPhoneClicked() {
        if (!isPresenterAttached()) return;
        getPresenter().callPhone(getActivity(), airlineModel);
    }
}
