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
    public List<ContainerSize> container_size;
    public List<WeightCatagory> weight_catagory;
    public List<PaymentType> payment_type;

    public PreOrderDataWrapperClass(List<Cargo> cargo, List<Container> container, List<MeasurementUnit> measurement_unit,
                                    List<Location> source, List<ContainerSize> container_size, List<WeightCatagory> weight_catagory,
                                    List<PaymentType> payment_type) {
        this.cargo = cargo;
        this.container = container;
        this.measurement_unit = measurement_unit;
        this.source = source;
        this.container_size = container_size;
        this.weight_catagory = weight_catagory;
        this.payment_type = payment_type;
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

    public List<ContainerSize> getContainer_size() {
        return container_size;
    }

    public void setContainer_size(List<ContainerSize> container_size) {
        this.container_size = container_size;
    }

    public List<WeightCatagory> getWeight_catagory() {
        return weight_catagory;
    }

    public void setWeight_catagory(List<WeightCatagory> weight_catagory) {
        this.weight_catagory = weight_catagory;
    }

    public List<PaymentType> getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(List<PaymentType> payment_type) {
        this.payment_type = payment_type;
    }
}
