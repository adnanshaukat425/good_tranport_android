package com.example.adnanshaukat.myapplication.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.GlobalClasses.LocationController;
import com.example.adnanshaukat.myapplication.Modals.SignalrTrackingManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

/**
 * Created by AdnanShaukat on 07/06/2019.
 */

public class SaveLastLocationService extends Service {

    private static final String TAG = SaveLastLocationService.class.getSimpleName();

    protected BroadcastReceiver stopReceiver;
    private LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationClient;

    int driver_id;
    String driver_name;
    int transporter_id;

    public SaveLastLocationService() {
        setBroadCastReceiver();
    }


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Bundle extras = intent.getExtras();
        if(extras == null) {
            Toast.makeText(this, "Service null", Toast.LENGTH_SHORT).show();
            Log.e("Locatin Update Service", "null");
        }
        else
        {
            Toast.makeText(this, "Service not null", Toast.LENGTH_SHORT).show();
            Log.e("Locatin Update Service","not null");
            this.driver_id = Integer.parseInt(extras.get("driver_id").toString());
            this.driver_name = extras.get("driver_name").toString();
            this.transporter_id = Integer.parseInt(extras.get("transporter_id").toString());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "Location Update Service Created");
        Log.e("Location Update Service","not null");
        setLocationCallback();
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        Log.e(TAG, "requestLocationUpdates User Method called");
        LocationRequest request = new LocationRequest();
        request.setInterval(5000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        int permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            // Request location updates and when an update is
            //Toast.makeText(this, "Starting Tracking in intervals", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Starting SERVICE Last Location in intervals");
            fusedLocationClient.requestLocationUpdates(request, locationCallback, null);
        }
        else{
            Toast.makeText(getApplicationContext(), "No permission to track location", Toast.LENGTH_SHORT).show();;
            Log.e(TAG, "No Service Last Location permission to track location");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback);
        Log.e(TAG, "Location Update Service Destroyed");
    }

    private void setLocationCallback(){
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    //Toast.makeText(getApplicationContext(), "location update ", Toast.LENGTH_SHORT).show();
                    for (Location location : locationResult.getLocations()) {
                        Log.e(TAG, "location update " + location);
                        Log.e(TAG, "driver_id" + driver_id);
                        LocationController locationController = new LocationController();
                        locationController.update_lat_long(getBaseContext(), driver_id, Double.toString(location.getLatitude()),
                                Double.toString(location.getLongitude()), driver_name, transporter_id);
                    }
                }
                else{
                    Log.e(TAG, "Location Is Null");
                }
            }
        };
    }

    private void setBroadCastReceiver() {
        stopReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Toast.makeText(context, "received stop broadcast", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "received stop broadcast");
                // Stop the service when the notification is tapped
                unregisterReceiver(stopReceiver);
                //stopSelf();
            }
        };
    }
}
