package com.example.adnanshaukat.myapplication.View.Common;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.GlobalClasses.ProgressDialogManager;
import com.example.adnanshaukat.myapplication.Modals.SQLiteDBUsersHandler;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.ISignUp;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IUploadFiles;
import com.example.adnanshaukat.myapplication.View.Customer.MainActivityCustomer;
import com.example.adnanshaukat.myapplication.View.Transporter.MainActivityTransporter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.security.AccessController.getContext;

public class CaptureImageActivity extends AppCompatActivity {

    Button btn_caputre_image, btn_gallery_image, btn_signup;
    TextView tvSkip;
    private String savedFileDestination;
    Uri imageUri;
    ImageView profile_image;
    static final int REQUEST_CAMERA_CAPTURE = 1;
    static final int REQUEST_GALLERY_CAPTURE = 2;
    Bitmap bitmap_image;
    String mCurrentPhotoPath;
    ProgressDialog progressDialog;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_image);
        populateUI();

        Intent i = getIntent();
        user = (User)i.getSerializableExtra("user");

        btn_caputre_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureIageFromCamera();
            }
        });

        btn_gallery_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromGallery();
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialogManager.showProgressDialogWithTitle(CaptureImageActivity.this, "", "Please wait");
                getSignup(user);
            }
        });

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialogManager.showProgressDialogWithTitle(CaptureImageActivity.this, "", "Please wait");
                getSignup(user);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void populateUI(){
        btn_caputre_image = (Button)findViewById(R.id.btn_capture_image);
        btn_gallery_image = (Button)findViewById(R.id.btn_gallery_image);
        btn_signup = (Button)findViewById(R.id.btn_signup);
        profile_image = (ImageView)findViewById(R.id.profile_image);
        tvSkip = (TextView)findViewById(R.id.tvSkip);
    }

    private void captureIageFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Long tsLong = System.currentTimeMillis()/1000;
        if(data != null){
            if (requestCode == REQUEST_CAMERA_CAPTURE) {
                Bundle extras = data.getExtras();
                imageUri = data.getData();
                Log.e("IMageURI", imageUri.toString());
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                Bitmap bitmap = Bitmap.createScaledBitmap(imageBitmap, 500, 500, true);
                profile_image.setImageBitmap(bitmap);
                bitmap_image = bitmap;
                savedFileDestination = tsLong.toString();
            }
            if (requestCode == REQUEST_GALLERY_CAPTURE && data != null && data.getData() != null) {
                imageUri = data.getData();
                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    Bitmap bitmap = Bitmap.createScaledBitmap(imageBitmap, 500, 500, true);
                    profile_image.setImageBitmap(bitmap);
                    bitmap_image = bitmap;
                    savedFileDestination = tsLong.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getSignup(User user) {
        try {
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.connectTimeout(60, TimeUnit.SECONDS);
            client.readTimeout(60, TimeUnit.SECONDS);
            client.writeTimeout(60, TimeUnit.SECONDS);

            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(ISignUp.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();

            ISignUp api = retrofit.create(ISignUp.class);

            if(!TextUtils.isEmpty(savedFileDestination)){
                user.setProfile_picture(savedFileDestination+"_user.png");
            }

            Call<User> call = api.get_signup(user);

            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User user = response.body();
                    int user_id = user.getUser_id();
                    if (user_id != 0) {
                        Toast.makeText(CaptureImageActivity.this, "Welcome " + user.getFirst_name().toString(), Toast.LENGTH_LONG).show();
                        uploadImage();
                        SQLiteDBUsersHandler sqLiteDBUsersHandler = new SQLiteDBUsersHandler(CaptureImageActivity.this);
                        sqLiteDBUsersHandler.storeCredentialsToSQLite(user);
                        Intent intent = null;

                        if(user.getUser_type_id() == 1){
                            intent = new Intent(CaptureImageActivity.this, MainActivityCustomer.class);
                        }
                        else if(user.getUser_type_id() == 3){
                            intent = new Intent(CaptureImageActivity.this, MainActivityTransporter.class);
                        }
                        intent.putExtra("user", user);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                    else {
                        Toast.makeText(CaptureImageActivity.this, "Username or password is not correct", Toast.LENGTH_SHORT).show();
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

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    private void uploadImage() {
        if (imageUri == null) {
            //Toast.makeText(this, "Please take photo first", Toast.LENGTH_LONG).show();
            return;
        }
        try{
            File file = new File(this.getCacheDir(), savedFileDestination + "_user.png");
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
