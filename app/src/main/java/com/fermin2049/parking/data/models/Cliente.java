package com.fermin2049.parking.data.models;

import java.util.Date;
import java.util.List;

public class Cliente {
    private int idCliente;
    private String nombre;
    private String telefono;
    private String email;
    private String vehiculoPlaca;
    private Date fechaRegistro;
    private String password;
    private String resetToken;
    private Date resetTokenExpiry;
    private List<Pago> pagos;
    private List<Reserva> reservas;

    public Cliente(int idCliente, String nombre, String telefono, String email, String vehiculoPlaca, Date fechaRegistro, String password, String resetToken, Date resetTokenExpiry, List<Pago> pagos, List<Reserva> reservas) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.vehiculoPlaca = vehiculoPlaca;
        this.fechaRegistro = fechaRegistro;
        this.password = password;
        this.resetToken = resetToken;
        this.resetTokenExpiry = resetTokenExpiry;
        this.pagos = pagos;
        this.reservas = reservas;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
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

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public Date getResetTokenExpiry() {
        return resetTokenExpiry;
    }

    public void setResetTokenExpiry(Date resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }

    public List<Pago> getPagos() {
        return pagos;
    }

    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }
}
