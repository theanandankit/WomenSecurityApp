package com.project.womensecurityapp.model.RetrofitModel;

import java.util.ArrayList;

public class PlaceResult {

    ArrayList<safeLocation> results;

    public ArrayList<safeLocation> getResults() {
        return results;
    }

    public void setResults(ArrayList<safeLocation> results) {
        this.results = results;
    }
}
