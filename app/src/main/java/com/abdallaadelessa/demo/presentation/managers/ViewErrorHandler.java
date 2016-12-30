package com.abdallaadelessa.demo.presentation.managers;

import android.app.Activity;
import android.content.Context;

import com.abdallaadelessa.android.dataplaceholder.DataPlaceHolder;
import com.abdallaadelessa.core.app.BaseCoreApp;
import com.abdallaadelessa.core.model.BaseCoreError;
import com.abdallaadelessa.core.utils.UIUtils;
import com.abdallaadelessa.core.view.IBaseView;
import com.abdallaadelessa.demo.R;
import com.abdallaadelessa.demo.app.MyApplication;

/**
 * Created by abdalla on 29/07/15.
 */
public class ViewErrorHandler {

    public static String getErrorMessage(Context context, Throwable error) {
        if (context == null || error == null) return null;
        String errorMsg = context.getString(com.abdallaadelessa.core.R.string.txt_unknown_error_occured);
        try {
            if (error instanceof BaseCoreError) {
                if (((BaseCoreError) error).getCode().equalsIgnoreCase(BaseCoreError.CODE_TIMEOUT_ERROR)) {
                    errorMsg = context.getString(com.abdallaadelessa.core.R.string.txt_timeout);
                } else if (((BaseCoreError) error).getCode().equalsIgnoreCase(BaseCoreError.CODE_NETWORK_ERROR)) {
                    errorMsg = context.getString(com.abdallaadelessa.core.R.string.txt_no_internet_connection);
                } else if (((BaseCoreError) error).getCode().equalsIgnoreCase(BaseCoreError.CODE_SERVER_ERROR)) {
                    errorMsg = context.getString(com.abdallaadelessa.core.R.string.txt_server_error);
                } else if (((BaseCoreError) error).getCode().equalsIgnoreCase(BaseCoreError.CODE_BAD_REQUEST_ERROR)) {
                    errorMsg = context.getString(com.abdallaadelessa.core.R.string.txt_server_error);
                }
            }
        } catch (Exception e) {
            MyApplication.getInstance().getLoggerComponent().getLogger().logError(e, false);
        }
        return errorMsg;
    }

    public static void handleError(DataPlaceHolder dataPlaceHolder, Throwable error, Runnable runnable) {
        if (dataPlaceHolder == null || dataPlaceHolder.getContext() == null) return;
        String errorMessage = getErrorMessage(dataPlaceHolder.getContext(), error);
        dataPlaceHolder.showMessage(errorMessage, -1, dataPlaceHolder.getContext().getString(R.string.txt_retry), runnable);
    }

    public static void handleError(Activity activity, Throwable error) {
        if (activity == null || activity.isFinishing()) return;
        String errorMessage = getErrorMessage(activity, error);
        UIUtils.showToast(activity, errorMessage);
    }
}
