package org.example.gimnasioproyect.model;

import org.example.gimnasioproyect.Utilidades.TipoPersonal;

import java.time.LocalDate;

public class Administradores extends Personal {
    private String docuAdministrador;
    private String cargo;

    public Administradores() {}

    public Administradores(Integer idPersonal, String nombres, String apellidos,
                         String telefono, String correo, String usuarioSistema,
                         String contrasena, LocalDate fechaContratacion,
                         String docuAdministrador, String cargo) {
        super(idPersonal, nombres, apellidos, telefono, correo,
                usuarioSistema, contrasena, TipoPersonal.ADMINISTRADOR, fechaContratacion);
        this.docuAdministrador = docuAdministrador;
        this.cargo = cargo;
    }

    public String getDocuAdministrador() { return docuAdministrador; }
    public void setDocuAdministrador(String docuAdministrador) {
        this.docuAdministrador = docuAdministrador;
    }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    @Override
    public String getDocumento() {
        return docuAdministrador;
    }
}
