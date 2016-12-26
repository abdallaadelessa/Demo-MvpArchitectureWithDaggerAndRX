package com.abdallaadelessa.demo.data.airline.util;

import com.abdallaadelessa.demo.data.airline.model.AirlineModel;

/**
 * Created by Abdalla on 20/10/2016.
 */

public class AirlinesUtils {
    public static String getFullLogoUrl(AirlineModel airLineModel) {
        if (airLineModel == null) return "";
        return "https://a1.r9cdn.net" + airLineModel.getLogoURL();
    }
}
