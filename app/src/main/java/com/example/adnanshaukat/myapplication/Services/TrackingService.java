package com.example.adnanshaukat.myapplication.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.Modals.SignalrTrackingManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class TrackingService extends Service {

    private static final String TAG = TrackingService.class.getSimpleName();

    int order_detail_id;
    protected BroadcastReceiver stopReceiver;
    SignalrTrackingManager signalrTrackingManager;
    private LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationClient;

    public TrackingService() {
        setBroadCastReceiver();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Bundle extras = intent.getExtras();
        if(extras == null) {
            //Toast.makeText(this, "Service null", Toast.LENGTH_SHORT).show();
            Log.d("Service", "null");
        }
        else
        {
            //Toast.makeText(this, "Service not null", Toast.LENGTH_SHORT).show();
            Log.d("Service","not null");
            this.order_detail_id = Integer.parseInt(extras.get("order_detail_id").toString());
            Log.d("ORrder Detail ID",order_detail_id + "");
            this.signalrTrackingManager = SignalrTrackingManager.SignalrTrackingManager();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //buildNotification();
        //Toast.makeText(getApplicationContext(), "Service Created", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Service Created");

        setLocationCallback();

        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        //Toast.makeText(getApplicationContext(), "requestLocationUpdates Method called", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "requestLocationUpdates Method called");
        LocationRequest request = new LocationRequest();
        request.setInterval(5000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

//        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                Toast.makeText(TrackingService.this, "last location update received", Toast.LENGTH_SHORT).show();
//                Log.e(TAG, "last location update received");
//            }
//        });

        int permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            // Request location updates and when an update is
            //Toast.makeText(this, "Starting Tracking in intervals", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Starting Tracking in intervals");
            fusedLocationClient.requestLocationUpdates(request, locationCallback, null);
        }
        else{
            Toast.makeText(getApplicationContext(), "No permission to track location", Toast.LENGTH_SHORT).show();;
            Log.e(TAG, "No permission to track location");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback);
        Log.e(TAG, "Tracking Service Destroyed");
    }

    private void setLocationCallback(){
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                //Location location = locationResult.getLastLocation();
                if (locationResult != null) {
                    //Toast.makeText(getApplicationContext(), "location update ", Toast.LENGTH_SHORT).show();
                    for (Location location : locationResult.getLocations()) {
                        Log.d(TAG, "location update " + location);
                        Log.d(TAG, "Order Detial Id" + order_detail_id);
                        signalrTrackingManager.insertLocation(location.getLatitude() + "", location.getLongitude() + "", order_detail_id);
                    }
                }
                else{
                    //Toast.makeText(getApplicationContext(), "Location Is Null", Toast.LENGTH_SHORT).show();;
                    Log.d(TAG, "Location Is Null");
                }
            }
        };
    }

    private void setBroadCastReceiver() {
        stopReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Toast.makeText(context, "received stop broadcast", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "received stop broadcast");
                // Stop the service when the notification is tapped
                unregisterReceiver(stopReceiver);
                //stopSelf();
            }
        };
    }
}
