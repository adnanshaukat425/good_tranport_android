package com.example.adnanshaukat.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.GlobalClasses.ProgressDialogManager;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.ISignUp;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpActivity extends AppCompatActivity {

    private Spinner spinner;
    private EditText et_first_name, et_last_name, et_email, et_password, et_confirm_password, et_phone_number, et_cnic;
    private String first_name, last_name, email, password, confirm_password, phone_number, cnic;
    Spinner cbo_user_type;
    Button btn_signup;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        addItemsOnSpinner();
        populateUI();

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                first_name = et_first_name.getText().toString();
                last_name = et_last_name.getText().toString();
                email = et_email.getText().toString();
                password = et_password.getText().toString();
                confirm_password = et_confirm_password.getText().toString();
                phone_number = et_phone_number.getText().toString();
                cnic = et_cnic.getText().toString();

                if (checkValidity()) {

                    progressDialog = ProgressDialogManager.showProgressDialogWithTitle(SignUpActivity.this, "Please wait", "Please wait");
                    String temp = Long.toString(cbo_user_type.getSelectedItemId() + 1);
                    int user_type_id = Integer.parseInt(temp);

                    User user = new User(0, user_type_id, first_name, last_name, email, phone_number, cnic, "profile_picture_path", password);
                    getSignup(user);

                }
            }
        });
    }

    public void addItemsOnSpinner() {
        spinner = (Spinner) findViewById(R.id.list_view_user_type);
        List<String> list = new ArrayList<String>();
        list.add("Customer");
        list.add("Driver");
        list.add("Transporter");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    private void populateUI() {
        et_first_name = (EditText) findViewById(R.id.txt_signup_first_name);
        et_last_name = (EditText) findViewById(R.id.txt_signup_last_name);
        et_email = (EditText) findViewById(R.id.txt_signup_email);
        et_password = (EditText) findViewById(R.id.txt_signup_password);
        et_confirm_password = (EditText) findViewById(R.id.txt_signup_confirm_password);
        et_phone_number = (EditText) findViewById(R.id.txt_signup_phone_number);
        et_cnic = (EditText) findViewById(R.id.txt_signup_cnic);
        cbo_user_type = (Spinner) findViewById(R.id.list_view_user_type);
        btn_signup = (Button) findViewById(R.id.btn_signup);
    }

    private boolean checkValidity() {
        if (!password.equals(confirm_password)) {
            //Toast.makeText(this, "Password missmatch", Toast.LENGTH_SHORT).show();
            et_confirm_password.setError("Password mismatch", getResources().getDrawable(R.drawable.error_icon));
            et_confirm_password.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(first_name)) {
            et_first_name.setError("First name required", getResources().getDrawable(R.drawable.error_icon));
            et_first_name.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(last_name)) {
            et_last_name.setError("Last name required", getResources().getDrawable(R.drawable.error_icon));
            et_last_name.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(email)) {
            et_email.setError("Email required", getResources().getDrawable(R.drawable.error_icon));
            et_email.requestFocus();
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.setError("Email not valid", getResources().getDrawable(R.drawable.error_icon));
            et_email.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(cnic)) {
            et_cnic.setError("CNIC required", getResources().getDrawable(R.drawable.error_icon));
            et_cnic.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(password)) {
            et_password.setError("Password required", getResources().getDrawable(R.drawable.error_icon));
            et_password.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(phone_number)) {
            et_phone_number.setError("Phone number required", getResources().getDrawable(R.drawable.error_icon));
            et_phone_number.requestFocus();
            return false;
        } else if (!TextUtils.isDigitsOnly(phone_number)) {
            et_phone_number.setError("Phone number not valid", getResources().getDrawable(R.drawable.error_icon));
            et_phone_number.requestFocus();
            return false;
        }
        return true;
    }
    //
    private void getSignup(User user) {
        try {
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.connectTimeout(30, TimeUnit.SECONDS);
            client.readTimeout(30, TimeUnit.SECONDS);
            client.writeTimeout(30, TimeUnit.SECONDS);

            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(ISignUp.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();

            ISignUp api = retrofit.create(ISignUp.class);

            Call<User> call = api.get_signup(user);

            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User user = response.body();
                    int user_id = user.getUser_id();
                    if (user_id != 0) {
                        Toast.makeText(SignUpActivity.this, "Welcome " + user.getFirst_name().toString(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                    else {
                        Toast.makeText(SignUpActivity.this, "Username or password is not correct", Toast.LENGTH_SHORT).show();
                    }
                    ProgressDialogManager.closeProgressDialog(progressDialog);
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
            ProgressDialogManager.closeProgressDialog(progressDialog);
            Toast.makeText(this, "Some error occour, please try again", Toast.LENGTH_SHORT).show();
        }
    }

}
