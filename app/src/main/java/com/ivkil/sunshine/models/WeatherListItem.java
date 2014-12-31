package com.ivkil.sunshine.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;


public class WeatherListItem {

    @SerializedName("dt")
    private Date dateTime;
    @SerializedName("temp")
    private Temperature temperature;
    @SerializedName("pressure")
    private double pressure;
    @SerializedName("humidity")
    private int humidity;
    @SerializedName("weather")
    private Weather[] weather;
    @SerializedName("speed")
    private double speed;
    @SerializedName("deg")
    private double direction;

    public Date getDateTime() {
        return dateTime;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public double getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public double getSpeed() {
        return speed;
    }

    public double getDirection() {
        return direction;
    }
}
