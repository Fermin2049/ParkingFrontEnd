package com.fermin2049.parking.data.models;

public class PaymentResponse {
    private String init_point;

    public PaymentResponse(String init_point) {
        this.init_point = init_point;
    }

    public String getInit_point() {
        return init_point;
    }

    public void setInit_point(String init_point) {
        this.init_point = init_point;
    }
}
