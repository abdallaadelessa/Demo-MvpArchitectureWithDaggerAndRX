package com.abdallaadelessa.core.model;

import android.content.Context;

/**
 * Created by Abdullah.Essa on 1/11/2016.
 */
public class MessageError extends Exception {
    public static final String NO_CODE = "0";
    private String code;
    private String exceptionMessage;

    public MessageError(String code, String message) {
        super(message);
        this.code = code;
        this.exceptionMessage = message;
    }

    public MessageError(String message) {
        this(NO_CODE, message);
    }

    public MessageError(Context context, int stringResId) {
        this(NO_CODE, context.getString(stringResId));
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "Code " + getCode() + " " + super.toString();
    }
}
