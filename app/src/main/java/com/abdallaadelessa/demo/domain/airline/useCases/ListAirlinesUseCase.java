package com.abdallaadelessa.demo.domain.airline.useCases;


import com.abdallaadelessa.demo.data.airline.model.AirlineModel;
import com.abdallaadelessa.demo.data.airline.repository.remote.AirlinesRemoteRepository;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by Abdalla on 20/10/2016.
 */

public class ListAirlinesUseCase {
    private AirlinesRemoteRepository cloudRepository;

    @Inject
    public ListAirlinesUseCase(AirlinesRemoteRepository airlinesRemoteRepository) {
        this.cloudRepository = airlinesRemoteRepository;
    }

    // ----------------->

    public Observable<List<AirlineModel>> listAirlines() {
        return cloudRepository.listAirlines();
    }

}
