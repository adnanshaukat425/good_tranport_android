package com.example.adnanshaukat.myapplication.Modals;

import java.io.Serializable;

/**
 * Created by AdnanShaukat on 03/01/2019.
 */
@SuppressWarnings("serial")
public class PaymentType implements Serializable {
    public int payment_type_id;
    public String payment_type;

    public PaymentType(int payment_type_id, String payment_type) {
        this.payment_type_id = payment_type_id;
        this.payment_type = payment_type;
    }

    public int getPayment_type_id() {
        return payment_type_id;
    }

    public void setPayment_type_id(int payment_type_id) {
        this.payment_type_id = payment_type_id;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    @Override
    public String toString() {
        return payment_type;
    }
}
