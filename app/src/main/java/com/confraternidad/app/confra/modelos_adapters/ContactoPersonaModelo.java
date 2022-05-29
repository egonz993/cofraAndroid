package com.confraternidad.app.confra.modelos_adapters;

import com.google.firebase.storage.StorageReference;

public class ContactoPersonaModelo {

    private String uid, nombre;
    private StorageReference avatar;

    public ContactoPersonaModelo(String uid, StorageReference avatar, String nombre) {
        this.uid = uid;
        this.avatar = avatar;
        this.nombre = nombre;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public StorageReference getAvatar() {
        return avatar;
    }

    public void setAvatar(StorageReference avatar) {
        this.avatar = avatar;
    }
}
