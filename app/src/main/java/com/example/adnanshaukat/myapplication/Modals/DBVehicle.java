package com.example.adnanshaukat.myapplication.Modals;

/**
 * Created by AdnanShaukat on 18/02/2019.
 */

public class DBVehicle {
    public int vehicle_id;
    public int vehicle_type_id;
    public int container_type_id;
    public String vehicle_number;
    public int transporter_id;

    public DBVehicle(int vehicle_id, int vehicle_type_id, int container_type_id, String vehicle_number, int transporter_id) {
        this.vehicle_id = vehicle_id;
        this.vehicle_type_id = vehicle_type_id;
        this.container_type_id = container_type_id;
        this.vehicle_number = vehicle_number;
        this.transporter_id = transporter_id;
    }

    public int getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(int vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public int getVehicle_type_id() {
        return vehicle_type_id;
    }

    public void setVehicle_type_id(int vehicle_type_id) {
        this.vehicle_type_id = vehicle_type_id;
    }

    public int getContainer_type_id() {
        return container_type_id;
    }

    public void setContainer_type_id(int container_type_id) {
        this.container_type_id = container_type_id;
    }

    public String getVehicle_number() {
        return vehicle_number;
    }

    public void setVehicle_number(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }

    public int getTransporter_id() {
        return transporter_id;
    }

    public void setTransporter_id(int transporter_id) {
        this.transporter_id = transporter_id;
    }
}
