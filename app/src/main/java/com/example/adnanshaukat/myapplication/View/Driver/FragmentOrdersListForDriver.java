package com.example.adnanshaukat.myapplication.View.Driver;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adnanshaukat.myapplication.Adapters.FragmentTabAdapter;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.View.Transporter.FragmentListOfVehicleWRTTransTabAllVehicle;
import com.example.adnanshaukat.myapplication.View.Transporter.FragmentListOfVehicleWRTTransTabAssignedVehicle;

/**
 * Created by AdnanShaukat on 31/03/2019.
 */

public class FragmentOrdersListForDriver extends Fragment {

    View view;
    User mUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_orders_list_for_driver, container, false);

        Bundle argument = getArguments();
        mUser = (User)argument.getSerializable("user");

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.fragment_list_of_order_for_driver_viewpager);
        setupViewPager(viewPager);


        TabLayout tabs = (TabLayout) view.findViewById(R.id.fragment_list_of_order_for_driver_tab_layout);
        tabs.setupWithViewPager(viewPager);

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentTabAdapter adapter = new FragmentTabAdapter(getChildFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putSerializable("user", mUser);

        FragmentListOfAllOrdersWRTDriver all_orders = new FragmentListOfAllOrdersWRTDriver();
        all_orders.setArguments(bundle);

        FragmentListOfActiveOrdersWRTDriver active_orders = new FragmentListOfActiveOrdersWRTDriver();
        active_orders.setArguments(bundle);

        adapter.addFragment(active_orders, "Active Orders");
        adapter.addFragment(all_orders, "Completed Orders");
        viewPager.setAdapter(adapter);
    }
}
