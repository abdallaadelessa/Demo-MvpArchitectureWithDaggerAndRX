package com.abdallaadelessa.core.dagger.errorHandlerModule.errorHandler;

import android.content.Context;

import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.R;
import com.abdallaadelessa.core.model.BaseCoreError;

/**
 * Created by Abdalla on 16/10/2016.
 */

public class CustomErrorHandlerImpl extends BaseErrorHandler {

    public CustomErrorHandlerImpl(Context context, BaseAppLogger appLogger) {
        super(context, appLogger);
    }

    @Override
    public String getErrorMessage(Throwable error) {
        if(contextWeakReference == null || contextWeakReference.get() == null) return null;
        String errorMsg = contextWeakReference.get().getString(R.string.txt_unknown_error_occured);
        if(error instanceof BaseCoreError) {
            if(((BaseCoreError) error).getCode().equalsIgnoreCase(BaseCoreError.CODE_TIMEOUT_ERROR)) {
                errorMsg = contextWeakReference.get().getString(R.string.txt_timeout);
            }
            else if(((BaseCoreError) error).getCode().equalsIgnoreCase(BaseCoreError.CODE_NETWORK_ERROR)) {
                errorMsg = contextWeakReference.get().getString(R.string.txt_no_internet_connection);
            }
            else if(((BaseCoreError) error).getCode().equalsIgnoreCase(BaseCoreError.CODE_SERVER_ERROR)) {
                errorMsg = contextWeakReference.get().getString(R.string.txt_server_error);
            }
            else if(((BaseCoreError) error).getCode().equalsIgnoreCase(BaseCoreError.CODE_BAD_REQUEST_ERROR)) {
                errorMsg = contextWeakReference.get().getString(R.string.txt_server_error);
            }
            return errorMsg;
        }
        else {
            return super.getErrorMessage(error);
        }
    }
}
