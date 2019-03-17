package com.example.adnanshaukat.myapplication.View.Customer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adnanshaukat.myapplication.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AdnanShaukat on 01/12/2018.
 */

public class FragmentMainCustomer extends Fragment {

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_customer, container, false);
        populate_chart();
        return view;
    }

    private void populate_chart(){
        LineChart line_chart = (LineChart) view.findViewById(R.id.line_chart);
        BarChart bar_chart = (BarChart) view.findViewById(R.id.bar_chart);

        final String[] bar_x_axis = new String[]{"Mon", "Tues", "Wed", "Thurs", "Fri", "Sat", "Sun"};
        final String[] line_x_axis = new String[]{"Hassan", "Ifran", "Mahmood", "Khan", "Rafique"};
        int[] line_y_axis = new int[]{25, 38, 53, 21, 9, 12, 69};
        int[] bar_y_axis = new int[]{105, 58, 23, 72, 40, 11, 99};

        List<Entry> line_entries = new ArrayList<Entry>();
        List<BarEntry> bar_entries = new ArrayList<BarEntry>();

        for (int i = 0; i < line_x_axis.length; i++) {
            Entry entry =  new Entry(i, line_y_axis[i], line_x_axis);
            line_entries.add(entry);
        }

        for (int i = 0; i < bar_x_axis.length; i++) {
            BarEntry barEntry =  new BarEntry(i, bar_y_axis[i], bar_x_axis);
            bar_entries.add(barEntry);
        }

        IAxisValueFormatter line_x_formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return line_x_axis[(int) value];
            }
        };

        IAxisValueFormatter bar_x_formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return bar_x_axis[(int) value];
            }
        };

        XAxis line_xAxis = line_chart.getXAxis();
        line_xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        line_xAxis.setValueFormatter(line_x_formatter);
        line_xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        line_xAxis.setTextSize(12f);

        XAxis bar_xAxis = bar_chart.getXAxis();
        bar_xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        bar_xAxis.setValueFormatter(bar_x_formatter);
        bar_xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        bar_xAxis.setTextSize(12f);

        LineDataSet dataset = new LineDataSet(line_entries, "");
        LineData line_data = new LineData(dataset);
        line_data.setValueTextSize(12);

        BarDataSet bar_data_set = new BarDataSet(bar_entries, "");
        BarData bar_data = new BarData(bar_data_set);

        bar_chart.setData(bar_data);
        bar_data.setValueTextColor(R.color.colorPrimary);
        bar_data.setValueTextSize(12);
        line_chart.setData(line_data);

//        Description bar_description = new Description();
//        bar_description.setText("Your last week trips count");
//        bar_description.setTextColor(R.color.colorPrimary);
//        bar_description.setTextSize(11f);
        bar_chart.setDescription(null);
//
//        Description line_description = new Description();
//        line_description.setText("Your top 5 dirver of the week");
//        line_description.setTextColor(R.color.colorPrimary);
//        line_description.setTextSize(11f);
        line_chart.setDescription(null);

        line_chart.setNoDataText("No Drivers available right now");
        line_chart.animateX(1000);
        line_chart.animateY(1000);
        line_chart.animate().setDuration(1000);
        line_chart.invalidate();

        bar_chart.setNoDataText("No trips available right now");
        bar_chart.setGridBackgroundColor(R.color.fillColor);
        bar_chart.animateX(1000);
        bar_chart.animateY(1000);
        bar_chart.animate().setDuration(1000);
        bar_chart.invalidate();
    }
}
