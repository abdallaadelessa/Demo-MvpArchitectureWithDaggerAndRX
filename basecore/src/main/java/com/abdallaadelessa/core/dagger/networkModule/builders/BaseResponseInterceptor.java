package com.abdallaadelessa.core.dagger.networkModule.builders;

import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.volley.VolleyRequestManager;
import com.abdallaadelessa.core.model.MessageError;
import com.abdallaadelessa.core.utils.AndroidUtils;
import com.android.volley.error.NoConnectionError;

import org.json.JSONException;

import java.lang.reflect.Type;

/**
 * Created by Abdalla on 18/10/2016.
 */
public abstract class BaseResponseInterceptor {
    private BaseAppLogger logger;

    public BaseResponseInterceptor(BaseAppLogger logger) {
        this.logger = logger;
    }

    public BaseAppLogger getLogger() {
        return logger;
    }

    // ------------->

    public abstract <T> T parse(String tag, Type type, String json) throws JSONException;

    public void onStart(String tag, String url) throws Exception {
        logger.log(tag, "Starting : " + url);
        if (!AndroidUtils.checkIfApplicationIsConnected()) {
            throw new NoConnectionError();
        }
    }

    public String interceptResponse(String tag, String url, String json) throws Exception {
        logger.log(tag, json);
        return json;
    }

    public Throwable interceptError(String tag, String url, Throwable throwable, final boolean fatal) {
        try {
            if (VolleyRequestManager.isVolleyError(throwable)) {
                String jsonError = VolleyRequestManager.getJsonError(throwable);
                logger.logError(tag, jsonError != null ? new MessageError(jsonError) : throwable, fatal);
            } else {
                logger.logError(tag, throwable, fatal);
            }
        } catch (Exception e) {
            //Eat it!
        }
        return throwable;
    }
}
