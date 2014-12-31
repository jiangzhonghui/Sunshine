package com.ivkil.sunshine.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ivan on 12/28/2014.
 */
public class Weather {

    @SerializedName("id")
    private long id;
    @SerializedName("main")
    private String describtion;

    public long getId() {
        return id;
    }

    public String getDescribtion() {
        return describtion;
    }
}
