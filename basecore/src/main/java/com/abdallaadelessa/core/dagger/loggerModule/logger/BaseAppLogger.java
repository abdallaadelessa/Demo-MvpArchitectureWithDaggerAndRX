package com.abdallaadelessa.core.dagger.loggerModule.logger;

/**
 * Created by Abdalla on 13/10/2016.
 */
public abstract class BaseAppLogger {
    protected String tag;
    protected boolean isEnabled;
    protected String path;
    protected BaseAppReporter reporter;

    public BaseAppLogger(String tag, boolean isEnabled, String path, BaseAppReporter reporter) {
        this.tag = tag;
        this.isEnabled = isEnabled;
        this.path = path;
        this.reporter = reporter;
    }

    //==================>

    public void setReporter(BaseAppReporter reporter) {
        this.reporter = reporter;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    public void setPath(String path) {
        this.path = path;
    }

    //==================>

    public String getTag() {
        return tag;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public String getPath() {
        return path;
    }

    public BaseAppReporter getReporter() {
        return reporter;
    }

    //==================>

    public abstract void log(String tag, String msg);

    public abstract void log(String msg);

    public abstract void logToFile(String msg);

    public abstract void logError(String tag, Throwable e, boolean fatal);

    public abstract void logError(Throwable e, boolean fatal);

    public abstract void logError(Throwable e);

    //==================>

    public interface BaseAppReporter {
        void reportError(Throwable throwable);

        void reportError(String title, String message, Throwable throwable);
    }
}
