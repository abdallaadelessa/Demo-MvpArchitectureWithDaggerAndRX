package com.abdallaadelessa.demo.app;

import com.abdallaadelessa.core.app.BaseCoreApp;
import com.abdallaadelessa.demo.BuildConfig;

/**
 * Created by Abdalla on 16/10/2016.
 */

public class MyApplication extends BaseCoreApp {

    public boolean isReleaseBuildType() {
        return BuildConfig.BUILD_TYPE.equalsIgnoreCase("release");
    }

    public boolean isDebugBuildType() {
        return BuildConfig.BUILD_TYPE.equalsIgnoreCase("debug");
    }
}
