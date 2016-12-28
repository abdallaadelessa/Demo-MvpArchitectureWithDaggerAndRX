package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.subModules.okhttpModule;

import android.text.TextUtils;

import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpExecutor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.MultiPartRequest;
import com.android.volley.error.NetworkError;

import java.io.File;
import java.lang.reflect.Type;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
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

public class MultiPartExecutor<M> extends BaseHttpExecutor<M, MultiPartRequest> {
    private static final int TIMEOUT_IN_SECONDS = 60;
    private OkHttpClient client;
    private Call call;

    public MultiPartExecutor(OkHttpClient client) {
        this.client = client;
    }

    public Observable<M> buildObservable(final MultiPartRequest multiPartRequest) {
        return Observable.create(new Observable.OnSubscribe<M>() {
            @Override
            public void call(Subscriber<? super M> subscriber) {
                try {
                    String url = multiPartRequest.getUrl();
                    Map<String, String> parameters = multiPartRequest.getParams();
                    ArrayList<MultiPartRequest.MultiPartFile> files = multiPartRequest.getFiles();
                    MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                    // Files
                    if (files != null) {
                        for (int i = 0; i < files.size(); i++) {
                            MultiPartRequest.MultiPartFile multiPartFileObj = files.get(i);
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
                    RequestBody requestBody = multipartBodyBuilder.build();
                    Request request = new Request.Builder().url(url).post(requestBody).build();
                    call = client.newCall(request);
                    Response response = call.execute();
                    String responseStr = response.body().string();
                    M m = parse(responseStr, multiPartRequest);
                    onSuccess(multiPartRequest, subscriber, m);
                } catch (Throwable e) {
                    if (e instanceof SocketException) {
                        e = new NetworkError(e);
                    }
                    onError(multiPartRequest, subscriber, e, false);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }

    @Override
    protected void cancelRequest(MultiPartRequest request) {
        if (call != null && request.isCancelOnUnSubscribe()) {
            call.cancel();
        }
    }

}