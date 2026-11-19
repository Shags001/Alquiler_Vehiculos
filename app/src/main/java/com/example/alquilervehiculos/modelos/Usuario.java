package com.example.alquilervehiculos.modelos;

public class Usuario {
    private int id;
    private String nombreCompleto;
    private String usuario;
    private String clave;
    private String email;
    private String telefono;
    private String rol; // "Administrador" o "Empleado"
    private boolean activo;

    public Usuario() {
        this.activo = true;
        this.rol = "Empleado";
    }

    public Usuario(int id, String nombreCompleto, String usuario, String clave, String email, String telefono, String rol, boolean activo) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.usuario = usuario;
        this.clave = clave;
        this.email = email;
        this.telefono = telefono;
        this.rol = rol;
        this.activo = activo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
