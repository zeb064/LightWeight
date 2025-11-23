package org.example.gimnasioproyect.services.bot.comandos;

import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.model.DetalleRutinas;
import org.example.gimnasioproyect.model.RutinaAsignadas;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Comando /mirutina - Muestra la rutina de entrenamiento asignada al cliente.
 *
 * Muestra:
 * - Objetivo de la rutina
 * - Ejercicios organizados por día de la semana
 * - Series, repeticiones y peso de cada ejercicio
 * - Notas adicionales
 * - Progreso de asistencias del mes
 */
public class ComandoMiRutina extends ComandoBotBase {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Orden de días para mostrar la rutina
    private static final List<String> ORDEN_DIAS = Arrays.asList(
            "LUNES", "MARTES", "MIÉRCOLES", "MIERCOLES", "JUEVES",
            "VIERNES", "SÁBADO", "SABADO", "DOMINGO"
    );

    @Override
    public String getNombreComando() {
        return "mirutina";
    }

    @Override
    public String getDescripcion() {
        return "Ver tu rutina de entrenamiento";
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

            // Obtener rutinas activas del cliente
            List<RutinaAsignadas> rutinasActivas = context.getRutinaService()
                    .obtenerRutinasActivasCliente(cliente.getDocumento());

            if (rutinasActivas.isEmpty()) {
                enviarMensajeSinRutina(context, chatId);
                return;
            }

            // Tomar la primera rutina activa (normalmente solo hay una)
            RutinaAsignadas rutinaAsignada = rutinasActivas.get(0);

            // Obtener detalles de la rutina
            List<DetalleRutinas> detalles = context.getRutinaService()
                    .obtenerDetallesRutina(rutinaAsignada.getRutina().getIdRutina());

            if (detalles.isEmpty()) {
                enviarMensajeRutinaSinDetalles(context, chatId);
                return;
            }

            // Obtener estadísticas de asistencias
            int asistenciasMes = context.getEstadisticaService()
                    .contarAsistenciasMesActual(cliente.getDocumento());

            // Construir y enviar mensaje
            String mensaje = construirMensajeRutina(rutinaAsignada, detalles, asistenciasMes);
            enviarMensaje(context, chatId, mensaje);

            System.out.println("Comando /mirutina ejecutado por: " + cliente.getNombreCompleto());

        } catch (SQLException e) {
            System.err.println("Error de BD en /mirutina: " + e.getMessage());
            e.printStackTrace();
            enviarMensajeError(context, chatId);
        }
    }

    /**
     * Construye el mensaje con la rutina completa.
     */
    private String construirMensajeRutina(RutinaAsignadas rutinaAsignada,
                                          List<DetalleRutinas> detalles,
                                          int asistenciasMes) {
        StringBuilder mensaje = new StringBuilder();

        // Encabezado
        mensaje.append("💪 *TU RUTINA DE ENTRENAMIENTO*\n\n");

        // Objetivo
        mensaje.append("🎯 *Objetivo:* ").append(rutinaAsignada.getRutina().getObjetivo()).append("\n");
        mensaje.append("📅 *Asignada:* ").append(rutinaAsignada.getFechaAsignacion().format(FORMATO_FECHA)).append("\n");
        mensaje.append("📊 *Asistencias este mes:* ").append(asistenciasMes).append("\n\n");

        mensaje.append("━━━━━━━━━━━━━━━━━\n\n");

        // Agrupar ejercicios por día
        Map<String, List<DetalleRutinas>> ejerciciosPorDia = detalles.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getDiaSemana().toUpperCase(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // Ordenar por día de la semana
        Map<String, List<DetalleRutinas>> ejerciciosOrdenados = ordenarPorDiaSemana(ejerciciosPorDia);

        // Construir cada día
        for (Map.Entry<String, List<DetalleRutinas>> entry : ejerciciosOrdenados.entrySet()) {
            String dia = entry.getKey();
            List<DetalleRutinas> ejerciciosDia = entry.getValue();

            // Ordenar ejercicios por orden dentro del día
            ejerciciosDia.sort(Comparator.comparing(DetalleRutinas::getOrden));

            // Encabezado del día
            mensaje.append("📆 *").append(capitalizarDia(dia)).append("*\n");

            // Ejercicios del día
            for (DetalleRutinas detalle : ejerciciosDia) {
                mensaje.append("  • ").append(detalle.getEjercicio()).append("\n");
                mensaje.append("    ").append(detalle.getSeries()).append(" series x ")
                        .append(detalle.getRepeticiones()).append(" reps");

                if (detalle.getPeso() != null && detalle.getPeso() > 0) {
                    mensaje.append(" - ").append(String.format("%.1f", detalle.getPeso())).append(" kg");
                }

                mensaje.append("\n");

                // Agregar notas si existen
                if (detalle.getNotas() != null && !detalle.getNotas().trim().isEmpty()) {
                    mensaje.append("    💡 ").append(detalle.getNotas()).append("\n");
                }
            }

            mensaje.append("\n");
        }

        // Pie de mensaje
        mensaje.append("━━━━━━━━━━━━━━━━━\n");
        mensaje.append("💡 *Tip:* Consulta con tu entrenador si tienes dudas.\n");
        mensaje.append("Usa /mientrenador para ver su contacto.");

        return mensaje.toString();
    }

    /**
     * Ordena el mapa de ejercicios por día de la semana.
     */
    private Map<String, List<DetalleRutinas>> ordenarPorDiaSemana(
            Map<String, List<DetalleRutinas>> ejerciciosPorDia) {

        Map<String, List<DetalleRutinas>> ordenado = new LinkedHashMap<>();

        for (String dia : ORDEN_DIAS) {
            if (ejerciciosPorDia.containsKey(dia)) {
                ordenado.put(dia, ejerciciosPorDia.get(dia));
            }
        }

        // Agregar cualquier día que no esté en la lista (por si acaso)
        for (Map.Entry<String, List<DetalleRutinas>> entry : ejerciciosPorDia.entrySet()) {
            if (!ordenado.containsKey(entry.getKey())) {
                ordenado.put(entry.getKey(), entry.getValue());
            }
        }

        return ordenado;
    }

    /**
     * Capitaliza el nombre del día.
     */
    private String capitalizarDia(String dia) {
        if (dia == null || dia.isEmpty()) return dia;

        // Normalizar tildes
        String normalizado = dia.replace("MIÉRCOLES", "Miércoles")
                .replace("SÁBADO", "Sábado");

        if (normalizado.equals(dia)) {
            // Si no se normalizó, capitalizar normalmente
            return dia.substring(0, 1).toUpperCase() + dia.substring(1).toLowerCase();
        }

        return normalizado;
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
     * Mensaje cuando el cliente no tiene rutina asignada.
     */
    private void enviarMensajeSinRutina(ComandoContext context, long chatId)
            throws TelegramApiException {
        String mensaje =
                "📭 No tienes una rutina asignada.\n\n" +
                        "Consulta con tu entrenador para que te asigne una rutina personalizada. 💪";

        enviarMensaje(context, chatId, mensaje);
    }

    /**
     * Mensaje cuando la rutina no tiene detalles.
     */
    private void enviarMensajeRutinaSinDetalles(ComandoContext context, long chatId)
            throws TelegramApiException {
        String mensaje =
                "⚠️ Tu rutina aún no tiene ejercicios configurados.\n\n" +
                        "Contacta con tu entrenador para completar tu rutina.";

        enviarMensaje(context, chatId, mensaje);
    }

    /**
     * Mensaje de error genérico.
     */
    private void enviarMensajeError(ComandoContext context, long chatId)
            throws TelegramApiException {
        String mensaje =
                "❌ Ocurrió un error al consultar tu rutina.\n\n" +
                        "Por favor, intenta nuevamente más tarde.";

        enviarMensaje(context, chatId, mensaje);
    }

    @Override
    public boolean requiereRegistro() {
        return true;
    }
}