package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests;

import android.support.annotation.Nullable;

import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpObservableExecutor;
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
    private Type type;
    private Map<String, String> params;
    private ArrayList<MultiPartFile> files;

    //=====> Builder

    public static MultiPartRequest create(HttpInterceptor interceptor, HttpParser parser, BaseAppLogger logger, BaseHttpObservableExecutor observableExecutor, ExecutorService executorService) {
        // Default Values
        MultiPartRequest multiPartRequest = new MultiPartRequest(interceptor, parser, logger, observableExecutor, executorService);
        multiPartRequest.setType(String.class);
        multiPartRequest.setParams(new HashMap<String, String>());
        multiPartRequest.setFiles(new ArrayList<MultiPartFile>());
        return multiPartRequest;
    }

    private MultiPartRequest(HttpInterceptor interceptor, HttpParser parser, BaseAppLogger logger, BaseHttpObservableExecutor observableExecutor, ExecutorService executorService) {
        super(interceptor, parser, logger, observableExecutor, executorService);
    }
    
    //=====> Setter
    
    public MultiPartRequest addParam(String key, String value) {
        getParams().put(key, value);
        return this;
    }

    public MultiPartRequest addFile(String fileName, String filepath, String mimeType) {
        getFiles().add(new MultiPartFile(fileName, filepath, mimeType));
        return this;
    }

    public MultiPartRequest addFile(String fileName, String filepath) {
        String mimeType = FileUtils.getMimeType(filepath);
        return addFile(fileName, filepath, mimeType != null ? mimeType : DEFAULT_MIME_TYPE);
    }

    public MultiPartRequest addFile(String filepath) {
        return addFile(FileUtils.getFileNameFromUrl(filepath), filepath);
    }

    public MultiPartRequest setType(Type type) {
        this.type = type;
        return this;
    }

    private MultiPartRequest setParams(Map<String, String> params) {
        this.params = params;
        return this;
    }

    private MultiPartRequest setFiles(ArrayList<MultiPartFile> files) {
        this.files = files;
        return this;
    }

    public MultiPartRequest setInterceptor(HttpInterceptor interceptor) {
        this.interceptor = interceptor;
        return this;
    }

    public MultiPartRequest setParser(HttpParser parser) {
        this.parser = parser;
        return this;
    }

    public MultiPartRequest setLogger(BaseAppLogger logger) {
        this.logger = logger;
        return this;
    }

    public MultiPartRequest setObservableExecutor(BaseHttpObservableExecutor observableExecutor) {
        this.observableExecutor = observableExecutor;
        return this;
    }

    public MultiPartRequest setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public MultiPartRequest setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public MultiPartRequest setUrl(String url) {
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

    public Type getType() {
        return type;
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
