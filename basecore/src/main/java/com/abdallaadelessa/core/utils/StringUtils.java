package com.abdallaadelessa.core.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

/**
 * Created by Abdalla on 16/10/2016.
 */

public class StringUtils {

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
}
