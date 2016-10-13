package com.abdallaadelessa.core.module.networkModule.volley;

import android.text.TextUtils;

import com.abdallaadelessa.core.module.networkModule.volley.volleyObservable.VolleyObservableRequest;
import com.abdallaadelessa.core.model.MessageError;
import com.abdallaadelessa.core.module.loggerModule.AppLogger;
import com.abdallaadelessa.core.utils.FileUtils;
import com.abdallaadelessa.core.utils.ValidationUtils;
import com.android.volley.error.NetworkError;
import com.google.gson.Gson;

import java.io.File;
import java.lang.reflect.Type;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MultipartObservableRequest<T> {
    public static final int TIMEOUT_IN_SECONDS = 60;

    public static MultipartObservableBuilder builder() {
        return new MultipartObservableBuilder();
    }

    // --------------------->

    public static class MultipartObservableBuilder {
        public static final String DEFAULT_MIME_TYPE = "image/jpeg";
        private AppLogger appLogger;
        private Gson gson;
        private String url;
        private Type type;
        private Map<String, String> parameters;
        private ArrayList<MultiPartFile> files;

        private MultipartObservableBuilder() {
            this(null, String.class, new HashMap<String, String>(), new ArrayList<MultiPartFile>());
        }

        private MultipartObservableBuilder(String url, Type type, Map<String, String> parameters, ArrayList<MultiPartFile> files) {
            this.url = url;
            this.type = type;
            this.parameters = parameters;
            this.files = files;
        }

        public MultipartObservableBuilder url(String url) {
            this.url = url;
            return this;
        }

        public MultipartObservableBuilder type(Type type) {
            this.type = type;
            return this;
        }

        public void gson(Gson gson) {
            this.gson = gson;
        }

        public void appLogger(AppLogger appLogger) {
            this.appLogger = appLogger;
        }

        public MultipartObservableBuilder addParameter(String key, String value) {
            parameters.put(key, value);
            return this;
        }

        public MultipartObservableBuilder addFile(String fileName, String filepath, String mimeType) {
            files.add(new MultiPartFile(fileName, filepath, mimeType));
            return this;
        }

        public MultipartObservableBuilder addFile(String fileName, String filepath) {
            String mimeType = FileUtils.getMimeType(filepath);
            return addFile(fileName, filepath, mimeType != null ? mimeType : DEFAULT_MIME_TYPE);
        }

        public MultipartObservableBuilder addFile(String filepath) {
            return addFile(FileUtils.getFileNameFromUrl(filepath), filepath);
        }

        // -------------->

        public <T> Observable<T> build() {
            return buildMultipartObservableRequest(this);
        }
    }

    private static <T> Observable<T> buildMultipartObservableRequest(final MultipartObservableBuilder multipartObservableBuilder) {
        Observable<T> observable = Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    String error = validateBuilder(multipartObservableBuilder);
                    if (!ValidationUtils.isStringEmpty(error)) {
                        subscriber.onError(new MessageError(error));
                        return;
                    }
                    String url = multipartObservableBuilder.url;
                    Type type = multipartObservableBuilder.type;
                    Map<String, String> parameters = multipartObservableBuilder.parameters;
                    ArrayList<MultiPartFile> files = multipartObservableBuilder.files;
                    MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                    // Files
                    if (files != null) {
                        for (int i = 0; i < files.size(); i++) {
                            MultiPartFile multiPartFileObj = files.get(i);
                            String fileUrl = multiPartFileObj.getUrl();
                            String fileName = TextUtils.isEmpty(multiPartFileObj.getName()) ? "file" + i : multiPartFileObj.getName();
                            String fileMimeType = multiPartFileObj.getMimeType();
                            File sourceFile = new File(fileUrl);
                            if (sourceFile.exists()) {
                                final MediaType MEDIA_TYPE = MediaType.parse(fileMimeType);
                                multipartBodyBuilder.addFormDataPart(fileName, sourceFile.getName(), RequestBody.create(MEDIA_TYPE, sourceFile));
                            }
                        }
                    }
                    // Parameters
                    if (parameters != null) {
                        for (String key : parameters.keySet()) {
                            String value = parameters.get(key);
                            multipartBodyBuilder.addFormDataPart(key, value);
                        }
                    }
                    // Send Request
                    multipartObservableBuilder.appLogger.log("Uploading " + url);
                    RequestBody requestBody = multipartBodyBuilder.build();
                    Request request = new Request.Builder().url(url).post(requestBody).build();
                    OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS).writeTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS).build();
                    Response response = client.newCall(request).execute();
                    String responseStr = response.body().string();
                    subscriber.onNext(VolleyObservableRequest.<T>parseJson(responseStr, multipartObservableBuilder.gson, type));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    if (e instanceof SocketException) {
                        e = new NetworkError(e);
                    }
                    multipartObservableBuilder.appLogger.logError(e);
                    subscriber.onError(e);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
        return observable;
    }

    // -------------------------> Helpers

    private static String validateBuilder(MultipartObservableBuilder multipartObservableBuilder) {
        String errorMessage = null;
        if (multipartObservableBuilder == null) {
            errorMessage = "multipartObservableBuilder is null";
        } else if (TextUtils.isEmpty(multipartObservableBuilder.url)) {
            errorMessage = "url is empty";
        } else if (multipartObservableBuilder.type == null) {
            errorMessage = "type is null";
        }
        return errorMessage;
    }

    // ----------------->

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