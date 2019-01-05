package com.example.adnanshaukat.myapplication.View;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
    int DATE_DIALOG_ID= 1;

    Spinner spinCargoType, spinContainerType, spinContainerSize, spinWeightUnit, spinCargoWeight, spinSource, spinDestination;
    TextView tvContainerSize, tvWeightUnit, tvCargoWeight, tvCargoVolume;
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
        Log.e("CREATE VIEW", "CREATE VIEW");
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
                Container containerType = (Container) spinContainerType.getSelectedItem();
                ContainerSize containerSize = (ContainerSize)spinContainerSize.getSelectedItem();
                WeightCatagory weightCatagory = (WeightCatagory)spinCargoWeight.getSelectedItem();
                String temp_volume = etCargoVolume.getText().toString();
                float cargoVolume = Float.parseFloat(temp_volume.isEmpty() ? "0" : temp_volume);
                MeasurementUnit measurementUnit = (MeasurementUnit) spinWeightUnit.getSelectedItem();
                Location source = (Location) spinSource.getSelectedItem();
                Location destination = (Location)spinDestination.getSelectedItem();

                Gson gson = new Gson();

                Bundle bundle =new Bundle();
                bundle.putSerializable("cargoType", cargoType);
                bundle.putSerializable("containerType", containerType);
                bundle.putSerializable("containerSize", containerSize);
                bundle.putSerializable("weightCatagory", weightCatagory);
                bundle.putFloat("cargoVolume", cargoVolume);
                bundle.putSerializable("measurementUnit", measurementUnit);
                bundle.putSerializable("source", source);
                bundle.putSerializable("destination", destination);
                bundle.putString("paymentType", gson.toJson(paymentType));;

                FragmentCreateOrderStep2 step_2 = new FragmentCreateOrderStep2();
                step_2.setArguments(bundle);

                if (paymentType.size() > 0){
                    Log.e("PAYMENT TYPE", paymentType.get(0).getPayment_type());
                }

                MainActivityCustomer activity = (MainActivityCustomer)getContext();

                activity.getSupportFragmentManager().beginTransaction().
                        setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.fade_in, R.anim.fade_out).
                        replace(R.id.main_content_frame_customer_container, step_2).
                        addToBackStack(null).
                        commit();
            }
        });

        spinContainerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Container container = (Container)parent.getSelectedItem();
                showHideUI(container.getContainer_type());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                        List<ContainerSize> containerSize = obj.getContainer_size();
                        List<MeasurementUnit> measurement_unit = obj.getMeasurement_unit();
                        List<WeightCatagory> weight_catagory = obj.getWeight_catagory();
                        List<Location> source = obj.getSource();
                        paymentType = obj.getPayment_type();

                        Location l1 = new Location(0, "Select Location", "0", "0");
                        source.add(0, l1);

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
        ArrayAdapter<Cargo> cargo_adapter = new ArrayAdapter<Cargo>(getContext(), android.R.layout.simple_spinner_item, cargo);
        cargo_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinCargoType.setAdapter(cargo_adapter);

        ArrayAdapter<Container> container_adapter = new ArrayAdapter<Container>(getContext(), android.R.layout.simple_spinner_item, container);
        container_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinContainerType.setAdapter(container_adapter);

        ArrayAdapter<ContainerSize> container_size_adapter = new ArrayAdapter<ContainerSize>(getContext(), android.R.layout.simple_spinner_item, containerSize);
        container_size_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinContainerSize.setAdapter(container_size_adapter);

        measurement_unit.remove(measurement_unit.size() - 1);
        ArrayAdapter<MeasurementUnit> measurement_unit_adapter = new ArrayAdapter<MeasurementUnit>(getContext(), android.R.layout.simple_spinner_item, measurement_unit);
        measurement_unit_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinWeightUnit.setAdapter(measurement_unit_adapter);

        ArrayAdapter<WeightCatagory> weight_catagory_adapter = new ArrayAdapter<WeightCatagory>(getContext(), android.R.layout.simple_spinner_item, weightCatagory);
        weight_catagory_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinCargoWeight.setAdapter(weight_catagory_adapter);

        ArrayAdapter<Location> source_adapter = new ArrayAdapter<Location>(getContext(), android.R.layout.simple_spinner_item, source);
        source_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinSource.setAdapter(source_adapter);
    }

    private void populateUI(){

        spinCargoType = (Spinner)view.findViewById(R.id.spin_cargo_type);
        spinContainerType = (Spinner)view.findViewById(R.id.spin_container_type);
        spinContainerSize = (Spinner)view.findViewById(R.id.spin_container_size);
        spinWeightUnit = (Spinner)view.findViewById(R.id.spin_weight_unit);
        spinCargoWeight = (Spinner)view.findViewById(R.id.spin_cargo_weight);
        spinSource = (Spinner)view.findViewById(R.id.spin_source_id);
        spinDestination = (Spinner)view.findViewById(R.id.spin_destination_id);

        tvContainerSize = (TextView)view.findViewById(R.id.tv_container_size);
        tvWeightUnit = (TextView)view.findViewById(R.id.tv_weight_unit);
        tvCargoWeight = (TextView)view.findViewById(R.id.tv_cargo_weight);
        tvCargoVolume = (TextView)view.findViewById(R.id.tv_cargo_volume);

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

            if (tvCargoVolume.getVisibility() == View.VISIBLE){
                tvCargoVolume.setAlpha(1.0f);
                // Start the animation
                tvCargoVolume.animate()
                        .translationY(tvCargoVolume.getHeight())
                        .alpha(0.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                tvCargoVolume.setVisibility(View.GONE);
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

            if (tvCargoWeight.getVisibility() == View.VISIBLE){
                //Make Cargo Volume row visible
                tvCargoVolume.setAlpha(1.0f);
                tvCargoVolume.animate()
                        .translationY(tvCargoVolume.getHeight())
                        .alpha(0.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                tvCargoVolume.setVisibility(View.GONE);
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

            if(tvCargoWeight.getVisibility() == View.GONE){
                tvCargoWeight.setAlpha(0.0f);
                tvCargoWeight.setVisibility(View.VISIBLE);
                // Start the animation
                tvCargoWeight.animate()
                        .translationY(0)
                        .alpha(1.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                tvCargoWeight.setVisibility(View.VISIBLE);
                            }
                        });
            }

            if (tvWeightUnit.getVisibility() == View.GONE){
                tvWeightUnit.setAlpha(0.0f);
                tvWeightUnit.setVisibility(View.VISIBLE);
                // Start the animation
                tvWeightUnit.animate()
                        .translationY(0)
                        .alpha(1.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                tvWeightUnit.setVisibility(View.VISIBLE);
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

            if (tvContainerSize.getVisibility() == View.GONE){
                tvContainerSize.setAlpha(0.0f);
                tvContainerSize.setVisibility(View.VISIBLE);
                // Start the animation
                tvContainerSize.animate()
                        .translationY(0)
                        .alpha(1.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                tvContainerSize.setVisibility(View.VISIBLE);
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

            if (tvCargoVolume.getVisibility() == View.GONE){
                tvCargoVolume.setAlpha(0.0f);
                tvCargoVolume.setVisibility(View.VISIBLE);
                // Start the animation
                tvCargoVolume.animate()
                        .translationY(0)
                        .alpha(1.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                tvCargoVolume.setVisibility(View.VISIBLE);
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

            if(tvCargoWeight.getVisibility() == View.VISIBLE){
                tvCargoWeight.setAlpha(1.0f);
                // Start the animation
                tvCargoWeight.animate()
                        .translationY(tvCargoWeight.getHeight())
                        .alpha(0.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                tvCargoWeight.setVisibility(View.GONE);
                            }
                        });
            }

            if (tvWeightUnit.getVisibility() == View.VISIBLE){
                tvWeightUnit.setAlpha(1.0f);
                // Start the animation
                tvWeightUnit.animate()
                        .translationY(tvWeightUnit.getHeight())
                        .alpha(0.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                tvWeightUnit.setVisibility(View.GONE);
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

            if (tvContainerSize.getVisibility() == View.VISIBLE){
                tvContainerSize.setAlpha(1.0f);
                // Start the animation
                tvContainerSize.animate()
                        .translationY(tvContainerSize.getHeight())
                        .alpha(0.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                tvContainerSize.setVisibility(View.GONE);
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