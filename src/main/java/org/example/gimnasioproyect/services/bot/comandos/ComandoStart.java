package org.example.gimnasioproyect.services.bot.comandos;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Comando /start - Inicia la conversación con el bot.
 *
 * Este es el primer comando que los usuarios ejecutan al iniciar
 * conversación con el bot. Muestra un mensaje de bienvenida y
 * explica cómo vincular su cuenta.
 */
public class ComandoStart extends ComandoBotBase {

    @Override
    public String getNombreComando() {
        return "start";
    }

    @Override
    public String getDescripcion() {
        return "Inicia la conversación con el bot";
    }

    @Override
    public void ejecutar(Update update, ComandoContext context) throws TelegramApiException {
        String nombreUsuario = getNombreUsuario(update);

        String mensaje = construirMensajeBienvenida(nombreUsuario);

        enviarMensaje(context, update, mensaje);

        System.out.println("📨 Comando /start ejecutado por: " + nombreUsuario);
    }

    /**
     * Construye el mensaje de bienvenida personalizado.
     *
     * @param nombreUsuario Nombre del usuario que inició el bot
     * @return Mensaje formateado
     */
    private String construirMensajeBienvenida(String nombreUsuario) {
        return "👋 ¡Hola " + nombreUsuario + "!\n\n" +
                "Bienvenido al bot de *LightWeight Gym* 💪\n\n" +
                "Para vincular tu cuenta de Telegram con el gimnasio, " +
                "usa el comando:\n\n" +
                "`/registrar TU_DOCUMENTO`\n\n" +
                "Ejemplo: `/registrar 1234567890`\n\n" +
                "Si necesitas ayuda, escribe /ayuda";
    }

    @Override
    public boolean requiereRegistro() {
        // /start NO requiere que el usuario esté registrado
        return false;
    }
}