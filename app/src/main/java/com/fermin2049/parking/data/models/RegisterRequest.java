package com.fermin2049.parking.data.models;

public class RegisterRequest {

    private String nombre;
    private String telefono;
    private String email;
    private String vehiculoPlaca;
    private String password;

    public RegisterRequest(String nombre, String telefono, String email, String vehiculoPlaca, String password) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.vehiculoPlaca = vehiculoPlaca;
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVehiculoPlaca() {
        return vehiculoPlaca;
    }

    public void setVehiculoPlaca(String vehiculoPlaca) {
        this.vehiculoPlaca = vehiculoPlaca;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
