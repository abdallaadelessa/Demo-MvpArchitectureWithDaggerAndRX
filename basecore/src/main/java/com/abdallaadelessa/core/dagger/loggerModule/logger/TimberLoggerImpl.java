package com.abdallaadelessa.core.dagger.loggerModule.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import timber.log.Timber;

/**
 * Created by Abdullah.Essa on 3/6/2016.
 */
public class TimberLoggerImpl extends BaseAppLogger {

    public TimberLoggerImpl(String tag, boolean isLogsEnabled, String logsFolderPath, BaseAppReporter baseAppReporter) {
        super(tag, isLogsEnabled, logsFolderPath, baseAppReporter);
        if (isLogsEnabled) Timber.plant(new Timber.DebugTree());
    }

    //==================>

    @Override
    public void log(String tag, String msg) {
        try {
            if (isEnabled()) {
                Timber.tag(tag);
                Timber.d(msg);
            }
        } catch (Throwable e) {
            logError(e);
        }
    }

    @Override
    public void log(String msg) {
        log(getTag(), msg);
    }

    @Override
    public void logToFile(String msg) {
        File f = new File(getPath(), "Logs-" + System.currentTimeMillis());
        try {
            BufferedWriter outputStream = new BufferedWriter(new FileWriter(f));
            outputStream.write(msg);
        } catch (Exception e) {
            logError(e);
        }
    }

    @Override
    public void logError(String tag, Throwable e, boolean fatal) {
        try {
            if (e != null) {
                if (fatal && getReporter() != null) {
                    getReporter().reportError(e);
                }
                if (e.getMessage() != null && isEnabled()) {
                    Timber.tag(tag);
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
        logError(getTag(), e, fatal);
    }

    @Override
    public void logError(Throwable e) {
        logError(e, false);
    }

    //==================>
}
