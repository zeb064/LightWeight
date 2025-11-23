package org.example.gimnasioproyect.services.bot.comandos;

import org.example.gimnasioproyect.model.AsignacionEntrenadores;
import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.model.Entrenadores;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Comando /mientrenador - Muestra información del entrenador asignado.
 *
 * Muestra:
 * - Nombre completo del entrenador
 * - Especialidad
 * - Años de experiencia
 * - Información de contacto (teléfono y correo)
 * - Fecha de asignación
 */
public class ComandoMiEntrenador extends ComandoBotBase {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public String getNombreComando() {
        return "mientrenador";
    }

    @Override
    public String getDescripcion() {
        return "Ver información de tu entrenador";
    }

    @Override
    public void ejecutar(Update update, ComandoContext context) throws TelegramApiException {
        long chatId = getChatId(update);

        try {
            // Obtener el cliente por chatId
            Optional<Clientes> clienteOpt = context.getClienteRepository()
                    .findByChatId(String.valueOf(chatId));

            if (!clienteOpt.isPresent()) {
                enviarMensajeNoRegistrado(context, chatId);
                return;
            }

            Clientes cliente = clienteOpt.get();

            // Obtener asignación de entrenador activa
            Optional<AsignacionEntrenadores> asignacionOpt = context.getEntrenadorService()
                    .obtenerEntrenadorDeCliente(cliente.getDocumento());

            if (!asignacionOpt.isPresent()) {
                enviarMensajeSinEntrenador(context, chatId);
                return;
            }

            AsignacionEntrenadores asignacion = asignacionOpt.get();
            Entrenadores entrenador = asignacion.getEntrenador();

            // Construir y enviar mensaje
            String mensaje = construirMensajeEntrenador(entrenador, asignacion);
            enviarMensaje(context, chatId, mensaje);

            System.out.println("Comando /mientrenador ejecutado por: " + cliente.getNombreCompleto());

        } catch (SQLException e) {
            System.err.println("Error de BD en /mientrenador: " + e.getMessage());
            e.printStackTrace();
            enviarMensajeError(context, chatId);
        }
    }

    /**
     * Construye el mensaje con información del entrenador.
     */
    private String construirMensajeEntrenador(Entrenadores entrenador,
                                              AsignacionEntrenadores asignacion) {
        StringBuilder mensaje = new StringBuilder();

        // Encabezado
        mensaje.append("👨‍🏫 *TU ENTRENADOR*\n\n");

        // Información personal
        mensaje.append("📝 *Nombre:* ").append(entrenador.getNombreCompleto()).append("\n");
        mensaje.append("🎯 *Especialidad:* ").append(entrenador.getEspecialidad()).append("\n");

        // Experiencia
        int experiencia = entrenador.getExperiencia() != null ? entrenador.getExperiencia() : 0;
        String textoExperiencia = experiencia == 1 ? "año" : "años";
        mensaje.append("⭐ *Experiencia:* ").append(experiencia).append(" ")
                .append(textoExperiencia).append("\n\n");

        // Separador
        mensaje.append("━━━━━━━━━━━━━━━━━\n\n");

        // Información de contacto
        mensaje.append("📞 *CONTACTO*\n\n");

        if (entrenador.getTelefono() != null && !entrenador.getTelefono().trim().isEmpty()) {
            mensaje.append("📱 *Teléfono:* ").append(entrenador.getTelefono()).append("\n");
        }

        if (entrenador.getCorreo() != null && !entrenador.getCorreo().trim().isEmpty()) {
            mensaje.append("📧 *Correo:* ").append(entrenador.getCorreo()).append("\n");
        }

        mensaje.append("\n");

        // Información de asignación
        mensaje.append("📅 *Asignado desde:* ")
                .append(asignacion.getFechaAsignacion().format(FORMATO_FECHA)).append("\n\n");

        // Pie de mensaje
        mensaje.append("━━━━━━━━━━━━━━━━━\n");
        mensaje.append("💡 *Tip:* Consulta con tu entrenador sobre tu progreso y objetivos.");

        return mensaje.toString();
    }

    /**
     * Mensaje cuando el usuario no está registrado.
     */
    private void enviarMensajeNoRegistrado(ComandoContext context, long chatId)
            throws TelegramApiException {
        String mensaje =
                "⚠️ No estás registrado en el sistema.\n\n" +
                        "Usa `/registrar TU_DOCUMENTO` para vincular tu cuenta.";

        enviarMensaje(context, chatId, mensaje);
    }

    /**
     * Mensaje cuando el cliente no tiene entrenador asignado.
     */
    private void enviarMensajeSinEntrenador(ComandoContext context, long chatId)
            throws TelegramApiException {
        String mensaje =
                "📭 No tienes un entrenador asignado.\n\n" +
                        "Visita recepción para solicitar la asignación de un entrenador personalizado. 💪";

        enviarMensaje(context, chatId, mensaje);
    }

    /**
     * Mensaje de error genérico.
     */
    private void enviarMensajeError(ComandoContext context, long chatId)
            throws TelegramApiException {
        String mensaje =
                "❌ Ocurrió un error al consultar tu entrenador.\n\n" +
                        "Por favor, intenta nuevamente más tarde.";

        enviarMensaje(context, chatId, mensaje);
    }

    @Override
    public boolean requiereRegistro() {
        return true;
    }
}