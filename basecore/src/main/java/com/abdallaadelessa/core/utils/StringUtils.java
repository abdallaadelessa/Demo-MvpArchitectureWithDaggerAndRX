package com.abdallaadelessa.core.utils;

import android.net.Uri;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by Abdalla on 16/10/2016.
 */

public class StringUtils {

    public static <T> T parseJson(String json, Gson gson, Type type) throws JSONException {
        T t = null;
        if (type == String.class) {
            t = (T) json;
        } else {
            Object parsedData = new JSONTokener(json).nextValue();
            if (parsedData instanceof JSONObject) {
                JSONObject response = new JSONObject(json);
                if (type == JSONObject.class) {
                    t = (T) response;
                } else {
                    t = (T) gson.fromJson(json, type);
                }
            } else if (parsedData instanceof JSONArray) {
                t = (T) gson.fromJson(json, type);
            }
        }
        return t;
    }

    public static String removeQuotes(String text) {
        return text.replaceAll("^\"|\"$", "");
    }

    public static String capitalizeFirstLetter(String text) {
        text = text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
        return text;
    }

    public static String removeWhiteSpaces(String text) {

        return text.replaceAll("\\s", "");
    }

    public static String convertTextToAsterisks(String text) {
        String hiddenText = "";
        if (!ValidationUtils.isStringEmpty(text)) {
            char[] pad = new char[text.length()];
            Arrays.fill(pad, '*');
            hiddenText = new StringBuilder().append(pad).toString();
        }
        return hiddenText;
    }

    public static String addQueryParams(String url, Map<String, String> queryParams, String paramsEncoding) throws UnsupportedEncodingException {
        Uri.Builder builder = Uri.parse(url).buildUpon();
        if (queryParams != null) {
            for (String key : queryParams.keySet()) {
                String value = queryParams.get(key);
                builder.appendQueryParameter(URLEncoder.encode(key, paramsEncoding), URLEncoder.encode(value, paramsEncoding));
            }
        }
        return builder.build().toString();
    }

    public static byte[] mapToBytes(Map<String, String> params, String encoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (null == entry.getValue()) {
                    continue;
                }
                encodedParams.append(URLEncoder.encode(entry.getKey(), encoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), encoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(encoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + encoding, uee);
        }
    }
}
