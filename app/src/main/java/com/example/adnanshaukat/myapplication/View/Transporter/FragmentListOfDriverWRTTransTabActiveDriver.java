package com.example.adnanshaukat.myapplication.View.Transporter;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.Adapters.DriverRecyclerViewAdapter;
import com.example.adnanshaukat.myapplication.GlobalClasses.ProgressDialogManager;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by AdnanShaukat on 05/12/2018.
 */

public class FragmentListOfDriverWRTTransTabActiveDriver extends Fragment{

    private List<User> mUser = new ArrayList<>();
    private ProgressDialog progressDialog;
    private View view;
//    private SwipeRefreshLayout fragment_list_of_active_driver_trans_refresher;
    User user;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_of_driver_wrt_trans_tab_active_driver, container, false);

        user = (User)getActivity().getIntent().getSerializableExtra("user");
        Log.e(FragmentListOfDriverWRTTransTabActiveDriver.this.toString(), user.getUser_id() + "");
        getDrivers(Integer.toString(user.getUser_id()));

//        fragment_list_of_active_driver_trans_refresher = (SwipeRefreshLayout)view.findViewById(R.id.fragment_list_of_active_driver_trans_refresher);
//        fragment_list_of_active_driver_trans_refresher.setHorizontalScrollBarEnabled(true);
//        fragment_list_of_active_driver_trans_refresher.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private List<User> getDrivers(final String transporter_id) {
        progressDialog = ProgressDialogManager.showProgressDialogWithTitle(getContext(), "Loading Active Drivers", "Please wait");
        final List<User> result_list = new ArrayList<>();
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

            Call<List<User>> call = api.get_active_drivers(transporter_id);

            call.enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    List<User> user = response.body();
                    if(user!=null && user.size() > 0) {
                        ProgressDialogManager.closeProgressDialog(progressDialog);
                        RecyclerView recyclerView  = (RecyclerView)view.findViewById(R.id.fragment_list_of_active_driver_trans_recycler_view);
                        DriverRecyclerViewAdapter adapter = new DriverRecyclerViewAdapter(getContext(), user, 1, transporter_id);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(layoutManager);

//                        if(fragment_list_of_active_driver_trans_refresher.isRefreshing()){
//                            fragment_list_of_active_driver_trans_refresher.setRefreshing(false);
//                        }

                        recyclerView.setAdapter(adapter);
                    }
                    else{
                        ProgressDialogManager.closeProgressDialog(progressDialog);
                        Toast.makeText(getContext(), "No drivers found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
                    Log.e("FAILURE", t.getMessage());
                    Log.e("FAILURE", t.toString());
                    Toast.makeText(getContext(), t.getMessage() + "", Toast.LENGTH_LONG).show();
                    ProgressDialogManager.closeProgressDialog(progressDialog);
                }
            });
        }
        catch (Exception ex){
            ProgressDialogManager.closeProgressDialog(progressDialog);
            Log.e("ERROR", ex.toString());
        }
        return result_list;
    }

//    @Override
//    public void onRefresh() {
//        getDrivers(Integer.toString(user.getUser_id()));
//    }
}
