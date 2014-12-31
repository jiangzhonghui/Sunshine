package com.ivkil.sunshine.models;

import com.google.gson.annotations.SerializedName;


public class Temperature {

    @SerializedName("min")
    private double low;
    @SerializedName("max")
    private double high;

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }
}
