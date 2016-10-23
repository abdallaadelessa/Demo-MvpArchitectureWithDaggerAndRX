package com.abdallaadelessa.demo.domain.airline;


import com.abdallaadelessa.demo.data.airline.model.AirlineModel;
import com.abdallaadelessa.demo.data.airline.repository.AirlinesRepository;
import com.abdallaadelessa.demo.data.airline.repository.di.DaggerAirlineRepositoryComponent;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by Abdalla on 20/10/2016.
 */

public class ListAirlinesUseCase {
    AirlinesRepository cloudRepository;

    @Inject
    public ListAirlinesUseCase() {
        cloudRepository = DaggerAirlineRepositoryComponent.create().getCloudRepository();
    }

    public Observable<List<AirlineModel>> listAirlines() {
        return cloudRepository.listAirlines();
    }
}
