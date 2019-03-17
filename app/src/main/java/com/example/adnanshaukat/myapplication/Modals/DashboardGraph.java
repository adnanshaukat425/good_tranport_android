package com.example.adnanshaukat.myapplication.Modals;

import java.util.List;

/**
 * Created by AdnanShaukat on 10/03/2019.
 */

public class DashboardGraph {
    public List<Object> x_axis;
    public List<Object> y_axis;

    public DashboardGraph(List<Object> x_axis, List<Object> y_axis) {
        this.x_axis = x_axis;
        this.y_axis = y_axis;
    }

    public List<Object> getX_axis() {
        return x_axis;
    }

    public void setX_axis(List<Object> x_axis) {
        this.x_axis = x_axis;
    }

    public List<Object> getY_axis() {
        return y_axis;
    }

    public void setY_axis(List<Object> y_axis) {
        this.y_axis = y_axis;
    }
}
