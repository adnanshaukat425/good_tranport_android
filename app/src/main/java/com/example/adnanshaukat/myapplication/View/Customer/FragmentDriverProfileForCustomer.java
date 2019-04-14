package com.example.adnanshaukat.myapplication.View.Customer;

import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.Adapters.DriverRecyclerViewWrtOrderAdapter;
import com.example.adnanshaukat.myapplication.GlobalClasses.MyApplication;
import com.example.adnanshaukat.myapplication.GlobalClasses.ProgressDialogManager;
import com.example.adnanshaukat.myapplication.Modals.DriverDetailsWrtOrder;
import com.example.adnanshaukat.myapplication.Modals.Notification;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IDriver;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IOrder;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.RetrofitManager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by AdnanShaukat on 31/03/2019.
 */

public class FragmentDriverProfileForCustomer extends Fragment {
    View view;
    CircleImageView driver_profile_image;
    TextView rating;
    EditText driver_name;
    EditText driver_phone_no;
    EditText driver_email;
    Button btn_hire_driver;

    User mUser;
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_driver_profile_for_customer, container, false);
        PopulateUI();

        Bundle argument = getArguments();
        final String driver_id = argument.get("driver_id").toString();
        final String order_id = argument.get("order_id").toString();

        Log.e("DRIVER ID", driver_id);
        Log.e("ORDER ID", order_id);

        getDriversWrtOrder(driver_id);

        MyApplication application = new MyApplication();
        mUser = application.getGlobalUser();

        btn_hire_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Driver_id", driver_id);
                Log.e("Customer_id", Integer.toString(mUser.getUser_id()));
                Log.e("Order_id", order_id);
                requestDriverForOrder(driver_id, order_id);
            }
        });
        return view;
    }

    private void PopulateUI() {
        driver_profile_image = (CircleImageView) view.findViewById(R.id.frag_driver_prof_for_cust_driver_img);
        rating = (TextView) view.findViewById(R.id.frag_driver_prof_for_cust_driver_rating);
        driver_name = (EditText) view.findViewById(R.id.frag_driver_prof_for_cust_driver_name);
        driver_phone_no = (EditText) view.findViewById(R.id.frag_driver_prof_for_cust_driver_phone_no);
        driver_email = (EditText) view.findViewById(R.id.frag_driver_prof_for_cust_driver_email);
        btn_hire_driver = (Button) view.findViewById(R.id.frag_driver_prof_for_cust_btn_hire_driver);
    }

    private void getDriversWrtOrder(String driver_id) {
        progressDialog = ProgressDialogManager.showProgressDialogWithTitle(getContext(), "Loading Driver Details", "Please wait");
        try {
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.connectTimeout(30, TimeUnit.SECONDS);
            client.readTimeout(30, TimeUnit.SECONDS);
            client.writeTimeout(30, TimeUnit.SECONDS);

            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(IDriver.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();

            IDriver api = retrofit.create(IDriver.class);

            Call<Object> call = api.get_driver_details_for_customer(driver_id);

            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    Object response_json = response.body();
                    if (response_json != null) {

                        Log.e("Response Driver Details", response_json.toString());
                        Gson gson = new Gson();
//                        try{
                        JsonObject obj1 = gson.fromJson(response_json.toString(), JsonObject.class);
                        String driver_json = obj1.get("data").toString();
                        Log.e("DRIVER DETAILS JSON", driver_json);
                        Log.e("DRIVER DETAILS Object", obj1.toString());

                        JsonArray obj = gson.fromJson(obj1.get("data").toString(), JsonArray.class);

                        Log.e("LOG OBJ", obj.toString());

                        if(obj.get(0).getAsJsonObject().get("profile_picture").toString().isEmpty()){
                            driver_profile_image.setImageResource(R.drawable.default_profile_image);
                        }
                            else{
                            String image_path =  "http://" + RetrofitManager.ip + "/" + RetrofitManager.domain + "/Images/AppImages/"
                                    + obj.get(0).getAsJsonObject().get("profile_picture").toString();

                            Picasso.with(getContext())
                                    .load(image_path)
                                    .into(driver_profile_image);
                        }
                        Log.e("NAME", obj.get(0).getAsJsonObject().get("name").toString());

                            driver_name.setText(obj.get(0).getAsJsonObject().get("name").toString());
                            driver_phone_no.setText(obj.get(0).getAsJsonObject().get("phone_number").toString());
                            driver_email.setText(obj.get(0).getAsJsonObject().get("email").toString());
//                        }
//                        catch(Exception ex){
//                            Log.e("EX", ex.getMessage() + " " + ex.getStackTrace());
//                            Toast.makeText(getContext(), "An Error Occur, Please Try Again", Toast.LENGTH_SHORT).show();
//                        }
                    } else {
                        Toast.makeText(getContext(), "No drivers found", Toast.LENGTH_SHORT).show();
                    }
                    ProgressDialogManager.closeProgressDialog(progressDialog);
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.e("F getDriversWrtOrder", t.getMessage());
                    Log.e("F getDriversWrtOrder", t.toString());
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    ProgressDialogManager.closeProgressDialog(progressDialog);
                }
            });
        } catch (Exception ex) {
            ProgressDialogManager.closeProgressDialog(progressDialog);
            Log.e("E getDriversWrtOrder", ex.toString());
        }
    }

    private void requestDriverForOrder(String driver_id, String order_id) {
        progressDialog = ProgressDialogManager.showProgressDialogWithTitle(getContext(), "Loading Driver Details", "Please wait");
        try {
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.connectTimeout(30, TimeUnit.SECONDS);
            client.readTimeout(30, TimeUnit.SECONDS);
            client.writeTimeout(30, TimeUnit.SECONDS);

            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(IOrder.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();

            IOrder api = retrofit.create(IOrder.class);

            Call<Object> call = api.request_driver_for_order(order_id, driver_id);

            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    Object response_json = response.body();
                    Log.e("RESPONSE MESSAGE", response.message());
                    if (response_json != null) {
                        Notification notification = new Notification(0, "New order from " + mUser.getFirst_name() + " " +
                                mUser.getLast_name(),
                                mUser.getUser_id(), 0, 0, "FragmentOrdersListForDriver", "driver", "New Order For You");
                        notification.pushNotification(getContext());
                        Toast.makeText(getContext(), "Driver notified Successfully!!!", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getContext(), "An Error Occour, Please Try Again", Toast.LENGTH_SHORT).show();
                    }
                    ProgressDialogManager.closeProgressDialog(progressDialog);
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.e("F requestDriverForOrder", t.getMessage());
                    Log.e("F requestDriverForOrder", t.toString());
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    ProgressDialogManager.closeProgressDialog(progressDialog);
                }
            });
        } catch (Exception ex) {
            ProgressDialogManager.closeProgressDialog(progressDialog);
            Log.e("E requestDriverForOrder", ex.toString());
        }
    }
}
