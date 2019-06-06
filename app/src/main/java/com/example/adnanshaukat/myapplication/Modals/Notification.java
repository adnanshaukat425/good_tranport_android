package com.example.adnanshaukat.myapplication.Modals;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.RetrofitInterfaces.INotification;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by AdnanShaukat on 18/03/2019.
 */

public class Notification {
    int notification_id;
    String notification_message;
    int user_id;
    int is_seen;
    int is_pushed;
    String redirected_page;
    String notification_for;
    String notification_title;
    String notification_type;
    int notification_for_user_id;

    public Notification(){}

    public Notification(int notification_id, String notification_message, int user_id, int is_seen, int is_pushed, String redirected_page,
                        String notification_for, String notification_title, String notification_type, int notification_for_user_id) {
        this.notification_id = notification_id;
        this.notification_message = notification_message;
        this.user_id = user_id;
        this.is_seen = is_seen;
        this.is_pushed = is_pushed;
        this.redirected_page = redirected_page;
        this.notification_for = notification_for;
        this.notification_title = notification_title;
        this.notification_type = notification_type;
        this.notification_for_user_id = notification_for_user_id;
    }

    public int getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(int notification_id) {
        this.notification_id = notification_id;
    }

    public String getNotification_message() {
        return notification_message;
    }

    public void setNotification_message(String notification_message) {
        this.notification_message = notification_message;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getIs_seen() {
        return is_seen;
    }

    public void setIs_seen(int is_seen) {
        this.is_seen = is_seen;
    }

    public int getIs_pushed() {
        return is_pushed;
    }

    public void setIs_pushed(int is_pushed) {
        this.is_pushed = is_pushed;
    }

    public String getRedirected_page() {
        return redirected_page;
    }

    public void setRedirected_page(String redirected_page) {
        this.redirected_page = redirected_page;
    }

    public String getNotification_for() {
        return notification_for;
    }

    public void setNotification_for(String notification_for) {
        this.notification_for = notification_for;
    }

    public String getNotification_title() {
        return notification_title;
    }

    public void setNotification_title(String notification_title) {
        this.notification_title = notification_title;
    }

    public void pushNotification(Context context){
        InsertNotification(this, context);
    }

    public String getNotification_type() {
        return notification_type;
    }

    public void setNotification_type(String notification_type) {
        this.notification_type = notification_type;
    }

    public int getNotification_for_user_id() {
        return notification_for_user_id;
    }

    public void setNotification_for_user_id(int notification_for_user_id) {
        this.notification_for_user_id = notification_for_user_id;
    }

    public void InsertNotification(Notification notification, final Context mContext){
        try {
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.connectTimeout(30, TimeUnit.SECONDS);
            client.readTimeout(30, TimeUnit.SECONDS);
            client.writeTimeout(30, TimeUnit.SECONDS);

            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(INotification.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();

            INotification api = retrofit.create(INotification.class);

            Call<Void> call = api.InsertNotification(notification);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.e("RESPONSE BODY", response.message());
                    Log.e("RESPONSE BODY", response + "");
                    Toast.makeText(mContext, "Driver will be notified!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("FAILURE", t.getMessage());
                    Log.e("FAILURE", t.toString());
                    Toast.makeText(mContext, "Failure: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    //ProgressDialogManager.closeProgressDialog(progressDialog);
                }
            });
        } catch (Exception ex) {
            Log.e("ERROR", ex.toString());
            //ProgressDialogManager.closeProgressDialog(progressDialog);
            Toast.makeText(mContext, "Some error occour, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    public void ChangeNotificationToPushed(ArrayList<Integer> notification_ids, final Context mContext){
        try {
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.connectTimeout(30, TimeUnit.SECONDS);
            client.readTimeout(30, TimeUnit.SECONDS);
            client.writeTimeout(30, TimeUnit.SECONDS);

            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(INotification.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();

            INotification api = retrofit.create(INotification.class);

            Call<Void> call = api.set_notification_to_pushed(notification_ids);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.e("PushedNotification", response.message());
                    Log.e("PushedNotification", response + "");
                    //Toast.makeText(mContext, "Driver will be notified!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("FAILURE", t.getMessage());
                    Log.e("FAILURE", t.toString());
                    Toast.makeText(mContext, "Failure: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    //ProgressDialogManager.closeProgressDialog(progressDialog);
                }
            });
        } catch (Exception ex) {
            Log.e("ERROR", ex.toString());
            //ProgressDialogManager.closeProgressDialog(progressDialog);
            Toast.makeText(mContext, "Some error occour, please try again", Toast.LENGTH_SHORT).show();
        }
    }
}
