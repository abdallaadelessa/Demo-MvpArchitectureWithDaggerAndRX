package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.subModules.okhttpModule;

import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpExecutor;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.BaseRequest;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.HttpRequest;
import com.abdallaadelessa.core.utils.ValidationUtils;

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

    public OkHttpExecutor() {
        client = DaggerOkHttpComponent.create().getOkHttpClient();
    }

    //=====================>

    @Override
    public Observable<M> buildObservable(final HttpRequest httpRequest) {
        return Observable.create(new Observable.OnSubscribe<M>() {
            @Override
            public void call(Subscriber<? super M> subscriber) {
                try {
                    final String tag = httpRequest.getTag();
                    final String url = httpRequest.getUrl();
                    final Map<String, String> headers = httpRequest.getHeaderParams();
                    final Map<String, String> params = httpRequest.getFormParams();
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
                    if (httpRequest.getFormParams() != null) {
                        FormBody.Builder formBuilder = new FormBody.Builder();
                        for (String key : params.keySet()) {
                            String value = params.get(key);
                            formBuilder.add(key, value);
                        }
                        RequestBody formBody = formBuilder.build();
                        builder.post(formBody);
                    }
                    //-----------------> Body
                    if (httpRequest.hasJsonBody()) {
                        builder.post(RequestBody.create(MediaType.parse(BaseRequest.CONTENT_TYPE_JSON), httpRequest.getJsonBody()));
                    }
                    //-----------------> Execute
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
