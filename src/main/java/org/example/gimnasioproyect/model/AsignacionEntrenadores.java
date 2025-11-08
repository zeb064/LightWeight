package org.example.gimnasioproyect.model;

import java.time.LocalDate;

public class AsignacionEntrenadores {
    private Integer idEntrenadorCliente;
    private Entrenadores entrenador;  // Composición
    private Clientes cliente;         // Composición
    private LocalDate fechaAsignacion;
    private LocalDate fechaFinalizacion;

    public AsignacionEntrenadores() {}

    public AsignacionEntrenadores(Integer idEntrenadorCliente, Entrenadores entrenador,
                                  Clientes cliente, LocalDate fechaAsignacion,
                                  LocalDate fechaFinalizacion) {
        this.idEntrenadorCliente = idEntrenadorCliente;
        this.entrenador = entrenador;
        this.cliente = cliente;
        this.fechaAsignacion = fechaAsignacion;
        this.fechaFinalizacion = fechaFinalizacion;
    }

    // Getters y Setters
    public Integer getIdEntrenadorCliente() { return idEntrenadorCliente; }
    public void setIdEntrenadorCliente(Integer idEntrenadorCliente) {
        this.idEntrenadorCliente = idEntrenadorCliente;
    }

    public Entrenadores getEntrenador() { return entrenador; }
    public void setEntrenador(Entrenadores entrenador) { this.entrenador = entrenador; }

    public Clientes getCliente() { return cliente; }
    public void setCliente(Clientes cliente) { this.cliente = cliente; }

    public LocalDate getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(LocalDate fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public LocalDate getFechaFinalizacion() { return fechaFinalizacion; }
    public void setFechaFinalizacion(LocalDate fechaFinalizacion) {
        this.fechaFinalizacion = fechaFinalizacion;
    }

    public boolean estaActiva() {
        return fechaFinalizacion == null || fechaFinalizacion.isAfter(LocalDate.now());
    }
}
