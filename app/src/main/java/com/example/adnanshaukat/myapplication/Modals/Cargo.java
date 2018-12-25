package com.example.adnanshaukat.myapplication.Modals;

/**
 * Created by AdnanShaukat on 01/12/2018.
 */

public class Cargo {
    public int cargo_type_id;
    public String cargo_type;

    public Cargo(int cargo_type_id, String cargo_type) {
        this.cargo_type_id = cargo_type_id;
        this.cargo_type = cargo_type;
    }

    public int getCargo_type_id() {
        return cargo_type_id;
    }

    public void setCargo_type_id(int cargo_type_id) {
        this.cargo_type_id = cargo_type_id;
    }

    public String getCargo_type() {
        return cargo_type;
    }

    public void setCargo_type(String cargo_type) {
        this.cargo_type = cargo_type;
    }

    @Override
    public String toString() {
        return cargo_type;
    }
}
