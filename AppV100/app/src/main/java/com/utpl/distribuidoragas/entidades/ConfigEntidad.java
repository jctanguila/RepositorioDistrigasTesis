package com.utpl.distribuidoragas.entidades;

public class ConfigEntidad {

    private String id;
    private String Nombres;
    private String Estados;

    public ConfigEntidad(String id, String nombres, String estados) {
        this.id = id;
        Nombres = nombres;
        Estados = estados;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombres() {
        return Nombres;
    }

    public void setNombres(String nombres) {
        Nombres = nombres;
    }

    public String getEstados() {
        return Estados;
    }

    public void setEstados(String estados) {
        Estados = estados;
    }
}
