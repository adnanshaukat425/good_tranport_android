package com.example.adnanshaukat.myapplication.GlobalClasses;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IDriver;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.ILocation;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by AdnanShaukat on 05/01/2019.
 */

public class LocationController {

    public void update_lat_long(final Context context, int driver_id, String latitude, String longitude,
                                String driver_name, int transporter_id){
        try {
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.connectTimeout(30, TimeUnit.SECONDS);
            client.readTimeout(30, TimeUnit.SECONDS);
            client.writeTimeout(30, TimeUnit.SECONDS);

            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(ILocation.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();

            ILocation api = retrofit.create(ILocation.class);

            Call<Object> call = api.update_current_lat_long(driver_id, latitude, longitude, driver_name, transporter_id);

            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    Object response_object = response.body();
                    Log.e("LocationController", response.toString());
                    if (response_object != null) {
                        String message = "";
                        Log.e("Location updated", response_object.toString());
                    } else {
                        Log.e("LOCATION UPDATED", "ERROR OCCOUR");
                        //Toast.makeText(context.getApplicationContext() , "Username or password is not correct", Toast.LENGTH_SHORT).show();
                    }
                    //ProgressDialogManager.closeProgressDialog(progressDialog);
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.e("AAFAILURE", t.getMessage());
                    Log.e("AAFAILURE", t.toString());
                    Toast.makeText(context.getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    //ProgressDialogManager.closeProgressDialog(progressDialog);
                }
            });
        } catch (Exception ex) {
            Log.e("ERROR", ex.toString());
        }
    }
}
