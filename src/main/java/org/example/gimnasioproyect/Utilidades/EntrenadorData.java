package org.example.gimnasioproyect.Utilidades;

public class EntrenadorData {
    private final String nombre;
    private final int clientes;

    public EntrenadorData(String nombre, int clientes) {
        this.nombre = nombre;
        this.clientes = clientes;
    }

    public String getNombre() { return nombre; }
    public int getClientes() { return clientes; }
}
