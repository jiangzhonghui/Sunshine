package com.ivkil.sunshine.models;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ivan on 12/28/2014.
 */
public class WeatherData {
    @SerializedName("list")
    private WeatherListItem[] list;

    public WeatherListItem[] getList() {
        return list;
    }

    public String[] getForecastString() {
        String[] resultStrs = new String[list.length];
        for (int i = 0; i < list.length; i++) {
            Temperature temperature = list[i].getTemperature();
            String highAndLow = formatHighLows(temperature.getHigh(), temperature.getLow());
            String description = list[i].getWeather()[0].getDescribtion();
            resultStrs[i] = getReadableDateString(list[i].getDateTime()) + " - " + description + " - " + highAndLow;
        }
        return resultStrs;
    }

    private String formatHighLows(double high, double low) {
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    private String getReadableDateString(long time) {
        Date date = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("E, MMM d", Locale.ENGLISH);
        return format.format(date).toString();
    }

}
