package com.example.adnanshaukat.myapplication.Modals;

import java.io.Serializable;

/**
 * Created by AdnanShaukat on 04/11/2018.
 */
public class User {
    private int user_id;
    private int user_type_id;
    private String first_name;
    private String last_name;
    private String email;
    private String phone_number;
    private String cnic_number;
    private String profile_picture;
    private String password;

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
