package org.example.gimnasioproyect.services.estrategia;

import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.model.MembresiaClientes;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Estrategia para notificaciones de MEMBRESÍA VENCIDA.
 *
 * Se envía cuando una membresía ya ha expirado (fecha de finalización pasó).
 *
 * Variables que maneja:
 * - {nombre}: Nombre completo del cliente
 * - {fecha_fin}: Fecha en que venció la membresía
 *
 * NOTA: Esta estrategia NO requiere contexto adicional
 */
public class NotificacionVencido implements EstrategiaNotificacion<Void> {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public String getTipoMensaje() {
        return "VENCIDO";
    }

    @Override
    public Map<String, String> construirVariables(Clientes cliente,
                                                  MembresiaClientes membresia,
                                                  Void contexto) {
        Map<String, String> variables = new HashMap<>();

        // Variable obligatoria: nombre del cliente
        variables.put("nombre", cliente.getNombreCompleto());

        // Variable de fecha de finalización
        if (membresia != null && membresia.getFechaFinalizacion() != null) {
            variables.put("fecha_fin", membresia.getFechaFinalizacion().format(FORMATO_FECHA));
        } else {
            variables.put("fecha_fin", "N/A");
        }

        return variables;
    }

    @Override
    public boolean validarContexto(Void contexto) {
        // VENCIDO no necesita contexto
        // Siempre retorna true porque Void siempre será null
        return true;
    }
}