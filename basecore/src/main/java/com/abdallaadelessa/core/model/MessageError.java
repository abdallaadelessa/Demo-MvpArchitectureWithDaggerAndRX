package com.abdallaadelessa.core.model;

import android.content.Context;

/**
 * Created by Abdullah.Essa on 1/11/2016.
 */
public class MessageError extends Exception {
    public static final String NO_CODE = "0";
    public static final String CODE_NETWORK_ERROR = "-100";
    public static final String CODE_TIMEOUT_ERROR = "-200";
    public static final String CODE_SERVER_ERROR = "-300";
    public static final String CODE_BAD_REQUEST_ERROR = "-400";
    private String code;
    private String message;
    private Throwable throwable;

    //===============>

    public MessageError(String code, String message, Throwable throwable) {
        super(message, throwable);
        this.code = code;
        this.message = message;
        this.throwable = throwable;
    }

    public MessageError(String code, Throwable throwable) {
        this(code, throwable.getMessage(), throwable);
    }

    public MessageError(String code, String message) {
        this(code, message, null);
    }

    public MessageError(String message) {
        this(NO_CODE, message, null);
    }

    public MessageError(Throwable throwable) {
        this(NO_CODE, throwable.getMessage(), throwable);
    }

    public MessageError(Context context, int stringResId) {
        this(NO_CODE, context.getString(stringResId), null);
    }

    //===============>

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "Code " + getCode() + " Message " + message;
    }

}
