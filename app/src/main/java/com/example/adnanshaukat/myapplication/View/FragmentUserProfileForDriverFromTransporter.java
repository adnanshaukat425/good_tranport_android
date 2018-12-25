package com.example.adnanshaukat.myapplication.View;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.GlobalClasses.EncoderDecoder;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.Modals.Vehicle;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IDriver;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IVehicleWrtDriver;

import org.json.JSONObject;

import java.lang.reflect.Field;
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
 * Created by AdnanShaukat on 09/12/2018.
 */

public class FragmentUserProfileForDriverFromTransporter extends Fragment {

    HashMap hm;
    ImageView profile_image;
    String[] vehicles_array;
    int[] vehicles_id;
    String vehicle_id;
    String vehicle_number;

    View view;
    User mUser;
    Spinner spinner;
    TextView tv_first_name, tv_last_name, tv_email, tv_phone_no, tv_cnic, tv_driver_since;

    Button btn_update;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_profile_for_driver_from_transporter, container, false);
        populateUI();
        //getVehicleFromSpinner();
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = spinner.getSelectedItemPosition();
                int id = vehicles_id[position];
                Toast.makeText(getContext(), id + "", Toast.LENGTH_SHORT).show();
                UpdateDriversVehicle(mUser.getUser_id() + "", id + "");
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle arguments = getArguments();
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);
            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow sourcePopup = (android.widget.ListPopupWindow) popup.get(spinner);

            // Set popupWindow height to 500px
            sourcePopup.setHeight(700);
        } catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }

        if (arguments != null) {
            mUser = (User) arguments.get("user_from_driver_list");
            String transporter_id = arguments.get("transporter_id").toString();
            getVehicles(transporter_id);

            tv_first_name.setText(mUser.getFirst_name());
            tv_last_name.setText(mUser.getLast_name());
            tv_email.setText(mUser.getEmail());
            tv_phone_no.setText(mUser.getPhone_number());
            tv_cnic.setText(mUser.getCnic_number());
            tv_driver_since.setText(mUser.getCreated_date());

            getVehicleFromSpinner();

            String encodedImage = mUser.getProfile_picture();
            Bitmap bitmap = EncoderDecoder.getDecodeImage(encodedImage);

            if(bitmap == null){
                profile_image.setImageResource(R.drawable.default_profile_image);
            }
            else{
                profile_image.setImageBitmap(bitmap);
            }

        } else {
            //Toast.makeText(getContext(), "ARGUMENT IS EMPTY", Toast.LENGTH_SHORT).show();
        }
    }

    private void populateUI() {
        spinner = (Spinner) view.findViewById(R.id.sp_vehicle_assign);
        tv_first_name = (TextView) view.findViewById(R.id.ed_fname);
        tv_last_name = (TextView) view.findViewById(R.id.ed_lname);
        tv_email = (TextView) view.findViewById(R.id.ed_email);
        tv_phone_no = (TextView) view.findViewById(R.id.ed_number);
        tv_cnic = (TextView) view.findViewById(R.id.ed_cnic);
        tv_driver_since = (TextView) view.findViewById(R.id.ed_driver_since);
        profile_image = (ImageView)view.findViewById(R.id.driver_frag_image_view);

        btn_update = (Button) view.findViewById(R.id.btn_fragment_user_profile_update);
    }

    private List<User> getVehicles(String transporter_id) {
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

            IVehicleWrtDriver api = retrofit.create(IVehicleWrtDriver.class);

            Log.e("Transporter Id", transporter_id);

            Call<List<Vehicle>> call = api.get_transporter(transporter_id);

            call.enqueue(new Callback<List<Vehicle>>() {
                @Override
                public void onResponse(Call<List<Vehicle>> call, Response<List<Vehicle>> response) {
                    List<Vehicle> vehicles = response.body();
                    //Log.e("VEHICLES NUMBER", Float.toString(vehicles.get(0).getVehicle_number()));
                    if (vehicles != null && vehicles.size() > 0) {

                        vehicles_array = new String[vehicles.size()];
                        vehicles_id = new int[vehicles.size()];
                        hm = new HashMap();
                        int selected_index = 0;
                        for (int i = 0; i < vehicles.size(); i++) {
                            String vehicle = Integer.toString(vehicles.get(i).getVehicle_number());
                            hm.put(vehicles.get(i).getVehicle_id(), vehicle);
                            vehicles_array[i] = vehicle;
                            vehicles_id[i] = vehicles.get(i).getVehicle_id();
                            if(vehicles.get(i).getDriver_id() == mUser.getUser_id()){
                                selected_index = i;
                            }
                        }
                        setDropdown(selected_index);
                    } else {
                        Toast.makeText(getContext(), "No Vehicles found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Vehicle>> call, Throwable t) {
                    Log.e("FAILURE", t.getMessage());
                    Log.e("FAILURE", t.toString());
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception ex) {
            Log.e("ERROR", ex.toString());
        }
        return result_list;
    }

    private void setDropdown(int selected_index) {
        spinner.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, vehicles_array));
        spinner.setSelection(selected_index, true);
    }

    private void getVehicleFromSpinner() {

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                Object key = hm.get(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void UpdateDriversVehicle(String user_id, String vehicle_id){
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

            String json_string = "";

            JSONObject obj = new JSONObject();
            obj.put("user_id", user_id);
            obj.put("vehicle_id", vehicle_id);

            json_string = obj.toString();

            Toast.makeText(getContext(), "JSON STRING " + json_string + "", Toast.LENGTH_SHORT).show();

            Call<String> call = api.update_drivers_vehicle(json_string);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    String result = response.body();
                    Toast.makeText(getContext(), result + "", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("FAILURE", t.getMessage());
                    Log.e("FAILURE", t.toString());
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception ex) {
            Log.e("ERROR", ex.toString());
        }
    }
}