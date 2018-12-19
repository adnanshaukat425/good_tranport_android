package com.example.adnanshaukat.myapplication.RetrofitInterfaces;

import com.example.adnanshaukat.myapplication.Modals.Vehicle;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by AdnanShaukat on 09/12/2018.
 */

public interface IVehicle {
    public String BASE_URL = "http://" + RetrofitManager.ip + "/" + RetrofitManager.domain + "/api/vehicle/";

    @GET("get_vehicle_wrt_transporter")
    Call<List<Vehicle>> get_transporter(@Query("transporter_id") String transporter_id);
}
