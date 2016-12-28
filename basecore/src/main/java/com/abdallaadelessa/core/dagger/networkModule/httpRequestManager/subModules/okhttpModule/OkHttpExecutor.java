package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.subModules.okhttpModule;

import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpExecutor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.BaseRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.HttpRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.MultiPartRequest;
import com.abdallaadelessa.core.utils.ValidationUtils;
import com.android.volley.RetryPolicy;
import com.android.volley.error.AuthFailureError;
import com.android.volley.request.StringRequest;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by abdullah on 12/28/16.
 */

public class OkHttpExecutor<M> extends BaseHttpExecutor<M, HttpRequest> {
    private OkHttpClient client;
    private Call call;

    public OkHttpExecutor(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public Observable<M> buildObservable(final HttpRequest httpRequest) {
        return Observable.create(new Observable.OnSubscribe<M>() {
            @Override
            public void call(Subscriber<? super M> subscriber) {
                try {
                    final String tag = httpRequest.getTag();
                    final String url = httpRequest.getUrl();
                    final int method = httpRequest.getMethod();
                    final Map<String, String> headers = httpRequest.getHeaders();
                    final Map<String, String> params = httpRequest.getParams();
                    //---------> Request
                    Request.Builder builder = new Request.Builder();
                    //-----------------> Params
                    if (!ValidationUtils.isStringEmpty(tag)) builder.tag(tag);
                    builder.url(url);
                    //TODO builder.method()
                    //-----------------> Headers
                    if (headers != null) {
                        for (String key : headers.keySet()) {
                            String value = headers.get(key);
                            builder.addHeader(key, value);
                        }
                    }
                    //-----------------> Params
                    if (httpRequest.getParams() != null) {
                        FormBody.Builder formBuilder = new FormBody.Builder();
                        for (String key : params.keySet()) {
                            String value = params.get(key);
                            formBuilder.add(key, value);
                        }
                        RequestBody formBody = formBuilder.build();
                        builder.post(formBody);
                    }
                    //-----------------> Body
                    if (httpRequest.hasBody()) {
                        builder.post(RequestBody.create(MediaType.parse(BaseRequest.CONTENT_TYPE_JSON), httpRequest.getBody()));
                    }
                    //-----------------> Execute
                    call = client.newCall(builder.build());
                    Response response = call.execute();
                    String responseStr = response.body().string();
                    M m = parse(responseStr, httpRequest);
                    onSuccess(httpRequest, subscriber, m);
                } catch (Exception e) {
                    onError(httpRequest, subscriber, e, false);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }

    @Override
    protected void cancelRequest(HttpRequest request) {
        if (call != null && request.isCancelOnUnSubscribe()) {
            call.cancel();
        }
    }
}
