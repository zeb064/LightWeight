package org.example.gimnasioproyect.Utilidades;

public class BarrioData {
    private String nombre;
    private int clientes;
    private double porcentaje;

    public BarrioData(String nombre, int clientes, double porcentaje) {
        this.nombre = nombre;
        this.clientes = clientes;
        this.porcentaje = porcentaje;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getClientes() { return clientes; }
    public void setClientes(int clientes) { this.clientes = clientes; }

    public double getPorcentaje() { return porcentaje; }
    public void setPorcentaje(double porcentaje) { this.porcentaje = porcentaje; }
}
