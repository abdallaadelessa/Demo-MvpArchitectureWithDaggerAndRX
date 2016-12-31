package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.executors.okhttpModule.fileDownload;

/**
 * Created by abdullah on 12/31/16.
 */
public class FileDownloadModel {
    private String downloadFilePath;
    private long fileSize;
    private long currentRead;
    private int progress;

    public String getDownloadFilePath() {
        return downloadFilePath;
    }

    public void setDownloadFilePath(String downloadFilePath) {
        this.downloadFilePath = downloadFilePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getCurrentRead() {
        return currentRead;
    }

    public void setCurrentRead(long currentRead) {
        this.currentRead = currentRead;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
