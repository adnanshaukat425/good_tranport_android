package com.example.adnanshaukat.myapplication.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.adnanshaukat.myapplication.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by AdnanShaukat on 25/12/2018.
 */

public class FragmentCreateOrderStep2 extends Fragment {

    View view;

    Spinner spinCargoVolumn, spinPaymentType;
    TextView lv_order_date;
    CheckBox chkLabourReq;

    int day, month, year;
    String update_date;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_order_step2, container, false);

        return view;
    }

    public void populateUI(){

    }

    private void populateDateViews() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String curr_date = dateFormat.format(date);

        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        lv_order_date.setText(day + "/" + month + "/" + year);

        lv_order_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SelectDateFragment();

                Bundle args = new Bundle();
                args.putInt("date_id", R.id.lv_order_date);
                newFragment.setArguments(args);

                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });
    }
}
