package com.example.adnanshaukat.myapplication.View.Customer;

import android.support.design.widget.FloatingActionButton;
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

import com.example.adnanshaukat.myapplication.Adapters.DriverRecyclerViewWrtOrderAdapter;
import com.example.adnanshaukat.myapplication.GlobalClasses.ProgressDialogManager;
import com.example.adnanshaukat.myapplication.Modals.DriverDetailsWrtOrder;
import com.example.adnanshaukat.myapplication.Modals.Order;
import com.example.adnanshaukat.myapplication.Modals.PaymentType;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IDriver;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by AdnanShaukat on 05/01/2019.
 */

public class FragmentListDriverWRTOrder extends Fragment {

    View view;
    RecyclerView rv;
    ProgressDialog progressDialog;
    Order order;

    String show_wrt_order_id = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_driver_wrt_order, container, false);
        getActivity().setTitle("Drivers");

        populateUI();
        Bundle argument = getArguments();
        String order_details =  "";
        if (argument != null) {
            show_wrt_order_id = argument.getString("show_wrt_order_id");
            Log.e("FragLstDriWrtOrd", show_wrt_order_id);
            if(show_wrt_order_id == "false"){
                order_details = argument.getString("order");
                Log.e("order_details", order_details);
                Gson gson = new Gson();
                order = gson.fromJson(order_details, Order.class);
            }
            else if(show_wrt_order_id == "true"){
                order = (Order)argument.getSerializable("order");
            }
        }

        if(show_wrt_order_id == "true"){
            final FragmentListDriverWRTOrder fragmentListDriverWRTOrder = new FragmentListDriverWRTOrder();
            view.findViewById(R.id.fab_driver_list_assing_new_driver).setVisibility(View.VISIBLE);

            FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fab_driver_list_assing_new_driver);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getFragmentManager().beginTransaction().
                            setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out).
                            replace(R.id.main_content_frame_transporter_container, fragmentListDriverWRTOrder).
                            addToBackStack(null).
                            commit();
                }
            });


        }
        else if(show_wrt_order_id == "false"){
            view.findViewById(R.id.fab_driver_list_assing_new_driver).setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
            getDriversWrtOrder(order, show_wrt_order_id);
    }

    private void getDriversWrtOrder(final Order order, String show_wrt_order_id) {
        progressDialog = ProgressDialogManager.showProgressDialogWithTitle(getContext(), "Loading Active Drivers", "Please wait");
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

            Call<Object> call = null;
            if(show_wrt_order_id == "false"){
                call = api.get_driver_wrt_order(order);
            }
            else{
                Log.e("FraglstDriWrtOrd", "calling get_driver_wrt_order_id " + show_wrt_order_id);
                call = api.get_driver_wrt_order_id(order.getOrder_id() + "");
            }

            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    Object response_json = response.body();
                    Log.e("FraglstDriWrtOrd", response.toString());
                    if(response_json != null) {

                        Log.e("Response Driver", response_json.toString());
                        Gson gson = new Gson();
                        JsonObject obj = gson.fromJson(response_json.toString(), JsonObject.class);
                        String driver_json = obj.get("drivers").toString();
                        Log.e("DRIVER JSON", driver_json);

                        Type listType = new TypeToken<List<DriverDetailsWrtOrder>>(){}.getType();
                        List<DriverDetailsWrtOrder> drivers = gson.fromJson(driver_json, listType);

//                        List<DriverDetailsWrtOrder> drivers = (List<DriverDetailsWrtOrder>)gson.fromJson(driver_json, List.class);
                        if(drivers.size() > 0){
                            Log.e("DRIVER DETAILS First", drivers.get(0).getFirst_name());
                            DriverRecyclerViewWrtOrderAdapter adapter = new DriverRecyclerViewWrtOrderAdapter(getContext(), drivers, order.getOrder_id());
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            rv.setLayoutManager(layoutManager);
                            rv.setAdapter(adapter);
                        }
                        else{
                            Toast.makeText(getContext(), "No drivers found", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(getContext(), "No drivers found", Toast.LENGTH_SHORT).show();
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
        }
        catch (Exception ex){
            ProgressDialogManager.closeProgressDialog(progressDialog);
            Log.e("ERROR", ex.toString());
        }
    }

    private void populateUI(){
        rv = (RecyclerView)view.findViewById(R.id.fragment_list_driver_wrt_order_recycler_view);
    }
}
