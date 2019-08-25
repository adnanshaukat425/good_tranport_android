package com.example.adnanshaukat.myapplication.View.Driver;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.adnanshaukat.myapplication.GlobalClasses.LocationController;
import com.example.adnanshaukat.myapplication.GlobalClasses.MyApplication;
import com.example.adnanshaukat.myapplication.Modals.Notification;
import com.example.adnanshaukat.myapplication.Modals.SQLiteDBUsersHandler;
import com.example.adnanshaukat.myapplication.Modals.SignalrNotificationManager;
import com.example.adnanshaukat.myapplication.Modals.SignalrTrackingManager;
import com.example.adnanshaukat.myapplication.Modals.Status;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IDriver;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.INotification;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.RetrofitManager;
import com.example.adnanshaukat.myapplication.Services.SaveLastLocationService;
import com.example.adnanshaukat.myapplication.Services.TrackingService;
import com.example.adnanshaukat.myapplication.View.Common.AboutActivity;
import com.example.adnanshaukat.myapplication.View.Common.FragmentUserProfile;
import com.example.adnanshaukat.myapplication.View.Common.LoginActivity;
import com.example.adnanshaukat.myapplication.View.Common.MapsActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by AdnanShaukat on 05/01/2019.
 */

public class MainActivityDriver extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    User user;
    String latitude;
    String longitude;
    String CHANNEL_ID = "111";
    ToggleButton switch_tracking;
    private static final int PERMISSIONS_REQUEST = 1;
    private static final int REQUEST_LOCATION = 1;

    SignalrTrackingManager signalrTrackingManager;
    ArrayList<Integer> notification_ids = new ArrayList<>();

    private long backPressedTime;
    private Toast backToast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);
        this.setTitle("Dashboard");

        Intent i = getIntent();
        user = (User)i.getSerializableExtra("user");
        Object fragment_from_notification = i.getExtras().get("fragment_from_notification");

        Toolbar toolbar = findViewById(R.id.driver_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.driver_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.driver_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View view = navigationView.inflateHeaderView(R.layout.nav_header_main);
        ImageView profile_image = view.findViewById(R.id.drawer_profile_image);

        TextView driver_name = view.findViewById(R.id.drawer_name);
        TextView driver_email = view.findViewById(R.id.drawer_email);

        driver_name.setText(user.getFirst_name() + " " + user.getLast_name());
        driver_email.setText(user.getEmail());

        Menu menu = navigationView.getMenu();

        MenuItem txt_id = menu.findItem(R.id.nav_d_id);
        String user_id = user.getUser_id() + "";

        if(user_id.length() == 1){
            user_id = "0000" + user_id;
        }
        if(user_id.length() == 2){
            user_id = "000" + user_id;
        }
        if(user_id.length() == 3){
            user_id = "00" + user_id;
        }
        if(user_id.length() == 4){
            user_id = "0" + user_id;
        }

        txt_id.setTitle("Driver ID: " + user_id);

        String image_path = user.getProfile_picture();
        if(TextUtils.isEmpty(image_path)){
            profile_image.setImageResource(R.drawable.default_profile_image_2);
        }
        else{
            image_path =  "http://" + RetrofitManager.ip + "/" + RetrofitManager.domain + "/Images/AppImages/" + image_path;
            Picasso.with(this).load(image_path).into(profile_image);
        }
        Log.e("Driver Latitude", latitude + "");
        Log.e("Driver Longitude", longitude + "");

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                FragmentUserProfile fragment = new FragmentUserProfile();
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().
                        setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out).
                        replace(R.id.main_content_frame_driver_container, fragment).
                        addToBackStack(null).
                        commit();
                DrawerLayout drawer = findViewById(R.id.driver_drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.main_content_frame_driver_container, new FragmentMainDriver()).commit();

        if(fragment_from_notification != null) {
            if (fragment_from_notification.toString().trim().toLowerCase().equals("fragment_order_list_for_driver")) {
                FragmentOrdersListForDriver fragment = new FragmentOrdersListForDriver();
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                fragment.setArguments(bundle);
                this.setTitle("Order List");
                getSupportFragmentManager().beginTransaction().
                        setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out).
                        replace(R.id.main_content_frame_driver_container, fragment).
                        addToBackStack(null).
                        commit();
            }
        }
        getNotification();

        signalrTrackingManager = SignalrTrackingManager.SignalrTrackingManager();
        signalrTrackingManager.setContext(getApplicationContext());
        signalrTrackingManager.connectToSignalR(user.getUser_id(), 1);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        else{
            startLocationUpdateService();
            //buildAlertMessageNoGps();
        }
    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.driver_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int fragments = getSupportFragmentManager().getBackStackEntryCount();
            Log.e("Fragment Count",Integer.toString(fragments));
            if (fragments == 0){
                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    backToast.cancel();
                    super.onBackPressed();
                    finishAffinity();
                    System.exit(0);
                    return;
                } else {
                    backToast = Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT);
                    backToast.show();
                }
                backPressedTime = System.currentTimeMillis();
            }
            if (fragments == 1) {
                this.setTitle("Dashboard");
                getSupportFragmentManager().popBackStackImmediate();
                getSupportFragmentManager().beginTransaction().replace(R.id.main_content_frame_driver_container, new FragmentMainDriver()).commit();
            } else {
                if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                    getSupportFragmentManager().popBackStackImmediate();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);

        if(id == R.id.nav_d_order_request){
            FragmentRequestedOrderList fragment = new FragmentRequestedOrderList();
            fragment.setArguments(bundle);

            this.setTitle("Order Requests");

            getSupportFragmentManager().beginTransaction().
                    setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out).
                    replace(R.id.main_content_frame_driver_container, fragment).
                    addToBackStack(null).
                    commit();
        }

        if(id == R.id.nav_d_view_all_orders){
            FragmentOrdersListForDriver fragment = new FragmentOrdersListForDriver();
            fragment.setArguments(bundle);

            this.setTitle("Orders");
            getSupportFragmentManager().beginTransaction().
                    setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out).
                    replace(R.id.main_content_frame_driver_container, fragment).
                    addToBackStack(null).
                    commit();
        }

        if(id == R.id.nav_d_about){
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }

        if(id == R.id.nav_d_logout){
            if(logout()){
                stopLocationUpdateService();
                Intent tracking_intent = new Intent(this, TrackingService.class);
                stopService(tracking_intent);
                SignalrTrackingManager.SignalrTrackingManager().disconnect();
                Toast.makeText(this, "Logged out Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivityDriver.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }else{
                Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show();
            }
        }

        if (id == R.id.nav_d_dashboard){
//            FragmentMainCustomer fragment = new FragmentMainCustomer();
//            fragment.setArguments(bundle);
//            this.setTitle("Dashboard");
//            getSupportFragmentManager().beginTransaction().
//                    setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out).
//                    replace(R.id.main_content_frame_driver_container, fragment).
//                    addToBackStack(null).
//                    commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.driver_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean logout(){
        SQLiteDBUsersHandler sqLiteDBUsersHandler = new SQLiteDBUsersHandler(MainActivityDriver.this);
        int user_id = ((MyApplication) this.getApplication()).get_user_id();
        User user = new User();
        user.setUser_id(user_id);
        return sqLiteDBUsersHandler.update_logged_in_status(0, user);
    }

    protected void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                        buildAlertMessageNoGps();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.switchId);
        item.setActionView(R.layout.switch_layout);
        switch_tracking = item.getActionView().findViewById(R.id.switchAB);

        get_status(user.getUser_id());

        switch_tracking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Status status = new Status();
                if (isChecked) {
                    Toast.makeText(MainActivityDriver.this, "You're now Active", Toast.LENGTH_SHORT).show();
                    status.update_status(1, user.getUser_id());
                    startLocationUpdateService();
                } else {
                    Intent intent = new Intent(getApplicationContext(), TrackingService.class);
                    Toast.makeText(MainActivityDriver.this, "You're now Inactive, You will not receive order requests now.", Toast.LENGTH_SHORT).show();
                    stopService(intent);
                    //Toast.makeText(MainActivityDriver.this, "Tracking Stopped", Toast.LENGTH_SHORT).show();
                    status.update_status(7, user.getUser_id());
                    stopLocationUpdateService();
                }
            }
        });
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            int permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (permission == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdateService();
            }
            else{
                buildAlertMessageNoGps();
            }
         } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            }
            else{
                startLocationUpdateService();
            }
        }
    }

    private void startLocationUpdateService() {
        getTransporterId(user.getUser_id());
    }

    private void stopLocationUpdateService() {
        Intent serviceIntent = new Intent(this, SaveLastLocationService.class);
        stopService(serviceIntent);
    }

    public void getNotification(){
        try {
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.connectTimeout(30, TimeUnit.SECONDS);
            client.readTimeout(30, TimeUnit.SECONDS);
            client.writeTimeout(30, TimeUnit.SECONDS);

            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(INotification.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();

            INotification api = retrofit.create(INotification.class);

            Call<List<Notification>> call = api.get_broadcast_notification(user.getUser_id());

            call.enqueue(new Callback<List<Notification>>() {
                @Override
                public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                    Log.e("RESPONSE BODY", response.message());
                    Log.e("RESPONSE BODY", response + "");
                    List<Notification> notification = response.body();
                    if(notification != null && notification.size() > 0){
                        for (int i = 0; i < notification.size(); i++) {
                            Intent intent = new Intent(getApplicationContext(), MainActivityDriver.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("user", user);
                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                    .setSmallIcon(R.drawable.vehicle_icon)
                                    .setContentTitle("Hi " + user.getFirst_name())
                                    .setContentText(notification.get(i).getNotification_message())
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true);

                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                            //notificationId is a unique int for each notification that you must define
                            notificationManager.notify(notification.get(i).getNotification_id(), builder.build());
                            notification_ids.add(notification.get(i).getNotification_id());
                        }

                        setNotificationToPushed(notification_ids);
                    }
                }

                @Override
                public void onFailure(Call<List<Notification>> call, Throwable t) {
                    Log.e("FAILURE", t.getMessage());
                    Log.e("FAILURE", t.toString());
                    //Toast.makeText(mContext, "Failure: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    //ProgressDialogManager.closeProgressDialog(progressDialog);
                }
            });
        } catch (Exception ex) {
            Log.e("ERROR", ex.toString());
            //ProgressDialogManager.closeProgressDialog(progressDialog);
            //Toast.makeText(mContext, "Some error occour, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    public void setNotificationToPushed(final ArrayList<Integer> notification_id){
        new Thread(new Runnable() {
            public void run() {
                Log.e("MainDriver", "From SetNotificationToPushed" + notification_id.size());
                Notification notification = new Notification();
                notification.ChangeNotificationToPushed(notification_ids, getApplicationContext());
            }
        }).start();
    }

    public void get_status(int driver_id) {
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

            Call<Object> call = api.get_driver_status(driver_id);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    Log.e("Driver Status", response.message());
                    Log.e("Driver Status", response + "");
                    String body = response.body().toString();
                    try {
                        Log.e("DRIVER STATUS", body);
                        JSONObject obj = new JSONObject(body);
                        int status = Integer.parseInt(obj.get("data").toString());
                        if(status == 1){
                            switch_tracking.setChecked(true);
                        }
                        else{
                            switch_tracking.setChecked(false);
                        }
                        MyApplication myApplication = new MyApplication();
                        myApplication.setIs_active(status);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.e("FAILURE D", t.getMessage());
                    Log.e("FAILURE D", t.toString());
                }
            });
        } catch (Exception ex) {
            Log.e("ERROR D", ex.toString());
        }
    }

    private void getTransporterId(int driver_id){
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

            Call<Object> call = api.get_driver_transporter_id(driver_id);

            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    try {
                        Log.e("RESPONSE BODY", response.message());
                        Log.e("RESPONSE BODY", response + "");

                        JSONObject response_obj = new JSONObject(response.body().toString());
                        if((boolean)response_obj.get("success")) {
                            int transporter_id = Integer.parseInt(response_obj.get("data").toString());
                            Intent serviceIntent = new Intent(MainActivityDriver.this, SaveLastLocationService.class);
                            serviceIntent.putExtra("driver_id", user.getUser_id());
                            serviceIntent.putExtra("driver_name", user.getFirst_name() + " " + user.getLast_name());
                            serviceIntent.putExtra("transporter_id", transporter_id);
                            startService(serviceIntent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.e("FAILURE", t.getMessage());
                    Log.e("FAILURE", t.toString());
                }
            });
        }
        catch(Exception ex){
            Log.e("ERROR", ex.getMessage());
            Log.e("ERROR", ex.toString());
        }
    }
}
