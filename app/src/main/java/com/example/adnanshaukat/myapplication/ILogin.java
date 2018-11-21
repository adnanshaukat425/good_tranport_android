package com.example.adnanshaukat.myapplication;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by AdnanShaukat on 04/11/2018.
 */

public interface ILogin {

    public String BASE_URL = "http://" + RetrofitManager.ip + "/"  + RetrofitManager.domain + "/api/login/";
    @GET("login_details")
    Call<User> get_login(@Query("email") String email, @Query("password") String password);
    //String ip = "192.168.0.105";//home
//    String BASE_URL = "http://localhost:52687/api/Location/";
//    String BASE_URL = "https://jsonplaceholder.typicode.com/";
//    String BASE_URL = "https://simplifiedcoding.net/demos/";
//
//    @GET("marvel")
//    Call<List<Hero>> getHeroes();

//    @GET("users")
//    Call<List<User>> getUsers();
//
//    @GET("get_location")
//    Call<List<Location>> getLocation();
}
