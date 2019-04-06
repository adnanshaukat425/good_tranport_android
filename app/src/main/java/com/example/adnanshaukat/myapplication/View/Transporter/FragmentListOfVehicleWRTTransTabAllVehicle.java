package com.example.adnanshaukat.myapplication.View.Transporter;

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

import com.example.adnanshaukat.myapplication.Adapters.VehiclesRecyclerViewAdapter;
import com.example.adnanshaukat.myapplication.GlobalClasses.ProgressDialogManager;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.Modals.Vehicle;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IVehicle;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IVehicleWrtDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by AdnanShaukat on 17/02/2019.
 */

public class FragmentListOfVehicleWRTTransTabAllVehicle extends Fragment {

    View view;

    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_of_vehicle_wrt_trans_tab_all_vehicle, container, false);
        return view;
    }

    public void onResume() {
        super.onResume();
        User user = (User)getActivity().getIntent().getSerializableExtra("user");
        Log.e(FragmentListOfVehicleWRTTransTabAllVehicle.this.toString(), user.getUser_id() + "");
        getVehicles(Integer.toString(user.getUser_id()));
    }

    private List<User> getVehicles(final String transporter_id) {
        progressDialog = ProgressDialogManager.showProgressDialogWithTitle(getContext(), "Loading Vehicle", "Please wait");
        final List<User> result_list = new ArrayList<>();
        try {
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.connectTimeout(30, TimeUnit.SECONDS);
            client.readTimeout(30, TimeUnit.SECONDS);
            client.writeTimeout(30, TimeUnit.SECONDS);

            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(IVehicleWrtDriver.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();

            IVehicle api = retrofit.create(IVehicle.class);

            Log.e("Transporter Id", transporter_id);

            Call<List<Vehicle>> call = api.get_all_vehicle_wrt_transporter(transporter_id);

            call.enqueue(new Callback<List<Vehicle>>() {
                @Override
                public void onResponse(Call<List<Vehicle>> call, Response<List<Vehicle>> response) {
                    List<Vehicle> vehicles = response.body();

                    if(vehicles!=null && vehicles.size() > 0) {
                        ProgressDialogManager.closeProgressDialog(progressDialog);
                        RecyclerView recyclerView  = (RecyclerView)view.findViewById(R.id.fragment_list_of_all_vehicle_trans_recycler_view);
                        VehiclesRecyclerViewAdapter adapter = new VehiclesRecyclerViewAdapter(getContext(), vehicles, transporter_id);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                    }
                    else{
                        ProgressDialogManager.closeProgressDialog(progressDialog);
                        Toast.makeText(getContext(), "No Vehicle found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Vehicle>> call, Throwable t) {
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
        return result_list;
    }
}
