package com.example.adnanshaukat.myapplication.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adnanshaukat.myapplication.Modals.DriverDetailsWrtOrder;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.View.Customer.MainActivityCustomer;

import java.util.List;

/**
 * Created by AdnanShaukat on 05/01/2019.
 */

public class DriverRecyclerViewWrtOrderAdapter extends RecyclerView.Adapter<DriverRecyclerViewWrtOrderAdapter.MyViewHolder> {
    Context mContext;
    List<DriverDetailsWrtOrder> mDriver;
    public DriverRecyclerViewWrtOrderAdapter(Context context, List<DriverDetailsWrtOrder> users){
        mContext = context;
        mDriver = users;
    }

    @Override
    public DriverRecyclerViewWrtOrderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.driver_list, parent, false);
        DriverRecyclerViewWrtOrderAdapter.MyViewHolder viewHolder = new DriverRecyclerViewWrtOrderAdapter.MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DriverRecyclerViewWrtOrderAdapter.MyViewHolder holder, int position) {
        Log.e("USER Name", mDriver.get(position).getFirst_name());
        String encodedImage = mDriver.get(position).getProfile_picture();
        if (encodedImage.isEmpty()) {
            holder.profile_image.setImageResource(R.drawable.default_profile_image);
        } else {
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            holder.profile_image.setImageBitmap(decodedByte);
        }
        holder.driver_status_image.setImageResource(R.drawable.online_icon);
        holder.tv_driver_name.setText(mDriver.get(position).getFirst_name() + " " + mDriver.get(position).getLast_name());
        holder.tv_driver_email.setText(mDriver.get(position).getEmail());
        holder.tv_driver_phone_no.setText(mDriver.get(position).getPhone_number());

        holder.setItemClickListner(new ItemClickListner() {
            @Override
            public void onClick(View view, int position) {
                //Toast.makeText(mContext, mDriver.get(position).getFirst_name(), Toast.LENGTH_SHORT).show();
                MainActivityCustomer activity = (MainActivityCustomer)mContext;

                Bundle bundle = new Bundle();
                DriverDetailsWrtOrder user = mDriver.get(position);
                bundle.putSerializable("user_from_driver_list", user);
                bundle.putString("transporter_id", Integer.toString(user.getTransporter_id()));
                Toast.makeText(activity, "Will be available soon :-)", Toast.LENGTH_SHORT).show();
                
//                FragmentUserProfileForDriverFromTransporter fragment = new FragmentUserProfileForDriverFromTransporter();
//                fragment.setArguments(bundle);
//
//                activity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right).
//                        replace(R.id.main_content_frame_transporter_container, fragment).
//                        addToBackStack(null).
//                        commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDriver.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        LinearLayout linearLayout;
        ImageView profile_image, driver_status_image;
        TextView tv_driver_name, tv_driver_email, tv_driver_phone_no;
        ItemClickListner itemClickListner;

        public MyViewHolder(View v){
            super(v);
            linearLayout = (LinearLayout)v.findViewById(R.id.driver_list_linear_layout);
            profile_image = (ImageView)v.findViewById(R.id.driver_list_profile_image_view);
            driver_status_image = (ImageView)v.findViewById(R.id.driver_list_status_image_view);
            tv_driver_name = (TextView)v.findViewById(R.id.driver_list_driver_name);
            tv_driver_email = (TextView)v.findViewById(R.id.driver_list_driver_email);
            tv_driver_phone_no = (TextView)v.findViewById(R.id.driver_list_driver_phone_no);
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
