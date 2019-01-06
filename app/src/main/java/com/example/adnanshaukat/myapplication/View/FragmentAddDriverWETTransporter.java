package com.example.adnanshaukat.myapplication.View;

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
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.example.adnanshaukat.myapplication.GlobalClasses.ProgressDialogManager;
import com.example.adnanshaukat.myapplication.Modals.Cargo;
import com.example.adnanshaukat.myapplication.Modals.SQLiteDBUsersHandler;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.Modals.Vehicle;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IDriver;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.ISignUp;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IUser;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IVehicleWrtDriver;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by AdnanShaukat on 06/01/2019.
 */

public class FragmentAddDriverWETTransporter extends Fragment {

    View view;
    User mUser;

    Spinner spinner;
    CircleImageView profile_image;
    TextView tv_first_name, tv_last_name, tv_email, tv_phone_no, tv_cnic;
    EditText ed_fname, ed_lname, ed_email, ed_number, ed_cnic;
    CheckBox chk_assing_vehicle;
    static final int REQUEST_CAMERA_CAPTURE = 1;
    static final int REQUEST_GALLERY_CAPTURE = 2;

    RelativeLayout rl_assign_vehicle;
    int transporter_id = 0;

    Button btn_add;
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_driver_wrt_transporter, container, false);
        mUser = new User();
        populateUI();
        Bundle argument = getArguments();
        if (argument != null) {
            transporter_id = Integer.parseInt(argument.getString("trasnporter_id"));
            getVehicles(String.valueOf(transporter_id));
        }

        chk_assing_vehicle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        chk_assing_vehicle.setChecked(true);

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

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidity()) {
                    progressDialog = ProgressDialogManager.showProgressDialogWithTitle(getContext(), "Loading...", "Please wait");
                    User _mUser = populateModal();
                    if (_mUser.getProfile_picture() == null){
                        _mUser.setProfile_picture("");
                    }
                    getSignup(_mUser);
                }
            }
        });
    }

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
                    Log.e("RESPONSE", response.toString());
                    int user_id = user.getUser_id();
                    if (user_id != 0) {
                        if(chk_assing_vehicle.isChecked()){
                            Toast.makeText(getContext(), "Added Successfully", Toast.LENGTH_SHORT);
                            UpdateDriversVehicle(user.getUser_id() + "", chk_assing_vehicle + "");
                        }
                        else{
                            ProgressDialogManager.closeProgressDialog(progressDialog);
                            Toast.makeText(getContext(), "Driver Added Successfully", Toast.LENGTH_SHORT).show();
                        }
                        AddDriverToTransporter(user.getUser_id() + "", transporter_id + "");
                    }
                    else {

                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e("FAILURE", t.getMessage());
                    Log.e("FAILURE", t.toString());
                    //Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    ProgressDialogManager.closeProgressDialog(progressDialog);
                }
            });
        } catch (Exception ex) {
            Log.e("ERROR", ex.toString());
            ProgressDialogManager.closeProgressDialog(progressDialog);
            Toast.makeText(getContext(), "Some error occour, please try again", Toast.LENGTH_SHORT).show();
        }
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
                    ProgressDialogManager.closeProgressDialog(progressDialog);
                    Log.e(this.toString(), result + "");
                    Toast.makeText(getContext(), "Driver Added Successfully", Toast.LENGTH_SHORT).show();
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

    private void AddDriverToTransporter(String driver_id, String trasnporter_id){
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

            String json_string = "";

            JSONObject obj = new JSONObject();

            json_string = obj.toString();
            //Toast.makeText(getContext(), "JSON STRING " + json_string + "", Toast.LENGTH_SHORT).show();
            Call<String> call = api.add_driver_to_transporter(driver_id, trasnporter_id);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    String result = response.body();
                    ProgressDialogManager.closeProgressDialog(progressDialog);
                    Log.e("Add driver to transp", result + "");
                    Toast.makeText(getContext(), "Driver Added Successfully", Toast.LENGTH_SHORT).show();
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

    private void populateUI() {
        spinner = (Spinner) view.findViewById(R.id.add_driver_sp_vehicle_assign);
        tv_first_name = (TextView) view.findViewById(R.id.add_driver_ed_fname);
        tv_last_name = (TextView) view.findViewById(R.id.add_driver_ed_lname);
        tv_email = (TextView) view.findViewById(R.id.add_driver_ed_email);
        tv_phone_no = (TextView) view.findViewById(R.id.add_driver_ed_number);
        tv_cnic = (TextView) view.findViewById(R.id.add_driver_ed_cnic);
        profile_image = (CircleImageView) view.findViewById(R.id.add_driver_frag_image_view);

        ed_fname = (EditText)view.findViewById(R.id.add_driver_ed_fname);
        ed_lname = (EditText)view.findViewById(R.id.add_driver_ed_lname);
        ed_email = (EditText)view.findViewById(R.id.add_driver_ed_email);
        ed_number = (EditText)view.findViewById(R.id.add_driver_ed_number);
        ed_cnic = (EditText)view.findViewById(R.id.add_driver_ed_cnic);

        chk_assing_vehicle = (CheckBox)view.findViewById(R.id.chk_assing_vehicle);
        rl_assign_vehicle = (RelativeLayout)view.findViewById(R.id.rl_6);
        btn_add = (Button) view.findViewById(R.id.btn_fragment_add_driver);
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

    private User populateModal(){
        mUser.setFirst_name(ed_fname.getText().toString());
        mUser.setLast_name(ed_lname.getText().toString());
        mUser.setEmail(ed_email.getText().toString());
        mUser.setPhone_number(ed_number.getText().toString());
        mUser.setCnic_number(ed_cnic.getText().toString());
        mUser.setPassword(ed_fname.getText().toString());
        mUser.setUser_type_id(2);
        mUser.setStatus(2);
        Date d = new Date();
        CharSequence s  = DateFormat.format("yyyy-MM-dd", d.getTime());
        Log.e("DateTime", s.toString());
        mUser.setCreated_date(s.toString());
        return mUser;
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
                Log.e("Encoded Image LEngth", String.valueOf(encodedImage.length()));
            }
            catch(Exception ex) {
                Log.e("Encoded Image Exception", ex.toString());
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
                Log.e("Encoded Image LEngth", String.valueOf(encodedImage.length()));
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

            Call<List<Vehicle>> call = api.get_transporter(transporter_id);

            call.enqueue(new Callback<List<Vehicle>>() {
                @Override
                public void onResponse(Call<List<Vehicle>> call, Response<List<Vehicle>> response) {
                    List<Vehicle> vehicles = response.body();
                    //Log.e("VEHICLES NUMBER", Float.toString(vehicles.get(0).getVehicle_number()));
                    if (vehicles != null && vehicles.size() > 0) {
                        ArrayAdapter<Vehicle> vehicle_adapter = new ArrayAdapter<Vehicle>(getContext(), android.R.layout.simple_spinner_item, vehicles);
                        vehicle_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        spinner.setAdapter(vehicle_adapter);
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
}