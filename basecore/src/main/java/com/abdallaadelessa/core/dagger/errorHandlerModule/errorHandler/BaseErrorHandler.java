package com.abdallaadelessa.core.dagger.errorHandlerModule.errorHandler;

import android.content.Context;

import com.abdallaadelessa.core.model.BaseCoreError;
import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.R;

import java.lang.ref.WeakReference;

/**
 * Created by Abdalla on 16/10/2016.
 */

public abstract class BaseErrorHandler {
    protected WeakReference<Context> contextWeakReference;
    protected BaseAppLogger appLogger;

    public BaseErrorHandler(Context context, BaseAppLogger appLogger) {
        this.contextWeakReference = new WeakReference<Context>(context);
        this.appLogger = appLogger;
    }

    public String getErrorMessage(Throwable error) {
        if(contextWeakReference == null || contextWeakReference.get() == null) return null;
        String errorMsg = contextWeakReference.get().getString(R.string.txt_unknown_error_occured);
        try {
            if(isMessageError(error)) {
                errorMsg = error.getMessage();
            }
        }
        catch(Exception e) {
            appLogger.logError(e, true);
        }
        return errorMsg;
    }

    private boolean isMessageError(Throwable error) {
        return error instanceof BaseCoreError;
    }
}
