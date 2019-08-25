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

    @GET("update_current_lat_long")
    Call<Object> update_current_lat_long(@Query("driver_id") int driver_id, @Query("latitude") String latitude,
                                         @Query("longitude") String longitude, @Query("driver_name") String driver_name,
                                         @Query("transporter_id") int transporter_id);

    @GET("get_driver_latLng_from_transporter_id")
    Call<Object> get_driver_latLng_from_transporter_id(@Query("transporter_id") int transporter_id);
}
