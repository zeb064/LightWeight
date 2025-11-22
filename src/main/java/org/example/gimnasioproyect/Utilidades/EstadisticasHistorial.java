package org.example.gimnasioproyect.Utilidades;

public class EstadisticasHistorial {
    private final int total;
    private final int exitosos;
    private final int fallidos;
    private final int hoy;

    public EstadisticasHistorial(int total, int exitosos, int fallidos, int hoy) {
        this.total = total;
        this.exitosos = exitosos;
        this.fallidos = fallidos;
        this.hoy = hoy;
    }

    public int getTotal() { return total; }
    public int getExitosos() { return exitosos; }
    public int getFallidos() { return fallidos; }
    public int getHoy() { return hoy; }

}
