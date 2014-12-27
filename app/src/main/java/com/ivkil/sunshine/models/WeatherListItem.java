package com.ivkil.sunshine.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ivan on 12/28/2014.
 */
public class WeatherListItem {
    @SerializedName("dt")
    private long dateTime;

    @SerializedName("temp")
    private Temperature temperature;

    @SerializedName("weather")
    private WeatherItem[] weather;

    public WeatherItem[] getWeather() {
        return weather;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public long getDateTime() {
        return dateTime;
    }
}
