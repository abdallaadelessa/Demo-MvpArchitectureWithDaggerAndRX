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
import android.view.MenuItem;

import com.abdallaadelessa.core.R;
import com.abdallaadelessa.core.app.BaseCoreApp;
import com.abdallaadelessa.core.presenter.BaseCorePresenter;
import com.abdallaadelessa.core.utils.UIUtils;

import java.util.HashMap;

/**
 * Created by abdullah on 12/10/16.
 */

public abstract class BaseCoreActivity<p extends BaseCorePresenter> extends AppCompatActivity implements IBaseView {
    private static final int UP_DRAWABLE_ID = R.drawable.ic_white_up_arrow;
    public static final String KEY_RETAINED_MEMORY_CACHE = "RetainedMemoryCache";
    private p presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            HashMap cacheObject = (HashMap) savedInstanceState.getSerializable(KEY_RETAINED_MEMORY_CACHE);
            if (cacheObject != null) {
                BaseCoreApp.getInstance().getCacheComponent().getRetainedMemoryCache().clearCache();
                BaseCoreApp.getInstance().getCacheComponent().getRetainedMemoryCache().setCacheObject(cacheObject);
            }
        }
        setContentView(getLayoutRes());
        initUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        HashMap cacheObject = BaseCoreApp.getInstance().getCacheComponent().getRetainedMemoryCache().getCacheObject();
        if (cacheObject != null) {
            outState.putSerializable(KEY_RETAINED_MEMORY_CACHE, cacheObject);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getPresenter() != null) getPresenter().detachView();
        presenter = null;
    }

    public int getLayoutRes() {
        return R.layout.activity_with_content;
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

    }

    @Override
    public void handleError(Throwable throwable) {

    }

    @Override
    public void handleNoData() {

    }

    // --------------->

    protected void initUI() {
        presenter = initPresenter();
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
