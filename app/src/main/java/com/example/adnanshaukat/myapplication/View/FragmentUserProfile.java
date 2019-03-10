package com.example.adnanshaukat.myapplication.View;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.GlobalClasses.EncoderDecoder;
import com.example.adnanshaukat.myapplication.GlobalClasses.ProgressDialogManager;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IUploadFiles;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IUser;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.RetrofitManager;
import com.example.adnanshaukat.myapplication.View.Customer.MainActivityCustomer;
import com.example.adnanshaukat.myapplication.View.Driver.MainActivityDriver;
import com.example.adnanshaukat.myapplication.View.Transporter.MainActivityTransporter;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentUserProfile extends Fragment {

    View view;
    User mUser;
    String user_type = "";

    ImageView profile_image;
    EditText first_name, last_name, email, phone, cnic, password, confirm_password;
    String _password;
    Button btn_change_password;
    ProgressDialog progressDialog;
    String savedFileDestination;
    Bitmap bitmap_image;
    static final int REQUEST_CAMERA_CAPTURE = 1;
    static final int REQUEST_GALLERY_CAPTURE = 2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_user_profile, container, false);
        Bundle argument = getArguments();
        PopulateUI();

        if (argument != null) {
            mUser = (User) argument.get("user");
        }

        if(mUser.getUser_type_id() == 1){
            final MainActivityCustomer mainActivityCustomer = (MainActivityCustomer)getContext();
            user_type = "customer.png";
            mainActivityCustomer.setTitle("User Profile");
        }
        else if(mUser.getUser_type_id() == 2){
            final MainActivityDriver mainActivityDriver = (MainActivityDriver)getContext();
            user_type = "driver.png";
            mainActivityDriver.setTitle("User Profile");
        }
        else if(mUser.getUser_type_id() == 3){
            final MainActivityTransporter mainActivityTransporter = (MainActivityTransporter)getContext();
            user_type = "transporter.png";
            mainActivityTransporter.setTitle("User Profile");
        }

        String image_path = mUser.getProfile_picture();
        if(TextUtils.isEmpty(image_path)){
            profile_image.setImageResource(R.drawable.default_profile_image);
        }
        else{
            image_path =  "http://" + RetrofitManager.ip + "/" + RetrofitManager.domain + "/Images/AppImages/" + image_path;
            Picasso.with(getContext()).load(image_path).into(profile_image);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] colors = {"Capture Image", "Upload From Gallery"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Chose Image");
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            Toast.makeText(getContext(), "Get From Camera", Toast.LENGTH_SHORT).show();
                            captureImageFromCamera();
                        }
                        else{
                            Toast.makeText(getContext(), "Upload From Gallery", Toast.LENGTH_SHORT).show();
                            getImageFromGallery();
                        }
                    }
                });
                builder.show();
            }
        });

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

                Drawable errorIcon = getResources().getDrawable(R.drawable.ic_error);
                errorIcon.setBounds(new Rect(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight()));

                if(checkValidity()){
                    if(_password.equals(confirm_password.getText().toString())){
                        mUser = populateModal();
                        ChangePassword(mUser);
                    }
                    else{
                        confirm_password.setError("Password Mismatched", errorIcon);
                        //Toast.makeText(getContext(), "Password miss-matched", Toast.LENGTH_SHORT).show();
                        ProgressDialogManager.closeProgressDialog(progressDialog);
                    }
                }
                else{
                    ProgressDialogManager.closeProgressDialog(progressDialog);
                }
            }
        });
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

            if(!TextUtils.isEmpty(savedFileDestination)){
                updated_user.setProfile_picture(savedFileDestination + "_" + user_type);
            }

            Call<User> call = api.update_user(updated_user);

            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User response_object = response.body();
                    if(response_object != null){
                        ProgressDialogManager.closeProgressDialog(progressDialog);
                        Toast.makeText(getContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                        uploadImage();
                        if (response_object.getUser_type_id() == 3){
                            Intent i = new Intent(getContext(), MainActivityTransporter.class);
                            i.putExtra("user", response_object);
                            startActivity(i);
                        }
                        else if (response_object.getUser_type_id() == 2){
                            Intent i = new Intent(getContext(), MainActivityDriver.class);
                            i.putExtra("user", response_object);
                            startActivity(i);
                        }
                        else if(response_object.getUser_type_id() == 1){
                            Intent i = new Intent(getContext(), MainActivityCustomer.class);
                            i.putExtra("user", response_object);
                            startActivity(i);
                        }
                    }
                    else{
                        ProgressDialogManager.closeProgressDialog(progressDialog);
                        Toast.makeText(getContext(), "Request didn't processed correctly, Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
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

        Drawable errorIcon = getResources().getDrawable(R.drawable.ic_error);
        errorIcon.setBounds(new Rect(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight()));

        if (TextUtils.isEmpty(first_name.getText().toString())) {
            first_name.setError("First Name Required !", errorIcon);
            first_name.requestFocus();
            return false;
        }
        else if (TextUtils.isEmpty(last_name.getText().toString())) {
            last_name.setError("Last Name Required !", errorIcon);
            last_name.requestFocus();
            return false;
        }
        if (!TextUtils.isEmpty(_password) && TextUtils.isEmpty(confirm_password.getText().toString())) {
            confirm_password.setError("Confirm Password Required !", errorIcon);
            confirm_password.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(cnic.getText().toString())) {
            cnic.setError("CNIC Required !", errorIcon);
            cnic.requestFocus();
            return false;
        }
        else if (TextUtils.isEmpty(phone.getText().toString())) {
            phone.setError("Email Required !", errorIcon);
            phone.requestFocus();
            return false;
        }
        else if(phone.getText().length() < 11){
            phone.setError("Phone Number Is Not Valid !", errorIcon);
            phone.requestFocus();
            return false;
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            email.setError("Email Not Valid !", errorIcon);
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
        if(!password.getText().toString().isEmpty()){
            mUser.setPassword(password.getText().toString());
        }
        return mUser;
    }

    private void captureImageFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CAMERA_CAPTURE);
        }
    }

    private void getImageFromGallery(){
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Long tsLong = System.currentTimeMillis()/1000;
        if(data != null){
            if (requestCode == REQUEST_CAMERA_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                Bitmap bitmap = Bitmap.createScaledBitmap(imageBitmap, imageBitmap.getWidth(), imageBitmap.getHeight(), true);
                profile_image.setImageBitmap(bitmap);
                savedFileDestination = tsLong.toString();
                bitmap_image = bitmap;
            }
            if (requestCode == REQUEST_GALLERY_CAPTURE && data != null && data.getData() != null) {
                Uri uri = data.getData();
                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                    Bitmap bitmap = Bitmap.createScaledBitmap(imageBitmap, 500, 500, true);
                    profile_image.setImageBitmap(bitmap);
                    savedFileDestination = tsLong.toString();
                    bitmap_image = bitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                catch(Exception ex) {
                    Log.e("Fragment User Profile", ex.toString());
                }
            }
        }
    }

    private void uploadImage() {
        if(bitmap_image != null){
            try{

                File file = new File(getContext().getCacheDir(), savedFileDestination + "_" + user_type);
                file.createNewFile();

                Bitmap bitmap = bitmap_image;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();

                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file.getName(), requestFile);

                OkHttpClient.Builder client = new OkHttpClient.Builder();
                client.connectTimeout(30, TimeUnit.SECONDS);
                client.readTimeout(30, TimeUnit.SECONDS);
                client.writeTimeout(30, TimeUnit.SECONDS);

                retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                        .baseUrl(IUploadFiles.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client.build())
                        .build();

                IUploadFiles api = retrofit.create(IUploadFiles.class);

                Call<Void> call = api.upload(body);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.e("RESPONSE FROM API", response.toString());
                        //Toast.makeText(getContext(), "Image upload successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("FAILURE", t.getMessage());
                        Log.e("FAILURE", t.toString());
                    }
                });
            }
            catch (Exception ex){
                Log.e(getContext().toString(), ex.getMessage());
                Log.e(getContext().toString(), "Exception in Upload");
            }
        }
    }
}
