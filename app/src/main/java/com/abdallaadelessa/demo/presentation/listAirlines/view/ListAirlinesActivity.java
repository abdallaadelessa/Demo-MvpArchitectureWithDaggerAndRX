package com.abdallaadelessa.demo.presentation.listAirlines.view;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;

import com.abdallaadelessa.core.presenter.BaseCorePresenter;
import com.abdallaadelessa.core.utils.AndroidUtils;
import com.abdallaadelessa.core.view.BaseCoreActivity;
import com.abdallaadelessa.demo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListAirlinesActivity extends BaseCoreActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public static void startListAirLinesActivityAsNewTask(FragmentActivity activity) {
        AndroidUtils.clearStackAndStartNewActivity(activity, ListAirlinesActivity.class);
    }

    //------------->

    @Override
    public int getLayoutRes() {
        return R.layout.activity_toolbar_with_content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            ButterKnife.bind(this);
            setupToolbar(toolbar, "");
            getSupportFragmentManager().beginTransaction().replace(R.id.content, new ListAirlinesFragment()).commit();
        }
    }

    //------------->

    @Override
    protected BaseCorePresenter initPresenter() {
        return null;
    }
}
