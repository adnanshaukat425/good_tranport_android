package com.example.adnanshaukat.myapplication.RetrofitInterfaces;

import com.example.adnanshaukat.myapplication.Modals.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by AdnanShaukat on 04/11/2018.
 */

public interface ILogin {
   public String BASE_URL = "http://" + RetrofitManager.ip + "/" + RetrofitManager.domain + "/api/login/";

    @GET("login_details")
    Call<User> get_login(@Query("email") String email, @Query("password") String password);
}