package com.abdallaadelessa.core.view;

import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.abdallaadelessa.core.presenter.BaseCorePresenter;
import com.abdallaadelessa.demo.R;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * Created by abdullah on 12/10/16.
 */

public abstract class BaseCoreActivity<p extends BaseCorePresenter> extends AppCompatActivity {
    private static final int UP_DRAWABLE_ID = R.mipmap.ic_launcher;
    @Inject
    p presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());
        initUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getPresenter() != null) getPresenter().detachView();
    }

    public int getLayoutRes() {
        return R.layout.activity_main;
    }

    // --------------->

    protected void injectComponent() {

    }

    public p getPresenter() {
        return presenter;
    }

    // --------------->

    protected void initUI() {
        ButterKnife.bind(this);
        injectComponent();
        if (getPresenter() != null) getPresenter().attachView(this);
        forceScreenOrientation();
    }

    protected void forceScreenOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
    }

    public void setupToolbar(Toolbar toolbar, String title) {
        setupToolbar(toolbar, title, -1, -1);
    }

    public void setupToolbar(Toolbar toolbar, String title, int upButtonColorId, int logoDrawableId) {
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            // Title
            supportActionBar.setDisplayShowTitleEnabled(true);
            // Up Button
            if (upButtonColorId != -1) {
                Drawable upArrow = ContextCompat.getDrawable(this, UP_DRAWABLE_ID);
                upArrow.setColorFilter(ContextCompat.getColor(this, upButtonColorId), PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(upButtonColorId != -1);
            // Logo
            if (logoDrawableId != -1) {
                supportActionBar.setLogo(logoDrawableId);
            }
            supportActionBar.setDisplayUseLogoEnabled(logoDrawableId != -1);
        }
    }
}
