package org.example.gimnasioproyect.services.bot.comandos;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Interfaz que define el contrato para todos los comandos del bot de Telegram.
 *
 * PATRÓN COMMAND: Encapsula una solicitud como un objeto, permitiendo parametrizar
 * clientes con diferentes solicitudes, encolar o registrar solicitudes.
 *
 * Cada comando representa una acción específica que el bot puede realizar
 * (ej: /start, /registrar, /mirutina, etc.)
 *
 * Ventajas:
 * - Single Responsibility: Cada comando maneja UNA acción
 * - Open/Closed: Agregar comandos no requiere modificar código existente
 * - Testeable: Cada comando se puede probar independientemente
 */
public interface ComandoBot {

    /**
     * Obtiene el nombre del comando (sin la barra diagonal).
     *
     * Ejemplos: "start", "registrar", "ayuda", "mirutina"
     *
     * @return El nombre del comando en minúsculas
     */
    String getNombreComando();

    /**
     * Obtiene la descripción del comando para el menú de ayuda.
     *
     * @return Descripción corta del comando
     */
    String getDescripcion();

    /**
     * Ejecuta la lógica del comando.
     *
     * @param update El objeto Update de Telegram con toda la información del mensaje
     * @param context Contexto del bot que contiene servicios y utilidades necesarias
     * @throws TelegramApiException Si hay error al enviar respuesta por Telegram
     */
    void ejecutar(Update update, ComandoContext context) throws TelegramApiException;

    /**
     * Verifica si el comando requiere que el usuario esté registrado.
     *
     * Comandos como /start y /registrar NO requieren registro previo.
     * Comandos como /mirutina y /misasistencias SÍ lo requieren.
     *
     * @return true si requiere registro, false en caso contrario
     */
    default boolean requiereRegistro() {
        return false;
    }

    /**
     * Verifica si el comando está disponible para todos los usuarios.
     *
     * Algunos comandos pueden ser solo para administradores en el futuro.
     *
     * @return true si está disponible públicamente, false si es restringido
     */
    default boolean esPublico() {
        return true;
    }
}