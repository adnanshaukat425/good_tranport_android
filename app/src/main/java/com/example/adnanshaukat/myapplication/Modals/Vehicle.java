package com.example.adnanshaukat.myapplication.Modals;

/**
 * Created by AdnanShaukat on 09/12/2018.
 */

public class Vehicle {
    public int vehicle_id;
    public String vehicle_type;
    public int vehicle_number;
    public int driver_id;
    public String driver_name;

    public Vehicle(int vehicle_id, String vehicle_type, int vehicle_number, int driver_id, String driver_name) {
        this.vehicle_id = vehicle_id;
        this.vehicle_type = vehicle_type;
        this.vehicle_number = vehicle_number;
        this.driver_id = driver_id;
        this.driver_name = driver_name;
    }

    public int getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(int vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    public int getVehicle_number() {
        return vehicle_number;
    }

    public void setVehicle_number(int vehicle_number) {
        this.vehicle_number = vehicle_number;
    }

    public int getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(int driver_id) {
        this.driver_id = driver_id;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }
}
