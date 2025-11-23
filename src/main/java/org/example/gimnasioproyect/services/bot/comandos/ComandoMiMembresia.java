package org.example.gimnasioproyect.services.bot.comandos;

import org.example.gimnasioproyect.Utilidades.CalculadoraFechas;
import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.model.MembresiaClientes;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Comando /mimembresia - Muestra información de la membresía del cliente.
 *
 * Muestra:
 * - Tipo de membresía
 * - Precio
 * - Fecha de inicio
 * - Fecha de vencimiento
 * - Días restantes
 * - Estado (activa/vencida)
 */
public class ComandoMiMembresia extends ComandoBotBase {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public String getNombreComando() {
        return "mimembresia";
    }

    @Override
    public String getDescripcion() {
        return "Ver el estado de tu membresía";
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

            // Obtener membresía activa
            Optional<MembresiaClientes> membresiaOpt = context.getMembresiaClienteService()
                    .obtenerMembresiaActiva(cliente.getDocumento());

            if (!membresiaOpt.isPresent()) {
                enviarMensajeSinMembresia(context, chatId);
                return;
            }

            MembresiaClientes membresia = membresiaOpt.get();

            // Construir y enviar mensaje
            String mensaje = construirMensajeMembresia(membresia);
            enviarMensaje(context, chatId, mensaje);

            System.out.println("Comando /mimembresia ejecutado por: " + cliente.getNombreCompleto());

        } catch (SQLException e) {
            System.err.println("Error de BD en /mimembresia: " + e.getMessage());
            enviarMensajeError(context, chatId);
        }
    }

    /**
     * Construye el mensaje con la información de la membresía.
     */
    private String construirMensajeMembresia(MembresiaClientes membresia) {
        StringBuilder mensaje = new StringBuilder();

        // Encabezado
        mensaje.append("💳 *TU MEMBRESÍA*\n\n");

        // Tipo y precio
        mensaje.append("📌 *Tipo:* ").append(membresia.getMembresia().getTipoMembresia()).append("\n");
        mensaje.append("💰 *Precio:* $").append(String.format("%.2f", membresia.getMembresia().getPrecioMembresia())).append("\n\n");

        // Fechas
        mensaje.append("📅 *Fecha de inicio:* ")
                .append(membresia.getFechaAsignacion().format(FORMATO_FECHA)).append("\n");
        mensaje.append("📅 *Fecha de vencimiento:* ")
                .append(membresia.getFechaFinalizacion().format(FORMATO_FECHA)).append("\n\n");

        // Calcular días restantes
        long diasRestantes = CalculadoraFechas.calcularDiasRestantes(membresia.getFechaFinalizacion());

        // Estado y días restantes
        if (diasRestantes > 0) {
            mensaje.append("✅ *Estado:* Activa\n");
            mensaje.append("⏳ *Días restantes:* ").append(diasRestantes).append(" días\n\n");

            // Advertencia si está próxima a vencer
            if (diasRestantes <= 7) {
                mensaje.append("⚠️ *¡Tu membresía vence pronto!*\n");
                mensaje.append("Renueva en recepción para seguir entrenando.\n");
            }
        } else if (diasRestantes == 0) {
            mensaje.append("⏰ *Estado:* Vence hoy\n\n");
            mensaje.append("⚠️ *¡Tu membresía vence hoy!*\n");
            mensaje.append("Renueva en recepción.\n");
        } else {
            mensaje.append("❌ *Estado:* Vencida\n");
            mensaje.append("⏳ *Días vencida:* ").append(Math.abs(diasRestantes)).append(" días\n\n");
            mensaje.append("⚠️ *Tu membresía ha vencido.*\n");
            mensaje.append("Visita recepción para renovar.\n");
        }

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
     * Mensaje cuando el cliente no tiene membresía activa.
     */
    private void enviarMensajeSinMembresia(ComandoContext context, long chatId)
            throws TelegramApiException {
        String mensaje =
                "📭 No tienes una membresía activa.\n\n" +
                        "Visita recepción para adquirir tu membresía y comenzar a entrenar. 💪";

        enviarMensaje(context, chatId, mensaje);
    }

    /**
     * Mensaje de error genérico.
     */
    private void enviarMensajeError(ComandoContext context, long chatId)
            throws TelegramApiException {
        String mensaje =
                "❌ Ocurrió un error al consultar tu membresía.\n\n" +
                        "Por favor, intenta nuevamente más tarde.";

        enviarMensaje(context, chatId, mensaje);
    }

    @Override
    public boolean requiereRegistro() {
        return true; // Este comando SÍ requiere registro
    }
}