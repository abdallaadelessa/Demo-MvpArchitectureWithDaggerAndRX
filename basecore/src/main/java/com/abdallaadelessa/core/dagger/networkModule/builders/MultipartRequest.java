package com.abdallaadelessa.core.dagger.networkModule.builders;

import android.support.annotation.Nullable;

import com.abdallaadelessa.core.dagger.loggerModule.logger.BaseAppLogger;
import com.abdallaadelessa.core.dagger.networkModule.volley.MultipartRequestManager;
import com.abdallaadelessa.core.utils.FileUtils;
import com.google.auto.value.AutoValue;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;

/**
 * Created by Abdalla on 16/10/2016.
 */
@AutoValue
public abstract class MultipartRequest extends MultipartRequestGetters {
    public static MultipartRequest.Builder builder(HttpRequest.BaseResponseInterceptor baseResponseInterceptor, BaseAppLogger baseAppLogger) {
        MultipartRequest.Builder builder = new AutoValue_MultipartRequest.Builder();
        builder.responseInterceptor(baseResponseInterceptor);
        builder.appLogger(baseAppLogger);
        // Default Values
        builder.type(String.class);
        builder.params(new HashMap<String, String>());
        builder.files(new ArrayList<MultiPartFile>());
        return builder;
    }

    @AutoValue.Builder
    public abstract static class Builder extends MultipartRequestGetters {
        abstract MultipartRequest.Builder responseInterceptor(HttpRequest.BaseResponseInterceptor responseInterceptor);

        abstract MultipartRequest.Builder appLogger(BaseAppLogger baseAppLogger);

        public abstract MultipartRequest.Builder tag(String tag);

        public abstract MultipartRequest.Builder url(String url);

        public abstract MultipartRequest.Builder type(Type type);

        abstract MultipartRequest.Builder params(Map<String, String> params);

        abstract MultipartRequest.Builder files(ArrayList<MultipartRequest.MultiPartFile> files);

        //--------->

        public MultipartRequest.Builder addParam(String key, String value) {
            params().put(key, value);
            return this;
        }

        public MultipartRequest.Builder addFile(String fileName, String filepath, String mimeType) {
            files().add(new MultiPartFile(fileName, filepath, mimeType));
            return this;
        }

        public MultipartRequest.Builder addFile(String fileName, String filepath) {
            String mimeType = FileUtils.getMimeType(filepath);
            return addFile(fileName, filepath, mimeType != null ? mimeType : DEFAULT_MIME_TYPE);
        }

        public MultipartRequest.Builder addFile(String filepath) {
            return addFile(FileUtils.getFileNameFromUrl(filepath), filepath);
        }

        abstract MultipartRequest autoBuild();

        public <T> Observable<T> build() {
            return MultipartRequestManager.<T>createObservableFrom(autoBuild());
        }
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

abstract class MultipartRequestGetters {
    public abstract HttpRequest.BaseResponseInterceptor responseInterceptor();

    public abstract BaseAppLogger appLogger();

    @Nullable
    public abstract String tag();

    public abstract String url();

    public abstract Type type();

    public abstract Map<String, String> params();

    public abstract ArrayList<MultipartRequest.MultiPartFile> files();

    // ----------> Constants
    public static final String DEFAULT_MIME_TYPE = "image/jpeg";
}
