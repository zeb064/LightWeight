package org.example.gimnasioproyect.model;

public class Barrios {
    private Integer idBarrio;
    private String nombreBarrio;

    public Barrios() {}

    public Barrios(Integer idBarrio, String nombreBarrio) {
        this.idBarrio = idBarrio;
        this.nombreBarrio = nombreBarrio;
    }

    public Integer getIdBarrio() {
        return idBarrio;
    }

    public void setIdBarrio(Integer idBarrio) {
        this.idBarrio = idBarrio;
    }

    public String getNombreBarrio() {
        return nombreBarrio;
    }

    public void setNombreBarrio(String nombreBarrio) {
        this.nombreBarrio = nombreBarrio;
    }
}
