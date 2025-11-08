package org.example.gimnasioproyect.model;

import org.example.gimnasioproyect.Utilidades.TipoPersonal;

import java.time.LocalDate;

public class Entrenadores extends Personal {
    private String docuEntrenador;
    private String especialidad;
    private Integer experiencia;

    public Entrenadores() {}

    public Entrenadores(Integer idPersonal, String nombres, String apellidos,
                      String telefono, String correo, String usuarioSistema,
                      String contrasena, LocalDate fechaContratacion,
                      String docuEntrenador, String especialidad, Integer experiencia) {
        super(idPersonal, nombres, apellidos, telefono, correo,
                usuarioSistema, contrasena, TipoPersonal.ENTRENADOR, fechaContratacion);
        this.docuEntrenador = docuEntrenador;
        this.especialidad = especialidad;
        this.experiencia = experiencia;
    }

    public String getDocuEntrenador() { return docuEntrenador; }
    public void setDocuEntrenador(String docuEntrenador) {
        this.docuEntrenador = docuEntrenador;
    }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public Integer getExperiencia() { return experiencia; }
    public void setExperiencia(Integer experiencia) { this.experiencia = experiencia; }

    @Override
    public String getDocumento() {
        return docuEntrenador;
    }
}
