package com.fermin2049.parking.data.models;

import java.io.Serializable;
import java.util.Date;

public class EspacioEstacionamiento implements Serializable {
    private int idEspacio;
    private int numeroEspacio;
    private String estado; // Disponible, Ocupado, Reservado, etc.
    private String tipoEspacio; // Estándar, VIP, Moto, etc.
    private String sector; // Sección dentro del estacionamiento
    private Date fechaActualizacion;

    public EspacioEstacionamiento(int idEspacio, int numeroEspacio, String estado, String tipoEspacio, String sector, Date fechaActualizacion) {
        this.idEspacio = idEspacio;
        this.numeroEspacio = numeroEspacio;
        this.estado = estado;
        this.tipoEspacio = tipoEspacio;
        this.sector = sector;
        this.fechaActualizacion = fechaActualizacion;
    }

    public int getIdEspacio() {
        return idEspacio;
    }

    public void setIdEspacio(int idEspacio) {
        this.idEspacio = idEspacio;
    }

    public int getNumeroEspacio() {
        return numeroEspacio;
    }

    public void setNumeroEspacio(int numeroEspacio) {
        this.numeroEspacio = numeroEspacio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipoEspacio() {
        return tipoEspacio;
    }

    public void setTipoEspacio(String tipoEspacio) {
        this.tipoEspacio = tipoEspacio;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Date fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public boolean isDisponible() {
        return "Disponible".equalsIgnoreCase(this.estado);
    }


    @Override
    public String toString() {
        return "EspacioEstacionamiento{" +
                "idEspacio=" + idEspacio +
                ", numeroEspacio=" + numeroEspacio +
                ", estado='" + estado + '\'' +
                ", tipoEspacio='" + tipoEspacio + '\'' +
                ", sector='" + sector + '\'' +
                ", fechaActualizacion=" + fechaActualizacion +
                '}';
    }
}
