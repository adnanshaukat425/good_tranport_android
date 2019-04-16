package com.example.adnanshaukat.myapplication.View;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.DrawableRes;
import android.support.multidex.MultiDex;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.adnanshaukat.myapplication.Modals.DirectionJSONParser;
import com.example.adnanshaukat.myapplication.R;
import com.google.android.gms.location.places.PlacesOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static GoogleMap mMap;
    ArrayList markerPoints= new ArrayList();

    String latitude;
    String longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        MultiDex.install(this);
        Intent intent = getIntent();
        latitude = intent.getExtras().get("latitude").toString();
        longitude = intent.getExtras().get("longitude").toString();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void updateMap(String latitude, String longitude){
        Log.e("MapsActivity", "UpdateMap: " + latitude + " " + longitude);
        double Lat =  Double.parseDouble(latitude);
        double Lng = Double.parseDouble(longitude);
        try{
            if(mMap != null){
                LatLng Loc = new LatLng(Lat,Lng);
                //mMap.clear();
                mMap.addMarker(new MarkerOptions().position(Loc).title("Driver Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(Loc));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
            }
            else{
                Log.e("MapsActivity", "MAP OBJECT is empty");
            }
        }
        catch (Exception ex) {
            Log.e("MapsActivity", ex.toString());
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("MapsActivity", "creating map object");
        mMap = googleMap;

//        LatLng KIET = new LatLng(24.8617575,67.0712855);
//        mMap.addMarker(new MarkerOptions().position(KIET).title("Marker in PAF-KIET"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(KIET));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
//
////        LatLng sydney = new LatLng(-34, 151);
////        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
////        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17.0f));
//
//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//
//                if (markerPoints.size() > 1) {
//                    markerPoints.clear();
//                    mMap.clear();
//                }
//
//                // Adding new item to the ArrayList
//                markerPoints.add(latLng);
//
//                // Creating MarkerOptions
//                MarkerOptions options = new MarkerOptions();
//
//                // Setting the position of the marker
//                options.position(latLng);
//
//                if (markerPoints.size() == 1) {
//                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//                } else if (markerPoints.size() == 2) {
//                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                }
//
//                // Add new marker to the Google Map Android API V2
//                mMap.addMarker(options);
//
//                // Checks, whether start and end locations are captured
//                if (markerPoints.size() >= 2) {
//                    LatLng origin = (LatLng) markerPoints.get(0);
//                    LatLng dest = (LatLng) markerPoints.get(1);
//
//                    // Getting URL to the Google Directions API
//                    String url = getDirectionsUrl(origin, dest);
//
//                    DownloadTask downloadTask = new DownloadTask();
//
//                    // Start downloading json data from Google Directions API
//                    downloadTask.execute(url);
//                }
//
//            }
//        });
    }

    private class DownloadTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<Object, Object, List<List<HashMap<String, String>>>> {
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(Object... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject((String) jsonData[0]);
                DirectionJSONParser parser = new DirectionJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = new PolylineOptions();
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap point = path.get(j);

                    double lat = Double.parseDouble((String) point.get("lat"));
                    double lng = Double.parseDouble((String) point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }
            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

//    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
//        Drawable background = ContextCompat.getDrawable(context, R.drawable.vehicle_icon);
//        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
//        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
//        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
//        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        background.draw(canvas);
//        vectorDrawable.draw(canvas);
//        return BitmapDescriptorFactory.fromBitmap(bitmap);
//    }
}
