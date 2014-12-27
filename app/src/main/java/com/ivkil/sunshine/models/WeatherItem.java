package com.ivkil.sunshine.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ivan on 12/28/2014.
 */
public class WeatherItem {
    @SerializedName("main")
    private String describtion;

    public String getDescribtion() {
        return describtion;
    }
}
