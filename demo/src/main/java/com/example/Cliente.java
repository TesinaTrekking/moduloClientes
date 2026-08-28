package com.example;

import java.time.LocalDate;

public class Cliente {
    private int id;
    private String dni;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private String email;
    private String telefono;
    private String sexo;
    private String contactoEmergenciaNombre;
    private String contactoEmergenciaTelefono;
    private String contactoEmergenciaRelacion;
    private boolean activo;
    private LocalDate fechaAlta;
    private LocalDate fechaModificacion;

    public Cliente(int id, String dni, String nombre, String apellido, LocalDate fechaNacimiento, String email,
            String telefono, String sexo, String contactoEmergenciaNombre, String contactoEmergenciaTelefono,
            String contactoEmergenciaRelacion, boolean activo, LocalDate fechaAlta, LocalDate fechaModificacion) {
        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.email = email;
        this.telefono = telefono;
        this.sexo = sexo;
        this.contactoEmergenciaNombre = contactoEmergenciaNombre;
        this.contactoEmergenciaTelefono = contactoEmergenciaTelefono;
        this.contactoEmergenciaRelacion = contactoEmergenciaRelacion;
        this.activo = activo;
        this.fechaAlta = fechaAlta;
        this.fechaModificacion = fechaModificacion;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDni() {
        return dni;
    }

    public String getApellido() {
        return apellido;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getSexo() {
        return sexo;
    }

    public String getContactoEmergenciaNombre() {
        return contactoEmergenciaNombre;
    }

    public String getContactoEmergenciaTelefono() {
        return contactoEmergenciaTelefono;
    }

    public String getContactoEmergenciaRelacion() {
        return contactoEmergenciaRelacion;
    }

    public boolean isActivo() {
        return activo;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public LocalDate getFechaModificacion() {
        return fechaModificacion;
    }
}