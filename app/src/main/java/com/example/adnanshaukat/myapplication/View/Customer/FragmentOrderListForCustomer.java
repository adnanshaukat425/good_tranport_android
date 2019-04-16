package com.example.adnanshaukat.myapplication.View.Customer;

import android.support.constraint.solver.SolverVariable;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.Adapters.OrdersAdapter;
import com.example.adnanshaukat.myapplication.GlobalClasses.ProgressDialogManager;
import com.example.adnanshaukat.myapplication.Modals.Order;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IOrder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
 * Created by AdnanShaukat on 01/04/2019.
 */

public class FragmentOrderListForCustomer extends Fragment {

    View view;
    User mUser;

    RecyclerView recyclerView;
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_list_for_customer, container, false);
        getActivity().setTitle("Orders");

        populateUI();
        Bundle bundle = getArguments();
        mUser = (User) bundle.getSerializable("user");
        getOrders(mUser.getUser_id());

        return view;
    }

    private void populateUI() {
        recyclerView = (RecyclerView) view.findViewById(R.id.frag_order_list_for_customer_recycler_view);
    }

    private void getOrders(int customer_id) {
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

            Call<Object> call = api.get_all_orders_of_customer(customer_id);

            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    Object response_json = response.body();
                    if (response_json != null) {

                        Log.e("Response Driver", response_json.toString());
                        Gson gson = new Gson();
                        try {
                            JsonArray json_array = gson.fromJson(response_json.toString(), JsonArray.class);
//                            String driver_json = obj.get("drivers").toString();
//                            Log.e("DRIVER JSON", driver_json);
                            //JsonArray json_array = gson.fromJson(driver_json, JsonArray.class);

                            List<HashMap<String, String>> order = new ArrayList<HashMap<String, String>>();
                            Log.e("Array Size", json_array.size() + "");
                            for (int i = 0; i < json_array.size(); i++) {
                                HashMap<String, String> or = new HashMap<String, String>();
                                or.put("order_id",json_array.get(i).getAsJsonObject().get("order_id") != null ? json_array.get(i).getAsJsonObject().get("order_id").toString() : "0");
                                or.put("creation_datetime",json_array.get(i).getAsJsonObject().get("creation_datetime") != null ? json_array.get(i).getAsJsonObject().get("creation_datetime").toString() : "0");
                                or.put("source", json_array.get(i).getAsJsonObject().get("Source") != null ? json_array.get(i).getAsJsonObject().get("Source").toString() : "0");
                                or.put("destination", json_array.get(i).getAsJsonObject().get("Destination") != null ? json_array.get(i).getAsJsonObject().get("Destination").toString() : "0");
                                or.put("status", json_array.get(i).getAsJsonObject().get("status_type") != null ? json_array.get(i).getAsJsonObject().get("status_type").toString() : "0");

                                or.put("container_type_id", json_array.get(i).getAsJsonObject().get("container_type_id") != null ? json_array.get(i).getAsJsonObject().get("container_type_id").toString() : "0");
                                or.put("vehicle_type_id", json_array.get(i).getAsJsonObject().get("vehicle_type_id") != null ? json_array.get(i).getAsJsonObject().get("vehicle_type_id").toString() : "0");
                                or.put("source_id", json_array.get(i).getAsJsonObject().get("source_id") != null ? json_array.get(i).getAsJsonObject().get("source_id").toString() : "0");
                                order.add(or);
                            }

                            Log.e("order Size", order.size() + "");

                            OrdersAdapter adapter = new OrdersAdapter(getContext(), order);
                            final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(adapter);


                        } catch (Exception ex) {
                            Toast.makeText(getContext(), "No Orders found!!", Toast.LENGTH_SHORT).show();
                            Log.e("OrderLstForC", ex.toString() + ex.getStackTrace());
                        }
                    } else {
                        Toast.makeText(getContext(), "No Orders found", Toast.LENGTH_SHORT).show();
                    }
                    ProgressDialogManager.closeProgressDialog(progressDialog);

                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.e("FAILURE IN DRIVER", t.getMessage());
                    Log.e("FAILURE IN DRIVER", t.toString());
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    ProgressDialogManager.closeProgressDialog(progressDialog);
                }
            });
        } catch (Exception ex) {
            ProgressDialogManager.closeProgressDialog(progressDialog);
            Log.e("ERROR", ex.toString());
        }
    }
}
