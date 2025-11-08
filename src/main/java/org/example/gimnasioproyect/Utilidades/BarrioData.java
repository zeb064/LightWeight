package org.example.gimnasioproyect.Utilidades;

public class BarrioData {
    private final String nombre;
    private final int clientes;
    private final double porcentaje;

    public BarrioData(String nombre, int clientes, double porcentaje) {
        this.nombre = nombre;
        this.clientes = clientes;
        this.porcentaje = porcentaje;
    }

    public String getNombre() { return nombre; }
    public int getClientes() { return clientes; }
    public double getPorcentaje() { return porcentaje; }
}
