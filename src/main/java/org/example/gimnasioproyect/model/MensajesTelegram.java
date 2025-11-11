package org.example.gimnasioproyect.model;

public class MensajesTelegram {
    private Integer idMensaje;
    private String tipoMensaje;
    private String contenido;
    private boolean activo;

    public MensajesTelegram() {}

    public MensajesTelegram(Integer idMensaje, String tipoMensaje, String contenido, boolean activo) {
        this.idMensaje = idMensaje;
        this.tipoMensaje = tipoMensaje;
        this.contenido = contenido;
        this.activo = activo;
    }

    // Getters y Setters
    public Integer getIdMensaje() {
        return idMensaje;
    }

    public void setIdMensaje(Integer idMensaje) {
        this.idMensaje = idMensaje;
    }

    public String getTipoMensaje() {
        return tipoMensaje;
    }

    public void setTipoMensaje(String tipoMensaje) {
        this.tipoMensaje = tipoMensaje;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "MensajesTelegram{" +
                "idMensaje=" + idMensaje +
                ", tipoMensaje='" + tipoMensaje + '\'' +
                ", activo=" + activo +
                '}';
    }
}