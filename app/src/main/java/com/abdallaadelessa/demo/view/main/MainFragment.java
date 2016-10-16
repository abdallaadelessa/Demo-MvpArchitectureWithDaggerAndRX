package com.abdallaadelessa.demo.view.main;

import android.widget.Button;
import android.widget.Toast;

import com.abdallaadelessa.core.app.BaseCoreApp;
import com.abdallaadelessa.core.model.MessageError;
import com.abdallaadelessa.core.view.BaseCoreFragment;
import com.abdallaadelessa.demo.R;
import com.abdallaadelessa.demo.view.managers.ViewErrorHandler;

import butterknife.BindView;
import butterknife.OnClick;

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
        ViewErrorHandler.handleError(getActivity(),new MessageError("Error"));
        Toast toast = BaseCoreApp.getAppComponent().getToast();
        toast.setText("Message : " + title);
        toast.show();
    }

    // ------------>

    @OnClick(R.id.button)
    public void onClick() {
        getPresenter().loadViewData();
    }
}

