package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager;

import org.json.JSONException;

import java.lang.reflect.Type;

/**
 * Created by abdullah on 12/26/16.
 */

public interface HttpParser {
    <T> T parse(String tag, Type type, String response) throws JSONException;
}
