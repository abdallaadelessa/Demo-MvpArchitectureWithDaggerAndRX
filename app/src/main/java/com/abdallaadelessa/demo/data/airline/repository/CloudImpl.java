package com.abdallaadelessa.demo.data.airline.repository;

import com.abdallaadelessa.core.dagger.networkModule.builders.HttpRequest;
import com.abdallaadelessa.demo.app.MyApplication;
import com.abdallaadelessa.demo.data.airline.model.AirlineModel;
import com.google.common.reflect.TypeToken;

import java.util.List;

import rx.Observable;

/**
 * Created by Abdalla on 20/10/2016.
 */
public class CloudImpl implements AirlinesRepository {
    @Override
    public Observable<List<AirlineModel>> listAirlines() {
        return getHttpRequestBuilder()
                .tag("aaaaaaa")
                .url("https://www.kayak.com/h/mobileapis/directory/airlines")
                .type(new TypeToken<List<AirlineModel>>() {
                }.getType())
                .build();
    }

    private HttpRequest.Builder getHttpRequestBuilder() {
        return MyApplication.getAppComponent().getHttpRequestBuilder();
    }
}
