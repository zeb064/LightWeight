package org.example.gimnasioproyect.services.estrategia;

import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.model.MembresiaClientes;

import java.util.Map;

/**
 * Interfaz que define el contrato para todas las estrategias de notificación.
 *
 * Cada estrategia representa un TIPO de mensaje (BIENVENIDA, VENCE_PRONTO, VENCIDO)
 * y sabe cómo construir las variables específicas para ese mensaje.
 *
 * PATRÓN STRATEGY: Permite agregar nuevos tipos de notificaciones sin modificar
 * el código existente (Open/Closed Principle).
 */
public interface EstrategiaNotificacion<T> {

    /**
     * Retorna el tipo de mensaje que maneja esta estrategia.
     * Este tipo debe coincidir con el campo 'tipo_mensaje' en la tabla MENSAJES_TELEGRAM.
     *
     * Ejemplos: "BIENVENIDA", "VENCE_PRONTO", "VENCIDO"
     *
     * @return El tipo de mensaje (debe estar en mayúsculas)
     */
    String getTipoMensaje();

    /**
     * Construye el mapa de variables específicas para este tipo de notificación.
     *
     * Este mapa se usará para reemplazar las variables en la plantilla del mensaje.
     * Por ejemplo: {nombre} → "Juan Pérez", {dias} → "5"
     *
     * @param cliente El cliente que recibirá la notificación
     * @param membresia La membresía asociada al cliente (puede ser null)
     * @param contexto Información adicional necesaria para construir el mensaje
     *                 (ej: días restantes para "VENCE_PRONTO")
     * @return Mapa con las variables a reemplazar en la plantilla
     */
    Map<String, String> construirVariables(Clientes cliente,
                                           MembresiaClientes membresia,
                                           T contexto);

    /**
     * Valida que el contexto proporcionado sea válido para esta estrategia.
     *
     * Algunas estrategias requieren contexto específico:
     * - BIENVENIDA: no requiere contexto
     * - VENCE_PRONTO: requiere días restantes (Long)
     * - VENCIDO: no requiere contexto
     *
     * @param contexto El contexto a validar
     * @return true si el contexto es válido, false en caso contrario
     */
    default boolean validarContexto(T contexto) {
        // Por defecto, cualquier contexto es válido
        // Las estrategias pueden sobrescribir esto si necesitan validaciones específicas
        return true;
    }
}