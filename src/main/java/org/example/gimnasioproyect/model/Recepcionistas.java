package org.example.gimnasioproyect.model;

import org.example.gimnasioproyect.Utilidades.TipoPersonal;

import java.time.LocalDate;

public class Recepcionistas extends Personal {
    private String docuRecepcionista;
    private String horarioTurno;

    public Recepcionistas() {}

    public Recepcionistas(Integer idPersonal, String nombres, String apellidos,
                         String telefono, String correo, String usuarioSistema,
                         String contrasena, LocalDate fechaContratacion,
                         String docuRecepcionista, String horarioTurno) {
        super(idPersonal, nombres, apellidos, telefono, correo,
                usuarioSistema, contrasena, TipoPersonal.RECEPCIONISTA, fechaContratacion);
        this.docuRecepcionista = docuRecepcionista;
        this.horarioTurno = horarioTurno;
    }

    public String getDocuRecepcionista() { return docuRecepcionista; }
    public void setDocuRecepcionista(String docuRecepcionista) {
        this.docuRecepcionista = docuRecepcionista;
    }

    public String getHorarioTurno() { return horarioTurno; }
    public void setHorarioTurno(String horarioTurno) { this.horarioTurno = horarioTurno; }

    @Override
    public String getDocumento() {
        return docuRecepcionista;
    }
}
