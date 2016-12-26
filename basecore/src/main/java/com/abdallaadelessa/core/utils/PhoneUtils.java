package com.abdallaadelessa.core.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

public class PhoneUtils {
    public static String getPhoneSerial(Context cxt) {
        TelephonyManager mTelephonyMgr = ((TelephonyManager) cxt
                .getSystemService(Context.TELEPHONY_SERVICE));
        String imei = mTelephonyMgr.getDeviceId();
        return imei;
    }

    public static String getSimSerial(Context cxt) {
        TelephonyManager mTelephonyMgr = ((TelephonyManager) cxt
                .getSystemService(Context.TELEPHONY_SERVICE));
        String simno = mTelephonyMgr.getSimSerialNumber();
        return simno;
    }

    public static String getSubscriberId(Context cxt) {
        TelephonyManager mTelephonyMgr = ((TelephonyManager) cxt
                .getSystemService(Context.TELEPHONY_SERVICE));
        String imsi = mTelephonyMgr.getSubscriberId();
        return imsi;
    }
}
