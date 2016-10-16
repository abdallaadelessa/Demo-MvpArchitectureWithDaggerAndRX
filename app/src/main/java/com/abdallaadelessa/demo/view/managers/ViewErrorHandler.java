package com.abdallaadelessa.demo.view.managers;

import android.app.Activity;

import com.abdallaadelessa.android.dataplaceholder.DataPlaceHolder;
import com.abdallaadelessa.core.dagger.errorHandlerModule.errorHandler.BaseErrorHandler;
import com.abdallaadelessa.core.utils.UIUtils;
import com.abdallaadelessa.demo.R;

import javax.inject.Inject;

/**
 * Created by abdalla on 29/07/15.
 */
public class ViewErrorHandler {
    @Inject
    static BaseErrorHandler baseErrorHandler;

    public static void handleError(DataPlaceHolder dataPlaceHolder, Throwable error, Runnable runnable) {
        if (dataPlaceHolder == null) return;
        String errorMessage = baseErrorHandler.getErrorMessage(error);
        dataPlaceHolder.showMessage(errorMessage, -1, dataPlaceHolder.getContext().getString(R.string.txt_retry), runnable);
    }

    public static void handleError(Activity activity, Throwable error) {
        if (activity == null) return;
        String errorMessage = baseErrorHandler.getErrorMessage(error);
        UIUtils.showToast(activity, errorMessage);
    }
}
