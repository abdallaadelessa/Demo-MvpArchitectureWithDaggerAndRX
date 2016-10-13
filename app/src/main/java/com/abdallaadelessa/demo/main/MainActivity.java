package com.abdallaadelessa.demo.main;

import android.os.Bundle;

import com.abdallaadelessa.core.view.BaseCoreActivity;
import com.abdallaadelessa.demo.R;

public class MainActivity extends BaseCoreActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content, new MainFragment()).commit();
        }
    }

}
