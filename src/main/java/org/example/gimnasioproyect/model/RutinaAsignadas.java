package org.example.gimnasioproyect.model;

import org.example.gimnasioproyect.Utilidades.EstadoRutina;

import java.time.LocalDate;

public class RutinaAsignadas {
    private Integer idRutinaCliente;
    private Rutinas rutina;
    private Clientes cliente;
    private LocalDate fechaAsignacion;
    private LocalDate fechaFinalizacion;
    private String estado;

    public RutinaAsignadas() {}

    public RutinaAsignadas(Integer idRutinaCliente, Rutinas rutina, Clientes cliente,
                           LocalDate fechaAsignacion, LocalDate fechaFinalizacion,
                           String estado) {
        this.idRutinaCliente = idRutinaCliente;
        this.rutina = rutina;
        this.cliente = cliente;
        this.fechaAsignacion = fechaAsignacion;
        this.fechaFinalizacion = fechaFinalizacion;
        this.estado = estado;
    }

    public Integer getIdRutinaCliente() { return idRutinaCliente; }
    public void setIdRutinaCliente(Integer idRutinaCliente) {
        this.idRutinaCliente = idRutinaCliente;
    }

    public Rutinas getRutina() { return rutina; }
    public void setRutina(Rutinas rutina) { this.rutina = rutina; }

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

    public String getEstado() { return estado;}

    public void setEstado(String estado) { this.estado = estado;}

    public EstadoRutina getEstadoEnum() {
        return EstadoRutina.from(this.estado);
    }

    public void setEstadoEnum(EstadoRutina estado) {
        this.estado = estado.name();
    }

    public boolean estaActiva() {
        return getEstadoEnum() == EstadoRutina.ACTIVA;
    }

    public boolean estaFinalizada() {
        EstadoRutina e = getEstadoEnum();
        return e == EstadoRutina.COMPLETADA || e == EstadoRutina.CANCELADA;
    }
}
