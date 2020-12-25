package com.mazghul.merabills.models;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;

public class PaymentList implements Serializable {

    @Expose
    private List<Payment> payments;

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

}
