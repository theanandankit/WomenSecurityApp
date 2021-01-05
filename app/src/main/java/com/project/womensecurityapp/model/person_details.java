package com.project.womensecurityapp.model;

public class person_details {

    location_model location;
    person_info info;
    String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public person_details(location_model location, person_info info, String type) {
        this.location = location;
        this.info = info;
        this.type = type;
    }

    public person_details() {

    }

    public person_details(location_model location, person_info info) {
        this.location = location;
        this.info = info;
    }

    public location_model getLocation() {
        return location;
    }

    public void setLocation(location_model location) {
        this.location = location;
    }

    public person_info getInfo() {
        return info;
    }

    public void setInfo(person_info info) {
        this.info = info;
    }
}
