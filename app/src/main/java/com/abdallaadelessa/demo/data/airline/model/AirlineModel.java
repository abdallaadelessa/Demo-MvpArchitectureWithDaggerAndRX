package com.abdallaadelessa.demo.data.airline.model;

import org.parceler.Parcel;

/**
 * Created by Abdalla on 18/10/2016.
 */
@Parcel
public class AirlineModel {
    private String site;

    private String defaultName;

    private String logoURL;

    private String phone;

    private String usName;

    private String name;

    private String code;

    public AirlineModel() {
    }

    public AirlineModel(String code) {
        this.code = code;
    }

    public String getSite() {
        return site;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public String getLogoURL() {
        return logoURL;
    }

    public String getPhone() {
        return phone;
    }

    public String getUsName() {
        return usName;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AirlineModel that = (AirlineModel) o;

        return code.equals(that.code);

    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}
