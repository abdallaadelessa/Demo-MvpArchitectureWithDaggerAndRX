package com.abdallaadelessa.core.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abdallaadelessa.core.presenter.BaseCorePresenter;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by abdullah on 12/10/16.
 */

public abstract class BaseCoreFragment<p extends BaseCorePresenter> extends Fragment {
    private Unbinder unbinder;
    @Inject
    p presenter;

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
            initUI(view);
            return view;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) unbinder.unbind();
        if (getPresenter() != null) getPresenter().detachView();
    }

    // --------------->

    protected void injectComponent() {

    }

    public p getPresenter() {
        return presenter;
    }

    // --------------->

    protected abstract int getLayoutRes();

    protected void initUI(View view) {
        unbinder = ButterKnife.bind(this, view);
        injectComponent();
        if (getPresenter() != null) getPresenter().attachView(this);
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
