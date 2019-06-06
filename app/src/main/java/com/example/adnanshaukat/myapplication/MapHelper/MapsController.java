package com.example.adnanshaukat.myapplication.MapHelper;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.DrawableRes;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.Services.TrackingService;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by AdnanShaukat on 25/05/2019.
 */

public class MapsController {

    Context context;
    String api_key;

    public MapsController(Context context, String api_key){
        this.context = context;
        this.api_key = api_key;
    }

    public LatLng getLatLongFromLocation(String location) {

        List<LatLng> ll = new ArrayList<>(); // A list to save the coordinates if they are available

        for (int i = 0; i < 5; i++){
            if(Geocoder.isPresent()){
                try {
                    Geocoder gc = new Geocoder(context);
                    List<Address> addresses= gc.getFromLocationName(location, 1); // get the found Address Objects

                    for(Address a : addresses){
                        if(a.hasLatitude() && a.hasLongitude()){
                            ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                        }
                    }
                } catch (IOException e) {
                    // handle the exception
                }
            }
            if (ll.size() != 0){
                break;
            }
        }

        if (ll.size() != 0) {
            return ll.get(0);
        }
        return null;
    }

    public String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + api_key;
        return url;
    }

    public String getUrl(String origin, String dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin;
        // Destination of route
        String str_dest = "destination=" + dest;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + api_key;
        return url;
    }

    public String getUrl(LatLng origin, String dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + api_key;
        return url;
    }

    public BitmapDescriptor getBitmapDescriptor(Context context, @DrawableRes int id) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(context.getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(1, 1, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public boolean isReachedDestination(CircleOptions circle, LatLng marker){
        float[] distance = new float[2];
        Location.distanceBetween(marker.latitude, marker.longitude, circle.getCenter().latitude, circle.getCenter().longitude, distance);

        Log.e("HIHIO", distance[0] + "," + distance[1]);

        if (distance[0] < circle.getRadius()){
            Log.e("LESS", distance[0] +  ", " + circle.getRadius());
            return true;
        }

        if (distance[0] > circle.getRadius()){
            Log.e("GREATER", distance[0] +  ", " + circle.getRadius());
            return false;
        }

        return false;
    }
}
