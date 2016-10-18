package com.abdallaadelessa.core.dagger.networkModule.volley;

import android.text.TextUtils;

import com.abdallaadelessa.core.model.MessageError;
import com.abdallaadelessa.core.dagger.networkModule.builders.MultipartRequestBuilder;
import com.abdallaadelessa.core.utils.StringUtils;
import com.abdallaadelessa.core.utils.ValidationUtils;
import com.android.volley.error.NetworkError;

import java.io.File;
import java.lang.reflect.Type;
import java.net.SocketException;
import java.util.ArrayList;
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

public class MultipartRequestManager<T> {
    public static final int TIMEOUT_IN_SECONDS = 60;

    // --------------------->

    public static <T> Observable<T> createObservableFrom(final MultipartRequestBuilder multipartRequestBuilder) {
        Observable<T> observable = Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    String error = validateBuilder(multipartRequestBuilder);
                    if (!ValidationUtils.isStringEmpty(error)) {
                        subscriber.onError(new MessageError(error));
                        return;
                    }
                    String url = multipartRequestBuilder.getUrl();
                    Type type = multipartRequestBuilder.getType();
                    Map<String, String> parameters = multipartRequestBuilder.getParameters();
                    ArrayList<MultipartRequestBuilder.MultiPartFile> files = multipartRequestBuilder.getFiles();
                    MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                    // Files
                    if (files != null) {
                        for (int i = 0; i < files.size(); i++) {
                            MultipartRequestBuilder.MultiPartFile multiPartFileObj = files.get(i);
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
                    multipartRequestBuilder.getBaseAppLogger().log("Uploading " + url);
                    RequestBody requestBody = multipartBodyBuilder.build();
                    Request request = new Request.Builder().url(url).post(requestBody).build();
                    OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS).writeTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS).build();
                    Response response = client.newCall(request).execute();
                    String responseStr = response.body().string();
                    subscriber.onNext(StringUtils.<T>parseJson(responseStr, multipartRequestBuilder.getGson(), type));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    if (e instanceof SocketException) {
                        e = new NetworkError(e);
                    }
                    multipartRequestBuilder.getBaseAppLogger().logError(e);
                    subscriber.onError(e);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
        return observable;
    }

    // -------------------------> Helpers

    private static String validateBuilder(MultipartRequestBuilder multipartRequestBuilder) {
        String errorMessage = null;
        if (multipartRequestBuilder == null) {
            errorMessage = "multipartRequestBuilder is null";
        } else if (TextUtils.isEmpty(multipartRequestBuilder.getUrl())) {
            errorMessage = "url is empty";
        } else if (multipartRequestBuilder.getType() == null) {
            errorMessage = "type is null";
        } else if (multipartRequestBuilder.getBaseAppLogger() == null) {
            errorMessage = "BaseAppLogger is null";
        } else if (multipartRequestBuilder.getGson() == null) {
            errorMessage = "Gson is null";
        } else if (multipartRequestBuilder.getFiles() == null) {
            errorMessage = "Files is null";
        }
        return errorMessage;
    }

}