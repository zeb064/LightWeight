package org.example.gimnasioproyect.services.bot.comandos;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Comando /ayuda - Muestra la lista de comandos disponibles.
 *
 * Este comando también responde al alias /help para usuarios
 * que prefieren comandos en inglés.
 *
 * En el futuro, este comando podría generar dinámicamente la lista
 * consultando todos los comandos registrados en el ComandoFactory.
 */
public class ComandoAyuda extends ComandoBotBase {

    @Override
    public String getNombreComando() {
        return "ayuda";
    }

    @Override
    public String getDescripcion() {
        return "Muestra la lista de comandos disponibles";
    }

    @Override
    public void ejecutar(Update update, ComandoContext context) throws TelegramApiException {
        String mensaje = construirMensajeAyuda();

        enviarMensaje(context, update, mensaje);

        System.out.println("📨 Comando /ayuda ejecutado por: " + getNombreUsuario(update));
    }

    /**
     * Construye el mensaje de ayuda con todos los comandos disponibles.
     *
     * @return Mensaje formateado con la ayuda
     */
    private String construirMensajeAyuda() {
        StringBuilder mensaje = new StringBuilder();

        mensaje.append("📖 *Comandos disponibles:*\n\n");

        // Comandos básicos
        mensaje.append("*Comandos Básicos:*\n");
        mensaje.append("• `/start` - Inicia la conversación\n");
        mensaje.append("• `/registrar DOCUMENTO` - Vincula tu cuenta\n");
        mensaje.append("• `/ayuda` - Muestra esta ayuda\n\n");

        // Comandos de consulta (requieren registro)
        mensaje.append("*Consulta tu información:*\n");
        mensaje.append("• `/mimembresia` - Ver estado de tu membresía\n");
        mensaje.append("• `/mirutina` - Ver tu rutina de entrenamiento\n");
        mensaje.append("• `/mientrenador` - Ver info de tu entrenador\n");
        mensaje.append("• `/misasistencias` - Ver historial de asistencias\n\n");

        // Información importante
        mensaje.append("ℹ️ *Importante:*\n");
        mensaje.append("Debes estar registrado en el gimnasio para usar este bot.\n\n");

        // Contacto
        mensaje.append("📞 Si tienes problemas, contacta con recepción.");

        return mensaje.toString();
    }

    @Override
    public boolean requiereRegistro() {
        // /ayuda NO requiere registro, cualquiera puede consultar la ayuda
        return false;
    }
}