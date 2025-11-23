package org.example.gimnasioproyect.services.estrategia;

import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.model.MembresiaClientes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Estrategia para notificaciones de INACTIVIDAD (7 días sin asistir).
 *
 * Se envía cuando un cliente lleva 7 días sin registrar asistencia.
 *
 * Variables que maneja:
 * - {nombre}: Nombre completo del cliente
 * - {dias_inactivo}: Número de días sin asistir
 * - {ultima_asistencia}: Fecha de la última asistencia
 *
 * Contexto requerido: LocalDate con la fecha de la última asistencia
 */
public class NotificacionInactividad implements EstrategiaNotificacion<LocalDate> {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public String getTipoMensaje() {
        return "INACTIVIDAD_7_DIAS";
    }

    @Override
    public Map<String, String> construirVariables(Clientes cliente,
                                                  MembresiaClientes membresia,
                                                  LocalDate ultimaAsistencia) {
        Map<String, String> variables = new HashMap<>();

        // Variable obligatoria: nombre del cliente
        variables.put("nombre", cliente.getNombreCompleto());

        // Calcular días de inactividad
        if (ultimaAsistencia != null) {
            long diasInactivo = java.time.temporal.ChronoUnit.DAYS.between(
                    ultimaAsistencia,
                    LocalDate.now()
            );

            variables.put("dias_inactivo", String.valueOf(diasInactivo));
            variables.put("ultima_asistencia", ultimaAsistencia.format(FORMATO_FECHA));
        } else {
            // Si no hay última asistencia, significa que nunca ha asistido
            variables.put("dias_inactivo", "muchos");
            variables.put("ultima_asistencia", "Nunca");
        }

        return variables;
    }

    @Override
    public boolean validarContexto(LocalDate ultimaAsistencia) {
        // El contexto puede ser null (nunca ha asistido) o una fecha válida
        // Ambos casos son válidos para esta notificación
        return true;
    }
}