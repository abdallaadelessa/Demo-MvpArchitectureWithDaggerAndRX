package com.abdallaadelessa.core.utils;

import com.abdallaadelessa.core.app.BaseCoreApp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class BinaryUtils {
    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static final String ENCODING_WINDOWS = "Windows-1252";
    public static final String ENCODING_UTF8 = "UTF-8";

    // --------------------------------> Utils

    public static byte[] concatenateTwoByteArrays(byte a[], byte b[]) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(a);
        outputStream.write(b);
        return outputStream.toByteArray();
    }

    public static byte[] fillDummyArrayWithHexChar(int arraySize, byte hexChar) {
        byte[] array = new byte[arraySize];
        Arrays.fill(array, hexChar);
        return array;
    }

    public static byte[] truncateByteArray(byte org[], int start, int end) {
        // Arrays.copyOf(org, newLength);
        return Arrays.copyOfRange(org, start, end + 1);
    }

    // --------------------------------> Conversion

    public static byte[] convertHexToBytes(String hexString) {
        if (hexString == null) {
            return null;
        }
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    public static String convertStringToHex(String stringChars, String encoding) {
        if (stringChars == null) {
            return null;
        }

        String key = null;
        try {
            StringBuilder sb = new StringBuilder();
            byte[] passBytes = stringChars.getBytes(encoding);
            for (byte b : passBytes) {
                sb.append(String.format("%02X", b));
            }
            key = sb.toString();
        } catch (UnsupportedEncodingException e) {
            BaseCoreApp.getAppComponent().getLogger().logError(e);
        }
        return key;
    }

    public static String convertByteArrayToHex(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public static int convertByteArrayToInt(byte[] int_bytes) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(int_bytes);
        DataInputStream dis = new DataInputStream(bis);
        int my_int = dis.readInt();
        dis.close();
        return my_int;
    }

    public static byte[] convertIntToByteArray(int my_int) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeInt(my_int);
        dos.close();
        byte[] int_bytes = bos.toByteArray();
        bos.close();
        return int_bytes;
    }

    public static String convertByteArrayToBase32(byte[] bytes) {
        return Base32String.encode(bytes);
    }

    public static byte[] convertBase32ToByteArray(String s) {
        byte[] decode = null;
        try {
            decode = Base32String.decode(s);
        } catch (Base32String.DecodingException e) {
            BaseCoreApp.getAppComponent().getLogger().logError(e);
        }
        return decode;
    }


}
