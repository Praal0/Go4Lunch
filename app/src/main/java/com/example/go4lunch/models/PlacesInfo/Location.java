package com.example.go4lunch.models.PlacesInfo;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location {

    public Location(Double lat,Double lng){
        this.lat = lat;
        this.lng = lng;
    }
    @SerializedName("lat")
    @Expose
    public Double lat;
    @SerializedName("lng")
    @Expose
    public Double lng;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}