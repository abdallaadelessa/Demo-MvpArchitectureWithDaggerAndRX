package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.executors.okhttpModule;

import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpExecutor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.BaseRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.HttpMethod;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.HttpRequest;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by abdullah on 12/28/16.
 */

public class OkHttpExecutor<M> extends BaseHttpExecutor<M, HttpRequest> {
    private Call call;
    //=====================>

    @Override
    public Observable<M> buildObservable(final HttpRequest httpRequest) {
        return Observable.create(new Observable.OnSubscribe<M>() {
            @Override
            public void call(Subscriber<? super M> subscriber) {
                try {
                    //---------> Request
                    Request.Builder builder = new Request.Builder();
                    //-----------------> Params
                    builder.tag(httpRequest.getTag());
                    builder.url(httpRequest.getUrlWithQueryParams());
                    builder.get();
                    if (httpRequest.getMethod() != HttpMethod.GET) {
                        String contentType = httpRequest.getContentType();
                        byte[] content = httpRequest.bodyToBytes();
                        builder.method(httpRequest.getMethod().toString(),
                                RequestBody.create(MediaType.parse(contentType), content));
                    }
                    //-----------------> Headers
                    final Map<String, String> headers = httpRequest.getHeaderParams();
                    if (headers != null) {
                        for (String key : headers.keySet()) {
                            String value = headers.get(key);
                            builder.addHeader(key, value);
                        }
                    }
                    //-----------------> Execute
                    OkHttpClient client = new OkHttpClient().newBuilder().writeTimeout(httpRequest.getTimeout(), TimeUnit.MILLISECONDS)
                            .readTimeout(httpRequest.getTimeout(), TimeUnit.MILLISECONDS).build();
                    call = client.newCall(builder.build());
                    Response response = call.execute();
                    String responseStr = response.body().string();
                    onSuccess(subscriber, httpRequest, responseStr);
                } catch (Exception e) {
                    onError(subscriber, httpRequest, e, false);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }

    @Override
    protected void cancelExecutor() {
        if (call != null) {
            call.cancel();
        }
    }

    //=====================>
}
