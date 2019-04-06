package com.example.adnanshaukat.myapplication.View.Transporter;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.GlobalClasses.ProgressDialogManager;
import com.example.adnanshaukat.myapplication.Modals.DashboardGraph;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IDashboardGraph;
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
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by AdnanShaukat on 06/01/2019.
 */

public class FragmentMainTransporter extends Fragment{

    View view;

    MaterialSpinner spin_period;
    LineChart line_chart;
    BarChart bar_chart;
    TextView line_text_descirption;
    LinearLayout root_view;
    User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_transporter, container, false);
        populateUI();
        user = (User)getActivity().getIntent().getSerializableExtra("user");
        final List<String> period = new ArrayList<String>();

        period.add(0, "--Select Period--");
        period.add(1, "This Week");
        period.add(2, "This Month");
        period.add(3, "Last Week");
        period.add(4, "Last Month");

        ArrayAdapter<String> period_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, period);
        period_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_period.setAdapter(period_adapter);

        spin_period.setSelectedIndex(1);

        getGraphData("this week", user.getUser_id(), "","");
        line_text_descirption.setText("Top 5 driver of This Week");

        spin_period.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if(position != 0){
                    String selected_period =  period.get(position);
                    getGraphData(selected_period, user.getUser_id(), "","");
                    line_text_descirption.setText("Top 5 driver of " + selected_period);
                }
            }
        });

        return view;
    }

    private void populateUI(){
        spin_period = (MaterialSpinner)view.findViewById(R.id.t_period_top_driver);
        line_chart = (LineChart) view.findViewById(R.id.line_chart);
        //bar_chart = (BarChart) view.findViewById(R.id.bar_chart);
        line_text_descirption = (TextView)view.findViewById(R.id.line_text_descirption);
        root_view = (LinearLayout)view.findViewById(R.id.root_layou_transporter_dashboard);
    }

    private void populate_chart(List<Object> x_axis, List<Object> y_axis){
        //final String[] bar_x_axis = new String[]{"Mon", "Tues", "Wed", "Thurs", "Fri", "Sat", "Sun"};
        //final String[] line_x_axis = new String[]{"Hassan", "Ifran", "Mahmood", "Khan", "Rafique"};
        //int[] line_y_axis = new int[]{25, 38, 53, 21, 9, 12, 69};
        //int[] bar_y_axis = new int[]{105, 58, 23, 72, 40, 11, 99};
        final String[] line_x_axis = x_axis.toArray(new String[x_axis.size()]);
        Object[] line_y_axis = y_axis.toArray();
        List<Entry> line_entries = new ArrayList<Entry>();
        //List<BarEntry> bar_entries = new ArrayList<BarEntry>();
        for (int i = 0; i < line_x_axis.length; i++) {
            Entry entry =  new Entry(i, Float.parseFloat(line_y_axis[i].toString()), line_x_axis);
            line_entries.add(entry);
        }
//        for (int i = 0; i < bar_x_axis.length; i++) {
//            BarEntry barEntry =  new BarEntry(i, bar_y_axis[i], bar_x_axis);
//            bar_entries.add(barEntry);
//        }
        IAxisValueFormatter line_x_formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return line_x_axis[(int) value];
            }
        };
//        IAxisValueFormatter bar_x_formatter = new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                return bar_x_axis[(int) value];
//            }
//        };

        XAxis line_xAxis = line_chart.getXAxis();
        line_xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        line_xAxis.setValueFormatter(line_x_formatter);
        line_xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        line_xAxis.setTextSize(12f);
//        XAxis bar_xAxis = bar_chart.getXAxis();
//        bar_xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
//        bar_xAxis.setValueFormatter(bar_x_formatter);
//        bar_xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        bar_xAxis.setTextSize(12f);
        LineDataSet dataset = new LineDataSet(line_entries, "");
        LineData line_data = new LineData(dataset);
        line_data.setValueTextSize(12);
//        BarDataSet bar_data_set = new BarDataSet(bar_entries, "");
//        BarData bar_data = new BarData(bar_data_set);
//
//        bar_chart.setData(bar_data);
//        bar_data.setValueTextColor(R.color.colorPrimary);
//        bar_data.setValueTextSize(12);

        line_chart.setData(line_data);
//        Description bar_description = new Description();
//        bar_description.setText("Your last week trips count");
//        bar_description.setTextColor(R.color.colorPrimary);
//        bar_description.setTextSize(11f);
//        bar_chart.setDescription(null);
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
//        bar_chart.setNoDataText("No trips available right now");
//        bar_chart.setGridBackgroundColor(R.color.fillColor);
//        bar_chart.animateX(1000);
//        bar_chart.animateY(1000);
//        bar_chart.animate().setDuration(1000);
//        bar_chart.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void getGraphData(String period, int transporter_id, String date_from, String date_to){
        try {
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.connectTimeout(30, TimeUnit.SECONDS);
            client.readTimeout(30, TimeUnit.SECONDS);
            client.writeTimeout(30, TimeUnit.SECONDS);

            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(IDashboardGraph.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();

            IDashboardGraph api = retrofit.create(IDashboardGraph.class);

            Call<DashboardGraph> call = api.top_driver_wrt_transporter(period, transporter_id, date_from, date_to);

            call.enqueue(new Callback<DashboardGraph>() {
                @Override
                public void onResponse(Call<DashboardGraph> call, Response<DashboardGraph> response) {
                    Log.e("RESPONSE", response.toString());
                    DashboardGraph dashboardGraph = response.body();
                    if (dashboardGraph.getX_axis() != null && dashboardGraph.getY_axis().size() > 0) {
                        populate_chart(dashboardGraph.getX_axis(), dashboardGraph.getY_axis());
                    }
                    else {
                        populate_chart(new ArrayList<Object>(), new ArrayList<Object>());
                        //Toast.makeText(getContext(), "No Data Available", Toast.LENGTH_SHORT).show();
//                        Snackbar snackbar = Snackbar.make(root_view, "No Data Available", Snackbar.LENGTH_LONG);
//                        snackbar.show();
                    }
                }

                @Override
                public void onFailure(Call<DashboardGraph> call, Throwable t) {
                    Log.e("FAILURE", t.getMessage());
                    Log.e("FAILURE", t.toString());
                    Toast.makeText(getContext(), "An Error Occour, Please Try Again", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception ex) {
            Log.e("ERROR", ex.toString());
        }
    }
}
