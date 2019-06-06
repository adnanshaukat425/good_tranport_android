package com.example.adnanshaukat.myapplication.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adnanshaukat.myapplication.Modals.Order;
import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.View.Customer.FragmentListDriverWRTOrder;
import com.example.adnanshaukat.myapplication.View.Customer.MainActivityCustomer;
import com.example.adnanshaukat.myapplication.View.Driver.FragmentOrderDetailsWRTOrder;
import com.example.adnanshaukat.myapplication.View.Driver.MainActivityDriver;

import java.util.HashMap;
import java.util.List;

/**
 * Created by AdnanShaukat on 01/04/2019.
 */

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.MyViewHolder> {

    Context mContext;
    List<HashMap<String, String>> mOrders;
    String from_user;
    String orders_type;
    User mUser;

    public OrdersAdapter(Context context, List<HashMap<String, String>> orders, String from_user, String orders_type, User mUser){
        mContext = context;
        mOrders = orders;
        this.from_user = from_user;
        this.orders_type = orders_type;
        this.mUser = mUser;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.vehicle_list, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Log.e("FROM Order Adapter", position + " " + mOrders.get(position).get("order_id") + "");

        holder.tv_order_status.setText(mOrders.get(position).get("status").replace("\"", ""));
        holder.tv_source_destination.setText(mOrders.get(position).get("source").replace("\"", "") + " => " + mOrders.get(position).get("destination").replace("\"", ""));
        holder.tv_creation_datetime.setText((mOrders.get(position).get("creation_datetime").replace("\"", "")));


        holder.setItemClickListner(new ItemClickListner() {
            @Override
            public void onClick(View view, int position) {
                //Toast.makeText(mContext, mUsers.get(position).getFirst_name(), Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                Order ord = new Order();
                Log.e("ORDER ID", mOrders.get(position).get("order_id").toString());
                ord.setOrder_id(Integer.parseInt(mOrders.get(position).get("order_id").toString()));
                ord.setContainer_type_id(Integer.parseInt(mOrders.get(position).get("container_type_id").toString()));
                ord.setVehicle_type_id(Integer.parseInt(mOrders.get(position).get("vehicle_type_id").toString()));
                ord.setSource_id(Integer.parseInt(mOrders.get(position).get("source_id").toString()));

                bundle.putSerializable("order", ord);
                bundle.putString("show_wrt_order_id", "true");
                bundle.putString("order_type", orders_type);

                Fragment fragment = null;
                if (from_user == "customer"){
                    fragment = new FragmentListDriverWRTOrder();
                    fragment.setArguments(bundle);
                }
                else{
                    if(orders_type != "requested_order"){
                        if (orders_type == "active_order"){
                            bundle.putBoolean("is_order_request", true);
                        }
                        bundle.putBoolean("is_order_request", false);
                    }
                    else{
                        bundle.putBoolean("is_order_request", true);
                    }
                    fragment = new FragmentOrderDetailsWRTOrder();
                    bundle.putSerializable("order_details", mOrders.get(position));
                    bundle.putSerializable("user", mUser);
                    fragment.setArguments(bundle);
                }

                AppCompatActivity activity = null;
                if (from_user == "customer") {
                    activity = (MainActivityCustomer)mContext;

                    activity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right).
                            replace(R.id.main_content_frame_customer_container, fragment).
                            addToBackStack(null).
                            commit();
                }
                else if(from_user == "driver"){
                    activity = (MainActivityDriver)mContext;

                    activity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right).
                            replace(R.id.main_content_frame_driver_container, fragment).
                            addToBackStack(null).
                            commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mOrders.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView profile_image;
        TextView tv_creation_datetime, tv_source_destination, tv_order_status;
        ItemClickListner itemClickListner;

        public MyViewHolder(View v){
            super(v);
            profile_image = (ImageView)v.findViewById(R.id.vehicle_list_vehicle_image_view);
            tv_order_status = (TextView)v.findViewById(R.id.vehicle_list_driver_name);
            tv_creation_datetime = (TextView)v.findViewById(R.id.vehicle_list_vehicle_type);
            tv_source_destination = (TextView)v.findViewById(R.id.vehicle_list_vehicle_number);
            v.setOnClickListener(this);
        }

        public void setItemClickListner(ItemClickListner itemClickListner){
            this.itemClickListner = itemClickListner;
        }

        @Override
        public void onClick(View v) {
            itemClickListner.onClick(v, getAdapterPosition());
        }
    }
}