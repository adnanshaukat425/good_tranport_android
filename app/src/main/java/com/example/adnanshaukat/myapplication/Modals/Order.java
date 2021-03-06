package com.example.adnanshaukat.myapplication.Modals;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by AdnanShaukat on 01/12/2018.
 */

@SuppressWarnings("Serial")
public class Order implements Serializable{
    public int order_id;
    public int cargo_type_id;
    public int container_type_id;
    public int customer_id;
    public int vehicle_type_id;
    public int weight_catagory_id;
    public float cargo_volume;
    public int weight_unit_id;
    public int source_id;
    public int destination_id;
    public boolean is_labour_required;
    public float labour_cost;
    public int labour_quantity;
    public String description;
    public int payment_type_id;
    public String creation_datetime;
    public String order_datetime;

    public Order(){

    }

    public Order(int order_id, int cargo_type_id, int container_type_id, int vehicle_type_id, int weight_catagory_id, float cargo_volume, int weight_unit_id, int source_id, int destination_id, boolean is_labour_required, float labour_cost, int labour_quantity, String description, int payment_type_id, String creation_datetime, String order_datetime, int customer_id) {
        this.order_id = order_id;
        this.cargo_type_id = cargo_type_id;
        this.container_type_id = container_type_id;
        this.vehicle_type_id = vehicle_type_id;
        this.weight_catagory_id = weight_catagory_id;
        this.cargo_volume = cargo_volume;
        this.weight_unit_id = weight_unit_id;
        this.source_id = source_id;
        this.destination_id = destination_id;
        this.is_labour_required = is_labour_required;
        this.labour_cost = labour_cost;
        this.labour_quantity = labour_quantity;
        this.description = description;
        this.payment_type_id = payment_type_id;
        this.creation_datetime = creation_datetime;
        this.order_datetime = order_datetime;
        this.customer_id = customer_id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getCargo_type_id() {
        return cargo_type_id;
    }

    public void setCargo_type_id(int cargo_type_id) {
        this.cargo_type_id = cargo_type_id;
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

    public int getWeight_catagory_id() {
        return weight_catagory_id;
    }

    public void setWeight_catagory_id(int weight_catagory_id) {
        this.weight_catagory_id = weight_catagory_id;
    }

    public float getCargo_volume() {
        return cargo_volume;
    }

    public void setCargo_volume(float cargo_volume) {
        this.cargo_volume = cargo_volume;
    }

    public int getWeight_unit_id() {
        return weight_unit_id;
    }

    public void setWeight_unit_id(int weight_unit_id) {
        this.weight_unit_id = weight_unit_id;
    }

    public int getSource_id() {
        return source_id;
    }

    public void setSource_id(int source_id) {
        this.source_id = source_id;
    }

    public int getDestination_id() {
        return destination_id;
    }

    public void setDestination_id(int destination_id) {
        this.destination_id = destination_id;
    }

    public boolean is_labour_required() {
        return is_labour_required;
    }

    public void setIs_labour_required(boolean is_labour_required) {
        this.is_labour_required = is_labour_required;
    }

    public float getLabour_cost() {
        return labour_cost;
    }

    public void setLabour_cost(float labour_cost) {
        this.labour_cost = labour_cost;
    }

    public int getLabour_quantity() {
        return labour_quantity;
    }

    public void setLabour_quantity(int labour_quantity) {
        this.labour_quantity = labour_quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPayment_type_id() {
        return payment_type_id;
    }

    public void setPayment_type_id(int payment_type_id) {
        this.payment_type_id = payment_type_id;
    }

    public String getCreation_datetime() {
        return creation_datetime;
    }

    public void setCreation_datetime(String creation_datetime) {
        this.creation_datetime = creation_datetime;
    }

    public String getOrder_datetime() {
        return order_datetime;
    }

    public void setOrder_datetime(String order_datetime) {
        this.order_datetime = order_datetime;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }
}