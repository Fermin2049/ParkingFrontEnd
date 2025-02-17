package com.fermin2049.parking.data.models;

public class RecuperarPasswordRequest {
    private String email;

    public RecuperarPasswordRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
