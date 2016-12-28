package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests;

import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpExecutor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.HttpInterceptor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.HttpParser;
import com.abdallaadelessa.core.utils.FileUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Created by abdullah on 12/27/16.
 */

public class MultiPartRequest<T> extends BaseRequest<T> {
    public static final String DEFAULT_MIME_TYPE = "image/jpeg";
    private String tag;
    private String url;
    private Map<String, String> params;
    private ArrayList<MultiPartFile> files;

    //=====> Builder

    public static <T> MultiPartRequest<T> create(HttpInterceptor interceptor, HttpParser parser, BaseAppLogger logger, BaseHttpExecutor<T, MultiPartRequest> observableExecutor, ExecutorService executorService) {
        // Default Values
        MultiPartRequest<T> multiPartRequest = new MultiPartRequest<T>(parser, logger, observableExecutor, executorService);
        multiPartRequest.addInterceptor(interceptor);
        multiPartRequest.setType(String.class);
        multiPartRequest.setParams(new HashMap<String, String>());
        multiPartRequest.setFiles(new ArrayList<MultiPartFile>());
        return multiPartRequest;
    }

    private MultiPartRequest(HttpParser parser, BaseAppLogger logger, BaseHttpExecutor<T, MultiPartRequest> observableExecutor, ExecutorService executorService) {
        super(parser, logger, observableExecutor, executorService);
    }

    //=====> Setter

    public MultiPartRequest<T> addParam(String key, String value) {
        getParams().put(key, value);
        return this;
    }

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

    public MultiPartRequest<T> setType(Type type) {
        this.type = type;
        return this;
    }

    private MultiPartRequest<T> setFiles(ArrayList<MultiPartFile> files) {
        this.files = files;
        return this;
    }

    public MultiPartRequest<T> addInterceptor(HttpInterceptor interceptor) {
        getInterceptors().add(interceptor);
        return this;
    }

    public MultiPartRequest<T> clearInterceptors() {
        getInterceptors().clear();
        return this;
    }

    public MultiPartRequest<T> setParser(HttpParser parser) {
        this.parser = parser;
        return this;
    }

    public MultiPartRequest<T> setLogger(BaseAppLogger logger) {
        this.logger = logger;
        return this;
    }

    public MultiPartRequest<T> setObservableExecutor(BaseHttpExecutor observableExecutor) {
        this.observableExecutor = observableExecutor;
        return this;
    }

    public MultiPartRequest<T> setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public MultiPartRequest<T> setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public MultiPartRequest<T> setUrl(String url) {
        this.url = url;
        return this;
    }

    //=====> Getter

    public String getTag() {
        return tag;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public ArrayList<MultiPartFile> getFiles() {
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
