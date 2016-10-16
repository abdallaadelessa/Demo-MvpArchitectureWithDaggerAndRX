package com.abdallaadelessa.core.dagger.networkModule.builders;

import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.volley.MultipartRequestManager;
import com.abdallaadelessa.core.utils.FileUtils;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by Abdalla on 16/10/2016.
 */
public class MultipartRequestBuilder {
    public static final String DEFAULT_MIME_TYPE = "image/jpeg";
    // ------------------------->
    @Inject
    BaseAppLogger baseAppLogger;
    @Inject
    Gson gson;
    // ------------------------->
    private String url;
    private Type type;
    private Map<String, String> parameters;
    private ArrayList<MultiPartFile> files;

    public static MultipartRequestBuilder builder() {
        return new MultipartRequestBuilder();
    }

    private MultipartRequestBuilder() {
        this(null, String.class, new HashMap<String, String>(), new ArrayList<MultiPartFile>());
    }

    private MultipartRequestBuilder(String url, Type type, Map<String, String> parameters, ArrayList<MultiPartFile> files) {
        this.url = url;
        this.type = type;
        this.parameters = parameters;
        this.files = files;
    }

    // -------------->

    public MultipartRequestBuilder url(String url) {
        this.url = url;
        return this;
    }

    public MultipartRequestBuilder type(Type type) {
        this.type = type;
        return this;
    }

    public void gson(Gson gson) {
        this.gson = gson;
    }

    public void appLogger(BaseAppLogger baseAppLogger) {
        this.baseAppLogger = baseAppLogger;
    }

    public MultipartRequestBuilder addParameter(String key, String value) {
        parameters.put(key, value);
        return this;
    }

    public MultipartRequestBuilder addFile(String fileName, String filepath, String mimeType) {
        files.add(new MultiPartFile(fileName, filepath, mimeType));
        return this;
    }

    public MultipartRequestBuilder addFile(String fileName, String filepath) {
        String mimeType = FileUtils.getMimeType(filepath);
        return addFile(fileName, filepath, mimeType != null ? mimeType : DEFAULT_MIME_TYPE);
    }

    public MultipartRequestBuilder addFile(String filepath) {
        return addFile(FileUtils.getFileNameFromUrl(filepath), filepath);
    }

    // -------------->

    public BaseAppLogger getBaseAppLogger() {
        return baseAppLogger;
    }

    public Gson getGson() {
        return gson;
    }

    public String getUrl() {
        return url;
    }

    public Type getType() {
        return type;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public ArrayList<MultiPartFile> getFiles() {
        return files;
    }

    // -------------->

    public <T> Observable<T> build() {
        return MultipartRequestManager.createObservableFrom(this);
    }

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
