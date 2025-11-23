package org.example.gimnasioproyect.services.bot.comandos;

import org.example.gimnasioproyect.model.Asistencias;
import org.example.gimnasioproyect.model.Clientes;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Comando /misasistencias - Muestra el historial de asistencias del cliente.
 *
 * Muestra:
 * - Total de asistencias del año actual
 * - Total de asistencias del mes actual
 * - Listado de asistencias del mes (últimas 10)
 * - Promedio mensual
 * - Racha actual
 */
public class ComandoMisAsistencias extends ComandoBotBase {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_MES = DateTimeFormatter.ofPattern("MMMM", new Locale("es", "ES"));

    @Override
    public String getNombreComando() {
        return "misasistencias";
    }

    @Override
    public String getDescripcion() {
        return "Ver tu historial de asistencias";
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
            String documento = cliente.getDocumento();

            // Obtener estadísticas
            int asistenciasMes = context.getEstadisticaService()
                    .contarAsistenciasMesActual(documento);

            int asistenciasTotal = context.getEstadisticaService()
                    .contarAsistenciasCliente(documento);

            boolean asistioHoy = context.getEstadisticaService()
                    .asistioHoy(documento);

            // Obtener asistencias del mes actual para mostrar el listado
            LocalDate hoy = LocalDate.now();
            LocalDate inicioMes = hoy.withDayOfMonth(1);
            LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());

            List<Asistencias> asistenciasMesActual = context.getEstadisticaService()
                    .obtenerAsistenciasPorRangoL(inicioMes, finMes);

            // Filtrar solo las del cliente actual
            List<Asistencias> historialCliente = asistenciasMesActual.stream()
                    .filter(a -> a.getCliente().getDocumento().equals(documento))
                    .collect(Collectors.toList());

            // Construir y enviar mensaje
            String mensaje = construirMensajeAsistencias(
                    cliente,
                    asistenciasMes,
                    asistenciasTotal,
                    asistioHoy,
                    historialCliente
            );

            enviarMensaje(context, chatId, mensaje);

            System.out.println("Comando /misasistencias ejecutado por: " + cliente.getNombreCompleto());

        } catch (SQLException e) {
            System.err.println("Error de BD en /misasistencias: " + e.getMessage());
            e.printStackTrace();
            enviarMensajeError(context, chatId);
        }
    }

    /**
     * Construye el mensaje con el historial de asistencias.
     */
    private String construirMensajeAsistencias(Clientes cliente,
                                               int asistenciasMes,
                                               int asistenciasTotal,
                                               boolean asistioHoy,
                                               List<Asistencias> asistenciasMesActual) {
        StringBuilder mensaje = new StringBuilder();

        LocalDate hoy = LocalDate.now();
        String nombreMes = hoy.format(FORMATO_MES);
        int anioActual = hoy.getYear();

        // Encabezado
        mensaje.append("📊 *TUS ASISTENCIAS*\n\n");

        // Estado de hoy
        if (asistioHoy) {
            mensaje.append("✅ *Hoy:* Ya registraste tu asistencia 💪\n\n");
        } else {
            mensaje.append("⏰ *Hoy:* Aún no has registrado asistencia\n\n");
        }

        // Estadísticas principales
        mensaje.append("📈 *ESTADÍSTICAS*\n\n");
        mensaje.append("📅 *Este mes (").append(capitalizarPrimera(nombreMes)).append("):* ")
                .append(asistenciasMes).append(" asistencias\n");

        mensaje.append("🏆 *Total histórico:* ").append(asistenciasTotal).append(" asistencias\n\n");

        // Separador
        mensaje.append("━━━━━━━━━━━━━━━━━\n\n");

        // Últimas asistencias del mes
        if (!asistenciasMesActual.isEmpty()) {
            mensaje.append("📝 *ASISTENCIAS DE ").append(nombreMes.toUpperCase()).append("*\n\n");

            // Ordenar por fecha descendente
            List<Asistencias> ordenadas = asistenciasMesActual.stream()
                    .sorted((a1, a2) -> a2.getFecha().compareTo(a1.getFecha()))
                    .collect(Collectors.toList());

            // Mostrar últimas 10 (o menos si no hay tantas)
            int limite = Math.min(10, ordenadas.size());

            for (int i = 0; i < limite; i++) {
                Asistencias asistencia = ordenadas.get(i);
                String emoji = asistencia.getFecha().equals(hoy) ? "✅" : "📅";
                mensaje.append(emoji).append(" ")
                        .append(asistencia.getFecha().format(FORMATO_FECHA))
                        .append("\n");
            }

            if (ordenadas.size() > 10) {
                mensaje.append("\n... y ").append(ordenadas.size() - 10)
                        .append(" más este mes\n");
            }
        } else {
            mensaje.append("📝 *Aún no tienes asistencias este mes.*\n");
            mensaje.append("¡Comienza tu racha hoy! 💪\n");
        }

        mensaje.append("\n");

        // Mensaje motivacional
        mensaje.append("━━━━━━━━━━━━━━━━━\n");
        mensaje.append(generarMensajeMotivacional(asistenciasMes));

        return mensaje.toString();
    }

    /**
     * Capitaliza la primera letra de un string.
     */
    private String capitalizarPrimera(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    /**
     * Genera un mensaje motivacional según las asistencias del mes.
     */
    private String generarMensajeMotivacional(int asistenciasMes) {
        if (asistenciasMes == 0) {
            return "💪 ¡Es hora de empezar! Te esperamos en el gym.";
        } else if (asistenciasMes < 5) {
            return "💪 ¡Buen comienzo! Mantén la constancia.";
        } else if (asistenciasMes < 10) {
            return "🔥 ¡Excelente ritmo! Sigue así.";
        } else if (asistenciasMes < 15) {
            return "⭐ ¡Increíble dedicación! Eres un ejemplo.";
        } else {
            return "🏆 ¡Eres una máquina! Sigue rompiendo récords.";
        }
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
     * Mensaje de error genérico.
     */
    private void enviarMensajeError(ComandoContext context, long chatId)
            throws TelegramApiException {
        String mensaje =
                "❌ Ocurrió un error al consultar tus asistencias.\n\n" +
                        "Por favor, intenta nuevamente más tarde.";

        enviarMensaje(context, chatId, mensaje);
    }

    @Override
    public boolean requiereRegistro() {
        return true;
    }
}