package com.abdallaadelessa.core.utils;

import android.content.Context;

/**
 * Created by Abdalla on 16/10/2016.
 */

public class ResourcesUtils {
    private static final String DRAWABLE_TYPE = "drawable";
    private static final String STRING_TYPE = "string";

    public static int getResIdByName(Context context, String defType, String name) {
        int resource = context.getResources().getIdentifier(name, defType, context.getPackageName());
        return resource;
    }

    public static String getResNameById(Context context, int id) {
        String name = getStringResourceById(context, id);
        String[] imagePath = name.split("/");
        name = imagePath[imagePath.length - 1];
        return name;
    }

    public static int getDrawableIdByName(Context cxt, String name) {
        return getResIdByName(cxt, DRAWABLE_TYPE, name);
    }

    public static int getStringIdByName(Context cxt, String name) {
        return getResIdByName(cxt, STRING_TYPE, name);
    }

    private static String getStringResourceById(Context context, int id) {
        String name;
        name = context.getResources().getString(id);
        return name;
    }
}
