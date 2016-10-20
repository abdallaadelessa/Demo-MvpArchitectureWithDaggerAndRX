package com.abdallaadelessa.demo.domain.airline;


import com.abdallaadelessa.demo.data.airline.model.AirlineModel;
import com.abdallaadelessa.demo.data.airline.repository.AirlinesRepository;
import com.abdallaadelessa.demo.data.airline.repository.di.DaggerAirlineRepositoryComponent;

import java.util.List;

import rx.Observable;

/**
 * Created by Abdalla on 20/10/2016.
 */

public class AirlineUseCases {
    private AirlinesRepository repository;

    public AirlineUseCases() {
        repository = DaggerAirlineRepositoryComponent.create().getCloudRepository();
    }

    public Observable<List<AirlineModel>> getAirlines() {
        return repository.listAirlines();
    }
}
