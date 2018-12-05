package com.example.adnanshaukat.myapplication.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adnanshaukat.myapplication.R;

/**
 * Created by AdnanShaukat on 05/12/2018.
 */

public class FragmentListOfDriverWRTTransTabActiveDriver extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_of_driver_wrt_trans_tab_active_driver, container, false);
        return view;
    }
}
