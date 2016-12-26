package com.abdallaadelessa.core.utils;

import butterknife.Unbinder;

/**
 * Created by Abdalla on 16/10/2016.
 */

public class ButterKnifeUtils {
    public static void unBindButterKnife(Unbinder unbinder) {
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
