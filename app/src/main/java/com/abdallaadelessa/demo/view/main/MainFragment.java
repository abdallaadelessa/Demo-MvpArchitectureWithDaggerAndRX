package com.abdallaadelessa.demo.view.main;

import android.view.View;
import android.widget.EditText;

import com.abdallaadelessa.core.utils.UIUtils;
import com.abdallaadelessa.core.view.BaseCoreFragment;
import com.abdallaadelessa.demo.R;
import com.abdallaadelessa.demo.view.main.dagger.DaggerMainComponent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Abdalla on 13/10/2016.
 */

public class MainFragment extends BaseCoreFragment<MainPresenter> implements IMainView {
    @BindView(R.id.editText)
    EditText editText;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_main;
    }

    @Override
    protected void injectComponent() {
        DaggerMainComponent.create().inject(this);
    }

    @Override
    protected Unbinder initUI(View view) {
        super.initUI(view);
        Unbinder unbinder = ButterKnife.bind(this, view);
        if (isPresenterAttached()) getPresenter().loadViewData();
        return unbinder;
    }

    // ------------>

    @Override
    public void showToast(String title) {
        UIUtils.showToast(getActivity(), title);
    }

    // ------------>

    @OnClick(R.id.button)
    public void onClick() {
        if (isPresenterAttached()) getPresenter().displayData(editText.getText().toString());
    }
}

