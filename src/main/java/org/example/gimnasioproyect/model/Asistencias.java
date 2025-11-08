package org.example.gimnasioproyect.model;

import java.time.LocalDate;

public class Asistencias {
    private Integer idAsistencia;
    private LocalDate fecha;
    private Clientes cliente;  // Composición

    public Asistencias() {}

    public Asistencias(Integer idAsistencia, LocalDate fecha, Clientes cliente) {
        this.idAsistencia = idAsistencia;
        this.fecha = fecha;
        this.cliente = cliente;
    }

    // Getters y Setters
    public Integer getIdAsistencia() { return idAsistencia; }
    public void setIdAsistencia(Integer idAsistencia) { this.idAsistencia = idAsistencia; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Clientes getCliente() { return cliente; }
    public void setCliente(Clientes cliente) { this.cliente = cliente; }

    public boolean esHoy() {
        return fecha != null && fecha.equals(LocalDate.now());
    }
}
