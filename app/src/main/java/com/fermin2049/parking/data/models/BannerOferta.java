package com.fermin2049.parking.data.models;

public class BannerOferta {
    private String titulo;
    private String descripcion;
    private int imagenResId;

    public BannerOferta(String titulo, String descripcion, int imagenResId) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.imagenResId = imagenResId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getImagenResId() {
        return imagenResId;
    }
}
