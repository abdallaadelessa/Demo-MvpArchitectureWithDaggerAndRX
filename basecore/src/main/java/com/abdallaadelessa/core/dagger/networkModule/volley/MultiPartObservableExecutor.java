package com.abdallaadelessa.core.dagger.networkModule.volley;

import android.text.TextUtils;

import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpObservableExecutor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.HttpRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.MultiPartRequest;
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
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MultiPartObservableExecutor<T> extends BaseHttpObservableExecutor<T, MultiPartRequest> {
    private static final int TIMEOUT_IN_SECONDS = 60;

    @Override
    public Observable<T> toObservable(final MultiPartRequest request) {
        return Observable.create(new Observable.OnSubscribe<MultiPartRequest>() {
            @Override
            public void call(Subscriber<? super MultiPartRequest> subscriber) {
                try {
                    MultiPartRequest multiPartRequest = (MultiPartRequest) request.getInterceptor().interceptRequest(request);
                    subscriber.onNext(multiPartRequest);
                    subscriber.onCompleted();
                }
                catch(Exception e) {
                    onError(request, subscriber, e, false);
                }
            }
        }).flatMap(new Func1<MultiPartRequest, Observable<T>>() {
            @Override
            public Observable<T> call(final MultiPartRequest multiPartRequest) {
                return Observable.create(new Observable.OnSubscribe<T>() {
                    @Override
                    public void call(Subscriber<? super T> subscriber) {
                        try {
                            String tag = multiPartRequest.getTag();
                            String url = multiPartRequest.getUrl();
                            Type type = multiPartRequest.getType();
                            Map<String, String> parameters = multiPartRequest.getParams();
                            ArrayList<MultiPartRequest.MultiPartFile> files = multiPartRequest.getFiles();
                            MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                            // Files
                            if(files != null) {
                                for(int i = 0; i < files.size(); i++) {
                                    MultiPartRequest.MultiPartFile multiPartFileObj = files.get(i);
                                    String fileUrl = multiPartFileObj.getUrl();
                                    String fileName = TextUtils.isEmpty(multiPartFileObj.getName()) ? "file" + i : multiPartFileObj.getName();
                                    String fileMimeType = multiPartFileObj.getMimeType();
                                    File sourceFile = new File(fileUrl);
                                    if(sourceFile.exists()) {
                                        final MediaType MEDIA_TYPE = MediaType.parse(fileMimeType);
                                        multipartBodyBuilder.addFormDataPart(fileName, sourceFile.getName(), RequestBody.create(MEDIA_TYPE, sourceFile));
                                    }
                                }
                            }
                            // Parameters
                            if(parameters != null) {
                                for(String key : parameters.keySet()) {
                                    String value = parameters.get(key);
                                    multipartBodyBuilder.addFormDataPart(key, value);
                                }
                            }
                            // Send Request
                            RequestBody requestBody = multipartBodyBuilder.build();
                            Request request = new Request.Builder().url(url).post(requestBody).build();
                            OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS).writeTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS).build();
                            Response response = client.newCall(request).execute();
                            String responseStr = multiPartRequest.getInterceptor().interceptResponse(multiPartRequest, response.body().string());
                            subscriber.onNext(multiPartRequest.getParser().<T>parse(tag, type, responseStr));
                            subscriber.onCompleted();
                        }
                        catch(Throwable e) {
                            if(e instanceof SocketException) {
                                e = new NetworkError(e);
                            }
                            subscriber.onError(multiPartRequest.getInterceptor().interceptError(multiPartRequest, e, false));
                        }
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
            }
        });
    }

}