package org.example.gimnasioproyect.Utilidades;

public class MembresiaData {
    private final String tipo;
    private final int cantidad;
    private final double ingresos;
    private final double porcentaje;

    public MembresiaData(String tipo, int cantidad, double ingresos, double porcentaje) {
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.ingresos = ingresos;
        this.porcentaje = porcentaje;
    }

    public String getTipo() { return tipo; }
    public int getCantidad() { return cantidad; }
    public double getIngresos() { return ingresos; }
    public double getPorcentaje() { return porcentaje; }
}
