package org.example.gimnasioproyect.Utilidades;

public class EntrenadorData {
    private String nombre;
    private int clientes;

    public EntrenadorData(String nombre, int clientes) {
        this.nombre = nombre;
        this.clientes = clientes;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getClientes() {
        return clientes;
    }

    public void setClientes(int clientes) {
        this.clientes = clientes;
    }
}
