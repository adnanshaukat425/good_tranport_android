package com.example.adnanshaukat.myapplication.View;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.GlobalClasses.MyApplication;
import com.example.adnanshaukat.myapplication.Modals.SQLiteDBUsersHandler;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.R;

/**
 * Created by AdnanShaukat on 04/12/2018.
 */

public class MainActivityTransporter extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transporter_main);
        this.setTitle("Dashboard");

        Intent i = getIntent();
        user = (User)i.getSerializableExtra("user");

        Toolbar toolbar = (Toolbar) findViewById(R.id.transporter_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.transporter_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.transporter_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View view = navigationView.inflateHeaderView(R.layout.nav_header_main);
        ImageView profile_image =  (ImageView)view.findViewById(R.id.drawer_profile_image);
        TextView transporter_name = (TextView)view.findViewById(R.id.drawer_t_name);
        TextView transporter_email = (TextView)view.findViewById(R.id.drawer_t_email);

        transporter_name.setText(user.getFirst_name() + " " + user.getLast_name());
        transporter_email.setText(user.getEmail());
        Menu menu = navigationView.getMenu();

        MenuItem txt_id = (MenuItem) menu.findItem(R.id.nav_t_id);
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

        txt_id.setTitle("Transporter ID: " + user_id);

        String encodedImage = user.getProfile_picture();

        if (encodedImage.isEmpty()) {
            profile_image.setImageResource(R.drawable.default_profile_image_2);
        } else {
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profile_image.setImageBitmap(decodedByte);
        }

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                FragmentUserProfile fragment = new FragmentUserProfile();
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().
                        setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out).
                        replace(R.id.main_content_frame_transporter_container, fragment).
                        addToBackStack(null).
                        commit();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.transporter_drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.main_content_frame_transporter_container, new FragmentMainTransporter()).commit();
    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.transporter_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int fragments = getSupportFragmentManager().getBackStackEntryCount();
            Log.e("Fragment Count",Integer.toString(fragments));
            if (fragments == 0) {
                this.setTitle("Dashboard");
                getSupportFragmentManager().beginTransaction().replace(R.id.main_content_frame_transporter_container, new FragmentMainTransporter()).commit();
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);

        int fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {
            for (int i = 0; i < fragments; i++){
                getSupportFragmentManager().popBackStackImmediate();
            }
        }

        Log.e("Fragments Count",String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));

        if(id == R.id.nav_t_view_drivers){
            FragmentListOfDriverWRTTransporter fragment = new FragmentListOfDriverWRTTransporter();
            fragment.setArguments(bundle);
            this.setTitle("Drivers");
            getSupportFragmentManager().beginTransaction().
                    setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right).
                    replace(R.id.main_content_frame_transporter_container, fragment).
                    addToBackStack(null).
                    commit();
        }

        if (id == R.id.nav_t_view_vehicles){
            FragmentListOfVehicleWRTTransporter fragment = new FragmentListOfVehicleWRTTransporter();
            fragment.setArguments(bundle);
            this.setTitle("Vehicles");
            getSupportFragmentManager().beginTransaction().
                    setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right).
                    replace(R.id.main_content_frame_transporter_container, fragment).
                    addToBackStack(null).
                    commit();
        }

        if(id == R.id.nav_t_logout){
            if(logout()){
                Toast.makeText(this, "Logged out Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivityTransporter.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }else{
                Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show();
            }
        }

        if (id == R.id.nav_t_add_driver){
            bundle.putString("trasnporter_id",String.valueOf(user.getUser_id()));
            FragmentAddDriverWETTransporter fragmentAddDriverWETTransporter = new FragmentAddDriverWETTransporter();
            fragmentAddDriverWETTransporter.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().
                    setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out).
                    replace(R.id.main_content_frame_transporter_container, fragmentAddDriverWETTransporter).
                    addToBackStack(null).
                    commit();
        }

        if (id == R.id.nav_t_dashboard){
            FragmentMainTransporter fragment = new FragmentMainTransporter();
            fragment.setArguments(bundle);
            this.setTitle("Dashboard");
            getSupportFragmentManager().beginTransaction().
                    setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out).
                    replace(R.id.main_content_frame_transporter_container, fragment).
                    addToBackStack(null).
                    commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.transporter_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean logout(){
        SQLiteDBUsersHandler sqLiteDBUsersHandler = new SQLiteDBUsersHandler(MainActivityTransporter.this);
        int user_id = ((MyApplication) this.getApplication()).get_user_id();
        User user = new User();
        user.setUser_id(user_id);
        return sqLiteDBUsersHandler.update_logged_in_status(0, user);
    }
}
