package com.abdallaadelessa.core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by abdallah on 24/11/15.
 */
public class ValidationUtils {
    public static final String PHONE_NUMBER_PATTERN = "^([0][1][0-2][0-9]{8})$";
    public static final String NATIONAL_ID_PATTERN = "^([0-9]{14})$";
    public static final String NUMBER_PATTERN = "^([0-9]*)$";
    public static final String PASSWORD_PATTERN = "^.{7,25}$";

    public static boolean isStringEmpty(CharSequence str) {
        return TextUtils.isEmpty(str) || TextUtils.isEmpty(str.toString().trim()) || "null".equalsIgnoreCase(str.toString());
    }

    public static boolean isNumber(CharSequence str) {
        return !isStringEmpty(str) && TextUtils.isDigitsOnly(str);
    }

    public static boolean isUrl(String str) {
        try {
            new URL(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isBoolean(String str) {
        try {
            Boolean.parseBoolean(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (isStringEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public final static boolean isValidPassword(CharSequence target) {
        if (isStringEmpty(target)) {
            return false;
        } else {
            return Pattern.compile(PASSWORD_PATTERN).matcher(target).matches();
        }
    }

    public final static boolean isValidPhoneNumber(CharSequence target) {
        boolean valid = true;
        String phoneNumber = target.toString();
        if (isStringEmpty(target)) {
            valid = false;
        } else if (!PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {
            valid = false;
        } else if (phoneNumber.length() != 11) {
            valid = false;
        }
        return valid;
    }

    public static boolean hasArabicCharactersOnly(String characters) {
        String regex = "^[\\p{Arabic}\\s\\p{N}0-9]+$";

        CharSequence inputStr = characters;

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches()) return true;
        else return false;
    }

    public static boolean hasEnglishCharactersOnly(String characters) {
        String regex = "^[\\sa-zA-Z0-9]+$";

        CharSequence inputStr = characters;

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches()) return true;
        else {
            return false;
        }
    }

}
