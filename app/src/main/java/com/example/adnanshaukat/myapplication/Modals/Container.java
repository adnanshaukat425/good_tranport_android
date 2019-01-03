package com.example.adnanshaukat.myapplication.Modals;

import java.io.Serializable;

/**
 * Created by AdnanShaukat on 23/12/2018.
 */
@SuppressWarnings("serial")
public class Container implements Serializable {
    public int container_type_id;
    public String container_type;

    public Container(int container_type_id, String container_type) {
        this.container_type_id = container_type_id;
        this.container_type = container_type;
    }

    public int getContainer_type_id() {
        return container_type_id;
    }

    public void setContainer_type_id(int container_type_id) {
        this.container_type_id = container_type_id;
    }

    public String getContainer_type() {
        return container_type;
    }

    public void setContainer_type(String container_type) {
        this.container_type = container_type;
    }

    @Override
    public String toString() {
        return container_type;
    }
}
