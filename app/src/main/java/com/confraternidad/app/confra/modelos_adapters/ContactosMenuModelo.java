package com.confraternidad.app.confra.modelos_adapters;

public class ContactosMenuModelo {

    private String uid, titulo, uidLider;

    public ContactosMenuModelo(String uid, String titulo, String uidLider) {
        this.uid = uid;
        this.titulo = titulo;
        this.uidLider = uidLider;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getUidLider() {
        return uidLider;
    }

    public void setUidLider(String uidLider) {
        this.uidLider = uidLider;
    }
}
