package com.example.adnanshaukat.myapplication.Modals;

import java.util.List;

/**
 * Created by AdnanShaukat on 23/12/2018.
 */

public class PreOrderDataWrapperClass {
    public List<Cargo> cargo;
    public List<Container> container;
    public List<MeasurementUnit> measurement_unit;
    public List<Location> source;

    public PreOrderDataWrapperClass(List<Cargo> cargo, List<Container> container, List<MeasurementUnit> measurement_unit, List<Location> source) {
        this.cargo = cargo;
        this.container = container;
        this.measurement_unit = measurement_unit;
        this.source = source;
    }

    public List<Cargo> getCargo() {
        return cargo;
    }

    public void setCargo(List<Cargo> cargo) {
        this.cargo = cargo;
    }

    public List<Container> getContainer() {
        return container;
    }

    public void setContainer(List<Container> container) {
        this.container = container;
    }

    public List<MeasurementUnit> getMeasurement_unit() {
        return measurement_unit;
    }

    public void setMeasurement_unit(List<MeasurementUnit> measurement_unit) {
        this.measurement_unit = measurement_unit;
    }

    public List<Location> getSource() {
        return source;
    }

    public void setSource(List<Location> source) {
        this.source = source;
    }
}
