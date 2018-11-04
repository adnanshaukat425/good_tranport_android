package com.example.adnanshaukat.myapplication;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Query;

public class LoginActivity extends AppCompatActivity {

    Button btn_login;
    EditText et_email;
    EditText et_password;

    String base_url = "http://192.168.0.105:8080/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        TextView txtForgotPassword = (TextView)findViewById(R.id.tv_login_forgot_password);
        txtForgotPassword.setText(Html.fromHtml(String.format(getString(R.string.forgot_password))));

        TextView txtCreateNewAccount = (TextView)findViewById(R.id.tv_login_create_new_account);
        txtCreateNewAccount.setText(Html.fromHtml(String.format(getString(R.string.create_new_account))));

        btn_login = (Button)findViewById(R.id.btn_login);
        et_email = (EditText)findViewById(R.id.ev_login_email);
        et_password = (EditText)findViewById(R.id.ev_login_password);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetLoggedIn();
            }
        });
    }
    public void GetLoggedIn(){
        String email = et_email.getText().toString();

        String password = et_password.getText().toString();
        Log.e("EMAIL", email);
        Log.e("PASSWORD", password);

        Retrofit.Builder builder = new Retrofit.Builder().
                baseUrl(base_url).
                addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        Login login = retrofit.create(Login.class);
        Call<Users> call = login.get_login(email, password);

        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                Object result = response.body();
                Log.e("RESPONSE", result.toString());
                Toast.makeText(LoginActivity.this, result.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Log.e("RESPONSE", t.toString());
            }
        });
    }
}
