package com.example.adnanshaukat.myapplication.Modals;

import java.io.Serializable;

/**
 * Created by AdnanShaukat on 02/01/2019.
 */
@SuppressWarnings("serial")
public class ContainerSize implements Serializable {
    public int vehicle_type_id;
    public String vehicle_type;

    public ContainerSize(int vehicle_type_id, String vehicle_type) {
        this.vehicle_type_id = vehicle_type_id;
        this.vehicle_type = vehicle_type;
    }

    public int getVehicle_type_id() {
        return vehicle_type_id;
    }

    public void setVehicle_type_id(int vehicle_type_id) {
        this.vehicle_type_id = vehicle_type_id;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    @Override
    public String toString() {
        return vehicle_type;
    }
}