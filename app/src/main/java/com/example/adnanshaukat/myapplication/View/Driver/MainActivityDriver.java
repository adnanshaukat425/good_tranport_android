package com.example.adnanshaukat.myapplication.View.Driver;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.GlobalClasses.LocationController;
import com.example.adnanshaukat.myapplication.GlobalClasses.MyApplication;
import com.example.adnanshaukat.myapplication.Modals.SQLiteDBUsersHandler;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.RetrofitManager;
import com.example.adnanshaukat.myapplication.View.LoginActivity;
import com.example.adnanshaukat.myapplication.View.MapsActivity;
import com.squareup.picasso.Picasso;

/**
 * Created by AdnanShaukat on 05/01/2019.
 */

public class MainActivityDriver extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    User user;
    String latitude;
    String longitude;

    private static final int REQUEST_LOCATION = 1;

    LocationManager locationManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);
        this.setTitle("Dashboard");

        Intent i = getIntent();
        user = (User)i.getSerializableExtra("user");
//        latitude = i.getExtras().get("latitude").toString();
//        longitude = i.getExtras().get("longitude").toString();


        Toolbar toolbar = (Toolbar) findViewById(R.id.driver_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.driver_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.driver_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View view = navigationView.inflateHeaderView(R.layout.nav_header_main);
        ImageView profile_image =  (ImageView)view.findViewById(R.id.drawer_profile_image);

        TextView driver_name = (TextView)view.findViewById(R.id.drawer_name);
        TextView driver_email = (TextView)view.findViewById(R.id.drawer_email);

        driver_name.setText(user.getFirst_name() + " " + user.getLast_name());
        driver_email.setText(user.getEmail());

        Menu menu = navigationView.getMenu();

        MenuItem txt_id = (MenuItem) menu.findItem(R.id.nav_d_id);
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

        getSupportFragmentManager().beginTransaction().replace(R.id.main_content_frame_driver_container, new FragmentMainDriver()).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.driver_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int fragments = getSupportFragmentManager().getBackStackEntryCount();
            Log.e("Fragment Count",Integer.toString(fragments));
            if (fragments == 0) {
                this.setTitle("Dashboard");
                //finish();
            } else {
                if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);

        if(id == R.id.nav_d_view_all_orders){

        }
        if(id == R.id.nav_d_view_current_location){

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();
            } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                getLocation();
                LocationController controller = new LocationController();
                controller.update_lat_long(getApplicationContext(), user.getUser_id(), latitude, longitude);

                Intent intent = new Intent(MainActivityDriver.this, MapsActivity.class);
                intent.putExtra("latitude", latitude + "");
                intent.putExtra("longitude", longitude + "");
                startActivity(intent);
            }
        }
        if(id == R.id.nav_d_logout){
            if(logout()){
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
        private boolean getLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivityDriver.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (MainActivityDriver.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivityDriver.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            return false;
        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                latitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

            } else if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();
                latitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

            } else if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                latitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

            } else {
                Toast.makeText(MainActivityDriver.this,"Unable to Trace your location",Toast.LENGTH_SHORT).show();
                buildAlertMessageNoGps();
                return false;
            }
            Log.e("Driver Latitude", latitude + "");
            Log.e("Driver Longitude", longitude + "");
            return true;
        }
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
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}