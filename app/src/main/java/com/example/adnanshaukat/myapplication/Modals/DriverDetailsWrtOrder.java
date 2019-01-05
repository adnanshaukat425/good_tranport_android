package com.example.adnanshaukat.myapplication.Modals;

import java.io.Serializable;

/**
 * Created by AdnanShaukat on 05/01/2019.
 */
@SuppressWarnings("serial")
public class DriverDetailsWrtOrder implements Serializable{
    public int user_id;
    public int order_id;
    public String first_name;
    public String last_name;
    public String profile_picture;
    public String email;
    public String phone_number;
    public int user_type_id;
    public int transporter_id;
    public String vehicle_number;
    public int container_type_id;
    public int vehicle_type_id;
    public double current_latitude;
    public double current_longitude;
    public double distance;

    public DriverDetailsWrtOrder(int user_id, int order_id, String first_name, String last_name, String profile_picture, String email, String phone_number, int user_type_id, int transporter_id, String vehicle_number, int container_type_id, int vehicle_type_id, double current_latitude, double current_longitude, double distance) {
        this.user_id = user_id;
        this.order_id = order_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.profile_picture = profile_picture;
        this.email = email;
        this.phone_number = phone_number;
        this.user_type_id = user_type_id;
        this.transporter_id = transporter_id;
        this.vehicle_number = vehicle_number;
        this.container_type_id = container_type_id;
        this.vehicle_type_id = vehicle_type_id;
        this.current_latitude = current_latitude;
        this.current_longitude = current_longitude;
        this.distance = distance;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
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

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
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

    public int getUser_type_id() {
        return user_type_id;
    }

    public void setUser_type_id(int user_type_id) {
        this.user_type_id = user_type_id;
    }

    public int getTransporter_id() {
        return transporter_id;
    }

    public void setTransporter_id(int transporter_id) {
        this.transporter_id = transporter_id;
    }

    public String getVehicle_number() {
        return vehicle_number;
    }

    public void setVehicle_number(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }

    public int getContainer_type_id() {
        return container_type_id;
    }

    public void setContainer_type_id(int container_type_id) {
        this.container_type_id = container_type_id;
    }

    public int getVehicle_type_id() {
        return vehicle_type_id;
    }

    public void setVehicle_type_id(int vehicle_type_id) {
        this.vehicle_type_id = vehicle_type_id;
    }

    public double getCurrent_latitude() {
        return current_latitude;
    }

    public void setCurrent_latitude(double current_latitude) {
        this.current_latitude = current_latitude;
    }

    public double getCurrent_longitude() {
        return current_longitude;
    }

    public void setCurrent_longitude(double current_longitude) {
        this.current_longitude = current_longitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
