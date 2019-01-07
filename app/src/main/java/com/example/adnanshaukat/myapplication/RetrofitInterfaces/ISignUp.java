package com.example.adnanshaukat.myapplication.RetrofitInterfaces;

import com.example.adnanshaukat.myapplication.Modals.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by AdnanShaukat on 18/11/2018.
 */

public interface ISignUp {

    public String BASE_URL = "http://" + RetrofitManager.ip + "/" + RetrofitManager.domain + "/api/signup/";

    @POST("get_signup")
    Call<User> get_signup(@Body User user);

    @POST("check_if_email_already_present")
    Call<User> check_if_email_already_present(@Body User user);

    @GET("add_driver_to_transporter")
    Call<String> add_driver_to_transporter(@Query("driver_id") String driver_id, @Query("transporter_id") String transporter_id);
}
