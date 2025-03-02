package com.fermin2049.parking.data.models;

import java.util.Date;
import java.util.List;

public class Reserva {
    private int idReserva;
    private int idCliente;
    private int idEspacio;
    private Date fechaReserva;
    private Date fechaExpiracion;
    private String estado;
    private Cliente idClienteNavigation;
    private EspacioEstacionamiento idEspacioNavigation;
    private List<Pago> pagos;

    // Getters and Setters
    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdEspacio() {
        return idEspacio;
    }

    public void setIdEspacio(int idEspacio) {
        this.idEspacio = idEspacio;
    }

    public Date getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(Date fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public Date getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(Date fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
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

    public EspacioEstacionamiento getIdEspacioNavigation() {
        return idEspacioNavigation;
    }

    public void setIdEspacioNavigation(EspacioEstacionamiento idEspacioNavigation) {
        this.idEspacioNavigation = idEspacioNavigation;
    }

    public List<Pago> getPagos() {
        return pagos;
    }

    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }
}