package com.project.womensecurityapp.model;

public class User_residential_details {
    String name;
    String contact_no;
    String country;
    String city;
    String street;
    String house_no;
    String email;

    public User_residential_details() {
    }

    public User_residential_details(String name, String email, String contact_no, String country, String city, String street, String house_no) {
        this.name = name;
        this.email = email;
        this.contact_no = contact_no;
        this.country = country;
        this.city = city;
        this.street = street;
        this.house_no = house_no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouse_no() {
        return house_no;
    }

    public void setHouse_no(String house_no) {
        this.house_no = house_no;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
