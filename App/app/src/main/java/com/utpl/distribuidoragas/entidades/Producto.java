package com.utpl.distribuidoragas.entidades;

public class Producto {

    private int id;
    private String producto;
    private String descripcion;
    private String costo;
    private String pvp;
    private String imagen;

    public Producto() {

    }

    public Producto(int id, String producto, String descripcion, String costo, String pvp, String imagen) {
        this.id = id;
        this.producto = producto;
        this.descripcion = descripcion;
        this.costo = costo;
        this.pvp = pvp;
        this.imagen = imagen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCosto() {
        return costo;
    }

    public void setCosto(String costo) {
        this.costo = costo;
    }

    public String getPvp() {
        return pvp;
    }

    public void setPvp(String pvp) {
        this.pvp = pvp;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
