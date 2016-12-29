package com.abdallaadelessa.core.model;

import android.content.Context;

/**
 * Created by Abdullah.Essa on 1/11/2016.
 */
public class BaseCoreError extends Exception {
    public static final String NO_CODE = "2016";
    public static final String CODE_NETWORK_ERROR = "-2017";
    public static final String CODE_TIMEOUT_ERROR = "-2018";
    public static final String CODE_SERVER_ERROR = "-2019";
    public static final String CODE_BAD_REQUEST_ERROR = "-2020";
    private String code;

    public BaseCoreError(String code, String message, Throwable throwable) {
        super(message, throwable);
        this.code = code;
    }

    public BaseCoreError(String code, Throwable throwable) {
        this(code, throwable.getMessage(), throwable);
    }

    public BaseCoreError(String code, String message) {
        this(code, message, null);
    }

    public BaseCoreError(String message) {
        this(NO_CODE, message, null);
    }

    public BaseCoreError(Throwable throwable) {
        this(NO_CODE, throwable.getMessage(), throwable);
    }

    public BaseCoreError(Context context, int stringResId) {
        this(NO_CODE, context.getString(stringResId), null);
    }

    //===============>

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "Code " + getCode() + " Message " + getMessage();
    }

}
