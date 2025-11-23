package org.example.gimnasioproyect.services.estrategia;

import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.model.MembresiaClientes;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Estrategia para notificaciones de VENCIMIENTO PRÓXIMO.
 *
 * Se envía cuando una membresía está próxima a vencer (configurado en telegram.properties).
 *
 * Variables que maneja:
 * - {nombre}: Nombre completo del cliente
 * - {dias}: Número de días restantes para el vencimiento
 * - {fecha_fin}: Fecha de finalización de la membresía
 *
 * IMPORTANTE: Esta estrategia REQUIERE contexto (Long con los días restantes)
 */
public class NotificacionVenceProximo implements EstrategiaNotificacion<Long> {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public String getTipoMensaje() {
        return "VENCE_PRONTO";
    }

    @Override
    public Map<String, String> construirVariables(Clientes cliente,
                                                  MembresiaClientes membresia,
                                                  Long diasRestantes) {
        Map<String, String> variables = new HashMap<>();

        // Variable obligatoria: nombre del cliente
        variables.put("nombre", cliente.getNombreCompleto());

        // Variable obligatoria: días restantes (viene del contexto)
        if (diasRestantes != null) {
            variables.put("dias", String.valueOf(diasRestantes));
        } else {
            // Si por alguna razón el contexto es null, usar 0
            variables.put("dias", "0");
        }

        // Variable de fecha de finalización
        if (membresia != null && membresia.getFechaFinalizacion() != null) {
            variables.put("fecha_fin", membresia.getFechaFinalizacion().format(FORMATO_FECHA));
        } else {
            variables.put("fecha_fin", "N/A");
        }

        return variables;
    }

    @Override
    public boolean validarContexto(Long diasRestantes) {
        // El contexto DEBE ser un Long válido y mayor a 0
        // No tiene sentido notificar "vence pronto" si ya venció o si quedan 0 días
        return diasRestantes != null && diasRestantes > 0;
    }
}