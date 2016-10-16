package com.abdallaadelessa.core.utils;

import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by abdallah on 09/09/15.
 */
public class FileUtils {

    // -------------> Download File

    public static Observable<String> downloadFileAsync(final String url, final String saveToFilePath) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    downloadFile(url, saveToFilePath);
                    subscriber.onNext(saveToFilePath);
                    subscriber.onCompleted();
                }
                catch(Throwable e) {
                    subscriber.onError(e);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }

    public static void downloadFile(final String url, final String filePath) throws IOException {
        BufferedInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new BufferedInputStream(new URL(url).openStream());
            out = new FileOutputStream(filePath, false);
            int bufferSize = 8024;
            final byte data[] = new byte[bufferSize];
            int count;
            while((count = in.read(data, 0, bufferSize)) != -1) {
                out.write(data, 0, count);
            }
        }
        finally {
            if(in != null) {
                in.close();
            }
            if(out != null) {
                out.close();
            }
        }
    }

    // ------------->

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

    public static String getFileNameFromUrl(String url) {
        String fileName = null;
        if(url != null) {
            fileName = url;
            fileName = getLastUrlPart(fileName);
        }
        return fileName;
    }

    public static String cleanQueryParametersIfExists(String url) {
        if(url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        return url;
    }

    public static String getLastUrlPart(String url) {
        url = cleanQueryParametersIfExists(url);
        if(url.contains(File.separator)) {
            url = url.substring(url.lastIndexOf(File.separator) + 1);
        }
        return url;
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if(extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static boolean deleteDirectory(File directory) {
        if(directory == null) return false;
        if(directory.exists()) {
            File[] files = directory.listFiles();
            if(null != files) {
                for(int i = 0; i < files.length; i++) {
                    if(files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    }
                    else {
                        files[i].delete();
                    }
                }
            }
        }
        return (directory.delete());
    }
}
