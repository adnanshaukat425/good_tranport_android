package com.example.adnanshaukat.myapplication.RetrofitInterfaces;

import com.example.adnanshaukat.myapplication.Modals.DashboardGraph;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by AdnanShaukat on 10/03/2019.
 */

public interface IDashboardGraph {
    public String BASE_URL = "http://" + RetrofitManager.ip + "/"  + RetrofitManager.domain + "/api/dashboard_graph/";

    @GET("top_driver_wrt_transporter")
    Call<DashboardGraph> top_driver_wrt_transporter(@Query("period") String period, @Query("transporter_id") int transporter_id, @Query("date_from") String date_from, @Query("date_to") String date_to);

    @GET("get_last_six_month_order_of_driver")
    Call<DashboardGraph> get_last_six_month_order_of_driver(@Query("driver_id") int driver_id);
}