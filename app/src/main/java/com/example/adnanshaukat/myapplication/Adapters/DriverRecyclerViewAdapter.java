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

import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.View.FragmentUserProfileForDriverFromTransporter;
import com.example.adnanshaukat.myapplication.View.MainActivityTransporter;

import java.util.List;

/**
 * Created by AdnanShaukat on 06/12/2018.
 */

public class DriverRecyclerViewAdapter extends RecyclerView.Adapter<DriverRecyclerViewAdapter.MyViewHolder> {

    Context mContext;
    List<User> mUsers;
    int mStatus;
    String transporter_id;
    public DriverRecyclerViewAdapter(Context context, List<User> users, int status, String transporter_id){
        mContext = context;
        mUsers = users;
        this.mStatus = status;
        this.transporter_id = transporter_id;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.driver_list, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (mStatus != -1){
            Log.e("USER STATUS",Integer.toString(mStatus));
            Log.e("USER Name", mUsers.get(position).getFirst_name());

            String encodedImage = mUsers.get(position).getProfile_picture();
//            Log.e("ENCODED IMAGE FROM RECY", encodedImage + "D");

            try{
                if (encodedImage == null || encodedImage.isEmpty()) {
                    holder.profile_image.setImageResource(R.drawable.default_profile_image_2);
                } else {
                    byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    if (decodedString.length == 0 || decodedByte == null){
                        holder.profile_image.setImageResource(R.drawable.default_profile_image_2);
                    }
                    else{
                        holder.profile_image.setImageBitmap(decodedByte);
                    }
                }
            }
            catch(Exception ex){
                holder.profile_image.setImageResource(R.drawable.default_profile_image_2);
            }
//            if (encodedImage == null || encodedImage.isEmpty()) {
//                holder.profile_image.setImageResource(R.drawable.default_profile_image);
//            } else {
//                byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
//                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                if (decodedByte == null){
//                    holder.profile_image.setImageResource(R.drawable.default_profile_image);
//                }
//                holder.profile_image.setImageBitmap(decodedByte);
//            }

            if(mUsers.get(position).getStatus() == mStatus) {
                if (mUsers.get(position).getStatus() == 1) {
                    holder.driver_status_image.setImageResource(R.drawable.online_icon);
                } else if (mUsers.get(position).getStatus() == 0) {
                    holder.driver_status_image.setImageResource(R.drawable.offline_icon);
                }

                holder.tv_driver_name.setText(mUsers.get(position).getFirst_name() + " " + mUsers.get(position).getLast_name());
                holder.tv_driver_email.setText(mUsers.get(position).getEmail());
                holder.tv_driver_phone_no.setText(mUsers.get(position).getPhone_number());
            }
        }
        else{
            String encodedImage = mUsers.get(position).getProfile_picture();
//            Log.e("ENCODED IMAGE FROM RECY", encodedImage);
            if (encodedImage == null || encodedImage.isEmpty()) {
                holder.profile_image.setImageResource(R.drawable.default_profile_image);
            } else {
                byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                if (decodedByte == null){
                    holder.profile_image.setImageResource(R.drawable.default_profile_image);
                }
                holder.profile_image.setImageBitmap(decodedByte);
            }

            if (mUsers.get(position).getStatus() == 1) {
                holder.driver_status_image.setImageResource(R.drawable.online_icon);
            } else if (mUsers.get(position).getStatus() == 0) {
                holder.driver_status_image.setImageResource(R.drawable.offline_icon);
            }

            holder.tv_driver_name.setText(mUsers.get(position).getFirst_name() + " " + mUsers.get(position).getLast_name());
            holder.tv_driver_email.setText(mUsers.get(position).getEmail());
            holder.tv_driver_phone_no.setText(mUsers.get(position).getPhone_number());
        }

        holder.setItemClickListner(new ItemClickListner() {
            @Override
            public void onClick(View view, int position) {
                //Toast.makeText(mContext, mUsers.get(position).getFirst_name(), Toast.LENGTH_SHORT).show();
                MainActivityTransporter activity = (MainActivityTransporter)mContext;

                Bundle bundle = new Bundle();
                User user = mUsers.get(position);
                bundle.putSerializable("user_from_driver_list", user);
                bundle.putString("transporter_id", transporter_id);
                FragmentUserProfileForDriverFromTransporter fragment = new FragmentUserProfileForDriverFromTransporter();
                fragment.setArguments(bundle);

                activity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right).
                        replace(R.id.main_content_frame_transporter_container, fragment).
                        addToBackStack(null).
                        commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
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