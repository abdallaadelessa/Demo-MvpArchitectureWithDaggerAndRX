package com.abdallaadelessa.demo.presentation.managers;

import android.app.Activity;

import com.abdallaadelessa.android.dataplaceholder.DataPlaceHolder;
import com.abdallaadelessa.core.app.BaseCoreApp;
import com.abdallaadelessa.core.utils.UIUtils;
import com.abdallaadelessa.demo.R;

/**
 * Created by abdalla on 29/07/15.
 */
public class ViewErrorHandler {

    public static void handleError(DataPlaceHolder dataPlaceHolder, Throwable error, Runnable runnable) {
        if (dataPlaceHolder == null) return;
        String errorMessage = BaseCoreApp.getAppComponent().getErrorHandler().getErrorMessage(error);
        dataPlaceHolder.showMessage(errorMessage, -1, dataPlaceHolder.getContext().getString(R.string.txt_retry), runnable);
    }

    public static void handleError(Activity activity, Throwable error) {
        if (activity == null) return;
        String errorMessage = BaseCoreApp.getAppComponent().getErrorHandler().getErrorMessage(error);
        UIUtils.showToast(activity, errorMessage);
    }
}
