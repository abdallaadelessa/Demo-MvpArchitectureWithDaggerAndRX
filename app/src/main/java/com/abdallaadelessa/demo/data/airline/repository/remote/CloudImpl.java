package com.abdallaadelessa.demo.data.airline.repository.remote;

import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.BaseHttpRequestManager;
import com.abdallaadelessa.core.dagger.networkModule.httpRequestManager.executors.okhttpModule.OkHttpExecutor;
import com.abdallaadelessa.demo.app.MyApplication;
import com.abdallaadelessa.demo.data.airline.model.AirlineModel;
import com.google.common.reflect.TypeToken;

import java.util.List;

import rx.Observable;

/**
 * Created by Abdalla on 20/10/2016.
 */
public class CloudImpl implements AirlinesRemoteRepository {
    @Override
    public Observable<List<AirlineModel>> listAirlines() {
        return getHttpRequestManager()
                .<List<AirlineModel>>newHttpRequest()
                .setObservableExecutor(new OkHttpExecutor<List<AirlineModel>>())
                .setTag("aaaaaaa")
                .setUrl("https://www.kayak.com/h/mobileapis/directory/airlines")
                .setType(new TypeToken<List<AirlineModel>>() {
                }.getType()).toObservable();
    }

    private BaseHttpRequestManager getHttpRequestManager() {
        return MyApplication.getInstance().getNetworkComponent().getHttpRequestManager();
    }
}
