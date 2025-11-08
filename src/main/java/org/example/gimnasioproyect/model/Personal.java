package org.example.gimnasioproyect.model;

import org.example.gimnasioproyect.Utilidades.TipoPersonal;

import java.time.LocalDate;

public abstract class Personal {
    private Integer idPersonal;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String correo;
    private String usuarioSistema;
    private String contrasena;
    private TipoPersonal tipoPersonal;
    private LocalDate fechaContratacion;

    public Personal() {}

    public Personal(Integer idPersonal, String nombres, String apellidos,
                    String telefono, String correo, String usuarioSistema,
                    String contrasena, TipoPersonal tipoPersonal, LocalDate fechaContratacion) {
        this.idPersonal = idPersonal;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.correo = correo;
        this.usuarioSistema = usuarioSistema;
        this.contrasena = contrasena;
        this.tipoPersonal = tipoPersonal;
        this.fechaContratacion = fechaContratacion;
    }

    // Getters y Setters
    public Integer getIdPersonal() { return idPersonal; }
    public void setIdPersonal(Integer idPersonal) { this.idPersonal = idPersonal; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getUsuarioSistema() { return usuarioSistema; }
    public void setUsuarioSistema(String usuarioSistema) { this.usuarioSistema = usuarioSistema; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public TipoPersonal getTipoPersonal() { return tipoPersonal; }
    public void setTipoPersonal(TipoPersonal tipoPersonal) { this.tipoPersonal = tipoPersonal; }

    public LocalDate getFechaContratacion() { return fechaContratacion; }
    public void setFechaContratacion(LocalDate fechaContratacion) {
        this.fechaContratacion = fechaContratacion;
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }
    public String setNombreCompleto(String nombreCompleto) {
        return nombreCompleto;
    };

    public abstract String getDocumento();
}
