package com.abdallaadelessa.demo.data.airline.repository.local;

import com.abdallaadelessa.demo.data.airline.model.AirlineModel;

import java.util.List;

/**
 * Created by Abdalla on 23/10/2016.
 */

public interface AirlinesFavRepository {
    void addToFavourites(AirlineModel airlineModel);

    void removeFromFavourites(AirlineModel airlineModel);

    List<String> getFavourites();
}
