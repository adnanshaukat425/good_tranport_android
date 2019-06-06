package com.example.adnanshaukat.myapplication.RetrofitInterfaces;

import com.example.adnanshaukat.myapplication.Modals.Notification;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by AdnanShaukat on 18/03/2019.
 */

public interface INotification {
    public String BASE_URL = "http://" + RetrofitManager.ip + "/" + RetrofitManager.domain + "/api/notification/";

    @POST("insert_notification")
    Call<Void> InsertNotification(@Body Notification notification);

    @GET("get_broadcast_notification")
    Call<List<Notification>> get_broadcast_notification(@Query("user_id") Integer user_id);

    @POST("set_notification_to_pushed")
    Call<Void> set_notification_to_pushed(@Body ArrayList<Integer> notification_ids);
}
