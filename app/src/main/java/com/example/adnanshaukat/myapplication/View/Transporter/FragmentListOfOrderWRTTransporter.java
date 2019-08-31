package com.example.adnanshaukat.myapplication.View.Transporter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.R;

/**
 * Created by AdnanShaukat on 08/06/2019.
 */

public class FragmentListOfOrderWRTTransporter extends Fragment {

    View view;
    User mUser;
    int transporter_id;

    RecyclerView rv_orders_wrt_transporter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_of_order_wrt_transporter, container, false);
        mUser = (User)getArguments().getSerializable("user_from_driver_list");
        transporter_id = Integer.parseInt(getArguments().get("transporter_id").toString());
        populateView();

        get_orders_wrt_transporter();
        return view;
    }

    private void populateView(){
        rv_orders_wrt_transporter = view.findViewById(R.id.rv_orders_wrt_transporter);
    }

    public void get_orders_wrt_transporter(){

    }
}
