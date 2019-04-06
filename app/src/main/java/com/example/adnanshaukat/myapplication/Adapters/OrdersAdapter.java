package com.example.adnanshaukat.myapplication.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adnanshaukat.myapplication.Modals.Order;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.View.Customer.FragmentListDriverWRTOrder;
import com.example.adnanshaukat.myapplication.View.Customer.MainActivityCustomer;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by AdnanShaukat on 01/04/2019.
 */

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.MyViewHolder> {

    Context mContext;
    List<Order> mOrders;
    public OrdersAdapter(Context context, List<Order> orders){
        mContext = context;
        mOrders = orders;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.driver_list, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.tv_order_id.setText(mOrders.get(position).getOrder_id());
            holder.tv_creation_date.setText(mOrders.get(position).getCreation_datetime());
            holder.tv_order_status.setText(mOrders.get(position).getDescription());

        holder.setItemClickListner(new ItemClickListner() {
            @Override
            public void onClick(View view, int position) {
                //Toast.makeText(mContext, mUsers.get(position).getFirst_name(), Toast.LENGTH_SHORT).show();
                MainActivityCustomer activity = (MainActivityCustomer)mContext;

                Bundle bundle = new Bundle();
//                User user = mUsers.get(position);
                Gson gson = new Gson();
                bundle.putSerializable("order", gson.toJson(mOrders.get(position)));
                FragmentListDriverWRTOrder fragment = new FragmentListDriverWRTOrder();
                fragment.setArguments(bundle);

                activity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right).
                        replace(R.id.main_content_frame_customer_container, fragment).
                        addToBackStack(null).
                        commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mOrders.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView profile_image;
        TextView tv_order_id, tv_creation_date, tv_order_status;
        ItemClickListner itemClickListner;

        public MyViewHolder(View v){
            super(v);
            profile_image = (ImageView)v.findViewById(R.id.vehicle_list_vehicle_image_view);
            tv_order_id = (TextView)v.findViewById(R.id.vehicle_list_driver_name);
            tv_order_status = (TextView)v.findViewById(R.id.vehicle_list_vehicle_type);
            tv_creation_date = (TextView)v.findViewById(R.id.vehicle_list_vehicle_number);
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