package com.example.adnanshaukat.myapplication.RetrofitInterfaces;

import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.Modals.Vehicle;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by AdnanShaukat on 09/12/2018.
 */

public interface IVehicleWrtDriver {

    public String BASE_URL = "http://" + RetrofitManager.ip + "/" + RetrofitManager.domain + "/api/vehicle/";

    @GET("get_unassigned_vehicle_wrt_transporter")
    Call<List<Vehicle>> get_unassigned_vehicle_wrt_transporter(@Query("transporter_id") String transporter_id);

    @GET("get_vehicle_wrt_transporter_with_driver")
    Call<List<Vehicle>> get_vehicle_wrt_transporter_with_driver(@Query("transporter_id") String transporter_id, @Query("driver_id") String driver_id);
}
