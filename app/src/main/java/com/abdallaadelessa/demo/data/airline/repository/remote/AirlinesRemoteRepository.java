package com.abdallaadelessa.demo.data.airline.repository.remote;

import com.abdallaadelessa.demo.data.airline.model.AirlineModel;

import java.util.List;

import rx.Observable;

/**
 * Created by Abdalla on 20/10/2016.
 */

public interface AirlinesRemoteRepository {
    Observable<List<AirlineModel>> listAirlines();
}
