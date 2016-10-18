package com.abdallaadelessa.core.dagger.networkModule.volley;

import android.text.TextUtils;

import com.abdallaadelessa.core.dagger.networkModule.builders.MultipartRequest;
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

    public static <T> Observable<T> createObservableFrom(final MultipartRequest multipartRequest) {
        Observable<T> observable = Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    String tag = multipartRequest.tag();
                    String url = multipartRequest.url();
                    Type type = multipartRequest.type();
                    Map<String, String> parameters = multipartRequest.params();
                    ArrayList<MultipartRequest.MultiPartFile> files = multipartRequest.files();
                    MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                    // Files
                    if (files != null) {
                        for (int i = 0; i < files.size(); i++) {
                            MultipartRequest.MultiPartFile multiPartFileObj = files.get(i);
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
                    multipartRequest.responseInterceptor().onStart(tag, url);
                    RequestBody requestBody = multipartBodyBuilder.build();
                    Request request = new Request.Builder().url(url).post(requestBody).build();
                    OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS).writeTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS).build();
                    Response response = client.newCall(request).execute();
                    String responseStr = multipartRequest.responseInterceptor().interceptResponse(tag, url, response.body().string());
                    subscriber.onNext(multipartRequest.responseInterceptor().<T>parse(tag, type, responseStr));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    if (e instanceof SocketException) {
                        e = new NetworkError(e);
                    }
                    subscriber.onError(multipartRequest.responseInterceptor().interceptError(multipartRequest.tag(), multipartRequest.url(), e, false));
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
        return observable;
    }
}