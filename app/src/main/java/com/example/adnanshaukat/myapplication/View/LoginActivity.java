package com.example.adnanshaukat.myapplication.View;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.GlobalClasses.MyApplication;
import com.example.adnanshaukat.myapplication.GlobalClasses.ProgressDialogManager;
import com.example.adnanshaukat.myapplication.Modals.SQLiteDBUsersHandler;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.ILogin;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    Button btn_login;
    EditText et_email;
    EditText et_password;
    TextView tv_already_have_account;
    ProgressDialog progressDialog;
    TextView tv_login_forgot_password;

    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    private long backPressedTime;
    private Toast backToast;
    String latitude, longitude;

    SQLiteDBUsersHandler sqLiteDBUsersHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        sqLiteDBUsersHandler = new SQLiteDBUsersHandler(this);

        progressDialog = ProgressDialogManager.showProgressDialogWithTitle(LoginActivity.this, "Few moments more", "Loading");
        check_if_already_logged_in();
        populateUI();

        TextView txtForgotPassword = (TextView) findViewById(R.id.tv_login_forgot_password);
        txtForgotPassword.setText(Html.fromHtml(String.format(getString(R.string.forgot_password))));

        TextView txtCreateNewAccount = (TextView) findViewById(R.id.tv_login_create_new_account);
        txtCreateNewAccount.setText(Html.fromHtml(String.format(getString(R.string.create_new_account))));


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(et_email.getText())) {
                    //Toast.makeText(LoginActivity.this, "Email required", Toast.LENGTH_SHORT).show();
                    et_email.setError("Email required", getResources().getDrawable(R.drawable.error_icon));
                    et_email.requestFocus();
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(et_email.getText()).matches()) {
                    //Toast.makeText(LoginActivity.this, "Email not valid", Toast.LENGTH_SHORT).show();
                    et_email.setError("Email not valid", getResources().getDrawable(R.drawable.error_icon));
                    et_email.requestFocus();
                } else if (TextUtils.isEmpty(et_password.getText())) {
                    //Toast.makeText(LoginActivity.this, "Password required", Toast.LENGTH_SHORT).show();
                    et_password.setError("Password required", getResources().getDrawable(R.drawable.error_icon));
                    et_password.requestFocus();
                } else {
                    progressDialog = ProgressDialogManager.showProgressDialogWithTitle(LoginActivity.this, "", "Please wait");
                    getLogin(et_email.getText().toString(), et_password.getText().toString(), false);
                }
            }
        });

        tv_already_have_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        tv_login_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, FragmentUserProfile.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void populateUI() {
        btn_login = (Button) findViewById(R.id.btn_login);
        et_email = (EditText) findViewById(R.id.ev_login_email);
        et_password = (EditText) findViewById(R.id.ev_login_password);
        tv_already_have_account = (TextView) findViewById(R.id.tv_login_create_new_account);
        tv_login_forgot_password = (TextView) findViewById(R.id.tv_login_forgot_password);
    }

    private void getLogin(String email, String password, final boolean from_system) {
        try {
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.connectTimeout(30, TimeUnit.SECONDS);
            client.readTimeout(30, TimeUnit.SECONDS);
            client.writeTimeout(30, TimeUnit.SECONDS);

            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(ILogin.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();

            ILogin api = retrofit.create(ILogin.class);

            Call<User> call = api.get_login(email, password);

            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User user = response.body();
                    if (user != null) {
                        int user_id = user.getUser_id();
                        int user_type_id = user.getUser_type_id();
                        String message = "";
                        if (user_id != 0) {
//                            if (storeCredentialsToSQLite(user)) {
                                ((MyApplication) LoginActivity.this.getApplication()).set_user_id(user_id);
                                if (!from_system && storeCredentialsToSQLite(user)) {
                                    Toast.makeText(LoginActivity.this, "Welcome " + user.getFirst_name().toString(), Toast.LENGTH_LONG).show();
                                    Log.e("USER TYPE ID", user_type_id + "");
                                }
                                if (user_type_id == 1 && storeCredentialsToSQLite(user)) {
                                    Toast.makeText(LoginActivity.this, "Welcome Customer", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivityCustomer.class);
                                    intent.putExtra("user", user);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                }
                                else if (user_type_id == 3 && storeCredentialsToSQLite(user)) {
                                    Toast.makeText(LoginActivity.this, "Welcome Transporter", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivityTransporter.class);
                                    intent.putExtra("user", user);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                }
                                else if (user_type_id == 2 && storeCredentialsToSQLite(user)) {
                                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                        buildAlertMessageNoGps();
                                    } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                        if(storeCredentialsToSQLite(user) && getLocation()){
                                            Toast.makeText(LoginActivity.this, "Welcome Driver", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(LoginActivity.this, MainActivityDriver.class);
                                            intent.putExtra("user", user);
                                            intent.putExtra("latitude", latitude);
                                            intent.putExtra("longitude", longitude);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        }
                                    }
                                }
//                            } else {
//                                message = "Username or password is not correct";
//                            }
                        } else {
                            message = "Username or password is not correct";
                        }

                        if (!from_system && !message.isEmpty()) {
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                        ProgressDialogManager.closeProgressDialog(progressDialog);
                    } else {
                        Toast.makeText(LoginActivity.this, "Username or password is not correct", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e("FAILURE", t.getMessage());
                    Log.e("FAILURE", t.toString());
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    ProgressDialogManager.closeProgressDialog(progressDialog);
                }
            });
        } catch (Exception ex) {
            Log.e("ERROR", ex.toString());
        }
    }

    private boolean storeCredentialsToSQLite(User user) {
        return sqLiteDBUsersHandler.update_logged_in_status(1, user);
    }

    public void check_if_already_logged_in() {
        SQLiteDBUsersHandler sqLiteDBUsersHandler = new SQLiteDBUsersHandler(this);
        User _user = sqLiteDBUsersHandler.get_logged_in_user();
        if (_user.getEmail() != null && _user.getPassword() != null) {
            getLogin(_user.getEmail(), _user.getPassword(), true);
        } else {
            ProgressDialogManager.closeProgressDialog(progressDialog);
        }
    }

    private boolean getLocation() {
        if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
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
                Toast.makeText(LoginActivity.this,"Unable to Trace your location",Toast.LENGTH_SHORT).show();
                buildAlertMessageNoGps();
                return false;
            }
            Log.e("Driver Latitude", latitude);
            Log.e("Driver Longitude", longitude);
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
