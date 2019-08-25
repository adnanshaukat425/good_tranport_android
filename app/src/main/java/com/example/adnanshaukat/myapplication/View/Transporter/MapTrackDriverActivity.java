package com.example.adnanshaukat.myapplication.View.Transporter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.MapHelper.MapsController;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.ILocation;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IOrder;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.adnanshaukat.myapplication.R.id.map;

public class MapTrackDriverActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static GoogleMap mMap;
    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    MapsController mapsController;

    private static Hashtable<Integer, Marker> markers = new Hashtable<>();
    Object[][] marker_details;

    String TAG = this.getClass().getSimpleName();
    User mUser;
    int PADDING = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_track_driver);
        MultiDex.install(this);
        Intent intent = getIntent();
        mUser = (User)intent.getExtras().get("user");
        getDriverWrtTransporter(mUser.getUser_id());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.track_map);
        mapFragment.getMapAsync(MapTrackDriverActivity.this);

        mapsController = new MapsController(this, getResources().getString(R.string.google_maps_key));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (marker_details != null){
            Log.e(TAG + "SIZE", marker_details.length + "");
            for (Object[] marker_detail : marker_details) {
                MarkerOptions mo = new MarkerOptions()
                        .position((LatLng) marker_detail[0])
                        .title((String) marker_detail[1])
                        .flat(true)
                        .icon(mapsController.getBitmapDescriptor(this, R.drawable.vehicle_icon));

                Marker temp_ = mMap.addMarker(mo);
                temp_.showInfoWindow();
//                ImageView image = findViewById(R.id.main_image);
//                TextView markerText = new TextView(this);
//                markerText.setText(marker_detail[1].toString());
//
//                LinearLayout tv = (LinearLayout) this.getLayoutInflater().inflate(R.layout.marker_info_window, null, false);
//                tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//                tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());
//                tv.addView(markerText, 0);
//                tv.setDrawingCacheEnabled(true);
//                tv.buildDrawingCache();
//                Bitmap bm = tv.getDrawingCache();
//
//                temp_.setIcon(BitmapDescriptorFactory.fromBitmap(bm));

                markers.put(Integer.parseInt(marker_detail[2].toString()), temp_);
                builder.include(mo.getPosition());
            }

            LatLngBounds bounds = builder.build();
            final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, PADDING);
            mMap.animateCamera(cu);
        }
    }

    public void update_marker(LatLng new_latLng, int driver_id, String driver_name, Context context){
        Log.e(TAG, "UPDATING MARKER");
        try {
            if (markers.contains(driver_id)) {
                markers.get(driver_id).setPosition(new_latLng);
            }
            else{
                mapsController = new MapsController(context, context.getString(R.string.google_maps_key));
                if (mMap != null && mapsController != null){
                    MarkerOptions mo = new MarkerOptions()
                            .position(new_latLng)
                            .title(driver_name)
                            .flat(true)
                            .icon(mapsController.getBitmapDescriptor(context, R.drawable.vehicle_icon));

                    Marker temp_ = mMap.addMarker(mo);
                    temp_.showInfoWindow();
                    markers.put(driver_id, temp_);
                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void getDriverWrtTransporter(int transporter_id){
        try {
            OkHttpClient.Builder client = new OkHttpClient.Builder();

            client.connectTimeout(30, TimeUnit.SECONDS);
            client.readTimeout(30, TimeUnit.SECONDS);
            client.writeTimeout(30, TimeUnit.SECONDS);

            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().
                    baseUrl(ILocation.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();

            ILocation api = retrofit.create(ILocation .class);

            Call<Object> call = api.get_driver_latLng_from_transporter_id(transporter_id);

            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    Log.e(TAG + " Response ", response.toString());
                    Object response_json = response.body();
                    try{
                        JSONObject response_obj = new JSONObject(response_json.toString());
                        if((boolean)response_obj.get("success")){
                            JSONArray response_arr = response_obj.getJSONArray("data");
                            marker_details = new Object[response_arr.length()][3];
                            for (int i = 0; i < response_arr.length(); i++){
                                response_obj = response_arr.getJSONObject(i);
                                marker_details[i][0] = new LatLng(Double.parseDouble(response_obj.get("current_latitude").toString()),
                                        Double.parseDouble(response_obj.get("current_longitude").toString()));
                                marker_details[i][1] = response_obj.get("driver_name").toString();
                                marker_details[i][2] = response_obj.get("user_id").toString();
                            }

                            Log.e("SIZE", marker_details.length + "");
                            onMapReady(mMap);
                        }
                        else{
                            Toast.makeText(MapTrackDriverActivity.this, "No Active Drivers Available", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch(Exception ex){
                        ex.printStackTrace();
                        Toast.makeText(MapTrackDriverActivity.this, "An Error Occur, Please Try Again", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.e(TAG + "FAILURE", t.getMessage());
                    Log.e(TAG + "FAILURE", t.toString());
                }
            });
        }
        catch(Exception ex){
            Log.e(TAG + "ERROR", ex.getMessage());
            Log.e(TAG + "ERROR", ex.getStackTrace().toString());
        }
    }
}
