package com.example.adnanshaukat.myapplication.View;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.GlobalClasses.MyApplication;
import com.example.adnanshaukat.myapplication.GlobalClasses.ProgressDialogManager;
import com.example.adnanshaukat.myapplication.Modals.Cargo;
import com.example.adnanshaukat.myapplication.Modals.Container;
import com.example.adnanshaukat.myapplication.Modals.Location;
import com.example.adnanshaukat.myapplication.Modals.MeasurementUnit;
import com.example.adnanshaukat.myapplication.Modals.PreOrderDataWrapperClass;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.ILocation;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.ILogin;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IPreOrderFormData;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.Source;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.adnanshaukat.myapplication.R.drawable.email;

/**
 * Created by AdnanShaukat on 30/11/2018.
 */

public class FragmentCreateOrderStep1 extends DialogFragment {

    View view;
    int DATE_DIALOG_ID= 1;

    Spinner spinCargoType, spinContainerType, spinCargoWeight, spinWeightUnit, spinSource, spinDestination;
    TextView btn_next;
    int day, month, year;
    String update_date;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_create_order_step1, container, false);
        populateUI();
        getPreOrderFormData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        spinSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Location source = (Location)parent.getSelectedItem();
                if(source.location_id != 0){
                    getDestination(source.location_id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cargo cargoType = (Cargo) spinCargoType.getSelectedItem();
                Container containerType = (Container) spinCargoType.getSelectedItem();
                MeasurementUnit measurementUnit = (MeasurementUnit) spinWeightUnit.getSelectedItem();
                Location source = (Location) spinSource.getSelectedItem();
                Location destination = (Location)spinDestination.getSelectedItem();


            }
        });
    }

    private void getPreOrderFormData() {
        try {
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.connectTimeout(30, TimeUnit.SECONDS);
            client.readTimeout(30, TimeUnit.SECONDS);
            client.writeTimeout(30, TimeUnit.SECONDS);

            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(IPreOrderFormData.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();

            IPreOrderFormData api = retrofit.create(IPreOrderFormData.class);

            Call<Object> call = api.get_pre_order_form_data();

            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    Object _response = response.body();
                    //Response will be in json format
                    if (!_response.toString().isEmpty()){
                        Gson gson = new Gson();
                        Log.e("JSON", _response.toString());
                        PreOrderDataWrapperClass obj = gson.fromJson(_response.toString(), PreOrderDataWrapperClass.class);
                        List<Cargo> cargo = obj.getCargo();
                        List<Container> container = obj.getContainer();
                        List<MeasurementUnit> measurement_unit = obj.getMeasurement_unit();
                        List<Location> source = obj.getSource();

                        Location l1 = new Location(0, "Select Location", "0", "0");
                        source.add(0, l1);

                        populateSpinners(cargo, container, measurement_unit, source);
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.e("FAILURE", t.getMessage());
                    Log.e("FAILURE", t.toString());
                }
            });
        } catch (Exception ex) {
            Log.e("ERROR", ex.toString());
        }
    }

    private void getDestination(int source_id){
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

            Log.e("SOURCE ID", source_id + "");

            Call<List<Location>> call = api.get_all_destination_wrt_source(source_id);

            call.enqueue(new Callback<List<Location>>() {
                @Override
                public void onResponse(Call<List<Location>> call, Response<List<Location>> response) {
                    List<Location> destinations  = response.body();
                    //Response will be in json format
                    Log.e("DESTINATIONS", destinations.toString());
                    if (destinations  != null && destinations .size() > 0){
                        ArrayAdapter<Location> destination_adapter = new ArrayAdapter<Location>(getContext(), android.R.layout.simple_spinner_item, destinations);
                        destination_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinDestination.setAdapter(destination_adapter);
                    }
                }

                @Override
                public void onFailure(Call<List<Location>> call, Throwable t) {
                    Log.e("FAILURE", t.getMessage());
                    Log.e("FAILURE", t.toString());
                }
            });
        } catch (Exception ex) {
            Log.e("ERROR", ex.toString());
        }
    }

    private void populateSpinners(List<Cargo> cargo, List<Container> container, List<MeasurementUnit> measurement_unit, List<Location> source){
        ArrayAdapter<Cargo> cargo_adapter = new ArrayAdapter<Cargo>(getContext(), android.R.layout.simple_spinner_item, cargo);
        cargo_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinCargoType.setAdapter(cargo_adapter);

        ArrayAdapter<Container> container_adapter = new ArrayAdapter<Container>(getContext(), android.R.layout.simple_spinner_item, container);
        cargo_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinContainerType.setAdapter(container_adapter);

        ArrayAdapter<MeasurementUnit> measurement_unit_adapter = new ArrayAdapter<MeasurementUnit>(getContext(), android.R.layout.simple_spinner_item, measurement_unit);
        cargo_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinWeightUnit.setAdapter(measurement_unit_adapter);

        ArrayAdapter<Location> source_adapter = new ArrayAdapter<Location>(getContext(), android.R.layout.simple_spinner_item, source);
        cargo_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinSource.setAdapter(source_adapter);
    }

    private void populateUI(){
        spinCargoType = (Spinner)view.findViewById(R.id.spin_cargo_type);
        spinContainerType = (Spinner)view.findViewById(R.id.spin_container_type);
        spinCargoWeight = (Spinner)view.findViewById(R.id.spin_cargo_weight);
        spinWeightUnit = (Spinner)view.findViewById(R.id.spin_weight_unit);
        spinSource = (Spinner)view.findViewById(R.id.spin_source_id);
        spinDestination = (Spinner)view.findViewById(R.id.spin_destination_id);

        btn_next = (TextView) view.findViewById(R.id.btn_order_next_step);
    }
}