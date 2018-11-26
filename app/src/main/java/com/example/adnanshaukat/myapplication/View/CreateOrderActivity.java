package com.example.adnanshaukat.myapplication.View;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.adnanshaukat.myapplication.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateOrderActivity extends AppCompatActivity {

    int DATE_DIALOG_ID= 1;
    TextView lv_order_date, lv_creation_date;

    int day, month, year;
    String update_date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        lv_order_date = (TextView) findViewById(R.id.lv_order_date);
        lv_creation_date = (TextView) findViewById(R.id.lv_creation_date);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String curr_date = dateFormat.format(date);

        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        lv_order_date.setText(day + "/"+ month + "/" + year);
        lv_creation_date.setText(day + "/"+ month + "/" + year);

        lv_order_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
                update_date = "order_date";
            }
        });

        lv_creation_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
                update_date = "creation_date";
            }
        });
    }

    public void showDatePicker() {
        showDialog(DATE_DIALOG_ID);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DATE_DIALOG_ID){
            return new DatePickerDialog(this, R.style.datepicker, date_picker_listner, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener date_picker_listner = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int _year, int _month, int _dayOfMonth) {
            year = _year;
            month = _month + 1;
            day = _dayOfMonth;
            if (update_date == "order_date") {
                lv_order_date.setText(day + "/" + month + "/" + year);
            } else if (update_date == "creation_date") {
                lv_creation_date.setText(day + "/" + month + "/" + year);
            }
        }
    };
}
