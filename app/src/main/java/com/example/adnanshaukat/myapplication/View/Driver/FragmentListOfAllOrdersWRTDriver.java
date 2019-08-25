package com.example.adnanshaukat.myapplication.View.Driver;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.Adapters.OrdersAdapter;
import com.example.adnanshaukat.myapplication.GlobalClasses.ProgressDialogManager;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IOrder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by AdnanShaukat on 12/05/2019.
 */

public class FragmentListOfAllOrdersWRTDriver extends Fragment {

    View view;
    User mUser;

    RecyclerView recyclerView;
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_of_order_wrt_driver_tab_all_drivers, container, false);

        Bundle arguments = getArguments();
        mUser = (User)arguments.getSerializable("user");
        populateUI();

        getAllOrders(mUser.getUser_id(), "4");
        return view;
    }

    private void populateUI() {
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_list_of_all_order_wrt_driver_recycler_view);
    }

    private void getAllOrders(final int driver_id, String status_id) {
        progressDialog = ProgressDialogManager.showProgressDialogWithTitle(getContext(), "Loading Orders", "Please wait");
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

            Log.e("driver Id", driver_id + "");

            Call<Object> call = api.get_order_wrt_driver_and_status_id(driver_id, status_id);

            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    Log.e("AllOrderResponse", response.toString());
                    Object response_json = response.body();

                    if (response_json != null)
                    {
                        Log.e("Response Driver", response_json.toString());
                        Gson gson = new Gson();
                        try {
                            JsonArray json_array = gson.fromJson(response_json.toString(), JsonArray.class);
//                          String driver_json = obj.get("drivers").toString();
                            Log.e("DRIVER JSON", json_array.get(0).getAsJsonObject().get("order_id").toString().replace("\"", ""));
                            //JsonArray json_array = gson.fromJson(driver_json, JsonArray.class);List<HashMap<String, String>> order = new ArrayList<HashMap<String, String>>();
                            List<HashMap<String, String>> order = new ArrayList<HashMap<String, String>>();
                            Log.e("Array Size", json_array.size() + "");
                            for (int i = 0; i < json_array.size(); i++) {
                                HashMap<String, String> or = new HashMap<String, String>();
                                or.put("order_id", json_array.get(i).getAsJsonObject().get("order_id") != null ? json_array.get(i).getAsJsonObject().get("order_id").toString().replace("\"", "") : "0");
                                or.put("order_detail_id", json_array.get(i).getAsJsonObject().get("order_detail_id") != null ? json_array.get(i).getAsJsonObject().get("order_detail_id").toString().replace("\"", "") : "0");
                                or.put("labour_quantity", json_array.get(i).getAsJsonObject().get("labour_quantity") != null ? json_array.get(i).getAsJsonObject().get("labour_quantity").toString().replace("\"", "") : "0");
                                or.put("labour_cost", json_array.get(i).getAsJsonObject().get("labour_cost") != null ? json_array.get(i).getAsJsonObject().get("labour_cost").toString().replace("\"", "") : "0");
                                or.put("customer_id", json_array.get(i).getAsJsonObject().get("customer_id") != null ? json_array.get(i).getAsJsonObject().get("customer_id").toString().replace("\"", "") : "0");
                                or.put("customer_name", json_array.get(i).getAsJsonObject().get("customer_name") != null ? json_array.get(i).getAsJsonObject().get("customer_name").toString().replace("\"", "") : "");
                                or.put("creation_datetime", json_array.get(i).getAsJsonObject().get("creation_datetime") != null ? json_array.get(i).getAsJsonObject().get("creation_datetime").toString().replace("\"", "") : "0");
                                or.put("source", json_array.get(i).getAsJsonObject().get("Source") != null ? json_array.get(i).getAsJsonObject().get("Source").toString().replace("\"", "") : "0");
                                or.put("destination", json_array.get(i).getAsJsonObject().get("Destination") != null ? json_array.get(i).getAsJsonObject().get("Destination").toString().replace("\"", "") : "0");
                                or.put("status", json_array.get(i).getAsJsonObject().get("status_type") != null ? json_array.get(i).getAsJsonObject().get("status_type").toString().replace("\"", "") : "0");
                                or.put("description", json_array.get(i).getAsJsonObject().get("description") != null ? json_array.get(i).getAsJsonObject().get("description").toString().replace("\"", "") : "");
                                or.put("profile_picture", json_array.get(i).getAsJsonObject().get("profile_picture") != null ? json_array.get(i).getAsJsonObject().get("profile_picture").toString().replace("\"", "") : "");

                                or.put("container_type_id", json_array.get(i).getAsJsonObject().get("container_type_id") != null ? json_array.get(i).getAsJsonObject().get("container_type_id").toString().replace("\"", "") : "0");
                                or.put("vehicle_type_id", json_array.get(i).getAsJsonObject().get("vehicle_type_id") != null ? json_array.get(i).getAsJsonObject().get("vehicle_type_id").toString().replace("\"", "") : "0");
                                or.put("source_id", json_array.get(i).getAsJsonObject().get("source_id") != null ? json_array.get(i).getAsJsonObject().get("source_id").toString().replace("\"", "") : "0");
                                or.put("destination_id", json_array.get(i).getAsJsonObject().get("destination_id") != null ? json_array.get(i).getAsJsonObject().get("destination_id").toString().replace("\"", "") : "0");
                                order.add(or);
                            }

                            Log.e("order Size", order.size() + "");

                            OrdersAdapter adapter = new OrdersAdapter(getContext(), order, "driver", "all_order", mUser);
                            final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(adapter);

                        } catch (Exception ex) {
                            Toast.makeText(getContext(), "No Orders found!!", Toast.LENGTH_SHORT).show();
                            Log.e("OrderLstForAll", ex.toString() + ex.getStackTrace());
                        }
                    } else
                    {
                        Toast.makeText(getContext(), "No Orders found", Toast.LENGTH_SHORT).show();
                    }

                    ProgressDialogManager.closeProgressDialog(progressDialog);
                }
                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.e("FAILURE", t.getMessage());
                    Log.e("FAILURE", t.toString());
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    ProgressDialogManager.closeProgressDialog(progressDialog);
                }
            });
        }
        catch (Exception ex){
            ProgressDialogManager.closeProgressDialog(progressDialog);
            Log.e("ERROR", ex.toString());
        }
    }
}

