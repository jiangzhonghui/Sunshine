package com.ivkil.sunshine.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ivan on 12/28/2014.
 */
public class Temperature {
    @SerializedName("max")
    private double high;
    @SerializedName("min")
    private double low;

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }
}
