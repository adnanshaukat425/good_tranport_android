package com.example.adnanshaukat.myapplication.Modals;

/**
 * Created by AdnanShaukat on 23/12/2018.
 */

public class Location {
    public int location_id;
    public String location_name;
    public String latitude;
    public String longitude;

    public Location(int location_id, String location_name, String latitude, String longitude) {
        this.location_id = location_id;
        this.location_name = location_name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getLocation_id() {
        return location_id;
    }

    public void setLocation_id(int location_id) {
        this.location_id = location_id;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return location_name;
    }
}
