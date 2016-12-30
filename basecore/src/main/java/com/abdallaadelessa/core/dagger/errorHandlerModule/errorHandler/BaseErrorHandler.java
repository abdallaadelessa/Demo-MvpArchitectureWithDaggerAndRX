package com.abdallaadelessa.core.dagger.errorHandlerModule.errorHandler;

import android.app.Activity;
import android.content.Context;

import com.abdallaadelessa.core.app.BaseCoreApp;
import com.abdallaadelessa.core.model.BaseCoreError;
import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.R;
import com.abdallaadelessa.core.utils.UIUtils;

import java.lang.ref.WeakReference;

/**
 * Created by Abdalla on 16/10/2016.
 */

public abstract class BaseErrorHandler {
    protected WeakReference<Context> contextWeakReference;
    protected BaseAppLogger appLogger;

    public BaseErrorHandler(Context context, BaseAppLogger appLogger) {
        this.contextWeakReference = new WeakReference<>(context);
        this.appLogger = appLogger;
    }

    public BaseAppLogger getAppLogger() {
        return appLogger;
    }

    public String getErrorMessage(Throwable error) {
        if (contextWeakReference == null || contextWeakReference.get() == null) return null;
        String errorMsg = contextWeakReference.get().getString(R.string.txt_unknown_error_occured);
        try {
            if (error instanceof BaseCoreError) {
                if (((BaseCoreError) error).getCode().equalsIgnoreCase(BaseCoreError.CODE_TIMEOUT_ERROR)) {
                    errorMsg = contextWeakReference.get().getString(R.string.txt_timeout);
                } else if (((BaseCoreError) error).getCode().equalsIgnoreCase(BaseCoreError.CODE_NETWORK_ERROR)) {
                    errorMsg = contextWeakReference.get().getString(R.string.txt_no_internet_connection);
                } else if (((BaseCoreError) error).getCode().equalsIgnoreCase(BaseCoreError.CODE_SERVER_ERROR)) {
                    errorMsg = contextWeakReference.get().getString(R.string.txt_server_error);
                } else if (((BaseCoreError) error).getCode().equalsIgnoreCase(BaseCoreError.CODE_BAD_REQUEST_ERROR)) {
                    errorMsg = contextWeakReference.get().getString(R.string.txt_server_error);
                }
            }
        } catch (Exception e) {
            getAppLogger().logError(e, false);
        }
        return errorMsg;
    }
}
