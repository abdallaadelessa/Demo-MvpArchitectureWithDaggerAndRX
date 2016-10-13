package com.abdallaadelessa.core.module.loggerModule;

/**
 * Created by Abdalla on 13/10/2016.
 */
public interface AppLogger {
    void log(String tag, String msg);

    void log(String msg);

    void logToFile(String msg);

    void logError(String tag, Throwable e, boolean fatal);

    void logError(Throwable e, boolean fatal);

    void logError(Throwable e);

    interface IReporter {
        void reportError(Throwable throwable);

        void reportError(String title, String message, Throwable throwable);
    }
}
