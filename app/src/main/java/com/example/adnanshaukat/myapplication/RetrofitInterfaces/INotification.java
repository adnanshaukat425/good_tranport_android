package com.example.adnanshaukat.myapplication.RetrofitInterfaces;

import com.example.adnanshaukat.myapplication.Modals.Notification;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by AdnanShaukat on 18/03/2019.
 */

public interface INotification {
    public String BASE_URL = "http://" + RetrofitManager.ip + "/" + RetrofitManager.domain + "/api/notification/";

    @POST("insert_notification")
    Call<Void> InsertNotification(@Body Notification notification);

    @GET("get_notification")
    Call<List<Notification>> get_notification();
}
