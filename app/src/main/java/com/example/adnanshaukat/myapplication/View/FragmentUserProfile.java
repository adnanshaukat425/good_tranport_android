package com.example.adnanshaukat.myapplication.View;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    static final int REQUEST_CAMERA_CAPTURE = 1;
    static final int REQUEST_GALLERY_CAPTURE = 2;

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
        super.onResume();
        PopulateUI();

        String encodedImage = mUser.getProfile_picture();
        Bitmap bitmap = EncoderDecoder.getDecodeImage(encodedImage);
        if(bitmap == null){
            profile_image.setImageResource(R.drawable.default_profile_image);
        }
        else{
            profile_image.setImageBitmap(bitmap);
        }

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

            Call<User> call = api.update_user(updated_user);

            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User response_object = response.body();
                    if(response_object != null){
                        ProgressDialogManager.closeProgressDialog(progressDialog);
                        Toast.makeText(getContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
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
        if (requestCode == REQUEST_CAMERA_CAPTURE) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Bitmap bitmap = Bitmap.createScaledBitmap(imageBitmap, 500, 500, true);
            profile_image.setImageBitmap(bitmap);
            String encodedImage = "";
            try{
                encodedImage = encodeImage(bitmap);
                mUser.setProfile_picture(encodedImage);
                Log.e("Encoded Image", encodedImage);
            }
            catch(Exception ex) {
                Log.e("Encoded Image", ex.toString());
            }

        }
        if (requestCode == REQUEST_GALLERY_CAPTURE && data != null && data.getData() != null) {
            Uri uri = data.getData();
            String encodedImage = "";
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                Bitmap bitmap = Bitmap.createScaledBitmap(imageBitmap, 500, 500, true);
                profile_image.setImageBitmap(bitmap);
                encodedImage = encodeImage(bitmap);
                mUser.setProfile_picture(encodedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch(Exception ex) {
                Log.e("Fragment User Profile", ex.toString());
            }
        }
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        Log.e("Encoded Image", encImage);
        return encImage;
    }
}
