package com.fermin2049.parking.data.models;

public class Payer {
    private String email;

    public Payer(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
