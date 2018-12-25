package com.example.adnanshaukat.myapplication.RetrofitInterfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by AdnanShaukat on 23/12/2018.
 */

public interface IPreOrderFormData {
    public String BASE_URL = "http://" + RetrofitManager.ip + "/" + RetrofitManager.domain + "/api/order/";

    @GET("get_pre_order_form_data")
    Call<Object> get_pre_order_form_data();
}
