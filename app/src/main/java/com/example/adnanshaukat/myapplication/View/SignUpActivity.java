package com.example.adnanshaukat.myapplication.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
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
import com.example.adnanshaukat.myapplication.Modals.SQLiteDBUsersHandler;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.ISignUp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    Button btn_signup_next;
    ProgressDialog progressDialog;
    SQLiteDBUsersHandler sqLiteDBUsersHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        addItemsOnSpinner();
        populateUI();

        btn_signup_next.setOnClickListener(new View.OnClickListener() {
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
                    String temp = Long.toString(cbo_user_type.getSelectedItemId() + 1);
                    int user_type_id = Integer.parseInt(temp);

                    if(user_type_id == 2){
                        user_type_id = 3;
                    }

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();
                    String current_date = dateFormat.format(date);

                    User user = new User(0, user_type_id, first_name, last_name, email, phone_number, cnic, null, password, 1, current_date);

                    checkIfEmailAlreadyPresent(user);
                }
            }
        });
    }

    public void addItemsOnSpinner() {
        spinner = (Spinner) findViewById(R.id.list_view_user_type);
        List<String> list = new ArrayList<String>();
        list.add("Customer");
        //list.add("Driver");
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
        btn_signup_next = (Button) findViewById(R.id.btn_signup_next);
    }

    private boolean checkValidity() {
        Drawable errorIcon = getResources().getDrawable(R.drawable.ic_error);
        errorIcon.setBounds(new Rect(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight()));
        if (TextUtils.isEmpty(first_name)) {
            et_first_name.setError("First Name Required !", errorIcon);
            et_first_name.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(last_name)) {
            et_last_name.setError("Last Name Required !", errorIcon);
            et_last_name.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(email)) {
            et_email.setError("Email Required !", errorIcon);
            et_email.requestFocus();
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.setError("Email Not Valid !", errorIcon);
            et_email.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(cnic)) {
            et_cnic.setError("CNIC Required !", errorIcon);
            et_cnic.requestFocus();
            return false;
        } else if (!TextUtils.isDigitsOnly(cnic)) {
            et_phone_number.setError("CNIC Not Valid !", errorIcon);
            et_phone_number.requestFocus();
            return false;
        } else if (cnic.length() < 13) {
            et_cnic.setError("CNIC Not Valid !", errorIcon);
            et_cnic.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(password)) {
            et_password.setError("Password Required !", errorIcon);
            et_password.requestFocus();
            return false;
        } else if (!password.equals(confirm_password)) {
            //Toast.makeText(this, "Password missmatch", Toast.LENGTH_SHORT).show();
            et_confirm_password.setError("Password mismatch", errorIcon);
            et_confirm_password.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(phone_number)) {
            et_phone_number.setError("Phone Number Required !", errorIcon);
            et_phone_number.requestFocus();
            return false;
        } else if (!TextUtils.isDigitsOnly(phone_number)) {
            et_phone_number.setError("Phone Number Not Valid !", errorIcon);
            et_phone_number.requestFocus();
            return false;
        } else if (phone_number.length() < 11) {
            et_phone_number.setError("Phone Number Not Valid !", errorIcon);
            et_phone_number.requestFocus();
            return false;
        }
        return true;
    }

    private void checkIfEmailAlreadyPresent(User user){
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

            Call<User> call = api.check_if_email_already_present(user);

            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User _user = response.body();
                    Log.e("Response SignUp", response.toString());
                    int user_id = _user.getUser_id();
                    if (user_id == -2) {
                        Drawable errorIcon = getResources().getDrawable(R.drawable.ic_error);
                        errorIcon.setBounds(new Rect(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight()));
                        Toast.makeText(SignUpActivity.this, "Email Already Exist Please Try Another", Toast.LENGTH_LONG).show();
                        et_email.setError("Email Already Exist", errorIcon);
                    }
                    else if(user_id == -1){
                        Intent intent = new Intent(SignUpActivity.this, CaptureImageActivity.class);
                        intent.putExtra("user", _user);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e("FAILURE", t.getMessage());
                    Log.e("FAILURE", t.toString());
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    //ProgressDialogManager.closeProgressDialog(progressDialog);
                }
            });
        } catch (Exception ex) {
            Log.e("ERROR", ex.toString());
            //ProgressDialogManager.closeProgressDialog(progressDialog);
            Toast.makeText(this, "Some error occour, please try again", Toast.LENGTH_SHORT).show();
        }
    }
}
