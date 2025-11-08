package org.example.gimnasioproyect.model;

import java.time.LocalDate;

public class MembresiaClientes {
    private Integer idMembresiaCliente;
    private Membresias membresia;  // Composición
    private Clientes cliente;       // Composición
    private LocalDate fechaAsignacion;
    private LocalDate fechaFinalizacion;

    public MembresiaClientes() {}

    public MembresiaClientes(Integer idMembresiaCliente, Membresias membresia,
                            Clientes cliente, LocalDate fechaAsignacion,
                            LocalDate fechaFinalizacion) {
        this.idMembresiaCliente = idMembresiaCliente;
        this.membresia = membresia;
        this.cliente = cliente;
        this.fechaAsignacion = fechaAsignacion;
        this.fechaFinalizacion = fechaFinalizacion;
    }

    // Getters y Setters
    public Integer getIdMembresiaCliente() { return idMembresiaCliente; }
    public void setIdMembresiaCliente(Integer idMembresiaCliente) {
        this.idMembresiaCliente = idMembresiaCliente;
    }

    public Membresias getMembresia() { return membresia; }
    public void setMembresia(Membresias membresia) { this.membresia = membresia; }

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

    public boolean estaVencida() {
        return fechaFinalizacion != null && fechaFinalizacion.isBefore(LocalDate.now());
    }
}
