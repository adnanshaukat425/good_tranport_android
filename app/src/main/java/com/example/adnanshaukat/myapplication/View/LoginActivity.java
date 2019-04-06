package com.example.adnanshaukat.myapplication.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.GlobalClasses.MyApplication;
import com.example.adnanshaukat.myapplication.GlobalClasses.ProgressDialogManager;
import com.example.adnanshaukat.myapplication.Modals.SQLiteDBUsersHandler;
import com.example.adnanshaukat.myapplication.Modals.SignalrNotificationManager;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.ILogin;
import com.example.adnanshaukat.myapplication.View.Customer.MainActivityCustomer;
import com.example.adnanshaukat.myapplication.View.Driver.MainActivityDriver;
import com.example.adnanshaukat.myapplication.View.Transporter.MainActivityTransporter;

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

    LocationManager locationManager;
    private long backPressedTime;
    private Toast backToast;
    String latitude, longitude;

    SQLiteDBUsersHandler sqLiteDBUsersHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = ProgressDialogManager.showProgressDialogWithTitle(LoginActivity.this, "Few moments more", "Loading");
        sqLiteDBUsersHandler = new SQLiteDBUsersHandler(this);

        if (!check_if_already_logged_in()) {
            populateLoginActivity();
        }
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
                        Log.e("USER ID FROM LOGIN", user_id + "");
                        Log.e("USER TYPE FROM LOGIN", user_type_id + "");
                        if (user_id != 0) {
//                            if (storeCredentialsToSQLite(user)) {
                                ((MyApplication) LoginActivity.this.getApplication()).setGlobalUser(user);
                            SignalrNotificationManager signalrNotificationManager = new SignalrNotificationManager(getApplicationContext());
                            signalrNotificationManager.connectToSignalR(user.getUser_id());

                                if (!from_system) {
                                    if (storeCredentialsToSQLite(user)){
                                        //Toast.makeText(LoginActivity.this, "Welcome " + user.getFirst_name().toString(), Toast.LENGTH_LONG).show();
                                        Log.e("USER TYPE ID", user_type_id + "");
                                    }
                                }
                                if (user_type_id == 1) {

                                    if (storeCredentialsToSQLite(user)){
                                        //Toast.makeText(LoginActivity.this, "Welcome Customer", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this, MainActivityCustomer.class);
                                        intent.putExtra("user", user);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    }
                                }
                                else if (user_type_id == 3) {
                                    //Toast.makeText(LoginActivity.this, "Welcome Transporter", Toast.LENGTH_SHORT).show();
                                    if (storeCredentialsToSQLite(user)){
                                        Intent intent = new Intent(LoginActivity.this, MainActivityTransporter.class);
                                        intent.putExtra("user", user);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    }
                                }
                                else if (user_type_id == 2) {
                                    //boolean te = storeCredentialsToSQLite(user);
                                    //Log.e("TE", te + "");
                                    Log.e("DRIVEER", "D");
                                    Intent intent = new Intent(LoginActivity.this, MainActivityDriver.class);
                                    intent.putExtra("user", user);
//                                    intent.putExtra("latitude", latitude);
//                                    intent.putExtra("longitude", longitude);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                }else {
                                    Log.e("Username or not correct", "");
                                }
                        } else {
                            populateLoginActivity();
                            message = "Username or Password is not correct";
                        }

                        if (!from_system && !message.isEmpty()) {
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                        ProgressDialogManager.closeProgressDialog(progressDialog);
                    } else {
                        populateLoginActivity();
                        Toast.makeText(LoginActivity.this, "Username or Password is not correct", Toast.LENGTH_SHORT).show();
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

    public boolean check_if_already_logged_in() {
        SQLiteDBUsersHandler sqLiteDBUsersHandler = new SQLiteDBUsersHandler(this);
        User _user = sqLiteDBUsersHandler.get_logged_in_user();
        if (_user.getEmail() != null && _user.getPassword() != null) {
            getLogin(_user.getEmail(), _user.getPassword(), true);
            return true;
        } else {
            ProgressDialogManager.closeProgressDialog(progressDialog);
            return false;
        }

    }

    private void populateLoginActivity(){
        setContentView(R.layout.login);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        populateUI();

        TextView txtForgotPassword = (TextView) findViewById(R.id.tv_login_forgot_password);
        txtForgotPassword.setText(Html.fromHtml(String.format(getString(R.string.forgot_password))));

        TextView txtCreateNewAccount = (TextView) findViewById(R.id.tv_login_create_new_account);
        txtCreateNewAccount.setText(Html.fromHtml(String.format(getString(R.string.create_new_account))));

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Drawable errorIcon = getResources().getDrawable(R.drawable.ic_error);
                errorIcon.setBounds(new Rect(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight()));


                if (TextUtils.isEmpty(et_email.getText())) {
                    //Toast.makeText(LoginActivity.this, "Email required", Toast.LENGTH_SHORT).show();
                    et_email.setError("Email Required !", errorIcon);
                    et_email.requestFocus();
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(et_email.getText()).matches()) {
                    //Toast.makeText(LoginActivity.this, "Email not valid", Toast.LENGTH_SHORT).show();
                    et_email.setError("Email Not Valid !", errorIcon);
                    et_email.requestFocus();
                } else if (TextUtils.isEmpty(et_password.getText())) {
                    //Toast.makeText(LoginActivity.this, "Password required", Toast.LENGTH_SHORT).show();
                    et_password.setError("Password Required !", errorIcon);
                    et_password.requestFocus();
                } else {
                    progressDialog = ProgressDialogManager.showProgressDialogWithTitle(LoginActivity.this, "Loading", "Please wait");
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
                intent.putExtra("from_transporter", false);
                startActivity(intent);
            }
        });
    }
}
