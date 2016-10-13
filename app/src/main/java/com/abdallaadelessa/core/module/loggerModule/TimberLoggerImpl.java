package com.abdallaadelessa.core.module.loggerModule;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import timber.log.Timber;

/**
 * Created by Abdullah.Essa on 3/6/2016.
 */
public class TimberLoggerImpl implements AppLogger {
    private IReporter iReporter;
    private String tag;
    private boolean isLogsEnabled;
    private String storageLogsFolderPath;

    public TimberLoggerImpl(String tag, boolean isLogsEnabled, String storageLogsFolderPath, IReporter iReporter) {
        this.isLogsEnabled = isLogsEnabled;
        this.iReporter = iReporter;
        this.tag = tag;
        this.storageLogsFolderPath = storageLogsFolderPath;
    }

    //-----> Log Messages

    @Override
    public void log(String tag, String msg) {
        try {
            Timber.tag(tag);
            if (isLogsEnabled) {
                Timber.d(msg);
            }
        } catch (Throwable e) {
            logError(e);
        }
    }

    @Override
    public void log(String msg) {
        log(tag, msg);
    }

    @Override
    public void logToFile(String msg) {
        File f = new File(storageLogsFolderPath, "Logs-" + System.currentTimeMillis());
        try {
            BufferedWriter outputStream = new BufferedWriter(new FileWriter(f));
            outputStream.write(msg);
        } catch (Exception e) {
            logError(e);
        }
    }

    //-----> Log Errors

    @Override
    public void logError(String tag, Throwable e, boolean fatal) {
        try {
            Timber.tag(tag);
            if (e != null) {
                if (fatal && iReporter != null) {
                    iReporter.reportError(e);
                }
                if (e.getMessage() != null && isLogsEnabled) {
                    if (fatal) {
                        Timber.e(e);
                    } else {
                        Timber.w(e);
                    }
                }
            }
        } catch (Throwable ee) {
            ee.printStackTrace();
        }
    }

    @Override
    public void logError(Throwable e, boolean fatal) {
        logError(tag, e, fatal);
    }

    @Override
    public void logError(Throwable e) {
        logError(e, false);
    }


}
