package org.example.gimnasioproyect.services.bot;

import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.repository.ClienteRepository;
import org.example.gimnasioproyect.services.bot.comandos.ComandoBot;
import org.example.gimnasioproyect.services.bot.comandos.ComandoContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Handler que procesa y ejecuta comandos del bot de Telegram.
 *
 * PATRÓN CHAIN OF RESPONSIBILITY: Maneja la cadena de validaciones
 * antes de ejecutar un comando.
 *
 * Flujo de procesamiento:
 * 1. Extrae el nombre del comando del mensaje
 * 2. Busca el comando en el Factory
 * 3. Valida permisos (si requiere registro)
 * 4. Ejecuta el comando con el contexto adecuado
 * 5. Maneja errores de forma centralizada
 *
 * Ventajas:
 * - Centraliza la lógica de validación
 * - Separa el procesamiento de la ejecución
 * - Manejo consistente de errores
 * - Fácil agregar nuevas validaciones
 */
public class ComandoHandler {

    private final ComandoContext context;
    private final ClienteRepository clienteRepository;

    /**
     * Constructor del handler.
     *
     * @param context Contexto con las dependencias para los comandos
     */
    public ComandoHandler(ComandoContext context) {
        this.context = context;
        this.clienteRepository = context.getClienteRepository();
    }

    /**
     * Procesa un update de Telegram y ejecuta el comando correspondiente.
     *
     * @param update Update recibido de Telegram
     */
    public void procesarComando(Update update) {
        // Validar que sea un mensaje de texto
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        String textoMensaje = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        // Validar que sea un comando (empiece con '/')
        if (!textoMensaje.startsWith("/")) {
            enviarMensajeComandoNoReconocido(chatId);
            return;
        }

        // Extraer el nombre del comando
        String nombreComando = extraerNombreComando(textoMensaje);

        // Buscar el comando en el Factory
        ComandoBot comando = ComandoFactory.obtenerComando(nombreComando);

        if (comando == null) {
            enviarMensajeComandoNoExiste(chatId, nombreComando);
            return;
        }

        // Validar si el comando requiere registro
        if (comando.requiereRegistro()) {
            if (!validarUsuarioRegistrado(chatId)) {
                enviarMensajeRequiereRegistro(chatId);
                return;
            }
        }

        // Ejecutar el comando
        try {
            System.out.println("Ejecutando comando: /" + nombreComando +
                    " (chatId: " + chatId + ")");

            comando.ejecutar(update, context);

        } catch (TelegramApiException e) {
            System.err.println("Error de Telegram al ejecutar /" + nombreComando +
                    ": " + e.getMessage());
            enviarMensajeErrorEjecucion(chatId);

        } catch (Exception e) {
            System.err.println("Error inesperado al ejecutar /" + nombreComando +
                    ": " + e.getMessage());
            e.printStackTrace();
            enviarMensajeErrorEjecucion(chatId);
        }
    }

    /**
     * Extrae el nombre del comando del texto del mensaje.
     *
     * Ejemplos:
     * - "/start" → "start"
     * - "/registrar 1234567890" → "registrar"
     * - "/ayuda" → "ayuda"
     *
     * @param textoMensaje Texto completo del mensaje
     * @return Nombre del comando sin la barra diagonal
     */
    private String extraerNombreComando(String textoMensaje) {
        // Eliminar la barra diagonal inicial
        String sinBarra = textoMensaje.substring(1);

        // Tomar solo la primera palabra (el comando)
        int espacioIndex = sinBarra.indexOf(' ');
        if (espacioIndex > 0) {
            return sinBarra.substring(0, espacioIndex).toLowerCase();
        }

        return sinBarra.toLowerCase();
    }

    /**
     * Valida si un usuario está registrado en el sistema.
     *
     * @param chatId ID del chat de Telegram
     * @return true si está registrado, false en caso contrario
     */
    private boolean validarUsuarioRegistrado(long chatId) {
        try {
            Optional<Clientes> clienteOpt =
                    clienteRepository.findByChatId(String.valueOf(chatId));

            return clienteOpt.isPresent();

        } catch (SQLException e) {
            System.err.println("Error al validar registro: " + e.getMessage());
            return false;
        }
    }

    /**
     * Envía mensaje cuando el texto no es un comando válido.
     */
    private void enviarMensajeComandoNoReconocido(long chatId) {
        String mensaje =
                "⚠️ Mensaje no reconocido.\n\n" +
                        "Los comandos deben empezar con `/`\n\n" +
                        "Escribe /ayuda para ver los comandos disponibles.";

        enviarMensaje(chatId, mensaje);
    }

    /**
     * Envía mensaje cuando el comando no existe.
     */
    private void enviarMensajeComandoNoExiste(long chatId, String nombreComando) {
        String mensaje =
                "❌ Comando no reconocido: `/" + nombreComando + "`\n\n" +
                        "Escribe /ayuda para ver los comandos disponibles.";

        enviarMensaje(chatId, mensaje);
    }

    /**
     * Envía mensaje cuando se intenta usar un comando que requiere registro.
     */
    private void enviarMensajeRequiereRegistro(long chatId) {
        String mensaje =
                "🔒 Este comando requiere que estés registrado.\n\n" +
                        "Usa `/registrar TU_DOCUMENTO` para vincular tu cuenta.\n\n" +
                        "Ejemplo: `/registrar 1234567890`";

        enviarMensaje(chatId, mensaje);
    }

    /**
     * Envía mensaje cuando hay un error al ejecutar el comando.
     */
    private void enviarMensajeErrorEjecucion(long chatId) {
        String mensaje =
                "❌ Ocurrió un error al procesar tu comando.\n\n" +
                        "Por favor, intenta nuevamente más tarde.";

        enviarMensaje(chatId, mensaje);
    }

    /**
     * Envía un mensaje de texto a un chat específico.
     *
     * @param chatId ID del chat
     * @param texto Texto del mensaje
     */
    private void enviarMensaje(long chatId, String texto) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(texto);
        message.enableMarkdown(true);

        try {
            context.getBotSender().execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Error al enviar mensaje de error: " + e.getMessage());
        }
    }
}