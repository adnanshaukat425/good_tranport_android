package com.example.adnanshaukat.myapplication.RetrofitInterfaces;

import com.example.adnanshaukat.myapplication.Modals.Order;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by AdnanShaukat on 23/12/2018.
 */

public interface IOrder {
    public String BASE_URL = "http://" + RetrofitManager.ip + "/" + RetrofitManager.domain + "/api/order/";

    @GET("get_pre_order_form_data")
    Call<Object> get_pre_order_form_data(@Query("is_cargo") boolean is_cargo, @Query("is_measurement_unit") boolean is_measurement_unit, @Query("is_container") boolean is_container, @Query("is_source") boolean is_source, @Query("is_container_size") boolean is_container_size, @Query("is_weight_catagory") boolean is_weight_catagory, @Query("is_payment_type") boolean is_payment_type);

    @POST("place_order")
    Call<Order> place_order(@Body Order order);

    @GET("request_driver_for_order")
    Call<Object> request_driver_for_order(@Query("order_id") String order_id, @Query("driver_id") String driver_id);

    @GET("get_all_orders_of_customer")
    Call<Object> get_all_orders_of_customer(@Query("customer_id") int customer_id);
}
