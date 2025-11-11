package org.example.gimnasioproyect.model;
import java.sql.Timestamp;

/**
 * Modelo de la tabla HISTORIAL_MENSAJES_TELEGRAM
 */
public class HistorialMensajeTelegram {

    private int idHistorial;
    private int idMensaje;
    private String documento;
    private String mensajeFinal;
    private Timestamp fechaEnvio;
    private String estado;
    private String chatId;

    // Constructores
    public HistorialMensajeTelegram() {
        this.fechaEnvio = new Timestamp(System.currentTimeMillis());
        this.estado = "ENVIADO";
    }

    public HistorialMensajeTelegram(int idMensaje, String documento, String mensajeFinal, String chatId) {
        this();
        this.idMensaje = idMensaje;
        this.documento = documento;
        this.mensajeFinal = mensajeFinal;
        this.chatId = chatId;
    }

    public HistorialMensajeTelegram(int idHistorial, int idMensaje, String documento,
                                    String mensajeFinal, Timestamp fechaEnvio,
                                    String estado, String chatId) {
        this.idHistorial = idHistorial;
        this.idMensaje = idMensaje;
        this.documento = documento;
        this.mensajeFinal = mensajeFinal;
        this.fechaEnvio = fechaEnvio;
        this.estado = estado;
        this.chatId = chatId;
    }

    // Getters y Setters
    public int getIdHistorial() {
        return idHistorial;
    }

    public void setIdHistorial(int idHistorial) {
        this.idHistorial = idHistorial;
    }

    public int getIdMensaje() {
        return idMensaje;
    }

    public void setIdMensaje(int idMensaje) {
        this.idMensaje = idMensaje;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getMensajeFinal() {
        return mensajeFinal;
    }

    public void setMensajeFinal(String mensajeFinal) {
        this.mensajeFinal = mensajeFinal;
    }

    public Timestamp getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Timestamp fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    // toString
    @Override
    public String toString() {
        return "HistorialMensajeTelegramRepository{" +
                "idHistorial=" + idHistorial +
                ", idMensaje=" + idMensaje +
                ", documento='" + documento + '\'' +
                ", mensajeFinal='" + mensajeFinal + '\'' +
                ", fechaEnvio=" + fechaEnvio +
                ", estado='" + estado + '\'' +
                ", chatId='" + chatId + '\'' +
                '}';
    }
}
