package com.example.adnanshaukat.myapplication.RetrofitInterfaces;

import com.example.adnanshaukat.myapplication.Modals.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by AdnanShaukat on 19/12/2018.
 */

public interface IUser {

    public String BASE_URL = "http://" + RetrofitManager.ip + "/" + RetrofitManager.domain + "/api/user/";

    @POST("change_password")
    Call<String> change_password(@Query("user_id") int user_id, @Query("password") String password);
}
