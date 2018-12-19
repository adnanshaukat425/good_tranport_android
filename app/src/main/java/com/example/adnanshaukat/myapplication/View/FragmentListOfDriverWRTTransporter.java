package com.example.adnanshaukat.myapplication.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adnanshaukat.myapplication.Adapters.FragmentTabAdapter;
import com.example.adnanshaukat.myapplication.R;

/**
 * Created by AdnanShaukat on 05/12/2018.
 */

public class FragmentListOfDriverWRTTransporter extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_of_driver_wrt_transporter, container, false);

//        Toolbar toolbar = (Toolbar)view.findViewById(R.id.toolbar);
//        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.fragment_list_of_driver_wrt_trans_viewpager);
        setupViewPager(viewPager);

        TabLayout tabs = (TabLayout) view.findViewById(R.id.fragment_list_of_driver_wrt_trans_tab_layout);
        tabs.setupWithViewPager(viewPager);

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentTabAdapter adapter = new FragmentTabAdapter(getChildFragmentManager());
        adapter.addFragment(new FragmentListOfDriverWRTTransTabActiveDriver(), "Active Drivers");
        adapter.addFragment(new FragmentListOfDriverWRTTransTabAllDriver(), "All Drivers");
        viewPager.setAdapter(adapter);
    }
}
