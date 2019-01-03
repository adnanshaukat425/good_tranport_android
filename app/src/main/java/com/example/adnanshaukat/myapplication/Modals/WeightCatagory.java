package com.example.adnanshaukat.myapplication.Modals;

import java.io.Serializable;

/**
 * Created by AdnanShaukat on 02/01/2019.
 */

@SuppressWarnings("serial")
public class WeightCatagory implements Serializable {
    public int weight_id;
    public String weight_catagory;

    public WeightCatagory(int weight_id, String weight_catagory) {
        this.weight_id = weight_id;
        this.weight_catagory = weight_catagory;
    }

    public int getWeight_id() {
        return weight_id;
    }

    public void setWeight_id(int weight_id) {
        this.weight_id = weight_id;
    }

    public String getWeight_catagory() {
        return weight_catagory;
    }

    public void setWeight_catagory(String weight_catagory) {
        this.weight_catagory = weight_catagory;
    }

    @Override
    public String toString() {
        return weight_catagory;
    }
}
