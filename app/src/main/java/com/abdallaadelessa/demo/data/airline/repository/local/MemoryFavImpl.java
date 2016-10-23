package com.abdallaadelessa.demo.data.airline.repository.local;

import com.abdallaadelessa.core.app.BaseCoreApp;
import com.abdallaadelessa.demo.data.airline.model.AirlineModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abdalla on 23/10/2016.
 */

public class MemoryFavImpl implements AirlinesFavRepository {
    private static final String FAV_KEY = "Fav_Key";

    @Override
    public void addToFavourites(AirlineModel airlineModel) {
        List<String> favourites = getFavourites();
        favourites.add(airlineModel.getCode());
        BaseCoreApp.getAppComponent().getMemoryCache().putEntry(FAV_KEY, favourites);
    }

    @Override
    public void removeFromFavourites(AirlineModel airlineModel) {
        List<String> favourites = getFavourites();
        favourites.remove(airlineModel.getCode());
        BaseCoreApp.getAppComponent().getMemoryCache().putEntry(FAV_KEY, favourites);
    }

    @Override
    public List<String> getFavourites() {
        return (List<String>) BaseCoreApp.getAppComponent().getMemoryCache().getEntry(FAV_KEY, new ArrayList<String>());
    }
}
