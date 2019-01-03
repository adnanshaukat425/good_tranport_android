package com.example.adnanshaukat.myapplication.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.example.adnanshaukat.myapplication.Modals.PreOrderDataWrapperClass;
import com.example.adnanshaukat.myapplication.Modals.WeightCatagory;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IOrder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

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

    Spinner spinPaymentType;
    CheckBox chkLabourReq;
    EditText etLabourQuantity;
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
    Integer labour_quantity;
    float labour_cost, cargoVolume;

    ProgressDialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_order_step2, container, false);

        Bundle argument = getArguments();
        populateUI();

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

        ArrayAdapter<PaymentType> payment_adapter = new ArrayAdapter<PaymentType>(getContext(), android.R.layout.simple_spinner_item, paymentTypes);
        payment_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinPaymentType.setAdapter(payment_adapter);

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialogManager.showProgressDialogWithTitle(getContext(), "Placing order", "Please wait");
                Order order = new Order(-1, cargoType.getCargo_type_id(), containerType.getContainer_type_id(),
                        containerSize.getVehicle_type_id(), weightCatagory.getWeight_id(), cargoVolume, measurementUnit.unit_id,
                        source.getLocation_id(), destination.getLocation_id(), is_labour_required, labour_cost,
                        paymentType.payment_type_id, null, null);

                placeOrder(order);
            }
        });
    }

    public void populateUI(){
        spinPaymentType = (Spinner)view.findViewById(R.id.spin_payment_type);
        chkLabourReq = (CheckBox)view.findViewById(R.id.chk_labour_required);
        tvLabourCost = (TextView)view.findViewById(R.id.tv_labour_cost);
        etLabourQuantity = (EditText)view.findViewById(R.id.et_labour_quantity);
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
                    if (response_order.order_id != -1) {
                        Toast.makeText(getContext(), "Welcome " + response_order.order_id + "", Toast.LENGTH_LONG).show();
                        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        Toast.makeText(getContext(), "Username or password is not correct", Toast.LENGTH_SHORT).show();
                    }
                    ProgressDialogManager.closeProgressDialog(progressDialog);
                }

                @Override
                public void onFailure(Call<Order> call, Throwable t) {
                    Log.e("FAILURE", t.getMessage());
                    Log.e("FAILURE", t.toString());
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    ProgressDialogManager.closeProgressDialog(progressDialog);
                }
            });
        } catch (Exception ex) {
            Log.e("ERROR", ex.toString());
            ProgressDialogManager.closeProgressDialog(progressDialog);
            Toast.makeText(getContext(), "Some error occour, please try again", Toast.LENGTH_SHORT).show();
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
