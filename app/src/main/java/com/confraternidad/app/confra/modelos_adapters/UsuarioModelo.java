package com.confraternidad.app.confra.modelos_adapters;

public class UsuarioModelo {

    private String password, avatar, nombre, apellido, fullName, descripcionPersonal,
            direccion, telefono, correo, fechaNac, genero, profesion,
            iglesiaSucursal, discipulados, ministerios, grupos, edad, isAdmin;


    public boolean isAdmin(String userisAdmin){
        if(userisAdmin == "si")     return true;
        return false;
    }


    public UsuarioModelo() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescripcionPersonal() {
        return descripcionPersonal;
    }

    public void setDescripcionPersonal(String descripcionPersonal) {
        this.descripcionPersonal = descripcionPersonal;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(String fechaNac) {
        this.fechaNac = fechaNac;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getProfesion() {
        return profesion;
    }

    public void setProfesion(String profesion) {
        this.profesion = profesion;
    }

    public String getIglesiaSucursal() {
        return iglesiaSucursal;
    }

    public void setIglesiaSucursal(String iglesiaSucursal) {
        this.iglesiaSucursal = iglesiaSucursal;
    }

    public String getDiscipulados() {
        return discipulados;
    }

    public void setDiscipulados(String discipulados) {
        this.discipulados = discipulados;
    }

    public String getMinisterios() {
        return ministerios;
    }

    public void setMinisterios(String ministerios) {
        this.ministerios = ministerios;
    }

    public String getGrupos() {
        return grupos;
    }

    public void setGrupos(String grupos) {
        this.grupos = grupos;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }
}
