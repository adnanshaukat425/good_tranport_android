package com.example.adnanshaukat.myapplication;

import android.content.Intent;
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

import com.example.adnanshaukat.myapplication.GlobalClasses.ProgressDialogManager;
import com.example.adnanshaukat.myapplication.Modals.User;
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

    //String base_url = "http://192.168.0.105:8080/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        populateUI();

        TextView txtForgotPassword = (TextView)findViewById(R.id.tv_login_forgot_password);
        txtForgotPassword.setText(Html.fromHtml(String.format(getString(R.string.forgot_password))));

        TextView txtCreateNewAccount = (TextView)findViewById(R.id.tv_login_create_new_account);
        txtCreateNewAccount.setText(Html.fromHtml(String.format(getString(R.string.create_new_account))));

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( TextUtils.isEmpty(et_email.getText())){
                    //Toast.makeText(LoginActivity.this, "Email required", Toast.LENGTH_SHORT).show();
                    et_email.setError("Email required", getResources().getDrawable(R.drawable.error_icon));
                    et_email.requestFocus();
                }
                else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(et_email.getText()).matches()){
                    //Toast.makeText(LoginActivity.this, "Email not valid", Toast.LENGTH_SHORT).show();
                    et_email.setError("Email not valid", getResources().getDrawable(R.drawable.error_icon));
                    et_email.requestFocus();
                }
                else if(TextUtils.isEmpty(et_password.getText())){
                    //Toast.makeText(LoginActivity.this, "Password required", Toast.LENGTH_SHORT).show();
                    et_password.setError("Password required", getResources().getDrawable(R.drawable.error_icon));
                    et_password.requestFocus();
                }
                else{
                    ProgressDialogManager.showProgressDialogWithTitle(LoginActivity.this, "", "Please wait");
                    getLogin();
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
    }

//    public void GetLoggedIn(){
//        String email = et_email.getText().toString();
//
//        String password = et_password.getText().toString();
//        Log.e("EMAIL", email);
//        Log.e("PASSWORD", password);
//
//        Retrofit.Builder builder = new Retrofit.Builder().
//                baseUrl(base_url).
//                addConverterFactory(GsonConverterFactory.create());
//
//        Retrofit retrofit = builder.build();
//
//        ILogin login = retrofit.create(ILogin.class);
//        Call<User> call = login.get_login(email, password);
//
//        call.enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
//                Object result = response.body();
//                Log.e("RESPONSE", result.toString());
//                Toast.makeText(LoginActivity.this, result.toString(), Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onFailure(Call<User> call, Throwable t) {
//                Log.e("RESPONSE", t.toString());
//            }
//        });
//    }

    private void populateUI(){
        btn_login = (Button)findViewById(R.id.btn_login);
        et_email = (EditText)findViewById(R.id.ev_login_email);
        et_password = (EditText)findViewById(R.id.ev_login_password);
        tv_already_have_account = (TextView)findViewById(R.id.tv_login_create_new_account);
    }

    private void getLogin() {
        try {
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.connectTimeout(15, TimeUnit.SECONDS);
            client.readTimeout(15, TimeUnit.SECONDS);
            client.writeTimeout(15, TimeUnit.SECONDS);

            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(ILogin.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();

            ILogin api = retrofit.create(ILogin.class);

            Call<User> call = api.get_login(et_email.getText().toString(), et_password.getText().toString());

            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User user = response.body();
                    int user_id = user.getUser_id();
                    if(user_id != 0){
                        Toast.makeText(LoginActivity.this, "Welcome " + user.getFirst_name().toString(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Username or password is not correct", Toast.LENGTH_SHORT).show();
                    }
                    ProgressDialogManager.closeProgressDialog();
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e("FAILURE", t.getMessage());
                    Log.e("FAILURE", t.toString());
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    ProgressDialogManager.closeProgressDialog();
                }
            });
        }
        catch (Exception ex){
            Log.e("ERROR", ex.toString());
        }
    }
}
