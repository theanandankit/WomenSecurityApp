package com.project.womensecurityapp.Report;

public class ModelReport {

    String time;
    String place;
    String latitude;
    String longitude;

    public ModelReport(String time, String place, String latitude, String longitude) {
        this.time = time;
        this.place = place;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public ModelReport() {

    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
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
