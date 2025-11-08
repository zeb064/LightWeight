package org.example.gimnasioproyect.Utilidades;

public class TopClienteData {
    private String posicion;
    private String documento;
    private String nombre;
    private int asistencias;

    public TopClienteData(String posicion, String documento, String nombre, int asistencias) {
        this.posicion = posicion;
        this.documento = documento;
        this.nombre = nombre;
        this.asistencias = asistencias;
    }

    public String getPosicion() { return posicion; }
    public void setPosicion(String posicion) { this.posicion = posicion; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getAsistencias() { return asistencias; }
    public void setAsistencias(int asistencias) { this.asistencias = asistencias; }
}
