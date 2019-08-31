package com.example.adnanshaukat.myapplication.View.Common;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.MapHelper.FetchURL;
import com.example.adnanshaukat.myapplication.MapHelper.MapsController;
import com.example.adnanshaukat.myapplication.MapHelper.TaskLoadedCallback;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IOrder;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.ISignUp;
import com.example.adnanshaukat.myapplication.Services.TrackingService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.adnanshaukat.myapplication.R.id.map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private static GoogleMap mMap;

    MapsController mapsController;

    List<Marker> marker_ = new ArrayList<>();
    Context context;

    private static LatLng sourceLatLng, destinationLatLng;
    private static String move_to = "";
    private static Polyline currentPolyline;
    PolylineOptions currentPolylineOptions;

    List<MarkerOptions> markers_option = new ArrayList<>();
    private static Marker current_marker;
    private static Intent static_intent;
    LatLngBounds.Builder builder = new LatLngBounds.Builder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        MultiDex.install(this);
        Intent intent = getIntent();
        static_intent = intent;
        mapsController = new MapsController(this, getString(R.string.google_maps_key));

        Log.e("DESTINATION", intent.getExtras().get("destination").toString());

        HashMap<String, LatLng> hashMap = getSourceDestinationLatLng(intent);
        sourceLatLng = hashMap.get("source");
        destinationLatLng = hashMap.get("destination");

        Log.e(MapsActivity.class.toString() + "SLat", sourceLatLng.latitude + "");
        Log.e(MapsActivity.class.toString() + "SLong", sourceLatLng.longitude + "");

        Log.e(MapsActivity.class.toString() + "DLat", destinationLatLng.latitude + "");
        Log.e(MapsActivity.class.toString() + "DLong", destinationLatLng.longitude + "");

        MarkerOptions source = new MarkerOptions().position(sourceLatLng);
        MarkerOptions destination = new MarkerOptions().position(destinationLatLng).title("Destination");

        move_to = intent.getExtras().get("moving_to").toString();
        if (intent.getExtras().get("moving_to").toString().trim().toLowerCase().equals("pick_order")) {
            source.title("Source(Your's Position)");
        } else {
            source.title("Source");
        }

        markers_option.add(source);
        markers_option.add(destination);

        String url = mapsController.getUrl(sourceLatLng, destinationLatLng, "driving");
        Log.e(MapsActivity.class.toString() + "URL", url);
        new FetchURL(MapsActivity.this).execute(url, "driving");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);
        context = this;
    }

    public void updateMap(String latitude, String longitude, Context _context){
        Log.e("MapsActivity", "UpdateMap: " + latitude + " " + longitude);
        double Lat =  Double.parseDouble(latitude);
        double Lng = Double.parseDouble(longitude);
//        try{
            if(mMap != null){
                if (mapsController == null){
                    mapsController = new MapsController(_context, _context.getResources().getString(R.string.google_maps_key));
                }
                LatLng Loc = new LatLng(Lat,Lng);
                if (current_marker == null){
                    Log.e("MAPS ACTIVITY", "Adding new marker");
                    current_marker = mMap.addMarker(new MarkerOptions()
                            .position(Loc)
                            .icon(mapsController.getBitmapDescriptor(_context, R.drawable.vehicle_icon)).flat(true)
                            //.infoWindowAnchor(1, 1)
                            //.anchor(1, 1)
                            //.snippet("Hi how are you")
                            .title("Driver Location"));
                }
                else{
                    Log.e("MapActivity", "SETTING MARKER");
                    current_marker.setPosition(Loc);
                    //current_marker.showInfoWindow();
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Loc, 16.0f));

                if (sourceLatLng != null){
                    CircleOptions circleOptions = new CircleOptions();
                    circleOptions.center(destinationLatLng);
                    circleOptions.radius(10);
                    circleOptions.visible(false);
                    mMap.addCircle(circleOptions);

                    if(mapsController.isReachedDestination(circleOptions, current_marker.getPosition())){
                        Toast.makeText(_context, "You have reached to the destination", Toast.LENGTH_SHORT).show();
                        if (move_to.trim().toLowerCase().equals("pick_order")) {
                            Log.e("MAPS ACTIVITY", "Updating map with new route");
                            Log.e("MAPS ACTIVITY", static_intent.getExtras().getString("order_destination"));
                            LatLng order_destination = mapsController.getLatLongFromLocation(static_intent.getExtras().getString("order_destination"));
                            reloadMapWithNewRoute(destinationLatLng, order_destination);
                            move_to = "deliver_order";
                        }
                        else{
                            Intent trackingService = new Intent(_context, TrackingService.class);
                            _context.stopService(trackingService);
                            Log.e("MAPS ACTIVITY", "DONE WITH DELIVERING ORDER");
                            setOrderCompletion(static_intent.getExtras().getString("order_detail_id"));
                        }

                        //setDialog(_context);
                    }
                }
            }
            else{
                Log.e("MapsActivity", "MAP OBJECT is empty");
            }
//        }
//        catch (Exception ex) {
//            Log.e("MapsActivity", ex.toString());
//        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("MapsActivity", "creating map object");
        mMap = googleMap;

        Log.e("MapsActivity", "Adding Markers");
        for (int i = 0; i < markers_option.size(); i++) {
            MarkerOptions mo = new MarkerOptions()
                    .position(markers_option.get(i).getPosition())
                    .title(markers_option.get(i).getTitle());

            if (mo.getTitle().equals("Your's Position")) {
                mo.icon(mapsController.getBitmapDescriptor(this, R.drawable.vehicle_icon)).flat(true);
            }
            marker_.add(mMap.addMarker(mo));
        }
    }

    @Override
    public void onTaskDone(Object... values) {
        PolylineOptions polyLine = (PolylineOptions) values[0];
        placePolyLine(polyLine);
    }

    public HashMap<String, LatLng> getSourceDestinationLatLng(Intent intent) {
        LatLng destination_latLng, source_latLng;
        HashMap<String, LatLng> hashMap = new HashMap<>();

        if((boolean)intent.getExtras().get("string_source")){
            String source = intent.getExtras().get("source").toString();
            source_latLng = mapsController.getLatLongFromLocation(source);
        }
        else{
            Double latitude = Double.parseDouble(intent.getExtras().get("source_latitude").toString());
            Double longitude = Double.parseDouble(intent.getExtras().get("source_longitude").toString());
            source_latLng = new LatLng(latitude, longitude);
        }

        if((boolean)intent.getExtras().get("string_destination")){
            String destination = intent.getExtras().get("destination").toString();
            destination_latLng = mapsController.getLatLongFromLocation(destination);
        }
        else{
            Double latitude = Double.parseDouble(intent.getExtras().get("destination_latitude").toString());
            Double longitude = Double.parseDouble(intent.getExtras().get("destination_longitude").toString());
            destination_latLng = new LatLng(latitude, longitude);
        }

        hashMap.put("source", source_latLng);
        hashMap.put("destination", destination_latLng);

        return hashMap;
    }

    private void placePolyLine(PolylineOptions polyLine) {
        if (currentPolyline != null)
            currentPolyline.remove();

        currentPolyline = mMap.addPolyline(polyLine);
        currentPolyline.getPoints();

        for (LatLng item : currentPolyline.getPoints()) {
            builder.include(item);
        }

        int padding = 200;
        LatLngBounds bounds = builder.build();

        final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
    }

    private void setDialog(Context context){
        LayoutInflater alert_layout_inflater = LayoutInflater.from(context);
        View alertLayout = alert_layout_inflater.inflate(R.layout.order_completion_alert_layout, null);
        showDialog(alertLayout, context);
    }

    private void showDialog(View alertLayout,final Context _context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(_context);
        alert.setTitle("You're going to ?");
        alert.setView(alertLayout);
        alert.setCancelable(false);
//        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(_context, "Thank you", Toast.LENGTH_SHORT).show();
//            }
//        });

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(_context, "Thank you", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void reloadMapWithNewRoute(LatLng source, LatLng destination){
        Log.e("ReLoading MAP ROUTE", source.toString());
        Log.e("ReLoading MAP ROUTE", destination.toString());
        MarkerOptions source_option = new MarkerOptions().position(source).title("Source");
        MarkerOptions destination_option = new MarkerOptions().position(destination).title("Destination");

        markers_option = new ArrayList<>();
        markers_option.add(source_option);
        markers_option.add(destination_option);
        addMarkers(markers_option);
        sourceLatLng = source;
        destinationLatLng = destination;
        String url = mapsController.getUrl(source, destination, "driving");
        Log.e(MapsActivity.class.toString() + "URL", url);
        new FetchURL(MapsActivity.this).execute(url, "driving");
    }

    private void addMarkers(List<MarkerOptions> markers_options){
        for (int i = 0; i < markers_options.size(); i++) {
            MarkerOptions mo = new MarkerOptions()
                    .position(markers_options.get(i).getPosition())
                    .title(markers_options.get(i).getTitle());

            if (mo.getTitle().equals("Your's Position")) {
                mo.icon(mapsController.getBitmapDescriptor(this, R.drawable.vehicle_icon)).flat(true);
            }
            marker_.clear();
            marker_.add(mMap.addMarker(mo));
        }
    }

    private void setOrderCompletion(String order_detail_id){
        try{
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
            Call<Object> call = api.set_order_completion(order_detail_id);

            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    Object obj = response.body();
                    Log.e("RESPONSE", response.toString());

                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.e("FAILURE", t.getMessage());
                    Log.e("FAILURE", t.toString());
                }
            });
        } catch (Exception ex) {
            Log.e("ERROR", ex.toString());
            Toast.makeText(getApplicationContext(), "Some error occour, please try again", Toast.LENGTH_SHORT).show();
        }
    }
}
