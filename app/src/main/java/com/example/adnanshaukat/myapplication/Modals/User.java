package com.example.adnanshaukat.myapplication.Modals;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by AdnanShaukat on 04/11/2018.
 */
@SuppressWarnings("serial")
public class User implements Serializable {

    @SerializedName("user_id")
    @Expose
    private int user_id;

    @SerializedName("user_type_id")
    @Expose
    private int user_type_id;

    @SerializedName("first_name")
    @Expose
    private String first_name;

    @SerializedName("last_name")
    @Expose
    private String last_name;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("phone_number")
    @Expose
    private String phone_number;

    @SerializedName("cnic_number")
    @Expose
    private String cnic_number;

    @SerializedName("profile_picture")
    @Expose
    private String profile_picture;

    @SerializedName("password")
    @Expose
    private String password;

    public User(){

    }

    public User(int user_id, int user_type_id, String first_name, String last_name, String email, String phone_number, String cnic_number, String profile_picture, String password) {

        this.user_id = user_id;
        this.user_type_id = user_type_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.phone_number = phone_number;
        this.cnic_number = cnic_number;
        this.profile_picture = profile_picture;
        this.password = password;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getUser_type_id() {
        return user_type_id;
    }

    public void setUser_type_id(int user_type_id) {
        this.user_type_id = user_type_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getCnic_number() {
        return cnic_number;
    }

    public void setCnic_number(String cnic_number) {
        this.cnic_number = cnic_number;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
