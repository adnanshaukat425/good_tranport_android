package com.example.adnanshaukat.myapplication.View.Customer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.Modals.Cargo;
import com.example.adnanshaukat.myapplication.Modals.Container;
import com.example.adnanshaukat.myapplication.Modals.ContainerSize;
import com.example.adnanshaukat.myapplication.Modals.Location;
import com.example.adnanshaukat.myapplication.Modals.MeasurementUnit;
import com.example.adnanshaukat.myapplication.Modals.PaymentType;
import com.example.adnanshaukat.myapplication.Modals.PreOrderDataWrapperClass;
import com.example.adnanshaukat.myapplication.Modals.WeightCatagory;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.ILocation;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IOrder;
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
 * Created by AdnanShaukat on 30/11/2018.
 */

public class FragmentCreateOrderStep1 extends Fragment {

    View view;
    int DATE_DIALOG_ID = 1;

    MaterialSpinner spinCargoType, spinContainerType, spinContainerSize, spinWeightUnit, spinCargoWeight, spinSource, spinDestination;
    FrameLayout btn_next;
    TextInputEditText etCargoVolume;
    TextInputLayout tilCargoVolume;
    int day, month, year;
    String update_date;

    List<PaymentType> paymentType;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_order_step1, container, false);
        populateUI();
        getPreOrderFormData();
        showHideUI("FCL");
        return view;
    }

    @Override
    public void onPause() {
        Log.e("PAUSE", "PAUSE");
        super.onPause();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.e("CREATE", "CREATE");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("RESUME", "RESUME");
        spinSource.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                Location source = (Location) spinSource.getItems().get(spinSource.getSelectedIndex());
                if(source.location_id != 0){
                    getDestination(source.location_id);
                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cargo cargoType = (Cargo) spinCargoType.getItems().get(spinCargoType.getSelectedIndex());
                Container containerType = (Container) spinContainerType.getItems().get(spinContainerType.getSelectedIndex());
                ContainerSize containerSize = (ContainerSize)spinContainerSize.getItems().get(spinContainerSize.getSelectedIndex());
                WeightCatagory weightCatagory = (WeightCatagory)spinCargoWeight.getItems().get(spinCargoWeight.getSelectedIndex());
                String temp_volume = etCargoVolume.getText().toString();
                float cargoVolume = Float.parseFloat(temp_volume.isEmpty() ? "0" : temp_volume);
                MeasurementUnit measurementUnit = (MeasurementUnit) spinWeightUnit.getItems().get(spinWeightUnit.getSelectedIndex());
                Location source = (Location) spinSource.getItems().get(spinSource.getSelectedIndex());

                Log.e("Cargo Type", cargoType.getCargo_type_id() + "");
                Log.e("Container Type", containerType.getContainer_type_id() + "");
                Log.e("Container Size", containerSize.getVehicle_type_id() + "");
                Log.e("weightCatagory", weightCatagory.getWeight_id() + "");
                Log.e("Cargo Volumn", cargoVolume + "");
                Log.e("MeasurementUnit", measurementUnit.getUnit_id() + "");
                Log.e("Source", source.getLocation_id() + "");

                Gson gson = new Gson();

                Bundle bundle =new Bundle();
                bundle.putSerializable("cargoType", cargoType);
                bundle.putSerializable("containerType", containerType);
                bundle.putSerializable("containerSize", containerSize);
                bundle.putSerializable("weightCatagory", weightCatagory);
                bundle.putFloat("cargoVolume", cargoVolume);
                bundle.putSerializable("measurementUnit", measurementUnit);
                bundle.putSerializable("source", source);
                bundle.putString("paymentType", gson.toJson(paymentType));;

                FragmentCreateOrderStep2 step_2 = new FragmentCreateOrderStep2();
                step_2.setArguments(bundle);

                if (paymentType.size() > 0){
                    Log.e("PAYMENT TYPE", paymentType.get(0).getPayment_type());
                }

                MainActivityCustomer activity = (MainActivityCustomer)getContext();

                if(cargoType.getCargo_type_id() == -1){
                    Toast.makeText(activity, "Please Select Cargo", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(containerType.getContainer_type_id() == -1){
                    Toast.makeText(activity, "Please Select Container Type", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (containerType.getContainer_type_id() == 1){
                    if(cargoVolume == 0) {
                        Drawable errorIcon = getResources().getDrawable(R.drawable.ic_error);
                        errorIcon.setBounds(new Rect(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight()));
                        etCargoVolume.setError("Please Enter Volume", errorIcon);
                        Toast.makeText(activity, "Please Enter Volume", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (source.getLocation_id() != -1){
                        Location destination = (Location)spinDestination.getItems().get(spinDestination.getSelectedIndex());
                        if(destination.getLocation_id() != -1) {
                            bundle.putSerializable("destination", destination);
                            activity.getSupportFragmentManager().beginTransaction().
                                    setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.fade_in, R.anim.fade_out).
                                    replace(R.id.main_content_frame_customer_container, step_2).
                                    addToBackStack(null).
                                    commit();
                        }
                        else{
                            Toast.makeText(activity, "Please Select Destination", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(activity, "Please Select Source", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(containerType.getContainer_type_id() == 2){
                    if(containerSize.getVehicle_type_id() == -1){
                        Toast.makeText(activity, "Please Select Container Size", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(measurementUnit.getUnit_id() == -1){
                        Toast.makeText(activity, "Please Select Unit", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(weightCatagory.getWeight_id() == -1){
                        Toast.makeText(activity, "Please Select Weight", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (source.getLocation_id() != -1){
                        Location destination = (Location)spinDestination.getItems().get(spinDestination.getSelectedIndex());
                        if(destination.location_id != -1){
                            bundle.putSerializable("destination", destination);
                            activity.getSupportFragmentManager().beginTransaction().
                                    setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.fade_in, R.anim.fade_out).
                                    replace(R.id.main_content_frame_customer_container, step_2).
                                    addToBackStack(null).
                                    commit();
                        }
                        else{
                            Toast.makeText(activity, "Please Select Destination", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(activity, "Please Select Source", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        spinContainerType.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                Container container = (Container)spinContainerType.getItems().get(spinContainerType.getSelectedIndex());
                showHideUI(container.getContainer_type());
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
                    .baseUrl(IOrder.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();

            IOrder api = retrofit.create(IOrder.class);

            Call<Object> call = api.get_pre_order_form_data(true, true, true, true, true, true, true);

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
                        List<ContainerSize> containerSize = obj.getContainer_size();
                        List<MeasurementUnit> measurement_unit = obj.getMeasurement_unit();
                        List<WeightCatagory> weight_catagory = obj.getWeight_catagory();
                        List<Location> source = obj.getSource();
                        paymentType = obj.getPayment_type();

                        populateSpinners(cargo, container, measurement_unit, source, containerSize, weight_catagory);
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
                        destinations.add(0, new Location(-1, "Select Destination", "-1", "-1"));
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

    private void populateSpinners(List<Cargo> cargo, List<Container> container, List<MeasurementUnit> measurement_unit,
                                  List<Location> source, List<ContainerSize> containerSize, List<WeightCatagory> weightCatagory){

        cargo.add(0, new Cargo(-1, "Select Cargo"));
        ArrayAdapter<Cargo> cargo_adapter = new ArrayAdapter<Cargo>(getContext(), android.R.layout.simple_spinner_item, cargo);
        cargo_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinCargoType.setAdapter(cargo_adapter);

        container.add(0, new Container(-1, "Select Container Type"));
        ArrayAdapter<Container> container_adapter = new ArrayAdapter<Container>(getContext(), android.R.layout.simple_spinner_item, container);
        container_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinContainerType.setAdapter(container_adapter);

        containerSize.add(0, new ContainerSize(-1, "Select Container Size"));
        ArrayAdapter<ContainerSize> container_size_adapter = new ArrayAdapter<ContainerSize>(getContext(), android.R.layout.simple_spinner_item, containerSize);
        container_size_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinContainerSize.setAdapter(container_size_adapter);

        measurement_unit.add(0, new MeasurementUnit(-1, "Select Unit"));
        measurement_unit.remove(measurement_unit.size() - 1);
        ArrayAdapter<MeasurementUnit> measurement_unit_adapter = new ArrayAdapter<MeasurementUnit>(getContext(), android.R.layout.simple_spinner_item, measurement_unit);
        measurement_unit_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinWeightUnit.setAdapter(measurement_unit_adapter);

        weightCatagory.add(0, new WeightCatagory(-1, "Select Weight"));
        ArrayAdapter<WeightCatagory> weight_catagory_adapter = new ArrayAdapter<WeightCatagory>(getContext(), android.R.layout.simple_spinner_item, weightCatagory);
        weight_catagory_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinCargoWeight.setAdapter(weight_catagory_adapter);

        source.add(0, new Location(-1,"Select Location", "-1", "-1"));
        ArrayAdapter<Location> source_adapter = new ArrayAdapter<Location>(getContext(), android.R.layout.simple_spinner_item, source);
        source_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinSource.setAdapter(source_adapter);
    }

    private void populateUI(){

        spinCargoType = (MaterialSpinner)view.findViewById(R.id.spin_cargo_type);
        spinContainerType = (MaterialSpinner)view.findViewById(R.id.spin_container_type);
        spinContainerSize = (MaterialSpinner)view.findViewById(R.id.spin_container_size);
        spinWeightUnit = (MaterialSpinner)view.findViewById(R.id.spin_weight_unit);
        spinCargoWeight = (MaterialSpinner)view.findViewById(R.id.spin_cargo_weight);
        spinSource = (MaterialSpinner)view.findViewById(R.id.spin_source_id);
        spinDestination = (MaterialSpinner)view.findViewById(R.id.spin_destination_id);

        etCargoVolume = (TextInputEditText) view.findViewById(R.id.et_cargo_volume);
        tilCargoVolume = (TextInputLayout)view.findViewById(R.id.til_cargo_volume);
        btn_next = (FrameLayout) view.findViewById(R.id.btn_order_next_step);
    }

    private void showHideUI(String container_type){
        if (container_type.trim().toUpperCase().equals("FCL")){
            hideKeyboard(getActivity());
            if (etCargoVolume.getVisibility() == View.VISIBLE){
                etCargoVolume.setAlpha(1.0f);
                // Start the animation
                etCargoVolume.animate()
                        .translationY(etCargoVolume.getHeight())
                        .alpha(0.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                etCargoVolume.setVisibility(View.GONE);
                            }
                        });
            }

            if (tilCargoVolume.getVisibility() == View.VISIBLE){
                tilCargoVolume.setAlpha(1.0f);
                tilCargoVolume.animate()
                        .translationY(tilCargoVolume.getHeight())
                        .alpha(0.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                tilCargoVolume.setVisibility(View.GONE);
                            }
                        });
            }

            if (spinCargoWeight.getVisibility() == View.GONE){
                spinCargoWeight.setAlpha(1.0f);
                spinCargoWeight.setVisibility(View.VISIBLE);
                // Start the animation
                spinCargoWeight.animate()
                        .translationY(0)
                        .alpha(1.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                spinCargoWeight.setVisibility(View.VISIBLE);
                            }
                        });
            }

            if(spinWeightUnit.getVisibility() == View.GONE){
                spinWeightUnit.setAlpha(0.0f);
                spinWeightUnit.setVisibility(View.VISIBLE);
                // Start the animation
                spinWeightUnit.animate()
                        .translationY(0)
                        .alpha(1.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                spinWeightUnit.setVisibility(View.VISIBLE);
                            }
                        });
            }

            if(spinContainerSize.getVisibility() == View.GONE){
                spinContainerSize.setAlpha(0.0f);
                spinContainerSize.setVisibility(View.VISIBLE);
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
        else if(container_type.trim().toUpperCase().equals("LCL")){
            Toast.makeText(getContext(), "LCL Selected", Toast.LENGTH_SHORT).show();


            if (etCargoVolume.getVisibility() == View.GONE){
                //Make Cargo Volume row visible
                etCargoVolume.setAlpha(0.0f);
                etCargoVolume.setVisibility(View.VISIBLE);
                etCargoVolume.animate()
                        .translationY(0)
                        .alpha(1.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                etCargoVolume.setVisibility(View.VISIBLE);
                            }
                        });
            }

            if (tilCargoVolume.getVisibility() == View.GONE){
                tilCargoVolume.setAlpha(0.0f);
                tilCargoVolume.setVisibility(View.VISIBLE);
                tilCargoVolume.animate()
                        .translationY(0)
                        .alpha(1.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                tilCargoVolume.setVisibility(View.VISIBLE);
                            }
                        });
            }

            if (spinCargoWeight.getVisibility() == View.VISIBLE){
                spinCargoWeight.setAlpha(1.0f);
                // Start the animation
                spinCargoWeight.animate()
                        .translationY(spinCargoWeight.getHeight())
                        .alpha(0.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                spinCargoWeight.setVisibility(View.GONE);
                            }
                        });
            }

            if(spinWeightUnit.getVisibility() == View.VISIBLE){
                spinWeightUnit.setAlpha(1.0f);
                // Start the animation
                spinWeightUnit.animate()
                        .translationY(spinWeightUnit.getHeight())
                        .alpha(0.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                spinWeightUnit.setVisibility(View.GONE);
                            }
                        });
            }

            if(spinContainerSize.getVisibility() == View.VISIBLE){
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
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}