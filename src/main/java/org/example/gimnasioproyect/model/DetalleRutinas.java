package org.example.gimnasioproyect.model;

public class DetalleRutinas {
    private Integer idDetalle;
    private String diaSemana;
    private Integer orden;
    private String ejercicio;
    private Integer series;
    private Integer repeticiones;
    private Double peso;
    private String notas;
    private Rutinas rutina;

    public DetalleRutinas() {}

    public DetalleRutinas(Integer idDetalle, String diaSemana, Integer orden,
                         String ejercicio, Integer series, Integer repeticiones,
                         Double peso, String notas, Rutinas rutina) {
        this.idDetalle = idDetalle;
        this.diaSemana = diaSemana;
        this.orden = orden;
        this.ejercicio = ejercicio;
        this.series = series;
        this.repeticiones = repeticiones;
        this.peso = peso;
        this.notas = notas;
        this.rutina = rutina;
    }

    public Integer getIdDetalle() { return idDetalle; }
    public void setIdDetalle(Integer idDetalle) { this.idDetalle = idDetalle; }

    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }

    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }

    public String getEjercicio() { return ejercicio; }
    public void setEjercicio(String ejercicio) { this.ejercicio = ejercicio; }

    public Integer getSeries() { return series; }
    public void setSeries(Integer series) { this.series = series; }

    public Integer getRepeticiones() { return repeticiones; }
    public void setRepeticiones(Integer repeticiones) { this.repeticiones = repeticiones; }

    public Double getPeso() { return peso; }
    public void setPeso(Double peso) { this.peso = peso; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public Rutinas getRutina() { return rutina; }
    public void setRutina(Rutinas rutina) { this.rutina = rutina; }
}
