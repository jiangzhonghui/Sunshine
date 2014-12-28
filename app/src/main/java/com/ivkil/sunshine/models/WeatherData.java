package com.ivkil.sunshine.models;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ivan on 12/28/2014.
 */
public class WeatherData {

    private static final String LOG_TAG = WeatherData.class.getSimpleName();

    public final static int TYPE_METRIC = 0;
    public final static int TYPE_IMPERIAL = 1;

    private int unitsType;

    @SerializedName("list")
    private WeatherListItem[] list;

    public WeatherListItem[] getList() {
        return list;
    }

    public void setUnitsType(int unitsType) {
        this.unitsType = unitsType;
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
        if (unitsType == TYPE_IMPERIAL) {
            high = (high * 1.8) + 32;
            low = (low * 1.8) + 32;
        } else if (unitsType != TYPE_METRIC) {
            Log.d(LOG_TAG, "Units type not found: " + unitsType);
        }

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
