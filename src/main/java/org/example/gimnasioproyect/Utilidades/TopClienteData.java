package org.example.gimnasioproyect.Utilidades;

public class TopClienteData {
    private final String posicion;
    private final String documento;
    private final String nombre;
    private final int asistencias;

    public TopClienteData(String posicion, String documento, String nombre, int asistencias) {
        this.posicion = posicion;
        this.documento = documento;
        this.nombre = nombre;
        this.asistencias = asistencias;
    }

    public String getPosicion() { return posicion; }
    public String getDocumento() { return documento; }
    public String getNombre() { return nombre; }
    public int getAsistencias() { return asistencias; }
}
