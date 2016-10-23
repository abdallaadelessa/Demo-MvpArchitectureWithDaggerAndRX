package com.abdallaadelessa.demo.presentation.splash.view;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.abdallaadelessa.core.presenter.BaseCorePresenter;
import com.abdallaadelessa.core.view.BaseCoreActivity;
import com.abdallaadelessa.demo.R;
import com.abdallaadelessa.demo.presentation.listAirlines.view.ListAirlinesActivity;

import java.lang.ref.WeakReference;

public class SplashActivity extends BaseCoreActivity {
    private static final int DELAY_MILLIS = 500;
    private WeakReference<SplashActivity> splashActivityWeakReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splashActivityWeakReference = new WeakReference<>(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SplashActivity splashActivity = splashActivityWeakReference != null ? splashActivityWeakReference.get() : null;
                if (splashActivity == null || splashActivity.isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && splashActivity.isDestroyed()))
                    return;
                ListAirlinesActivity.startListAirLinesActivityAsNewTask(splashActivity);
                splashActivity.finish();
            }
        }, DELAY_MILLIS);
    }

    @Override
    protected BaseCorePresenter initPresenter() {
        return null;
    }
}
