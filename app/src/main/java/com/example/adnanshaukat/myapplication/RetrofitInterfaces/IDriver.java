package com.example.adnanshaukat.myapplication.RetrofitInterfaces;

import com.example.adnanshaukat.myapplication.Modals.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by AdnanShaukat on 06/12/2018.
 */

public interface IDriver {

    public String BASE_URL = "http://" + RetrofitManager.ip + "/"  + RetrofitManager.domain + "/api/dirver/";

    @GET("get_driver_wrt_transporter")
    Call<List<User>> get_all_drivers(@Query("transporter_id") String transporter_id);
}
