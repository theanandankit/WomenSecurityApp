package com.project.womensecurityapp.model;

public class person_info {

    String name;
    String contact;

    public person_info() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public person_info(String name, String contact) {
        this.name = name;
        this.contact = contact;
    }
}
