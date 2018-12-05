package com.example.adnanshaukat.myapplication.View;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.adnanshaukat.myapplication.Modals.Cargo;
import com.example.adnanshaukat.myapplication.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by AdnanShaukat on 30/11/2018.
 */

public class FragmentCreateOrder extends DialogFragment {

    int DATE_DIALOG_ID= 1;
    TextView lv_order_date, lv_creation_date;

    int day, month, year;
    String update_date;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_activity_order, container, false);
        lv_order_date = (TextView) view.findViewById(R.id.lv_order_date);
        lv_creation_date = (TextView) view.findViewById(R.id.lv_creation_date);

        Spinner spinner = (Spinner)view.findViewById(R.id.lv_cargo_type);

        Cargo[] cargo = new Cargo[2];
        cargo[0] = new Cargo(1, "Dense");
        cargo[1] = new Cargo(2, "Volumetric");

        SpinAdapter spinAdapter = new SpinAdapter(getContext(), R.layout.support_simple_spinner_dropdown_item, cargo);
        spinner.setAdapter(spinAdapter);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String curr_date = dateFormat.format(date);

        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        lv_order_date.setText(day + "/" + month + "/" + year);
        lv_creation_date.setText(day + "/" + month + "/" + year);

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

        lv_creation_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SelectDateFragment();

                Bundle args = new Bundle();
                args.putInt("date_id", R.id.lv_creation_date);
                newFragment.setArguments(args);

                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });
        return view;
    }
}

class SpinAdapter extends ArrayAdapter<Cargo>{

    // Your sent context
    private Context context;
    // Your custom values for the spinner (User)
    private Cargo[] values;

    public SpinAdapter(Context context, int textViewResourceId,
                       Cargo[] values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public int getCount(){
        return values.length;
    }

    @Override
    public Cargo getItem(int position){
        return values[position];
    }

    @Override
    public long getItemId(int position){
        return position;
    }


    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(values[position].getCargo_type());

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(values[position].getCargo_type());

        return label;
    }
}

