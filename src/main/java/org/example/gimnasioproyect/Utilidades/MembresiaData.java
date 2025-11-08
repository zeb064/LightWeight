package org.example.gimnasioproyect.Utilidades;

public class MembresiaData {
    private String tipo;
    private int cantidad;
    private double ingresos;
    private double porcentaje;

    public MembresiaData(String tipo, int cantidad, double ingresos, double porcentaje) {
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.ingresos = ingresos;
        this.porcentaje = porcentaje;
    }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getIngresos() { return ingresos; }
    public void setIngresos(double ingresos) { this.ingresos = ingresos; }

    public double getPorcentaje() { return porcentaje; }
    public void setPorcentaje(double porcentaje) { this.porcentaje = porcentaje; }
}
