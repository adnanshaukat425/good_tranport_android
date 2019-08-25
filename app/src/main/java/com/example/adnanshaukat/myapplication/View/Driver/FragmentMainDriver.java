package com.example.adnanshaukat.myapplication.View.Driver;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.Modals.DashboardGraph;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.IDashboardGraph;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
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
 * Created by AdnanShaukat on 05/01/2019.
 */

public class FragmentMainDriver extends Fragment {

    View view;
    BarChart bar_chart;
    LinearLayout root_view;
    TextView bar_chart_descirption;

    User mUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_driver, container, false);

        mUser = (User)getActivity().getIntent().getSerializableExtra("user");

        populateUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getGraphData(mUser.getUser_id());
    }

    private void populateUI(){
        bar_chart = view.findViewById(R.id.bar_d_chart);
        bar_chart_descirption = view.findViewById(R.id.bar_chart_d_description);
        root_view = view.findViewById(R.id.root_layou_transporter_dashboard);
    }

    private void populate_chart(List<Object> x_axis, List<Object> y_axis){

        if (x_axis == null || y_axis == null){
            return;
        }
        final String[] bar_x_axis = x_axis.toArray(new String[x_axis.size()]);
        Object[] bar_y_axis = y_axis.toArray();
        List<BarEntry> bar_entries = new ArrayList<>();

        for (int i = 0; i < bar_x_axis.length; i++) {
            BarEntry barEntry =  new BarEntry(i, Float.parseFloat(bar_y_axis[i].toString()), bar_x_axis);
            bar_entries.add(barEntry);
        }

        IAxisValueFormatter bar_x_formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return bar_x_axis[(int) value];
            }
        };

        XAxis bar_xAxis = bar_chart.getXAxis();
        bar_xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        bar_xAxis.setValueFormatter(bar_x_formatter);
        bar_xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        bar_xAxis.setTextSize(12f);

        BarDataSet bar_data_set = new BarDataSet(bar_entries, "");
        BarData bar_data = new BarData(bar_data_set);

        bar_chart.setData(bar_data);
        bar_data.setValueTextColor(R.color.colorPrimary);
        bar_data.setValueTextSize(12);

        Description bar_description = new Description();
        bar_description.setText("Last 6 Months Orders Count");
        bar_description.setTextColor(R.color.colorPrimary);
        bar_description.setTextSize(11f);
        bar_chart.setDescription(bar_description);

        bar_chart.setNoDataText("No trips available right now");
        bar_chart.animateX(1000);
        bar_chart.animateY(1000);
        bar_chart.animate().setDuration(1000);
        bar_chart.setGridBackgroundColor(R.color.colorPrimary);
        //bar_chart.setBackgroundColor(getContext().getResources().getColor(R.color.colorAccent));
        bar_chart.invalidate();
    }

    private void getGraphData(int driver_id){
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

            Call<DashboardGraph> call = api.get_last_six_month_order_of_driver(driver_id);

            call.enqueue(new Callback<DashboardGraph>() {
                @Override
                public void onResponse(Call<DashboardGraph> call, Response<DashboardGraph> response) {
                    Log.e("RESPONSE", response.toString());
                    DashboardGraph dashboardGraph = response.body();
                    if (dashboardGraph.getX_axis() != null && dashboardGraph.getY_axis().size() > 0) {
                        populate_chart(dashboardGraph.getX_axis(), dashboardGraph.getY_axis());
                    }
                    else {
                        populate_chart(new ArrayList<>(), new ArrayList<>());

                        try {
                            Toast.makeText(getContext(), "No Data Available", Toast.LENGTH_SHORT).show();
//                        Snackbar snackbar = Snackbar.make(root_view, "No Data Available", Snackbar.LENGTH_LONG);
//                        snackbar.show();
                        }
                        catch(Exception ex){
                            ex.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<DashboardGraph> call, Throwable t) {
                    Log.e("FAILURE", "F: " + t.getMessage());
                    Log.e("FAILURE", "F: " + t.toString());
                    Toast.makeText(getContext(), "An Error Occour, Please Try Again", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception ex) {
            Log.e("ERROR", ex.toString());
        }
    }
}
