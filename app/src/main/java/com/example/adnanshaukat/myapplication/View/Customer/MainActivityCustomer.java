package com.example.adnanshaukat.myapplication.View.Customer;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.GlobalClasses.MyApplication;
import com.example.adnanshaukat.myapplication.Modals.Notification;
import com.example.adnanshaukat.myapplication.Modals.SQLiteDBUsersHandler;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.INotification;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.RetrofitManager;
import com.example.adnanshaukat.myapplication.View.Common.AboutActivity;
import com.example.adnanshaukat.myapplication.View.Common.FragmentUserProfile;
import com.example.adnanshaukat.myapplication.View.Common.LoginActivity;
import com.example.adnanshaukat.myapplication.View.Driver.MainActivityDriver;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivityCustomer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    User user;
    private long backPressedTime;
    private Toast backToast;
    String CHANNEL_ID = "111";
    ArrayList<Integer> notification_ids = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        Intent i = getIntent();
        user = (User)i.getSerializableExtra("user");

        Toolbar toolbar = (Toolbar) findViewById(R.id.customer_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.customer_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.customer_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View view = navigationView.inflateHeaderView(R.layout.nav_header_main);
        ImageView profile_image =  (ImageView)view.findViewById(R.id.drawer_profile_image);

        TextView customer_name = (TextView)view.findViewById(R.id.drawer_name);
        TextView customer_email = (TextView)view.findViewById(R.id.drawer_email);

        customer_name.setText(user.getFirst_name() + " " + user.getLast_name());
        customer_email.setText(user.getEmail());

        String image_path = user.getProfile_picture();
        if(TextUtils.isEmpty(image_path)){
            profile_image.setImageResource(R.drawable.default_profile_image_2);
        }
        else{
            image_path =  "http://" + RetrofitManager.ip + "/" + RetrofitManager.domain + "/Images/AppImages/" + image_path;
            Picasso.with(this).load(image_path).into(profile_image);
        }

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewProfile();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.customer_drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        Menu menu = navigationView.getMenu();

        MenuItem txt_id = (MenuItem) menu.findItem(R.id.nav_c_id);
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
        txt_id.setTitle("Customer ID: " + user_id);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_content_frame_customer_container, new FragmentMainCustomer()).commit();
        getNotification();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.customer_drawer_layout);
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
                getSupportFragmentManager().beginTransaction().replace(R.id.main_content_frame_customer_container, new FragmentMainCustomer()).commit();
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        int fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {
            for (int i = 0; i < fragments; i++){
                getSupportFragmentManager().popBackStackImmediate();
            }
        }
        Log.e("Fragments Count",String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));

        if(id == R.id.nav_c_dashboard){
            FragmentMainCustomer fragment = new FragmentMainCustomer();
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);
            this.setTitle("Dashboard");
            getSupportFragmentManager().beginTransaction().
                    setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out).
                    replace(R.id.main_content_frame_customer_container, fragment).
                    addToBackStack(null).
                    commit();
        }
        if (id == R.id.nav_c_place_order) {
            this.setTitle("Place Order");

            FragmentCreateOrderStep1 fragment = new FragmentCreateOrderStep1();
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);

            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().
                    setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right).
                    replace(R.id.main_content_frame_customer_container, fragment).
                    addToBackStack(null).
                    commit();
        }
        else if (id == R.id.nav_c_view_profile) {
            this.setTitle("User Profile");
            viewProfile();
        }
        else if(id == R.id.nav_c_view_order_details){
            FragmentOrderListForCustomer fragment = new FragmentOrderListForCustomer();
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);
            this.setTitle("Order List");
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().
                    setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out).
                    replace(R.id.main_content_frame_customer_container, fragment).
                    addToBackStack(null).
                    commit();
        }

        if(id == R.id.nav_c_about){
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }

        else if(id == R.id.nav_c_logout){
            if(logout()){
                Toast.makeText(this, "Logged out Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivityCustomer.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }else{
                Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.customer_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean logout(){
        SQLiteDBUsersHandler sqLiteDBUsersHandler = new SQLiteDBUsersHandler(MainActivityCustomer.this);
        int user_id = ((MyApplication) this.getApplication()).get_user_id();
        User user = new User();
        user.setUser_id(user_id);
        return sqLiteDBUsersHandler.update_logged_in_status(0, user);
    }

    private void viewProfile(){
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        FragmentUserProfile fragment = new FragmentUserProfile();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().
                setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out).
                replace(R.id.main_content_frame_customer_container, fragment).
                addToBackStack(null).
                commit();
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
                    Log.e("CRESPONSE BODY", response.message());
                    Log.e("CRESPONSE BODY", response + "");
                    List<Notification> notification = response.body();
                    if(notification != null && notification.size() > 0){
                        for (int i = 0; i < notification.size(); i++) {
                            Intent intent = new Intent(getApplicationContext(), MainActivityCustomer.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("user", user);
                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_order)
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
}
