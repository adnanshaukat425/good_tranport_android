package com.example.adnanshaukat.myapplication.View.Common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.multidex.MultiDex;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.MapHelper.DataParser;
import com.example.adnanshaukat.myapplication.MapHelper.FetchURL;
import com.example.adnanshaukat.myapplication.MapHelper.MapsController;
import com.example.adnanshaukat.myapplication.MapHelper.TaskLoadedCallback;
import com.example.adnanshaukat.myapplication.Modals.DirectionJSONParser;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.Services.TrackingService;
import com.google.android.gms.location.places.PlacesOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.adnanshaukat.myapplication.R.id.map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private static GoogleMap mMap;

    MapsController mapsController;

    List<Marker> marker_ = new ArrayList<>();
    Context context;

    private static LatLng sourceLatLng, destinationLatLng;

    Polyline currentPolyline;
    PolylineOptions currentPolylineOptions;

    List<MarkerOptions> markers_option = new ArrayList<>();
    Marker current_marker;

    LatLngBounds.Builder builder = new LatLngBounds.Builder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        MultiDex.install(this);
        Intent intent = getIntent();

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
                    current_marker = mMap.addMarker(new MarkerOptions()
                            .position(Loc)
                            .icon(mapsController.getBitmapDescriptor(_context, R.drawable.vehicle_icon)).flat(true)
                            //.infoWindowAnchor(1, 1)
                            //.anchor(1, 1)
                            //.snippet("Hi how are you")
                            .title("Driver Location"));

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Loc, 16.0f));
                }
                else{
                    Log.e("MapActivity", "SETTING MARKER");
                    current_marker.setPosition(Loc);
                    //current_marker.showInfoWindow();
                }

                if (sourceLatLng != null){
                    CircleOptions circleOptions = new CircleOptions();
                    circleOptions.center(sourceLatLng);
                    circleOptions.radius(10);
                    circleOptions.visible(false);
                    mMap.addCircle(circleOptions);

                    if(mapsController.isReachedDestination(circleOptions, current_marker.getPosition())){
                        Intent trackingService = new Intent(_context, TrackingService.class);
                        _context.stopService(trackingService);
                        Toast.makeText(_context, "You have reached to the destination", Toast.LENGTH_SHORT).show();
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
}
