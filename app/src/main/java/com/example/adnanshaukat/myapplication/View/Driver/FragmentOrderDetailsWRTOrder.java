package com.example.adnanshaukat.myapplication.View.Driver;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.Adapters.OrdersAdapter;
import com.example.adnanshaukat.myapplication.GlobalClasses.LocationController;
import com.example.adnanshaukat.myapplication.GlobalClasses.ProgressDialogManager;
import com.example.adnanshaukat.myapplication.Modals.Notification;
import com.example.adnanshaukat.myapplication.Modals.Order;
import com.example.adnanshaukat.myapplication.Modals.SignalrTrackingManager;
import com.example.adnanshaukat.myapplication.Modals.Status;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IDriver;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IOrder;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.RetrofitManager;
import com.example.adnanshaukat.myapplication.Services.TrackingService;
import com.example.adnanshaukat.myapplication.View.Common.MapsActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by AdnanShaukat on 18/05/2019.
 */

public class FragmentOrderDetailsWRTOrder extends Fragment {

    View view;
    Order order;
    HashMap<String, String> order_details;
    User mUser;
    String order_type;

    ProgressDialog progressDialog;
    boolean is_order_request;

    ImageView customerImage;
    EditText order_id, order_by, source, destination, description, total_cost, labour_quantity, labour_cost, order_cost;
    Button btn_accept_order_or_complete_order;
    LinearLayout root_view;

    RadioButton rb_picking_order, rb_delivering_order;

    String current_latitude;
    String current_longitude;

    LocationManager locationManager;

    private static final int PERMISSIONS_REQUEST = 2;
    private static final int REQUEST_LOCATION = 1;

    private String TAG = this.toString();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_details_wrt_order, container, false);

        Bundle argument = getArguments();
        order = (Order) argument.getSerializable("order");
        is_order_request = argument.getBoolean("is_order_request");
        order_details = (HashMap<String, String>) argument.getSerializable("order_details");
        mUser = (User) argument.getSerializable("user");
        order_type = argument.getString("order_type");

        populateUI();
        getOrderCost(order_details.get("source_id"), order_details.get("destination_id"));

        if (is_order_request) {
            //btn_accept_order_or_complete_order.setBackground(getResources().getDrawable(R.drawable.button_ripple_effect));
            btn_accept_order_or_complete_order.setText("Accept Order");
            btn_accept_order_or_complete_order.setVisibility(View.VISIBLE);
        } else {
            if (order_type.equals("active_order")) {
                btn_accept_order_or_complete_order.setText("Track Order");
                btn_accept_order_or_complete_order.setVisibility(View.VISIBLE);
            } else {
                btn_accept_order_or_complete_order.setVisibility(View.INVISIBLE);
            }
        }

        btn_accept_order_or_complete_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_accept_order_or_complete_order.getText().toString().trim().equals("Accept Order")) {
                    //Accept Order
                    Status status = new Status();
                    if(status.getStatus() == 1){
                        confirmOrder(order.getOrder_id(), mUser.getUser_id());
                    }
                    else{
                        Snackbar snackbar = Snackbar.make(root_view, "Can't Accept Order, You Are Currently Inactive", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        //Toast.makeText(getContext(), "Can't Accept Order, You Are Currently Inactive", Toast.LENGTH_LONG).show();
                    }
                } else if (btn_accept_order_or_complete_order.getText().toString().trim().equals("Track Order")) {
                    //Resume Tracking
                    getDriverSourceDestination(mUser.getUser_id());
                } else if (btn_accept_order_or_complete_order.getText().toString().trim().equals("Start Tracking")) {
                    //Start Tracking
                    Log.e(TAG, "inside start tracking");
                    LayoutInflater alert_layout_inflater = getLayoutInflater();
                    View alertLayout = alert_layout_inflater.inflate(R.layout.tracking_status_alert_layout, null);
                    rb_picking_order = alertLayout.findViewById(R.id.alert_dialog_picking_order);
                    rb_delivering_order = alertLayout.findViewById(R.id.alert_dialog_delivering_order);
                    showDialog(alertLayout);
                }
            }
        });

        return view;
    }

    private void populateUI() {
        root_view = view.findViewById(R.id.order_details_root_view);
        customerImage = view.findViewById(R.id.customer_image_for_order_details);
        order_id = view.findViewById(R.id.order_details_order_id);
        order_by = view.findViewById(R.id.order_details_order_by);
        source = view.findViewById(R.id.order_details_source);
        destination = view.findViewById(R.id.order_details_destination);
        total_cost = view.findViewById(R.id.order_details_total_cost);
        labour_quantity = view.findViewById(R.id.order_details_labour_quantity);
        labour_cost = view.findViewById(R.id.order_details_labour_cost);
        order_cost = view.findViewById(R.id.order_details_order_cost);
        description = view.findViewById(R.id.order_details_description);
        btn_accept_order_or_complete_order = view.findViewById(R.id.btn_order_details_accept_order);
    }

    private void setValues() {
        try {
            if (order_details.get("profile_picture") == null || order_details.get("profile_picture").isEmpty()) {
                customerImage.setImageResource(R.drawable.default_profile_image);
            } else {
                //new DownloadImageTask(holder.profile_image).execute(image_path);
                String image_path = "http://" + RetrofitManager.ip + "/" + RetrofitManager.domain + "/Images/AppImages/" + order_details.get("profile_picture");
                Picasso
                        .with(getContext())
                        .load(image_path)
                        .into(customerImage);
            }
        } catch (Exception ex) {
            customerImage.setImageResource(R.drawable.default_profile_image);
        }

        order_id.setText(order_details.get("order_id"));
        order_by.setText(order_details.get("customer_name"));
        source.setText(order_details.get("source"));
        labour_cost.setText(order_details.get("labour_cost"));
        labour_quantity.setText(order_details.get("labour_quantity"));
        destination.setText(order_details.get("destination"));
        if (order_details.get("description").isEmpty()) {
            description.setText("No description available");
        } else {
            description.setText(order_details.get("description"));
        }

        float total_cost_order = Float.parseFloat(order_details.get("labour_cost")) + Float.parseFloat(order_cost.getText().toString());
        total_cost.setText(Float.toString(total_cost_order));
    }

    private void confirmOrder(int order_id, int driver_id) {
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

            Log.e("driver Id", driver_id + " " + order_id);

            Call<Object> call = api.confirm_order(driver_id, order_id);

            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    Log.e("ConfirmResponse", response.toString());
                    Object response_json = response.body();

                    if (response_json != null) {
                        Log.e("ConfirmDriver", response_json.toString());
                        Gson gson = new Gson();
                        try {
                            JsonArray json_object = gson.fromJson(response_json.toString(), JsonArray.class);
                            String output = json_object.get(0).getAsJsonObject().get("output").toString().trim().replace("\"", "");
                            Log.e("Confirm", output);
                            if (output.equals("Order request expires")) {
                                Toast.makeText(getContext(), "Order request expires", Toast.LENGTH_SHORT).show();
                                btn_accept_order_or_complete_order.setEnabled(false);
                            } else if (output.equals("Order accepted, Customer will be notified")) {
                                Toast.makeText(getContext(), "Order accepted, Customer will be notified", Toast.LENGTH_SHORT).show();
                                Log.e("RESPONSE!!", "OK");
                                notifyCustomer();
                                Log.e(TAG, "tracking order starting");
                                btn_accept_order_or_complete_order.setText("Start Tracking");
                                btn_accept_order_or_complete_order.setVisibility(View.VISIBLE);
                                btn_accept_order_or_complete_order.callOnClick();
                            }
                            else if(output.equals("Can not accept order, You already have active order")){
                                Snackbar snackbar = Snackbar.make(root_view, output, Snackbar.LENGTH_LONG);
                                snackbar.show();
                                //Toast.makeText(getContext(), output, Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception ex) {
                            Toast.makeText(getContext(), "An error occour, Please try again", Toast.LENGTH_SHORT).show();
                            Log.e(this.toString(), ex.toString() + ex.getStackTrace());
                        }
                    } else {
                        Toast.makeText(getContext(), "Order Expires, Please try again", Toast.LENGTH_SHORT).show();
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
        } catch (Exception ex) {
            ProgressDialogManager.closeProgressDialog(progressDialog);
            Log.e("ERROR", ex.toString());
        }
    }

    private void notifyCustomer() {
        Notification notificaton = new Notification(0, mUser.getFirst_name() + " " + mUser.getLast_name() + " accepted your order request", mUser.getUser_id(), 0, 0, "FragmentOrderListForCustomer", "customer", "Order Request Accepted", "unicast", Integer.parseInt(order_details.get("customer_id")));
        notificaton.pushNotification(getContext());
    }

    private void showDialog(View alertLayout) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("You're going to ?");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (rb_picking_order.isChecked()) {
                    Toast.makeText(getContext(), "Picking Order", Toast.LENGTH_SHORT).show();
                    getCurrentLocation();
                } else if (rb_delivering_order.isChecked()) {
                    if(check_permissions()){

                        Intent intent = new Intent(getContext(), MapsActivity.class);
                        intent.putExtra("source", order_details.get("source"));
                        intent.putExtra("destination", order_details.get("destination"));
                        intent.putExtra("string_source", true);
                        intent.putExtra("string_destination", true);
                        intent.putExtra("moving_to", "deliver_order");

                        updateDriverSourceDestination(order_details.get("source"), order_details.get("destination"), "deliver_order");

                        startActivity(intent);
                        startTrackerService();
                    }
                }
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void getCurrentLocation() {
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }
    }

    private boolean getLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            return false;
        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                current_latitude = String.valueOf(latti);
                current_longitude = String.valueOf(longi);

            } else if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();
                current_latitude = String.valueOf(latti);
                current_longitude = String.valueOf(longi);

            } else if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                current_latitude = String.valueOf(latti);
                current_longitude = String.valueOf(longi);

            } else {
                Toast.makeText(getContext(),"Unable to Trace your location",Toast.LENGTH_SHORT).show();
                buildAlertMessageNoGps();
                return false;
            }

            if (check_permissions()){
                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("source_latitude", current_latitude + "");
                intent.putExtra("source_longitude", current_longitude + "");
                intent.putExtra("string_source", false);
                intent.putExtra("string_destination", true);
                intent.putExtra("destination", order_details.get("source"));
                intent.putExtra("moving_to", "pick_order");

                updateDriverSourceDestination(current_latitude + "|" + current_longitude, order_details.get("source"), "pick_order");

                Log.e("Driver Latitude", current_latitude + "");
                Log.e("Driver Longitude", current_longitude + "");
                Log.e("SOURCE", order_details.get("source"));

                startActivity(intent);
                startTrackerService();
            }
            return true;
        }
    }

    protected void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Please Turn ON your mobile Location")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {

        }
    }

    public void updateDriverSourceDestination(String source, String destination, String status) {
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


            Call<Object> call = api.update_driver_source_destination(mUser.getUser_id(), source, destination, status);

            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    Log.e(TAG + "update_source_dst", response.toString());
                    Log.e(TAG + "update_source_dst", response.body().toString());

                    Object response_json = response.body();
                    if (response_json != null) {

                    } else {
                        //Toast.makeText(getContext(), "Order Expires, Please try again", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.e(TAG + "FAILURE", t.getMessage());
                    Log.e(TAG + "FAILURE", t.toString());
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception ex) {
            Log.e(TAG + "ERROR", ex.toString());
        }
    }

    public void getDriverSourceDestination(Integer driver_id) {
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


            Call<Object> call = api.get_driver_source_destination(driver_id);

            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    Log.e(TAG + "get_source_dst", response.toString());
                    Log.e(TAG + "get_source_dst", response.body().toString());
                    Object response_json = response.body();
                    if (response_json != null) {
                        try {
                            JSONObject obj = new JSONObject(response_json.toString());
                            JSONArray arr = obj.getJSONArray("data");
                            obj = arr.getJSONObject(0);
                            String source = obj.get("source").toString();
                            String destination = obj.get("destination").toString();
                            String status = obj.get("status").toString();

                            Intent intent = new Intent(getContext(), MapsActivity.class);
                            intent.putExtra("moving_to", status);
                            if (status.trim().toLowerCase().equals("pick_order")){
                                List<String> sources = get_split_result(source, "|");
                                Log.e(TAG, sources.get(0));
                                Log.e(TAG, sources.get(1));
                                intent.putExtra("source_latitude", sources.get(0));
                                intent.putExtra("source_longitude", sources.get(1));
                                intent.putExtra("destination", destination);
                                intent.putExtra("string_source", false);
                                intent.putExtra("string_destination", true);
                            }
                            else{
                                intent.putExtra("source", source);
                                intent.putExtra("destination", destination);
                                intent.putExtra("string_source", true);
                                intent.putExtra("string_destination", true);
                            }

                            startActivity(intent);

                        } catch (JSONException e) {
                            Log.e(TAG + "ParingError", e.toString());
                            e.printStackTrace();
                        }

                    } else {
                        //Toast.makeText(getContext(), "Order Expires, Please try again", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.e(TAG + "FAILURE", t.getMessage());
                    Log.e(TAG + "FAILURE", t.toString());
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception ex) {
            Log.e(TAG + "ERROR", ex.toString());
        }
    }

    private List<String> get_split_result(String source, String seperator) {

        List<String> list = new ArrayList<>();
        String item = "";
        char[] char_array = source.toCharArray();
        for (Integer i = 0; i <  char_array.length; i++){
            if (String.valueOf(char_array[i]).equals(seperator)){
                list.add(item);
                item = "";
            }
            else{
                item += source.toCharArray()[i] + "";
            }
        }

        if (!item.isEmpty()){
            list.add(item);
        }
        return list;
    }

    private boolean check_permissions(){
        LocationManager lm = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getContext(), "Please enable location services", Toast.LENGTH_SHORT).show();
            //finish();
        }
        // Check location permission is granted - if it is, start
        // the service, otherwise request the permission
        int permission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST);
            return false;
        }
    }

    private void startTrackerService() {
        Intent serviceIntent = new Intent(getContext(), TrackingService.class);
        serviceIntent.putExtra("order_detail_id", order_details.get("order_detail_id"));
        getActivity().startService(serviceIntent);
    }

    private void getOrderCost(String source_id, String destination_id){
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
            Log.e("SOURCE", source_id + " a");
            Log.e("DESTINATION", destination_id + " a");

            Call<Object> call = api.get_order_cost(source_id, destination_id);

            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    Log.e("AllOrderResponse", response.toString());
                    Object response_json = response.body();

                    if (response_json != null)
                    {
                        Log.e("Response Driver Dtls", response_json.toString());
                        try {
                            JSONObject temp_obj = new JSONObject(response_json.toString());
                            boolean success = (boolean)temp_obj.get("success");
                            String price = "0";
                            if (success){
                                JSONObject obj = new JSONArray(temp_obj.get("data").toString()).getJSONObject(0);
                                if (order_details.get("container_type_id").equals("1")){
                                    price = obj.get("lcl_price").toString();
                                }
                                else{
                                    if (order_details.get("vehicle_type_id").equals("1")){
                                        price = obj.get("20ft_price").toString();
                                    }
                                    else{
                                        price = obj.get("40ft_price").toString();
                                    }
                                }
                            }
                            else{

                            }
                            order_cost.setText(price);
                            setValues();
                        } catch (Exception ex) {
                            Log.e("OrderDetails", ex.toString() + ex.getStackTrace());
                        }
                    } else
                    {
                        order_cost.setText("0");
                        setValues();
                    }
                }
                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.e("OrderDetails FAILURE", t.getMessage());
                    Log.e("OrderDetails FAILURE", t.toString());
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    ProgressDialogManager.closeProgressDialog(progressDialog);
                }
            });
        }
        catch (Exception ex){
            ProgressDialogManager.closeProgressDialog(progressDialog);
            Log.e("OrderDetails ERROR", ex.toString());
        }
    }
}
