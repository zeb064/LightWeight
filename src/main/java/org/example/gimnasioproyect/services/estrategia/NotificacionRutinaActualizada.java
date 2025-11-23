package org.example.gimnasioproyect.services.estrategia;

import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.model.MembresiaClientes;
import org.example.gimnasioproyect.model.RutinaAsignadas;

import java.util.HashMap;
import java.util.Map;

/**
 * Estrategia para notificaciones de RUTINA ACTUALIZADA.
 *
 * Se envía cuando un entrenador asigna o actualiza la rutina de un cliente.
 *
 * Variables que maneja:
 * - {nombre}: Nombre completo del cliente
 * - {objetivo}: Objetivo de la rutina
 * - {nombre_entrenador}: Nombre del entrenador que asignó la rutina
 *
 * Contexto requerido: RutinaAsignadas con la rutina asignada
 */
public class NotificacionRutinaActualizada implements EstrategiaNotificacion<RutinaAsignadas> {

    @Override
    public String getTipoMensaje() {
        return "RUTINA_ACTUALIZADA";
    }

    @Override
    public Map<String, String> construirVariables(Clientes cliente,
                                                  MembresiaClientes membresia,
                                                  RutinaAsignadas rutinaAsignada) {
        Map<String, String> variables = new HashMap<>();

        // Variable obligatoria: nombre del cliente
        variables.put("nombre", cliente.getNombreCompleto());

        // Variables de la rutina
        if (rutinaAsignada != null && rutinaAsignada.getRutina() != null) {
            String objetivo = rutinaAsignada.getRutina().getObjetivo();
            variables.put("objetivo", objetivo != null ? objetivo : "Entrenamiento general");
        } else {
            variables.put("objetivo", "Entrenamiento general");
        }

        // Nombre del entrenador (si está disponible en el cliente)
        // Como RutinaAsignadas no tiene referencia directa al entrenador,
        // usaremos "tu entrenador" genérico
        variables.put("nombre_entrenador", "tu entrenador");

        return variables;
    }

    @Override
    public boolean validarContexto(RutinaAsignadas rutinaAsignada) {
        // La rutina asignada debe existir y tener una rutina asociada
        return rutinaAsignada != null && rutinaAsignada.getRutina() != null;
    }
}