package com.example.adnanshaukat.myapplication;

import retrofit2.Call;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by AdnanShaukat on 04/11/2018.
 */

public interface Login {

    @GET("login/login_details")
    Call<Users> get_login(@Query("email") String email, @Query("password") String password);
}
