package com.example.adnanshaukat.myapplication.RetrofitInterfaces;

import com.example.adnanshaukat.myapplication.Modals.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by AdnanShaukat on 18/11/2018.
 */

public interface ISignUp {

    public String BASE_URL = "http://" + RetrofitManager.ip + "/" + RetrofitManager.domain + "/api/signup/";

    @POST("/get_signup")
    Call<User> get_signup(@Body User user);
}
