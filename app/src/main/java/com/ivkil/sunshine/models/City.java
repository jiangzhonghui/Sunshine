package com.ivkil.sunshine.models;

import android.location.Location;

import com.google.gson.annotations.SerializedName;

public class City {

    @SerializedName("name")
    private String name;
    @SerializedName("coord")
    private Location location;

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

}
