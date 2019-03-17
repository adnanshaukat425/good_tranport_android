package com.example.adnanshaukat.myapplication.View.Transporter;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.Adapters.FragmentTabAdapter;
import com.example.adnanshaukat.myapplication.Adapters.VehiclesRecyclerViewAdapter;
import com.example.adnanshaukat.myapplication.GlobalClasses.ProgressDialogManager;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.Modals.Vehicle;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IVehicleWrtDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by AdnanShaukat on 09/12/2018.
 */

public class FragmentListOfVehicleWRTTransporter extends Fragment {

    private User mUser = new User();
    private ProgressDialog progressDialog;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_of_vehicle_wrt_transporter, container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.fragment_list_of_vehicle_wrt_trans_viewpager);
        setupViewPager(viewPager);

        Bundle bundle = new Bundle();
        Bundle argument = getArguments();
        mUser = (User)argument.getSerializable("user");
        bundle.putString("trasnporter_id",String.valueOf(mUser.getUser_id()));
        final FragmentAddVehicleWRTTransporter fragmentAddVehicleWRTTransporter = new FragmentAddVehicleWRTTransporter();
        fragmentAddVehicleWRTTransporter.setArguments(bundle);

        FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fab_add_transporter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().
                        setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out).
                        replace(R.id.main_content_frame_transporter_container, fragmentAddVehicleWRTTransporter).
                        addToBackStack(null).
                        commit();
            }
        });

        TabLayout tabs = (TabLayout) view.findViewById(R.id.fragment_list_of_vehicle_wrt_trans_tab_layout);
        tabs.setupWithViewPager(viewPager);

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentTabAdapter adapter = new FragmentTabAdapter(getChildFragmentManager());
        adapter.addFragment(new FragmentListOfVehicleWRTTransTabAllVehicle(), "All Vehicle");
        adapter.addFragment(new FragmentListOfVehicleWRTTransTabAssignedVehicle(), "Assigned Vehicles");
        viewPager.setAdapter(adapter);
    }
}
