package com.example.adnanshaukat.myapplication.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.Modals.Vehicle;
import com.example.adnanshaukat.myapplication.R;

import java.util.List;

/**
 * Created by AdnanShaukat on 09/12/2018.
 */

public class VehiclesRecyclerViewAdapter extends RecyclerView.Adapter<VehiclesRecyclerViewAdapter.MyViewHolder> {
    Context mContext;
    List<Vehicle> mVehicles;
    int mStatus;

    public VehiclesRecyclerViewAdapter(Context context, List<Vehicle> vehicles) {
        mContext = context;
        mVehicles = vehicles;
    }

    @Override
    public VehiclesRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.vehicle_list, parent, false);
        VehiclesRecyclerViewAdapter.MyViewHolder viewHolder = new VehiclesRecyclerViewAdapter.MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VehiclesRecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.tv_vehicle_number.setText(Integer.toString(mVehicles.get(position).getVehicle_number()));
        holder.tv_vehicle_type.setText(mVehicles.get(position).getVehicle_type());
        holder.tv_driver_name.setText(mVehicles.get(position).getDriver_name());
    }

    @Override
    public int getItemCount() {
        return mVehicles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView vehicle_image;
        TextView tv_vehicle_number, tv_vehicle_type, tv_driver_name;

        public MyViewHolder(View v){
            super(v);
            vehicle_image = (ImageView)v.findViewById(R.id.vehicle_list_vehicle_image_view);
            tv_vehicle_number = (TextView)v.findViewById(R.id.vehicle_list_vehicle_number);
            tv_vehicle_type = (TextView)v.findViewById(R.id.vehicle_list_vehicle_type);
            tv_driver_name = (TextView)v.findViewById(R.id.vehicle_list_driver_name);
        }
    }
}
