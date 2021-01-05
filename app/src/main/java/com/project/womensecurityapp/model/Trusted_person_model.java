package com.project.womensecurityapp.model;

public class Trusted_person_model {

    String name;
    String contact;
    String email;
    String address;
    String relation;


    public Trusted_person_model() {

    }

    public Trusted_person_model(String name, String contact, String email, String address, String relation) {
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.address = address;
        this.relation = relation;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }
}
