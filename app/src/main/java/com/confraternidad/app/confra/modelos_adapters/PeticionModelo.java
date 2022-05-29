package com.confraternidad.app.confra.modelos_adapters;

import com.google.firebase.storage.StorageReference;

public class PeticionModelo {

    private String uidContainer, uidPersona, uidDestino, nombre, fecha, leido, titulo, peticion;
    private StorageReference avatar;

    public PeticionModelo(String uidContainer, String uidPersona, String uidDestino, String nombre, String fecha, String leido, String titulo, String peticion, StorageReference avatar) {
        this.uidContainer = uidContainer;
        this.uidPersona = uidPersona;
        this.uidDestino = uidDestino;
        this.nombre = nombre;
        this.fecha = fecha;
        this.leido = leido;
        this.titulo = titulo;
        this.peticion = peticion;
        this.avatar = avatar;
    }

    public String getUidContainer() {
        return uidContainer;
    }

    public void setUidContainer(String uidContainer) {
        this.uidContainer = uidContainer;
    }

    public String getUidPersona() {
        return uidPersona;
    }

    public void setUidPersona(String uidPersona) {
        this.uidPersona = uidPersona;
    }

    public String getUidDestino() {
        return uidDestino;
    }

    public void setUidDestino(String uidDestino) {
        this.uidDestino = uidDestino;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getLeido() {
        return leido;
    }

    public void setLeido(String leido) {
        this.leido = leido;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPeticion() {
        return peticion;
    }

    public void setPeticion(String peticion) {
        this.peticion = peticion;
    }

    public StorageReference getAvatar() {
        return avatar;
    }

    public void setAvatar(StorageReference avatar) {
        this.avatar = avatar;
    }
}
