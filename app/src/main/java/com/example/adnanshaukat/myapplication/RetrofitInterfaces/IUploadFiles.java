package com.example.adnanshaukat.myapplication.RetrofitInterfaces;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by AdnanShaukat on 03/03/2019.
 */

public interface IUploadFiles {
    public String BASE_URL = "http://" + RetrofitManager.ip + "/" + RetrofitManager.domain + "/api/";

    @Multipart
    @POST("upload")
    Call<Void> upload(@Part MultipartBody.Part file);
}
