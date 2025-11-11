package org.example.gimnasioproyect.model;
import java.sql.Timestamp;

public class HistorialMensajeTelegram {

    private int idHistorial;
    private MensajesTelegram mensajesTelegram;
    private Clientes cliente;
    private String mensajeFinal;
    private Timestamp fechaEnvio;
    private String estado;
    private String chatId;

    // Constructores
    public HistorialMensajeTelegram() {
        this.fechaEnvio = new Timestamp(System.currentTimeMillis());
        this.estado = "ENVIADO";
    }

    public HistorialMensajeTelegram(MensajesTelegram idMensaje, Clientes documento, String mensajeFinal, String chatId) {
        this();
        this.mensajesTelegram = idMensaje;
        this.cliente = documento;
        this.mensajeFinal = mensajeFinal;
        this.chatId = chatId;
    }

    public HistorialMensajeTelegram(int idHistorial, MensajesTelegram idMensaje, Clientes documento,
                                    String mensajeFinal, Timestamp fechaEnvio,
                                    String estado, String chatId) {
        this.idHistorial = idHistorial;
        this.mensajesTelegram = idMensaje;
        this.cliente = documento;
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

    public MensajesTelegram getMensaje() {
        return mensajesTelegram;
    }

    public void setMensaje(MensajesTelegram idMensaje) {
        this.mensajesTelegram = idMensaje;
    }

    public Clientes getClientes() {
        return cliente;
    }

    public void setClientes(Clientes cliente) {
        this.cliente = cliente;
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
                ", mensajesTelegram=" + mensajesTelegram +
                ", documento='" + cliente + '\'' +
                ", mensajeFinal='" + mensajeFinal + '\'' +
                ", fechaEnvio=" + fechaEnvio +
                ", estado='" + estado + '\'' +
                ", chatId='" + chatId + '\'' +
                '}';
    }
}
