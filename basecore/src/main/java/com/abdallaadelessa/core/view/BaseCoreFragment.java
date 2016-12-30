package com.abdallaadelessa.core.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abdallaadelessa.core.app.BaseCoreApp;
import com.abdallaadelessa.core.presenter.BaseCorePresenter;
import com.abdallaadelessa.core.utils.ButterKnifeUtils;
import com.abdallaadelessa.core.utils.UIUtils;

import butterknife.Unbinder;

/**
 * Created by abdullah on 12/10/16.
 */

public abstract class BaseCoreFragment<p extends BaseCorePresenter> extends Fragment implements IBaseView {
    private Unbinder unbinder;
    private p presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getParentFragment() != null) {
            setRetainInstance(false);
        } else {
            setRetainInstance(forceCanRetainInstance());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getLayoutRes() != -1) {
            View view = inflater.inflate(getLayoutRes(), container, false);
            unbinder = initUI(view);
            return view;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getPresenter() != null) getPresenter().detachView();
        presenter = null;
        ButterKnifeUtils.unBindButterKnife(unbinder);
    }

    // --------------->

    protected abstract p initPresenter();

    public p getPresenter() {
        return presenter;
    }

    public boolean isPresenterAttached() {
        return getPresenter() != null;
    }

    // --------------->

    @Override
    public void showProgress(boolean show) {
        if (getActivity() == null || !(getActivity() instanceof IBaseView)) return;
        ((IBaseView) getActivity()).showProgress(show);
    }

    @Override
    public void handleError(Throwable throwable) {
        if (getActivity() == null || !(getActivity() instanceof IBaseView)) return;
        ((IBaseView) getActivity()).handleError(throwable);
    }

    @Override
    public void handleNoData() {
        if (getActivity() == null || !(getActivity() instanceof IBaseView)) return;
        ((IBaseView) getActivity()).handleNoData();
    }

    // --------------->

    protected abstract int getLayoutRes();

    protected Unbinder initUI(View view) {
        presenter = initPresenter();
        if (getPresenter() != null) getPresenter().attachView(this);
        return null;
    }

    protected boolean forceCanRetainInstance() {
        return true;
    }

    public void setTitle(String title) {
        if (getActivity() != null && getActivity() instanceof BaseCoreActivity) {
            ActionBar supportActionBar = ((BaseCoreActivity) getActivity()).getSupportActionBar();
            if (supportActionBar != null) supportActionBar.setTitle(title);
        }
    }

    public void setTitle(int titleResId) {
        setTitle(getString(titleResId));
    }

}
