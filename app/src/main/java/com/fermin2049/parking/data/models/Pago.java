package com.fermin2049.parking.data.models;

import java.util.Date;

public class Pago {
    private int idPago;
    private int idCliente;
    private int idReserva;
    private double monto;
    private String metodoPago;
    private Date fechaPago;
    private String estado;
    private Cliente idClienteNavigation;
    private Reserva idReservaNavigation;

    public Pago(int idPago, int idCliente, int idReserva, double monto, String metodoPago, Date fechaPago, String estado, Cliente idClienteNavigation, Reserva idReservaNavigation) {
        this.idPago = idPago;
        this.idCliente = idCliente;
        this.idReserva = idReserva;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.fechaPago = fechaPago;
        this.estado = estado;
        this.idClienteNavigation = idClienteNavigation;
        this.idReservaNavigation = idReservaNavigation;
    }

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Cliente getIdClienteNavigation() {
        return idClienteNavigation;
    }

    public void setIdClienteNavigation(Cliente idClienteNavigation) {
        this.idClienteNavigation = idClienteNavigation;
    }

    public Reserva getIdReservaNavigation() {
        return idReservaNavigation;
    }

    public void setIdReservaNavigation(Reserva idReservaNavigation) {
        this.idReservaNavigation = idReservaNavigation;
    }
}
