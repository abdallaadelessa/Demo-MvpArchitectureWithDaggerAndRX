package com.abdallaadelessa.core.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;

/**
 * Created by Abdalla on 16/10/2016.
 */

public class StringUtils {
    /**
     * Encode URl UTF-8
     *
     * @param originalUrl
     * @return
     * @throws UnsupportedEncodingException
     */

    public static String encodeURL(String originalUrl) throws UnsupportedEncodingException {
        int lastSlashIndexLarge = originalUrl.lastIndexOf('/');
        String encodedUrl = originalUrl.substring(0, lastSlashIndexLarge + 1) + URLEncoder.encode(originalUrl.substring(lastSlashIndexLarge + 1, originalUrl.length()), "UTF-8");
        String completeUrl = encodedUrl.replace("+", "%20");
        return completeUrl;
    }

    public static String removeQuotes(String text) {
        return text.replaceAll("^\"|\"$", "");
    }

    public static String capitalizeFirstLetter(String text) {
        text = text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
        return text;
    }

    public static String computeHash(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();

        byte[] byteData = digest.digest(input.getBytes());
        return bytesToHex(byteData);

    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
