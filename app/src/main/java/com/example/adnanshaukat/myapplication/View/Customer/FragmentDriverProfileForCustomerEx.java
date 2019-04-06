package com.example.adnanshaukat.myapplication.View.Customer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adnanshaukat.myapplication.R;

/**
 * Created by AdnanShaukat on 01/04/2019.
 */

public class FragmentDriverProfileForCustomerEx extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_testign, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
