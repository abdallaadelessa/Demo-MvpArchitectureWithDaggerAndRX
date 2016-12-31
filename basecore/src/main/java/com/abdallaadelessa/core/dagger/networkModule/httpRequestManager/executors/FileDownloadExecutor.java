package com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.executors;

import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.requests.HttpRequest;
import com.abdallaadelessa.core.dagger.networkModule.subModules.okhttpModule.DaggerOkHttpComponent;
import com.abdallaadelessa.core.model.FileDownloadModel;
import com.google.gson.Gson;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by abdullah on 12/31/16.
 */

public class FileDownloadExecutor extends OkHttpExecutor<FileDownloadModel> {
    private static final int DOWNLOAD_CHUNK_SIZE = 8024;

    //=====================>

    @Override
    public Observable<FileDownloadModel> buildObservable(final HttpRequest httpRequest) {
        return Observable.create(new Observable.OnSubscribe<FileDownloadModel>() {
            @Override
            public void call(Subscriber<? super FileDownloadModel> subscriber) {
                try {

                    String downloadPath = httpRequest.getDownloadPath();
                    if (checkIfShouldCacheResponse(subscriber, httpRequest)) {
                        return;
                    }
                    String urlWithQueryParams = httpRequest.getUrlWithQueryParams();
                    Request request = new Request.Builder().url(urlWithQueryParams).build();
                    OkHttpClient okHttpClient = DaggerOkHttpComponent.create().getOkHttpClientBuilder().writeTimeout(httpRequest.getTimeout(), TimeUnit.MILLISECONDS)
                            .readTimeout(httpRequest.getTimeout(), TimeUnit.MILLISECONDS).build();
                    call = okHttpClient.newCall(request);
                    Response response = call.execute();
                    ResponseBody body = response.body();
                    long contentLength = body.contentLength();
                    BufferedSource source = body.source();
                    File file = new File(downloadPath);
                    BufferedSink sink = Okio.buffer(Okio.sink(file));
                    long totalRead = 0;
                    long read;
                    FileDownloadModel fileDownloadModel = new FileDownloadModel();
                    fileDownloadModel.setDownloadFilePath(downloadPath);
                    while ((read = (source.read(sink.buffer(), DOWNLOAD_CHUNK_SIZE))) != -1) {
                        totalRead += read;
                        int progress = (int) ((totalRead * 100) / contentLength);
                        fileDownloadModel.setProgress(progress);
                        fileDownloadModel.setFileSize(contentLength);
                        fileDownloadModel.setCurrentRead(totalRead);
                        onNext(subscriber, httpRequest, new Gson().toJson(fileDownloadModel));
                    }
                    sink.writeAll(source);
                    sink.flush();
                    sink.close();
                    onCompleted(subscriber, httpRequest);
                } catch (Exception e) {
                    onError(subscriber, httpRequest, e, false);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }

    //=====================>

    private boolean checkIfShouldCacheResponse(Subscriber<? super FileDownloadModel> subscriber, HttpRequest httpRequest) {
        String downloadPath = httpRequest.getDownloadPath();
        if (httpRequest.isShouldCacheResponse() && new File(downloadPath).exists()) {
            FileDownloadModel fileDownloadModel = new FileDownloadModel();
            fileDownloadModel.setDownloadFilePath(downloadPath);
            fileDownloadModel.setFileSize(new File(downloadPath).length());
            fileDownloadModel.setCurrentRead(new File(downloadPath).length());
            fileDownloadModel.setProgress(100);
            onNext(subscriber, httpRequest, new Gson().toJson(fileDownloadModel));
            onCompleted(subscriber, httpRequest);
            return true;
        }
        return false;
    }
}
