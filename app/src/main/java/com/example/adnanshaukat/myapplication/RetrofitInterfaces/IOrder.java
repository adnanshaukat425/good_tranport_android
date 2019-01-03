package com.example.adnanshaukat.myapplication.RetrofitInterfaces;

import com.example.adnanshaukat.myapplication.Modals.Order;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by AdnanShaukat on 23/12/2018.
 */

public interface IOrder {
    public String BASE_URL = "http://" + RetrofitManager.ip + "/" + RetrofitManager.domain + "/api/order/";

    @GET("get_pre_order_form_data")
    Call<Object> get_pre_order_form_data();

    @POST("place_order")
    Call<Order> place_order(@Body Order order);
}
