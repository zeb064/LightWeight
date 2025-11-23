package org.example.gimnasioproyect.services.estrategia;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory para crear instancias de estrategias de notificación.
 *
 * PATRÓN FACTORY: Centraliza la creación de objetos complejos.
 * En lugar de usar 'new' directamente, el Factory decide qué clase instanciar.
 *
 * Ventajas:
 * - Punto único de creación de estrategias
 * - Fácil agregar nuevas estrategias (solo modificas este Factory)
 * - El código cliente no necesita conocer las clases concretas
 *
 * Uso:
 * <pre>
 * EstrategiaNotificacion<?> estrategia = NotificacionFactory.obtenerEstrategia("BIENVENIDA");
 * </pre>
 */
public class NotificacionFactory {

    // Cache de estrategias para no crear instancias repetidamente
    // Como las estrategias no tienen estado, podemos reutilizarlas (Patrón Singleton ligero)
    private static final Map<String, EstrategiaNotificacion<?>> ESTRATEGIAS = new HashMap<>();

    // Bloque estático: se ejecuta una sola vez cuando se carga la clase
    static {
        // Registrar todas las estrategias disponibles
        registrarEstrategia(new NotificacionBienvenida());
        registrarEstrategia(new NotificacionVenceProximo());
        registrarEstrategia(new NotificacionVencido());
        registrarEstrategia(new NotificacionInactividad());
        registrarEstrategia(new NotificacionRutinaActualizada());
        registrarEstrategia(new NotificacionNuevoEntrenador());
    }

    /**
     * Registra una estrategia en el factory.
     *
     * @param estrategia La estrategia a registrar
     */
    private static void registrarEstrategia(EstrategiaNotificacion<?> estrategia) {
        ESTRATEGIAS.put(estrategia.getTipoMensaje(), estrategia);
    }

    /**
     * Obtiene la estrategia correspondiente al tipo de mensaje.
     *
     * @param tipoMensaje El tipo de mensaje (BIENVENIDA, VENCE_PRONTO, VENCIDO)
     * @return La estrategia correspondiente
     * @throws IllegalArgumentException Si el tipo de mensaje no está registrado
     */
    public static EstrategiaNotificacion<?> obtenerEstrategia(String tipoMensaje) {
        if (tipoMensaje == null || tipoMensaje.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de mensaje no puede ser nulo o vacío");
        }

        // Buscar la estrategia (case-insensitive para mayor flexibilidad)
        String tipoNormalizado = tipoMensaje.trim().toUpperCase();
        EstrategiaNotificacion<?> estrategia = ESTRATEGIAS.get(tipoNormalizado);

        if (estrategia == null) {
            throw new IllegalArgumentException(
                    "No existe una estrategia para el tipo de mensaje: " + tipoMensaje +
                            ". Tipos disponibles: " + ESTRATEGIAS.keySet()
            );
        }

        return estrategia;
    }

    /**
     * Verifica si existe una estrategia para el tipo de mensaje especificado.
     *
     * @param tipoMensaje El tipo de mensaje a verificar
     * @return true si existe la estrategia, false en caso contrario
     */
    public static boolean existeEstrategia(String tipoMensaje) {
        if (tipoMensaje == null || tipoMensaje.trim().isEmpty()) {
            return false;
        }
        return ESTRATEGIAS.containsKey(tipoMensaje.trim().toUpperCase());
    }

    /**
     * Retorna todos los tipos de mensajes disponibles.
     *
     * @return Array con los tipos de mensajes registrados
     */
    public static String[] getTiposDisponibles() {
        return ESTRATEGIAS.keySet().toArray(new String[0]);
    }

    // Constructor privado para prevenir instanciación
    // Esta clase solo tiene métodos estáticos
    private NotificacionFactory() {
        throw new UnsupportedOperationException("Esta es una clase utilitaria y no debe ser instanciada");
    }
}