package com.example.adnanshaukat.myapplication.View.Customer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.GlobalClasses.ProgressDialogManager;
import com.example.adnanshaukat.myapplication.Modals.Cargo;
import com.example.adnanshaukat.myapplication.Modals.Container;
import com.example.adnanshaukat.myapplication.Modals.ContainerSize;
import com.example.adnanshaukat.myapplication.Modals.Location;
import com.example.adnanshaukat.myapplication.Modals.MeasurementUnit;
import com.example.adnanshaukat.myapplication.Modals.Order;
import com.example.adnanshaukat.myapplication.Modals.PaymentType;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.Modals.WeightCatagory;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IOrder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by AdnanShaukat on 25/12/2018.
 */

public class FragmentCreateOrderStep2 extends Fragment {

    View view;

    MaterialSpinner spinPaymentType;
    CheckBox chkLabourReq;
    EditText etLabourQuantity;
    TextInputEditText etDescription;
    TextInputLayout tilLabourQuantity;
    TextView tvLabourCost;
    Button placeOrder;

    int day, month, year;
    String update_date;

    List<PaymentType> paymentTypes;

    Cargo cargoType;
    Container containerType;
    ContainerSize containerSize;
    WeightCatagory weightCatagory;
    MeasurementUnit measurementUnit;
    Location source;
    Location destination;
    PaymentType paymentType;
    boolean is_labour_required;
    int labour_quantity;
    float labour_cost, cargoVolume;
    String description;
    User mUser;

    ProgressDialog progressDialog;

    int FCL_20FT_LABOUR_COST = 4000;
    int FCL_40FT_LABOUR_COST = 7000;
    int LCL_CM3_LABOUR_COST = 200;
    String TAG = this.getTag();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_order_step2, container, false);

        Bundle argument = getArguments();
        mUser = (User)argument.getSerializable("user");

        //populateUI();
        if (argument != null) {
            String paymentTypeString = argument.getString("paymentType");
            Log.e("PAYMENT TYPE STIRING", paymentTypeString);
            Gson gson = new Gson();
            JsonArray array = gson.fromJson(paymentTypeString, JsonArray.class);

            paymentTypes = new ArrayList<PaymentType>();

            for (int i = 0; i < array.size(); i++){
                Integer payment_type_id = Integer.parseInt(array.get(i).getAsJsonObject().get("payment_type_id").toString());
                String payment_type = array.get(i).getAsJsonObject().get("payment_type").toString();
                payment_type = payment_type.replace("\"", "");
                Log.e("PAYMENT TYPE", payment_type.replace("\"", ""));
                paymentTypes.add(new PaymentType(payment_type_id, payment_type));
            }
        }

        cargoType = (Cargo)argument.getSerializable("cargoType");
        containerType = (Container)argument.getSerializable("containerType");
        containerSize = (ContainerSize)argument.getSerializable("containerSize");
        weightCatagory = (WeightCatagory)argument.getSerializable("weightCatagory");
        cargoVolume = (Float)argument.get("cargoVolume");
        measurementUnit = (MeasurementUnit)argument.getSerializable("measurementUnit");
        source = (Location)argument.getSerializable("source");
        destination = (Location)argument.getSerializable("destination");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        populateUI();

        chkLabourReq.setChecked(true);

        paymentTypes.add(0, new PaymentType(-1, "Select Payment Type"));
        ArrayAdapter<PaymentType> payment_adapter = new ArrayAdapter<PaymentType>(getContext(), android.R.layout.simple_spinner_item, paymentTypes);
        payment_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinPaymentType.setAdapter(payment_adapter);

        etLabourQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    //if true FCL else LCL
                    if (containerType.getContainer_type_id() == 2) {
                        if (containerSize.getVehicle_type_id() == 1) {
                            Log.e("Labour Quantity", s.toString() + "aa");
                            tvLabourCost.setText(Integer.toString(Integer.parseInt(s.toString()) * FCL_20FT_LABOUR_COST));
                        }
                        else{
                            tvLabourCost.setText(Integer.toString(Integer.parseInt(s.toString()) * FCL_40FT_LABOUR_COST));
                        }
                    }
                    else{
                        Log.e("Labour Quantity", s.toString() + "aa");
                        tvLabourCost.setText(Integer.toString(Integer.parseInt(s.toString()) * LCL_CM3_LABOUR_COST));
                    }
                }
            }
        });

        chkLabourReq.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //showHideUI(isChecked);
            }
        });

        placeOrder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String temp_labour_cost = tvLabourCost.getText().toString();
                String temp_labour_quantity  = etLabourQuantity.getText().toString();
                try{
                    labour_cost = Float.parseFloat(temp_labour_cost.isEmpty() ? "0" : temp_labour_cost);
                }
                catch (Exception ex){
                    labour_cost = 0;
                }
                try{
                    labour_quantity = Integer.parseInt(etLabourQuantity.getText().toString());
                }
                catch (Exception ex){
                    labour_quantity = 0;
                }
                Log.e(TAG, "Customer_id " + mUser.getUser_id());

                is_labour_required = chkLabourReq.isChecked();
                paymentType = (PaymentType) spinPaymentType.getItems().get(spinPaymentType.getSelectedIndex());
                description = etDescription.getText().toString();
                progressDialog = ProgressDialogManager.showProgressDialogWithTitle(getContext(), "Placing order", "Please wait");
                Order order = new Order(-1, cargoType.getCargo_type_id(), containerType.getContainer_type_id(),
                        containerSize.getVehicle_type_id(), weightCatagory.getWeight_id(), cargoVolume, measurementUnit.unit_id,
                        source.getLocation_id(), destination.getLocation_id(), is_labour_required, labour_cost, labour_quantity,
                        description, paymentType.payment_type_id, "", "", mUser.getUser_id());

                Gson gson = new Gson();
                String order_json = gson.toJson(order);
                Log.e("FragmentStep2OrderJson", order_json);
                placeOrder(order);
            }
        });
    }

    public void populateUI(){
        spinPaymentType = (MaterialSpinner)view.findViewById(R.id.spin_payment_type);
        chkLabourReq = (CheckBox)view.findViewById(R.id.chk_labour_required);
        tvLabourCost = (TextView)view.findViewById(R.id.tv_labour_cost);
        tilLabourQuantity = (TextInputLayout)view.findViewById(R.id.til_labour_quantity);
        etLabourQuantity = (EditText)view.findViewById(R.id.et_labour_quantity);
        etDescription = (TextInputEditText)view.findViewById(R.id.et_description_id);
        placeOrder = (Button)view.findViewById(R.id.btn_place_order);
    }

    private void placeOrder(Order order) {
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

            Call<Order> call = api.place_order(order);

            call.enqueue(new Callback<Order>() {
                @Override
                public void onResponse(Call<Order> call, Response<Order> response) {
                    Order response_order = response.body();
                    Log.e("RESPONSE BODY", response.message());
                    Log.e("RESPONSE BODY", response + "");
                    if (response_order != null && response_order.order_id != -1) {
                        Toast.makeText(getContext(), "Order placed", Toast.LENGTH_LONG).show();
                        Bundle bundle =new Bundle();
                        Gson gson = new Gson();
                        bundle.putSerializable("order", gson.toJson(response_order));
                        bundle.putString("show_wrt_order_id", "false");

                        FragmentListDriverWRTOrder fragment_list_driver = new FragmentListDriverWRTOrder();
                        fragment_list_driver.setArguments(bundle);

                        MainActivityCustomer activity = (MainActivityCustomer)getContext();

                        activity.getSupportFragmentManager().beginTransaction().
                                setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.fade_in, R.anim.fade_out).
                                replace(R.id.main_content_frame_customer_container, fragment_list_driver).
                                addToBackStack(null).
                                commit();
                    } else {
                        Toast.makeText(getContext(), "Please try again", Toast.LENGTH_SHORT).show();
                    }
                    ProgressDialogManager.closeProgressDialog(progressDialog);
                }

                @Override
                public void onFailure(Call<Order> call, Throwable t) {
                    Log.e("FAILURE", t.getMessage());
                    Log.e("FAILURE", t.toString());
                    Toast.makeText(getContext(), "Failure: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    ProgressDialogManager.closeProgressDialog(progressDialog);
                }
            });
        } catch (Exception ex) {
            Log.e("ERROR", ex.toString());
            ProgressDialogManager.closeProgressDialog(progressDialog);
            Toast.makeText(getContext(), "Some error occour, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void showHideUI(boolean is_labour_required){
        if (is_labour_required) {
            if (tilLabourQuantity.getVisibility() == View.GONE){
                tilLabourQuantity.setAlpha(0.0f);
                tilLabourQuantity.setVisibility(View.VISIBLE);
                // Start the animation
                tilLabourQuantity.animate()
                        .translationY(0)
                        .alpha(1.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                tilLabourQuantity.setVisibility(View.VISIBLE);
                            }
                        });
            }
            if (etLabourQuantity.getVisibility() == View.GONE){
                etLabourQuantity.setAlpha(0.0f);
                etLabourQuantity.setVisibility(View.VISIBLE);
                // Start the animation
                etLabourQuantity.animate()
                        .translationY(0)
                        .alpha(1.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                etLabourQuantity.setVisibility(View.VISIBLE);
                                etLabourQuantity.requestFocus();
                            }
                        });
            }
            if (tvLabourCost.getVisibility() == View.GONE){
                tvLabourCost.setAlpha(0.0f);
                tvLabourCost.setVisibility(View.VISIBLE);
                // Start the animation
                tvLabourCost.animate()
                        .translationY(0)
                        .alpha(1.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                tvLabourCost.setVisibility(View.VISIBLE);
                            }
                        });
            }
        }
        else {
            etDescription.requestFocus();
            if (tilLabourQuantity.getVisibility() == View.VISIBLE) {
                tilLabourQuantity.setAlpha(1.0f);
                // Start the animation
                tilLabourQuantity.animate()
                        .translationY(tilLabourQuantity.getHeight())
                        .alpha(0.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                tilLabourQuantity.setVisibility(View.GONE);
                            }
                        });
            }
            if (etLabourQuantity.getVisibility() == View.VISIBLE) {
                etLabourQuantity.setAlpha(1.0f);
                // Start the animation
                etLabourQuantity.animate()
                        .translationY(etLabourQuantity.getHeight())
                        .alpha(0.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                etLabourQuantity.setVisibility(View.GONE);
                            }
                        });
            }
            if (tvLabourCost.getVisibility() == View.VISIBLE) {
                tvLabourCost.setAlpha(1.0f);
                // Start the animation
                tvLabourCost.animate()
                        .translationY(tvLabourCost.getHeight())
                        .alpha(0.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                tvLabourCost.setVisibility(View.GONE);
                            }
                        });
            }
        }
    }

    private void populateDateViews() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String curr_date = dateFormat.format(date);

        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
//        lv_order_date.setText(day + "/" + month + "/" + year);

//        lv_order_date.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DialogFragment newFragment = new SelectDateFragment();
//
//                Bundle args = new Bundle();
//                args.putInt("date_id", R.id.lv_order_date);
//                newFragment.setArguments(args);
//
//                newFragment.show(getFragmentManager(), "DatePicker");
//            }
//        });
    }
}
