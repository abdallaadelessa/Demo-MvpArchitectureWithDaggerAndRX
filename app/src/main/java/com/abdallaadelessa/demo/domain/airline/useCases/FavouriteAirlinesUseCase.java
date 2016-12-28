package com.abdallaadelessa.demo.domain.airline.useCases;

import com.abdallaadelessa.demo.app.MyApplication;
import com.abdallaadelessa.demo.data.airline.model.AirlineModel;
import com.abdallaadelessa.demo.data.airline.model.FavouriteAirLineStateChanged;
import com.abdallaadelessa.demo.data.airline.repository.local.AirlinesFavRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Abdalla on 23/10/2016.
 */

public class FavouriteAirlinesUseCase {
    private AirlinesFavRepository airlinesFavRepository;

    @Inject
    public FavouriteAirlinesUseCase(AirlinesFavRepository airlinesFavRepository) {
        this.airlinesFavRepository = airlinesFavRepository;
    }

    // ----------------->

    public boolean isFavAirline(AirlineModel airlineModel) {
        return airlinesFavRepository.getFavourites().contains(airlineModel.getCode());
    }

    public void favAirline(AirlineModel airlineModel, boolean isFav) {
        if (isFav) {
            airlinesFavRepository.addToFavourites(airlineModel);
        } else {
            airlinesFavRepository.removeFromFavourites(airlineModel);
        }
        MyApplication.getInstance().getEventBusComponent().getGlobalEventBus().send(FavouriteAirLineStateChanged.newInstance());
    }

    public void toggleFavAirline(AirlineModel airlineModel) {
        if (!isFavAirline(airlineModel)) {
            airlinesFavRepository.addToFavourites(airlineModel);
        } else {
            airlinesFavRepository.removeFromFavourites(airlineModel);
        }
        MyApplication.getInstance().getEventBusComponent().getGlobalEventBus().send(FavouriteAirLineStateChanged.newInstance());
    }

    public Observable<List<AirlineModel>> filterAirlinesAsObservable(final List<AirlineModel> allAirLines, final boolean favOnly) {
        return Observable.create(new Observable.OnSubscribe<List<AirlineModel>>() {
            @Override
            public void call(Subscriber<? super List<AirlineModel>> subscriber) {
                subscriber.onNext(filterAirlines(allAirLines, favOnly));
                subscriber.onCompleted();
            }
        });
    }

    // ----------------->

    private List<AirlineModel> filterAirlines(List<AirlineModel> allAirLines, boolean favOnly) {
        List<AirlineModel> result;
        if (allAirLines == null || allAirLines.isEmpty()) {
            result = new ArrayList<>();
        } else {
            if (favOnly) {
                List<AirlineModel> filteredAirLines = new ArrayList<>();
                for (AirlineModel airLineModel : allAirLines) {
                    if (airlinesFavRepository.getFavourites().contains(airLineModel.getCode())) {
                        filteredAirLines.add(airLineModel);
                    }
                }
                result = filteredAirLines;
            } else {
                result = allAirLines;
            }
        }
        return result;
    }
}
