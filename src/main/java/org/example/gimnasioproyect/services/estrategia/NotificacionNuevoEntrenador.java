package org.example.gimnasioproyect.services.estrategia;

import org.example.gimnasioproyect.model.AsignacionEntrenadores;
import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.model.Entrenadores;
import org.example.gimnasioproyect.model.MembresiaClientes;

import java.util.HashMap;
import java.util.Map;

/**
 * Estrategia para notificaciones de NUEVO ENTRENADOR.
 *
 * Se envía cuando se asigna un entrenador personal a un cliente.
 *
 * Variables que maneja:
 * - {nombre}: Nombre completo del cliente
 * - {nombre_entrenador}: Nombre completo del entrenador
 * - {especialidad}: Especialidad del entrenador
 *
 * Contexto requerido: AsignacionEntrenadores con la asignación
 */
public class NotificacionNuevoEntrenador implements EstrategiaNotificacion<AsignacionEntrenadores> {

    @Override
    public String getTipoMensaje() {
        return "NUEVO_ENTRENADOR";
    }

    @Override
    public Map<String, String> construirVariables(Clientes cliente,
                                                  MembresiaClientes membresia,
                                                  AsignacionEntrenadores asignacion) {
        Map<String, String> variables = new HashMap<>();

        // Variable obligatoria: nombre del cliente
        variables.put("nombre", cliente.getNombreCompleto());

        // Variables del entrenador
        if (asignacion != null && asignacion.getEntrenador() != null) {
            Entrenadores entrenador = asignacion.getEntrenador();

            variables.put("nombre_entrenador", entrenador.getNombreCompleto());

            String especialidad = entrenador.getEspecialidad();
            variables.put("especialidad",
                    especialidad != null && !especialidad.trim().isEmpty()
                            ? especialidad
                            : "Entrenamiento general");
        } else {
            variables.put("nombre_entrenador", "Entrenador");
            variables.put("especialidad", "Entrenamiento general");
        }

        return variables;
    }

    @Override
    public boolean validarContexto(AsignacionEntrenadores asignacion) {
        // La asignación debe existir y tener un entrenador asociado
        return asignacion != null && asignacion.getEntrenador() != null;
    }
}