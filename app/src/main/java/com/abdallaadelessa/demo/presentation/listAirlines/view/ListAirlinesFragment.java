package com.abdallaadelessa.demo.presentation.listAirlines.view;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.abdallaadelessa.android.dataplaceholder.DataPlaceHolder;
import com.abdallaadelessa.core.view.BaseCoreFragment;
import com.abdallaadelessa.demo.R;
import com.abdallaadelessa.demo.data.airline.model.AirlineModel;
import com.abdallaadelessa.demo.presentation.airlineDetails.view.AirLineDetailsActivity;
import com.abdallaadelessa.demo.presentation.listAirlines.presenter.DaggerListAirlinesComponent;
import com.abdallaadelessa.demo.presentation.listAirlines.presenter.ListAirlinesPresenter;
import com.abdallaadelessa.demo.presentation.managers.ViewErrorHandler;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Abdalla on 13/10/2016.
 */

public class ListAirlinesFragment extends BaseCoreFragment<ListAirlinesPresenter> implements IListAirlinesView, AirLinesRVAdapter.IAirLinesRVAdapter, SwipeRefreshLayout.OnRefreshListener {
    private static final int TYPE_FAV_ONLY = 1;
    @BindView(R.id.vgRefreshLayout)
    SwipeRefreshLayout vgRefreshLayout;
    @BindView(R.id.dataPlaceHolder)
    DataPlaceHolder dataPlaceHolder;
    @BindView(R.id.rvList)
    RecyclerView rvList;
    private int currentSelectedFiltrationIndex;
    private boolean isMenuEnabled;
    private AirLinesRVAdapter airLinesRVAdapter;

    @Override
    protected ListAirlinesPresenter initPresenter() {
        return DaggerListAirlinesComponent.create().getListAirlinesPresenter();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_list_airlines;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected Unbinder initUI(View view) {
        super.initUI(view);
        Unbinder unbinder = ButterKnife.bind(this, view);
        //-->
        dataPlaceHolder.setProgressWheelColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        dataPlaceHolder.setActionButtonBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        dataPlaceHolder.setMessageTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        airLinesRVAdapter = new AirLinesRVAdapter();
        airLinesRVAdapter.setIAirLinesRVAdapter(this);
        vgRefreshLayout.setOnRefreshListener(this);
        rvList.setAdapter(airLinesRVAdapter);
        //-->
        if (isPresenterAttached()) getPresenter().loadViewData();
        //-->
        return unbinder;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_list_air_lines, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionFilter) {
            showFilterAirLinesDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.actionFilter).setVisible(isMenuEnabled);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isPresenterAttached()) getPresenter().detachView();
    }

    @Override
    public void onRefresh() {
        vgRefreshLayout.setRefreshing(false);
        if (isPresenterAttached()) getPresenter().startListAirlinesRequest();
    }

    // ------------> Adapter Listener

    @Override
    public boolean isFav(AirlineModel airLineModel) {
        return isPresenterAttached() && getPresenter().isFavouriteAirline(airLineModel);
    }

    @Override
    public void onFavouriteAirLine(AirlineModel airlineModel, boolean isFav) {
        if (isPresenterAttached()) getPresenter().favouriteAirline(airlineModel, isFav);
    }

    @Override
    public void onAirLineClicked(View tvTitle, View ivIcon, AirlineModel airlineModel) {
        if (isPresenterAttached()) getPresenter().goToAirlineDetails(tvTitle, ivIcon, airlineModel);
    }

    // ------------------> View Methods

    @Override
    public void showProgress(boolean show) {
        if (show) {
            dataPlaceHolder.showProgress();
        } else {
            dataPlaceHolder.dismissAll();
        }
    }

    @Override
    public void handleNoData() {
        dataPlaceHolder.showMessage(getString(com.abdallaadelessa.core.R.string.txt_no_content));
    }

    @Override
    public void enableMenu(boolean enable) {
        isMenuEnabled = enable;
        getActivity().supportInvalidateOptionsMenu();
    }

    @Override
    public boolean isFavouriteOnlyOptionSelected() {
        return currentSelectedFiltrationIndex == TYPE_FAV_ONLY;
    }

    @Override
    public void loadData(List<AirlineModel> data) {
        if (airLinesRVAdapter != null) airLinesRVAdapter.addAll(true, data);
    }

    @Override
    public void refreshData() {
        if (airLinesRVAdapter != null) airLinesRVAdapter.notifyDataSetChanged();
    }

    public void showFilterAirLinesDialog() {
        String title = getString(R.string.filter_airlines);
        final String[] menuItems = {getString(R.string.txt_all), getString(R.string.txt_favourite_air_lines_only)};
        String buttonTxt = getString(R.string.filter);
        new MaterialDialog.Builder(getActivity()).title(title).items(menuItems).itemsCallbackSingleChoice
                (currentSelectedFiltrationIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        currentSelectedFiltrationIndex = which;
                        if (isPresenterAttached()) getPresenter().filterAirlinesByType(false);
                        return true;
                    }
                }).positiveText(buttonTxt).show();
    }

    public void goToAirlineDetails(View tvTitle, View ivIcon, AirlineModel airlineModel) {
        AirLineDetailsActivity.startAirLineDetailsActivity(getActivity(), tvTitle, ivIcon, airlineModel);
    }

}

