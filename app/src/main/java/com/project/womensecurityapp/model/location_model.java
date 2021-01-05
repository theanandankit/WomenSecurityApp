package com.project.womensecurityapp.model;

public class location_model {

    String latitude;
    String longitude;

    public location_model(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public location_model() {

    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
