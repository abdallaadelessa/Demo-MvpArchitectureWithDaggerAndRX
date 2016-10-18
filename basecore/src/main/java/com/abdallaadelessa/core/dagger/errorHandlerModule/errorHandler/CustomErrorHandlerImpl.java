package com.abdallaadelessa.core.dagger.errorHandlerModule.errorHandler;

import android.content.Context;

import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.volley.VolleyRequestManager;
import com.abdallaadelessa.core.R;

/**
 * Created by Abdalla on 16/10/2016.
 */

public class CustomErrorHandlerImpl extends BaseErrorHandler {

    public CustomErrorHandlerImpl(Context context, BaseAppLogger appLogger) {
        super(context, appLogger);
    }

    @Override
    public String getErrorMessage(Throwable error) {
        if (contextWeakReference == null || contextWeakReference.get() == null) return null;
        if (VolleyRequestManager.isVolleyError(error)) {
            String errorMsg = contextWeakReference.get().getString(R.string.txt_unknown_error_occured);
            if (VolleyRequestManager.isTimeoutError(error)) {
                errorMsg = contextWeakReference.get().getString(R.string.txt_timeout);
            } else if (VolleyRequestManager.isNetworkError(error)) {
                errorMsg = contextWeakReference.get().getString(R.string.txt_no_internet_connection);
            } else if (VolleyRequestManager.isServerError(error)) {
                errorMsg = contextWeakReference.get().getString(R.string.txt_server_error);
            }
            return errorMsg;
        } else {
            return super.getErrorMessage(error);
        }
    }
}
