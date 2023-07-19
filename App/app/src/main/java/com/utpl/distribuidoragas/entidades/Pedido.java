package com.utpl.distribuidoragas.entidades;

public class Pedido {

    private int id;
    private String idCliente;
    private String idRepartidor;
    private String fecha;
    private String hora;
    private String total;
    private String estado;
    private String nombreProducto;
    private String descripcion;
    private String imagen;

    public Pedido() {
    }

    public Pedido(int id, String idCliente, String idRepartidor, String fecha, String hora, String total, String estado, String nombreProducto, String descripcion, String imagen) {
        this.id = id;
        this.idCliente = idCliente;
        this.idRepartidor = idRepartidor;
        this.fecha = fecha;
        this.hora = hora;
        this.total = total;
        this.estado = estado;
        this.nombreProducto = nombreProducto;
        this.descripcion = descripcion;
        this.imagen = imagen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdRepartidor() {
        return idRepartidor;
    }

    public void setIdRepartidor(String idRepartidor) {
        this.idRepartidor = idRepartidor;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
