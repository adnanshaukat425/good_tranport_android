package com.example.adnanshaukat.myapplication.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.GlobalClasses.EncoderDecoder;
import com.example.adnanshaukat.myapplication.GlobalClasses.MyApplication;
import com.example.adnanshaukat.myapplication.GlobalClasses.ProgressDialogManager;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.ILogin;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IUser;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentUserProfile extends Fragment {

    View view;
    User mUser;

    ImageView profile_image;
    EditText first_name, last_name, email, phone, cnic, password, confirm_password;
    String _password;
    Button btn_change_password;
    ProgressDialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_user_profile, container, false);
        Bundle argument = getArguments();

        if (argument != null) {
            mUser = (User) argument.get("user");
        }
        return view;
    }

    @Override
    public void onResume() {
        PopulateUI();

        String encodedImage = mUser.getProfile_picture();
        Bitmap bitmap = EncoderDecoder.getDecodeImage(encodedImage);
        if(bitmap == null){
            profile_image.setImageResource(R.drawable.default_profile_image);
        }
        else{
            profile_image.setImageBitmap(bitmap);
        }

        first_name.setText(mUser.getFirst_name());
        last_name.setText(mUser.getLast_name());
        email.setText(mUser.getEmail());
        phone.setText(mUser.getPhone_number());
        cnic.setText(mUser.getCnic_number());

        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialogManager.showProgressDialogWithTitle(getContext(), "Loading", "Please wait...");
                _password = password.getText().toString();

                if(checkValidity()){

                    if(_password.equals(confirm_password.getText().toString())){
                        mUser = populateModal();
                        ChangePassword(mUser);
                    }
                    else{
                        confirm_password.setError("Password Mismatched", getResources().getDrawable(R.drawable.error_icon));
                        //Toast.makeText(getContext(), "Password miss-matched", Toast.LENGTH_SHORT).show();
                        ProgressDialogManager.closeProgressDialog(progressDialog);
                    }
                }
                else{
                    ProgressDialogManager.closeProgressDialog(progressDialog);
                }
            }
        });

        super.onResume();
    }

    private void PopulateUI(){
        profile_image = (ImageView)view.findViewById(R.id.user_img);
        first_name = (EditText)view.findViewById(R.id.ed_fname);
        last_name = (EditText)view.findViewById(R.id.ed_lname);
        email = (EditText)view.findViewById(R.id.ed_email);
        phone = (EditText)view.findViewById(R.id.ed_number);
        cnic = (EditText)view.findViewById(R.id.ed_cnic);
        password = (EditText)view.findViewById(R.id.ed_pass);
        confirm_password = (EditText)view.findViewById(R.id.ed_pass_confirm);

        btn_change_password = (Button)view.findViewById(R.id.btn_change_password);
    }

    private void ChangePassword(User updated_user){
        try {
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.connectTimeout(30, TimeUnit.SECONDS);
            client.readTimeout(30, TimeUnit.SECONDS);
            client.writeTimeout(30, TimeUnit.SECONDS);

            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(IUser.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();

            IUser api = retrofit.create(IUser.class);

            Call<String> call = api.update_user(updated_user);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    String is_updated = response.body();
                    if(is_updated != null && is_updated.trim().toLowerCase() == "true"){
                        ProgressDialogManager.closeProgressDialog(progressDialog);
                        Toast.makeText(getContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        ProgressDialogManager.closeProgressDialog(progressDialog);
                        Toast.makeText(getContext(), "Request didn't processed correctly, Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    ProgressDialogManager.closeProgressDialog(progressDialog);
                    Log.e("FAILURE", t.getMessage());
                    Log.e("FAILURE", t.toString());
                }
            });
        }
        catch (Exception ex){
            ProgressDialogManager.closeProgressDialog(progressDialog);
            Log.e("ERROR", ex.toString());
        }
    }

    private boolean checkValidity() {
        if (TextUtils.isEmpty(_password)) {
            password.setError("Password Required", getResources().getDrawable(R.drawable.error_icon));
            password.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(confirm_password.getText().toString())) {
            confirm_password.setError("Confirm Password Required", getResources().getDrawable(R.drawable.error_icon));
            confirm_password.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(cnic.getText().toString())) {
            cnic.setError("CNIC Required", getResources().getDrawable(R.drawable.error_icon));
            cnic.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(phone.getText().toString())) {
            phone.setError("Email required", getResources().getDrawable(R.drawable.error_icon));
            phone.requestFocus();
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            email.setError("Email not valid", getResources().getDrawable(R.drawable.error_icon));
            email.requestFocus();
            return false;
        }
        return true;
    }

    private User populateModal(){
        mUser.setFirst_name(first_name.getText().toString());
        mUser.setLast_name(last_name.getText().toString());
        mUser.setEmail(email.getText().toString());
        mUser.setPhone_number(phone.getText().toString());
        mUser.setCnic_number(cnic.getText().toString());
        mUser.setPassword(password.getText().toString());
        return mUser;
    }
}
