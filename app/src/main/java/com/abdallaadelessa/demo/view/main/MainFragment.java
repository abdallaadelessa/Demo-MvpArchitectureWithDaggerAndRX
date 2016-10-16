package com.abdallaadelessa.demo.view.main;

import android.widget.Button;

import com.abdallaadelessa.core.app.BaseCoreApp;
import com.abdallaadelessa.core.view.BaseCoreFragment;
import com.abdallaadelessa.demo.R;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by Abdalla on 13/10/2016.
 */

public class MainFragment extends BaseCoreFragment<MainPresenter> {
    @BindView(R.id.button)
    Button button;

    @Override
    protected void injectComponent() {
        DaggerMainComponent.create().inject(this);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_main;
    }

    // ------------>

    public void showMessage(String title) {
        BaseCoreApp.getAppComponent().getHttpRequestBuilder()
                .url("https://www.kayak.com/h/mobileapis/directory/airlines")
                .tag("Test")
                .GET()
                .<String>build().subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                BaseCoreApp.getAppComponent().getLogger().log("Success");
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                BaseCoreApp.getAppComponent().getLogger().log("Failure");
            }
        });
    }

    // ------------>

    @OnClick(R.id.button)
    public void onClick() {
        getPresenter().loadViewData();
    }
}

