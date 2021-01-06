package com.project.womensecurityapp.model.RetrofitModel;

public class address {

    String streetName;

    String municipalitySubdivision;

    String municipality;

    String countrySecondarySubDivision;

    String postalCode;

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getMunicipalitySubdivision() {
        return municipalitySubdivision;
    }

    public void setMunicipalitySubdivision(String municipalitySubdivision) {
        this.municipalitySubdivision = municipalitySubdivision;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getCountrySecondarySubDivision() {
        return countrySecondarySubDivision;
    }

    public void setCountrySecondarySubDivision(String countrySecondarySubDivision) {
        this.countrySecondarySubDivision = countrySecondarySubDivision;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
