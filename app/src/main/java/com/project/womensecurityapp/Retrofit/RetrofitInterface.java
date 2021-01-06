package com.project.womensecurityapp.Retrofit;

import com.project.womensecurityapp.model.RetrofitModel.safeLocation;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitInterface {

    @GET("{parameter}.JSON?key={key}")
    Call<ArrayList<safeLocation>> getData(@Path("parameter") String parameter, @Path("ker") String key, @Query("lat") String lat,@Query("lon") String lon);

}
