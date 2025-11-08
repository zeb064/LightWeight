package org.example.gimnasioproyect.model;

public class Rutinas {
    private Integer idRutina;
    private String objetivo;

    public Rutinas() {}

    public Rutinas(Integer idRutina, String objetivo) {
        this.idRutina = idRutina;
        this.objetivo = objetivo;
    }

    public Integer getIdRutina() { return idRutina; }
    public void setIdRutina(Integer idRutina) { this.idRutina = idRutina; }

    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
}
