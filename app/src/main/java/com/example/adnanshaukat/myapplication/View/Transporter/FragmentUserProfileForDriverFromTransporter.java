package com.example.adnanshaukat.myapplication.View.Transporter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.GlobalClasses.EncoderDecoder;
import com.example.adnanshaukat.myapplication.GlobalClasses.ProgressDialogManager;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.Modals.Vehicle;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IDriver;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IUploadFiles;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IUser;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IVehicleWrtDriver;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.RetrofitManager;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by AdnanShaukat on 09/12/2018.
 */

public class FragmentUserProfileForDriverFromTransporter extends Fragment {

    ImageView profile_image;
    String vehicle_id;
    String vehicle_number;
    Uri imageUri;
    Bitmap bitmap_image;
    String savedFileDestination;

    View view;
    User mUser;
    MaterialSpinner spinner;
    TextView tv_first_name, tv_last_name, tv_email, tv_phone_no, tv_cnic;
    EditText ed_fname, ed_lname, ed_email, ed_number, ed_cnic;
    CheckBox chk_assing_vehicle_profile;
    RelativeLayout rl_assign_vehicle;

    static final int REQUEST_CAMERA_CAPTURE = 1;
    static final int REQUEST_GALLERY_CAPTURE = 2;

    Button btn_update;

    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_profile_for_driver_from_transporter, container, false);
        populateUI();
        chk_assing_vehicle_profile.setChecked(true);

        Bundle arguments = getArguments();

        if (arguments != null) {
            mUser = (User) arguments.get("user_from_driver_list");
            String transporter_id = arguments.get("transporter_id").toString();
            getVehicles(transporter_id);

            tv_first_name.setText(mUser.getFirst_name());
            tv_last_name.setText(mUser.getLast_name());
            tv_email.setText(mUser.getEmail());
            tv_phone_no.setText(mUser.getPhone_number());
            tv_cnic.setText(mUser.getCnic_number());

            String image_path = mUser.getProfile_picture();

            if(TextUtils.isEmpty(image_path)){
                profile_image.setImageResource(R.drawable.default_profile_image);
            }
            else{
                image_path =  "http://" + RetrofitManager.ip + "/" + RetrofitManager.domain + "/Images/AppImages/" + image_path;
                Picasso.with(getContext()).load(image_path).into(profile_image);
            }

        } else {
            //Toast.makeText(getContext(), "ARGUMENT IS EMPTY", Toast.LENGTH_SHORT).show();
        }

        //getVehicleFromSpinner();
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = 0;
                if(chk_assing_vehicle_profile.isChecked()){
                    id = ((Vehicle)spinner.getItems().get(spinner.getSelectedIndex())).getVehicle_id();
                }
                //Toast.makeText(getContext(), id + "", Toast.LENGTH_SHORT).show();
                if(checkValidity()){
                    progressDialog = ProgressDialogManager.showProgressDialogWithTitle(getContext(), "Loading", "Please wait...");
                    UpdateDriversVehicle(mUser.getUser_id() + "", id + "");
                    UpdateUser(populateModal());
                }
            }
        });

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

        chk_assing_vehicle_profile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showHideUI(isChecked);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void populateUI() {
        spinner = (MaterialSpinner) view.findViewById(R.id.sp_vehicle_assign);
        tv_first_name = (TextView) view.findViewById(R.id.ed_fname);
        tv_last_name = (TextView) view.findViewById(R.id.ed_lname);
        tv_email = (TextView) view.findViewById(R.id.ed_email);
        tv_phone_no = (TextView) view.findViewById(R.id.ed_number);
        tv_cnic = (TextView) view.findViewById(R.id.ed_cnic);
        profile_image = (ImageView)view.findViewById(R.id.driver_frag_image_view);

        ed_fname = (EditText)view.findViewById(R.id.ed_fname);
        ed_lname = (EditText)view.findViewById(R.id.ed_lname);
        ed_email = (EditText)view.findViewById(R.id.ed_email);
        ed_number = (EditText)view.findViewById(R.id.ed_number);
        ed_cnic = (EditText)view.findViewById(R.id.ed_cnic);

        rl_assign_vehicle = (RelativeLayout)view.findViewById(R.id.rl_6);
        chk_assing_vehicle_profile = (CheckBox)view.findViewById(R.id.chk_assing_vehicle_profile);

        btn_update = (Button) view.findViewById(R.id.btn_fragment_user_profile_update);
    }

    private List<User> getVehicles(String transporter_id) {
        final List<User> result_list = new ArrayList<>();
        try {
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.connectTimeout(30, TimeUnit.SECONDS);
            client.readTimeout(30, TimeUnit.SECONDS);
            client.writeTimeout(30, TimeUnit.SECONDS);

            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(IVehicleWrtDriver.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();

            IVehicleWrtDriver api = retrofit.create(IVehicleWrtDriver.class);

            Log.e("Transporter Id", transporter_id);

            Call<List<Vehicle>> call = api.get_vehicle_wrt_transporter_with_driver(transporter_id, mUser.getUser_id() + "");

            call.enqueue(new Callback<List<Vehicle>>() {
                @Override
                public void onResponse(Call<List<Vehicle>> call, Response<List<Vehicle>> response) {
                    List<Vehicle> vehicles = response.body();
                    //Log.e("VEHICLES NUMBER", Float.toString(vehicles.get(0).getVehicle_number()));
                    if (vehicles != null && vehicles.size() > 0) {
                        vehicles.add(0, new Vehicle(-1, "Select Vehicle", -1, "Select Vehicle", -1, "", -1));
                        int selected_index = 0;
                        for (int i = 1; i < vehicles.size(); i++) {
                            if(vehicles.get(i).getDriver_id() == mUser.getUser_id()){
                                selected_index = i;
                            }
                        }
                        setDropdown(selected_index, vehicles);
                    } else {
                        Toast.makeText(getContext(), "No Vehicles found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Vehicle>> call, Throwable t) {
                    Log.e("FAILURE", t.getMessage());
                    Log.e("FAILURE", t.toString());
                    //Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception ex) {
            Log.e("ERROR", ex.toString());
        }
        return result_list;
    }

    private void setDropdown(int selected_index, List<Vehicle> vehicles) {

        ArrayAdapter<Vehicle> adapter = new ArrayAdapter<Vehicle>(getContext(), android.R.layout.simple_spinner_item, vehicles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setSelectedIndex(selected_index);
    }

    private void UpdateDriversVehicle(String user_id, String vehicle_id){
        try {
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.connectTimeout(30, TimeUnit.SECONDS);
            client.readTimeout(30, TimeUnit.SECONDS);
            client.writeTimeout(30, TimeUnit.SECONDS);

            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(IDriver.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();

            IDriver api = retrofit.create(IDriver.class);

            String json_string = "";

            JSONObject obj = new JSONObject();
            obj.put("user_id", user_id);
            obj.put("vehicle_id", vehicle_id);

            json_string = obj.toString();

            //Toast.makeText(getContext(), "JSON STRING " + json_string + "", Toast.LENGTH_SHORT).show();

            Call<String> call = api.update_drivers_vehicle(json_string);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    String result = response.body();
                    Toast.makeText(getContext(), result + "", Toast.LENGTH_SHORT).show();
                    ProgressDialogManager.closeProgressDialog(progressDialog);
                    //Toast.makeText(getContext(), "Update Successfully", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("FAILURE", t.getMessage());
                    Log.e("FAILURE", t.toString());
                    ProgressDialogManager.closeProgressDialog(progressDialog);
                    //Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception ex) {
            ProgressDialogManager.closeProgressDialog(progressDialog);
            Log.e("ERROR", ex.toString());
        }
    }

    private void UpdateUser(User updated_user) {
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
                updated_user.setProfile_picture(savedFileDestination + "_driver.png");
            }

            Call<User> call = api.update_user(updated_user);

            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User is_updated = response.body();
                    if (is_updated != null) {
                        ProgressDialogManager.closeProgressDialog(progressDialog);
                        Toast.makeText(getContext(), "Update Successfully", Toast.LENGTH_SHORT).show();
                        uploadImage();
                    } else {
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
        } catch (Exception ex) {
            ProgressDialogManager.closeProgressDialog(progressDialog);
            Log.e("ERROR", ex.toString());
        }
    }

    private User populateModal(){
        mUser.setFirst_name(ed_fname.getText().toString());
        mUser.setLast_name(ed_lname.getText().toString());
        mUser.setEmail(ed_email.getText().toString());
        mUser.setPhone_number(ed_number.getText().toString());
        mUser.setCnic_number(ed_cnic.getText().toString());
        return mUser;
    }

    private boolean checkValidity() {

        Drawable errorIcon = getResources().getDrawable(R.drawable.ic_error);
        errorIcon.setBounds(new Rect(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight()));
        if (TextUtils.isEmpty(ed_fname.getText().toString())) {
            ed_fname.setError("First Name Required !", errorIcon);
            ed_fname.requestFocus();
            return false;
        }
        else if (TextUtils.isEmpty(ed_lname.getText().toString())) {
            ed_lname.setError("Last Name Required !", errorIcon);
            ed_lname.requestFocus();
            return false;
        }
        else if (TextUtils.isEmpty(ed_email.getText().toString())) {
            ed_number.setError("Email Required !", errorIcon);
            ed_number.requestFocus();
            return false;
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(ed_email.getText().toString()).matches()) {
            ed_email.setError("Email Not Valid !", errorIcon);
            ed_email.requestFocus();
            return false;
        }
        else if(TextUtils.isEmpty(ed_number.getText().toString())){
            ed_number.setError("Phone Number Required !", errorIcon);
            ed_number.requestFocus();
            return false;
        }
        else if(ed_number.getText().length() < 11){
            ed_number.setError("Phone Number Is Not Valid !", errorIcon);
            ed_number.requestFocus();
            return false;
        }
        else if (TextUtils.isEmpty(ed_cnic.getText().toString())) {
            ed_cnic.setError("CNIC Required !", errorIcon);
            ed_cnic.requestFocus();
            return false;
        }
        else if (ed_cnic.getText().length() < 13) {
            ed_cnic.setError("CNIC Is Not Valid !", errorIcon);
            ed_cnic.requestFocus();
            return false;
        }
        if(spinner.getSelectedIndex() == 0){
            spinner.requestFocus();
            Toast.makeText(getContext(), "Vehicle Can't Be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
        if(data  != null){
            if (requestCode == REQUEST_CAMERA_CAPTURE) {
                Bundle extras = data.getExtras();
                imageUri = data.getData();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                Bitmap bitmap = Bitmap.createScaledBitmap(imageBitmap, imageBitmap.getWidth(), imageBitmap.getHeight(), true);
                profile_image.setImageBitmap(bitmap);
                savedFileDestination = tsLong.toString();
                bitmap_image = bitmap;
            }
            if (requestCode == REQUEST_GALLERY_CAPTURE && data != null && data.getData() != null) {
                imageUri = data.getData();
                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                    Bitmap bitmap = Bitmap.createScaledBitmap(imageBitmap, imageBitmap.getWidth(), imageBitmap.getHeight(), true);
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

    private void showHideUI(boolean assign_vehicle) {
        if (!assign_vehicle) {
            if (rl_assign_vehicle.getVisibility() == View.VISIBLE) {
                rl_assign_vehicle.setAlpha(1.0f);
                // Start the animation
                rl_assign_vehicle.animate()
                        .translationY(rl_assign_vehicle.getHeight())
                        .alpha(0.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                rl_assign_vehicle.setVisibility(View.GONE);
                            }
                        });
            }
        }
        else{
            if (rl_assign_vehicle.getVisibility() == View.GONE) {
                rl_assign_vehicle.setAlpha(1.0f);
                rl_assign_vehicle.setVisibility(View.VISIBLE);
                // Start the animation
                rl_assign_vehicle.animate()
                        .translationY(0)
                        .alpha(1.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                rl_assign_vehicle.setVisibility(View.VISIBLE);
                            }
                        });
            }
        }
    }

    private void uploadImage() {
        if(bitmap_image != null){
            try{
                File file = new File(getContext().getCacheDir(), savedFileDestination + "_driver.png");
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
