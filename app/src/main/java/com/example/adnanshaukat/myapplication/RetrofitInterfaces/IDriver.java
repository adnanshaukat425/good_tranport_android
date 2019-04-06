package com.example.adnanshaukat.myapplication.RetrofitInterfaces;

import com.example.adnanshaukat.myapplication.Modals.Order;
import com.example.adnanshaukat.myapplication.Modals.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by AdnanShaukat on 06/12/2018.
 */

public interface IDriver {

    public String BASE_URL = "http://" + RetrofitManager.ip + "/"  + RetrofitManager.domain + "/api/driver/";

    @GET("get_driver_wrt_transporter")
    Call<List<User>> get_all_drivers(@Query("transporter_id") String transporter_id);

    @GET("get_active_driver_wrt_transporter")
    Call<List<User>> get_active_drivers(@Query("transporter_id") String transporter_id);

    @POST("update_drivers_vehicle")
    Call<String> update_drivers_vehicle(@Body String json_string);

    @POST("get_driver_wrt_order")
    Call<Object> get_driver_wrt_order(@Body Order order);

    @GET("get_driver_details_for_customer")
    Call<Object> get_driver_details_for_customer(@Query("driver_id") String driver_id);
}
