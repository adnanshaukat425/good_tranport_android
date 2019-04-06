package com.example.adnanshaukat.myapplication.Modals;

import java.io.Serializable;

/**
 * Created by AdnanShaukat on 09/12/2018.
 */

public class Vehicle implements Serializable {
    public int vehicle_id;
    public String vehicle_type;
    public String vehicle_number;
    public int transporter_id;
    public int driver_id;
    public int container_size;
    public String driver_name;

    public Vehicle(int vehicle_id, String vehicle_type, int container_size, String vehicle_number, int driver_id, String driver_name, int transporter_id) {
        this.vehicle_id = vehicle_id;
        this.vehicle_type = vehicle_type;
        this.vehicle_number = vehicle_number;
        this.driver_id = driver_id;
        this.driver_name = driver_name;
        this.transporter_id = transporter_id;
        this.container_size = container_size;
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

    public String getVehicle_number() {
        return vehicle_number;
    }

    public void setVehicle_number(String vehicle_number) {
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

    public int getTransporter_id() {
        return transporter_id;
    }

    public void setTransporter_id(int transporter_id) {
        this.transporter_id = transporter_id;
    }

    public int getContainer_size() {
        return container_size;
    }

    public void setContainer_size(int container_size) {
        this.container_size = container_size;
    }

    @Override
    public String toString() {
        return vehicle_number;
    }
}
