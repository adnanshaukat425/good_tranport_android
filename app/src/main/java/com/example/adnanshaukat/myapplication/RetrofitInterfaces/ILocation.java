package com.example.adnanshaukat.myapplication.RetrofitInterfaces;

import com.example.adnanshaukat.myapplication.Modals.Location;
import com.example.adnanshaukat.myapplication.Modals.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by AdnanShaukat on 24/12/2018.
 */

public interface ILocation {
    public String BASE_URL = "http://" + RetrofitManager.ip + "/" + RetrofitManager.domain + "/api/location/";

    @GET("get_all_destination_wrt_source")
    Call<List<Location>> get_all_destination_wrt_source(@Query("source_id") int source_id);

    @POST("update_current_lat_long")
    Call<Object> update_current_lat_long(@Query("user_id") int user_id, @Query("latitude") String latitude, @Query("longitude") String longitude);
}
