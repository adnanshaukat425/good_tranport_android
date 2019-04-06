package com.example.adnanshaukat.myapplication.View.Transporter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.GlobalClasses.ProgressDialogManager;
import com.example.adnanshaukat.myapplication.Modals.Container;
import com.example.adnanshaukat.myapplication.Modals.ContainerSize;
import com.example.adnanshaukat.myapplication.Modals.DBVehicle;
import com.example.adnanshaukat.myapplication.Modals.PreOrderDataWrapperClass;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.Modals.Vehicle;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IOrder;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IVehicle;
import com.example.adnanshaukat.myapplication.View.Customer.MainActivityCustomer;
import com.google.gson.Gson;
import com.jaredrummler.materialspinner.MaterialSpinner;

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

public class FragmentAddVehicleWRTTransporter extends Fragment {

    View view;
    EditText etVehicleNumber;
    MaterialSpinner spinContainerType, spinContainerSize;
    Button btn_next;
    ProgressDialog progressDialog;
    DBVehicle vehicle;
    Vehicle vehicle_from_vehicle_list;
    private String vehicle_number;
    private int container_type_id, container_size_id;

    private int transporter_id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_vehicle_wrt_transporter, container, false);
        final MainActivityTransporter mainActivityTransporter = (MainActivityTransporter) getContext();

        Bundle argument = getArguments();
        populateUI();
        if (argument != null) {
            vehicle_from_vehicle_list = (Vehicle) argument.get("vehicle_from_vehicle_list");
            //Toast.makeText(mainActivityTransporter, vehicle.get, Toast.LENGTH_SHORT).show();
            if (vehicle_from_vehicle_list == null) {
                mainActivityTransporter.setTitle("Add Vehicle");
                btn_next.setText("Add Vehicle");
            } else {
                mainActivityTransporter.setTitle("Vehicle");
                btn_next.setText("Update Vehicle");
            }
            transporter_id = Integer.parseInt(argument.getString("transporter_id"));
        }
        get_vehicle_type_and_container_size();

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vehicle_number = etVehicleNumber.getText().toString();
                container_type_id = ((Container) spinContainerType.getItems().get(spinContainerType.getSelectedIndex())).getContainer_type_id();
                container_size_id = ((ContainerSize) spinContainerSize.getItems().get(spinContainerSize.getSelectedIndex())).getVehicle_type_id();
                DBVehicle vehicle = new DBVehicle(0, container_size_id, container_type_id, vehicle_number, transporter_id);
                if (checkValidity()) {
                    progressDialog = ProgressDialogManager.showProgressDialogWithTitle(getContext(), "Please Wait...", "Loading");
                    if(btn_next.getText().toString().trim().toLowerCase().equals("add vehicle")){
                        add_vehicle(vehicle);
                    }
                    else if(btn_next.getText().toString().trim().toLowerCase().equals("update vehicle")){
                        vehicle.setVehicle_id(vehicle_from_vehicle_list.getVehicle_id());
                        update_vehicle(vehicle);
                    }
                }
            }
        });

        etVehicleNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                mainActivityTransporter.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

        spinContainerType.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                showHideUI(((Container)item).getContainer_type_id());
            }
        });
        return view;
    }

    public boolean checkValidity(){
        Drawable errorIcon = getResources().getDrawable(R.drawable.ic_error);
        errorIcon.setBounds(new Rect(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight()));
        if (TextUtils.isEmpty(vehicle_number)) {
            etVehicleNumber.setError("Vehicle Number Required!", errorIcon);
            etVehicleNumber.requestFocus();
            return false;
        }
        if(spinContainerType.getSelectedIndex() == 0){
            spinContainerType.requestFocus();
            Toast.makeText(getContext(), "Container Type Can't Be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(spinContainerType.getSelectedIndex() == 2) {
            if(spinContainerSize.getSelectedIndex() == 0){
                spinContainerSize.requestFocus();
                Toast.makeText(getContext(), "Container Size Can't Be Empty", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void add_vehicle(DBVehicle vehicle) {
        try {
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.connectTimeout(30, TimeUnit.SECONDS);
            client.readTimeout(30, TimeUnit.SECONDS);
            client.writeTimeout(30, TimeUnit.SECONDS);

            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(IVehicle.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();

            IVehicle api = retrofit.create(IVehicle.class);

            Call<DBVehicle> call = api.add_vehicle_wrt_transporter(vehicle);

            call.enqueue(new Callback<DBVehicle>() {
                @Override
                public void onResponse(Call<DBVehicle> call, Response<DBVehicle> response) {
                    Log.e("RESPONSE", response.toString());
                    DBVehicle response_vehicle = response.body();
                    if (response_vehicle != null && response_vehicle.vehicle_id != 0) {
                        Toast.makeText(getContext(), "Vehicle Added Succesfully", Toast.LENGTH_LONG).show();
                        clearFields();
                    }
                    else if(response.message().equals("Not Acceptable")){
                        Toast.makeText(getContext(), "Vehicle With This Number Is Already Present.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getContext(), "Something Went Wrong, Please Try Again Later", Toast.LENGTH_LONG).show();
                    }
                    ProgressDialogManager.closeProgressDialog(progressDialog);
                }

                @Override
                public void onFailure(Call<DBVehicle> call, Throwable t) {
                    Log.e("FAILURE", t.getMessage());
                    Log.e("FAILURE", t.toString());
                    ProgressDialogManager.closeProgressDialog(progressDialog);
                    Toast.makeText(getContext(), "An Error Occour, Please Try Again", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception ex) {
            ProgressDialogManager.closeProgressDialog(progressDialog);
            Log.e("ERROR", ex.toString());
        }
    }

    private void update_vehicle(DBVehicle vehicle) {
        try {
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.connectTimeout(30, TimeUnit.SECONDS);
            client.readTimeout(30, TimeUnit.SECONDS);
            client.writeTimeout(30, TimeUnit.SECONDS);

            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(IVehicle.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();

            IVehicle api = retrofit.create(IVehicle.class);

            Call<DBVehicle> call = api.update_vehicle_wrt_transporter(vehicle);

            call.enqueue(new Callback<DBVehicle>() {
                @Override
                public void onResponse(Call<DBVehicle> call, Response<DBVehicle> response) {
                    Log.e("RESPONSE", response.toString());
                    DBVehicle response_vehicle = response.body();
                    if (response_vehicle != null && response_vehicle.vehicle_id != 0) {
                        Toast.makeText(getContext(), "Vehicle Updated Successfully", Toast.LENGTH_LONG).show();
                        clearFields();
                    }
                    else if(response.message().equals("Not Acceptable")){
                        Toast.makeText(getContext(), "Vehicle With This Number Is Already Present.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getContext(), "Something Went Wrong, Please Try Again Later", Toast.LENGTH_LONG).show();
                    }
                    ProgressDialogManager.closeProgressDialog(progressDialog);
                }

                @Override
                public void onFailure(Call<DBVehicle> call, Throwable t) {
                    Log.e("FAILURE", t.getMessage());
                    Log.e("FAILURE", t.toString());
                    ProgressDialogManager.closeProgressDialog(progressDialog);
                    Toast.makeText(getContext(), "An Error Occour, Please Try Again", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception ex) {
            ProgressDialogManager.closeProgressDialog(progressDialog);
            Log.e("ERROR", ex.toString());
        }
    }

    private void get_vehicle_type_and_container_size(){
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

            Call<Object> call = api.get_pre_order_form_data(false, false, true, false, true, false, false);

            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    Object _response = response.body();
                    //Response will be in json format
                    if (!_response.toString().isEmpty()){
                        Gson gson = new Gson();
                        Log.e("JSON", _response.toString());
                        PreOrderDataWrapperClass obj = gson.fromJson(_response.toString(), PreOrderDataWrapperClass.class);
                        List<Container> container = obj.getContainer();
                        List<ContainerSize> containerSize = obj.getContainer_size();
                        populateSpinners(container, containerSize);
                    }
                    if(vehicle_from_vehicle_list != null){
                        populateUIFromModal(vehicle_from_vehicle_list);
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

    private void populateUI(){
        spinContainerType = (MaterialSpinner) view.findViewById(R.id.add_vehicle_spin_container_type);
        spinContainerSize = (MaterialSpinner)view.findViewById(R.id.add_vehicle_spin_container_size);
        etVehicleNumber = (EditText)view.findViewById(R.id.add_vehicle_et_vehicle_number);

        btn_next = (Button) view.findViewById(R.id.btn_add_vehicle);
    }

    private void populateSpinners(List<Container> containers, List<ContainerSize> containerSizes){
        containers.add(0, new Container(-1, "Select Container Type"));
        ArrayAdapter<Container> container_adapter = new ArrayAdapter<Container>(getContext(), android.R.layout.simple_spinner_item, containers);
        container_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinContainerType.setAdapter(container_adapter);

        containerSizes.add(0, new ContainerSize(-1, "Select Container Size"));
        ArrayAdapter<ContainerSize> container_size_adapter = new ArrayAdapter<ContainerSize>(getContext(), android.R.layout.simple_spinner_item, containerSizes);
        container_size_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinContainerSize.setAdapter(container_size_adapter);
    }

    private void clearFields(){
        etVehicleNumber.setText("");
        spinContainerSize.setSelectedIndex(0);
        spinContainerType.setSelectedIndex(0);
    }

    private void populateUIFromModal(Vehicle vehicle){
        try {
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.connectTimeout(30, TimeUnit.SECONDS);
            client.readTimeout(30, TimeUnit.SECONDS);
            client.writeTimeout(30, TimeUnit.SECONDS);

            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(IVehicle.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();

            IVehicle api = retrofit.create(IVehicle.class);

            Call<DBVehicle> call = api.get_vehicle(vehicle.getVehicle_id());

            call.enqueue(new Callback<DBVehicle>() {
                @Override
                public void onResponse(Call<DBVehicle> call, Response<DBVehicle> response) {
                    Log.e("RESPONSE", response.toString());
                    DBVehicle response_vehicle = response.body();
                    if (response_vehicle != null && response_vehicle.vehicle_id != 0) {
                        Log.e("Vehicle Number", response_vehicle.getVehicle_number());
                        Log.e("Vehicle Type Id", response_vehicle.getVehicle_type_id() + "");
                        Log.e("Container Type", response_vehicle.getContainer_type_id() + "");
                        showHideUI(response_vehicle.getVehicle_type_id());
                        etVehicleNumber.setText(response_vehicle.getVehicle_number());
                        spinContainerSize.setSelectedIndex(response_vehicle.getContainer_type_id());
                        spinContainerType.setSelectedIndex(response_vehicle.getVehicle_type_id());
                    }
                }

                @Override
                public void onFailure(Call<DBVehicle> call, Throwable t) {
                    Log.e("FAILURE", t.getMessage());
                    Log.e("FAILURE", t.toString());
                    Toast.makeText(getContext(), "An Error Occour, Please Try Again", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception ex) {
            Log.e("ERROR", ex.toString());
        }
    }

    private void showHideUI(int container_type_id){
        if(container_type_id == 1){
            //Toast.makeText(mainActivityTransporter, "LCL", Toast.LENGTH_SHORT).show();
            if (spinContainerSize.getVisibility() == View.VISIBLE) {
                spinContainerSize.setAlpha(1.0f);
                // Start the animation
                spinContainerSize.animate()
                        .translationY(spinContainerSize.getHeight())
                        .alpha(0.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                spinContainerSize.setVisibility(View.GONE);
                            }
                        });
            }
        }
        else{
            if (spinContainerSize.getVisibility() == View.GONE) {
                spinContainerSize.setAlpha(0.0f);
                // Start the animation
                spinContainerSize.animate()
                        .translationY(0)
                        .alpha(1.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                spinContainerSize.setVisibility(View.VISIBLE);
                            }
                        });
            }
        }
    }
}
