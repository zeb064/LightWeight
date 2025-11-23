package org.example.gimnasioproyect.services.estrategia;

import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.model.MembresiaClientes;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Estrategia para notificaciones de BIENVENIDA.
 *
 * Se envía cuando un cliente vincula su cuenta de Telegram por primera vez
 * o cuando se le asigna una nueva membresía.
 *
 * Variables que maneja:
 * - {nombre}: Nombre completo del cliente
 * - {tipo_membresia}: Tipo de membresía (MENSUAL, ANUAL, etc.)
 * - {fecha_inicio}: Fecha de inicio de la membresía
 * - {fecha_fin}: Fecha de finalización de la membresía
 */
public class NotificacionBienvenida implements EstrategiaNotificacion<Void> {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public String getTipoMensaje() {
        return "BIENVENIDA";
    }

    @Override
    public Map<String, String> construirVariables(Clientes cliente,
                                                  MembresiaClientes membresia,
                                                  Void contexto) {
        Map<String, String> variables = new HashMap<>();

        // Variable obligatoria: nombre del cliente
        variables.put("nombre", cliente.getNombreCompleto());

        // Variables relacionadas con la membresía
        if (membresia != null) {
            // Fecha de inicio
            variables.put("fecha_inicio", membresia.getFechaAsignacion() != null ?
                    membresia.getFechaAsignacion().format(FORMATO_FECHA) : "N/A");

            // Fecha de finalización
            variables.put("fecha_fin", membresia.getFechaFinalizacion() != null ?
                    membresia.getFechaFinalizacion().format(FORMATO_FECHA) : "N/A");

            // Tipo de membresía
            variables.put("tipo_membresia", membresia.getMembresia() != null ?
                    membresia.getMembresia().getTipoMembresia() : "N/A");
        } else {
            // Si no hay membresía, usar valores por defecto
            variables.put("fecha_inicio", "N/A");
            variables.put("fecha_fin", "N/A");
            variables.put("tipo_membresia", "N/A");
        }

        return variables;
    }

    @Override
    public boolean validarContexto(Void contexto) {
        // BIENVENIDA no necesita contexto adicional
        return true;
    }
}