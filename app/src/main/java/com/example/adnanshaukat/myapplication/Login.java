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
    String ip = "192.168.0.105";//mobile
    public String BASE_URL = "http://" + ip + "/";
    @GET("smart_transport/api/login/login_details")
    Call<Users> get_login();

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
