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
import com.example.adnanshaukat.myapplication.R;

import java.util.List;

/**
 * Created by AdnanShaukat on 06/12/2018.
 */

public class DriverRecyclerViewAdapter extends RecyclerView.Adapter<DriverRecyclerViewAdapter.MyViewHolder> {

    Context mContext;
    List<User> mUsers;

    public DriverRecyclerViewAdapter(Context context, List<User> users){
        mContext = context;
        mUsers = users;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.driver_list, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        String encodedImage = mUsers.get(position).getProfile_picture();
        if(encodedImage.isEmpty()){
            holder.profile_image.setImageResource(R.drawable.default_profile_image);
        }
        else{
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            holder.profile_image.setImageBitmap(decodedByte);
        }

        holder.tv_driver_name.setText(mUsers.get(position).getFirst_name() + " " + mUsers.get(position).getLast_name());
        holder.tv_driver_email.setText(mUsers.get(position).getEmail());
        holder.tv_driver_phone_no.setText(mUsers.get(position).getPhone_number());
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView profile_image;
        TextView tv_driver_name, tv_driver_email, tv_driver_phone_no;

        public MyViewHolder(View v){
            super(v);
            profile_image = (ImageView)v.findViewById(R.id.driver_list_profile_image_view);
            tv_driver_name = (TextView)v.findViewById(R.id.driver_list_driver_name);
            tv_driver_email = (TextView)v.findViewById(R.id.driver_list_driver_email);
            tv_driver_phone_no = (TextView)v.findViewById(R.id.driver_list_driver_phone_no);
        }
    }
}