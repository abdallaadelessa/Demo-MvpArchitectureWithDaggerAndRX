package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests;

import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpExecutor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpInterceptor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpParser;
import com.abdallaadelessa.core.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by abdullah on 12/27/16.
 */

public class MultiPartRequest<T> extends BaseRequest<MultiPartRequest<T>, T> {
    public static final String DEFAULT_MIME_TYPE = "image/jpeg";
    private List<MultiPartFile> files;

    //=====>

    public static <T> MultiPartRequest<T> create(BaseHttpParser parser, BaseAppLogger logger, BaseHttpExecutor<T, MultiPartRequest<T>> observableExecutor, ExecutorService executorService) {
        return new MultiPartRequest<>(parser, logger, observableExecutor, executorService);
    }

    public MultiPartRequest(BaseHttpParser parser, BaseAppLogger logger, BaseHttpExecutor<T, MultiPartRequest<T>> observableExecutor, ExecutorService executorService) {
        super(parser, logger, observableExecutor, executorService);
        timeout = TIMEOUT_LONG_IN_MILLIS;
        files = new ArrayList<>();
    }

    //=====> Setter

    public MultiPartRequest<T> addFile(String fileName, String filepath, String mimeType) {
        getFiles().add(new MultiPartFile(fileName, filepath, mimeType));
        return this;
    }

    public MultiPartRequest<T> addFile(String fileName, String filepath) {
        String mimeType = FileUtils.getMimeType(filepath);
        return addFile(fileName, filepath, mimeType != null ? mimeType : DEFAULT_MIME_TYPE);
    }

    public MultiPartRequest<T> addFile(String filepath) {
        return addFile(FileUtils.getFileNameFromUrl(filepath), filepath);
    }

    private MultiPartRequest<T> setFiles(ArrayList<MultiPartFile> files) {
        this.files = files;
        return this;
    }

    //=====> Getters

    public List<MultiPartFile> getFiles() {
        return files;
    }

    //=====>

    public static class MultiPartFile {
        private String url;
        private String name;
        private String mimeType;

        public MultiPartFile(String name, String url, String mimeType) {
            this.url = url;
            this.name = name;
            this.mimeType = mimeType;
        }

        public String getUrl() {
            return url;
        }

        public String getName() {
            return name;
        }

        public String getMimeType() {
            return mimeType;
        }
    }
}
