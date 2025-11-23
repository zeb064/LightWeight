package org.example.gimnasioproyect.services.bot.comandos;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Clase base abstracta para comandos del bot.
 *
 * Proporciona métodos de utilidad comunes para todos los comandos,
 * evitando duplicación de código.
 *
 * PATRÓN TEMPLATE METHOD: Define el esqueleto de operaciones comunes.
 */
public abstract class ComandoBotBase implements ComandoBot {

    /**
     * Envía un mensaje de texto al chat especificado.
     *
     * @param context Contexto del bot
     * @param chatId ID del chat de Telegram
     * @param texto Texto del mensaje
     * @throws TelegramApiException Si hay error al enviar
     */
    protected void enviarMensaje(ComandoContext context, long chatId, String texto)
            throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(texto);
        message.enableMarkdown(true);

        context.getBotSender().execute(message);
    }

    /**
     * Envía un mensaje de texto al chat desde donde vino el update.
     *
     * @param context Contexto del bot
     * @param update Update recibido
     * @param texto Texto del mensaje
     * @throws TelegramApiException Si hay error al enviar
     */
    protected void enviarMensaje(ComandoContext context, Update update, String texto)
            throws TelegramApiException {
        long chatId = update.getMessage().getChatId();
        enviarMensaje(context, chatId, texto);
    }

    /**
     * Extrae el chatId del update.
     *
     * @param update Update recibido
     * @return El chat ID
     */
    protected long getChatId(Update update) {
        return update.getMessage().getChatId();
    }

    /**
     * Extrae el nombre del usuario del update.
     *
     * @param update Update recibido
     * @return Nombre del usuario
     */
    protected String getNombreUsuario(Update update) {
        return update.getMessage().getFrom().getFirstName();
    }

    /**
     * Extrae el texto del mensaje del update.
     *
     * @param update Update recibido
     * @return Texto del mensaje
     */
    protected String getTextoMensaje(Update update) {
        return update.getMessage().getText();
    }

    /**
     * Extrae los argumentos del comando (texto después del comando).
     *
     * Ejemplo: "/registrar 1234567890" → ["1234567890"]
     *
     * @param update Update recibido
     * @return Array con los argumentos
     */
    protected String[] getArgumentos(Update update) {
        String texto = getTextoMensaje(update);
        String[] partes = texto.split("\\s+");

        if (partes.length <= 1) {
            return new String[0];
        }

        String[] argumentos = new String[partes.length - 1];
        System.arraycopy(partes, 1, argumentos, 0, argumentos.length);
        return argumentos;
    }

    // Valores por defecto para métodos de interface

    @Override
    public boolean requiereRegistro() {
        return false;
    }

    @Override
    public boolean esPublico() {
        return true;
    }
}