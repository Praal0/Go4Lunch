package com.example.go4lunch.models.PlacesInfo.PlacesDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Close {
    @SerializedName("day")
    @Expose
    public Integer day;
    @SerializedName("time")
    @Expose
    public String time;

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
