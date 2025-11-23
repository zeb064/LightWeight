package org.example.gimnasioproyect.services.bot.comandos;

import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.model.MembresiaClientes;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Comando /registrar - Vincula la cuenta de Telegram del usuario con el gimnasio.
 *
 * Uso: /registrar DOCUMENTO
 * Ejemplo: /registrar 1234567890
 *
 * Pasos:
 * 1. Valida que se proporcione el documento
 * 2. Busca al cliente en la base de datos
 * 3. Verifica que no esté ya registrado
 * 4. Asocia el chatId de Telegram con el cliente
 * 5. Envía mensaje de bienvenida con info de membresía
 */
public class ComandoRegistrar extends ComandoBotBase {

    @Override
    public String getNombreComando() {
        return "registrar";
    }

    @Override
    public String getDescripcion() {
        return "Vincula tu cuenta de Telegram con el gimnasio";
    }

    @Override
    public void ejecutar(Update update, ComandoContext context) throws TelegramApiException {
        long chatId = getChatId(update);
        String nombreUsuario = getNombreUsuario(update);
        String[] argumentos = getArgumentos(update);

        // 1. Validar que se proporcione el documento
        if (argumentos.length < 1) {
            enviarMensajeErrorFormato(context, chatId);
            return;
        }

        String documento = argumentos[0].trim();

        try {
            // 2. Buscar el cliente en la base de datos
            Optional<Clientes> clienteOpt = context.getClienteRepository().findByDocumento(documento);

            if (!clienteOpt.isPresent()) {
                enviarMensajeClienteNoEncontrado(context, chatId, documento);
                return;
            }

            Clientes cliente = clienteOpt.get();

            // 3. Verificar que no esté ya registrado
            if (cliente.getChatId() != null && !cliente.getChatId().trim().isEmpty()) {
                enviarMensajeYaRegistrado(context, chatId);
                return;
            }

            // 4. Asociar el chatId con el cliente
            cliente.setChatId(String.valueOf(chatId));
            context.getClienteRepository().update(cliente);

            System.out.println("✅ ChatId registrado para cliente: " + cliente.getDocumento());

            // 5. Obtener membresía activa y enviar mensaje de bienvenida
            Optional<MembresiaClientes> membresiaOpt =
                    context.getMembresiaClienteService().obtenerMembresiaActiva(documento);

            // Enviar notificación de bienvenida
            context.getNotificacionService().enviarNotificacion(
                    "BIENVENIDA",
                    cliente,
                    membresiaOpt.orElse(null),
                    null
            );

            System.out.println("📨 Registro completado para: " + nombreUsuario +
                    " (Documento: " + documento + ")");

        } catch (SQLException e) {
            System.err.println("❌ Error de BD al registrar chatId: " + e.getMessage());
            e.printStackTrace();
            enviarMensajeError(context, chatId);
        }
    }

    /**
     * Envía mensaje de error por formato incorrecto.
     */
    private void enviarMensajeErrorFormato(ComandoContext context, long chatId)
            throws TelegramApiException {
        String mensaje =
                "❌ Formato incorrecto.\n\n" +
                        "Uso correcto: `/registrar TU_DOCUMENTO`\n\n" +
                        "Ejemplo: `/registrar 1234567890`";

        enviarMensaje(context, chatId, mensaje);
    }

    /**
     * Envía mensaje cuando no se encuentra el cliente en la BD.
     */
    private void enviarMensajeClienteNoEncontrado(ComandoContext context, long chatId, String documento)
            throws TelegramApiException {
        String mensaje =
                "❌ No encontramos un cliente registrado con el documento: " + documento + "\n\n" +
                        "Por favor, verifica que tu documento esté registrado en el gimnasio.";

        enviarMensaje(context, chatId, mensaje);
    }

    /**
     * Envía mensaje cuando el cliente ya está vinculado.
     */
    private void enviarMensajeYaRegistrado(ComandoContext context, long chatId)
            throws TelegramApiException {
        String mensaje =
                "⚠️ Tu cuenta ya está vinculada a Telegram.\n\n" +
                        "Si necesitas cambiar tu vinculación, contacta con recepción.";

        enviarMensaje(context, chatId, mensaje);
    }

    /**
     * Envía mensaje genérico de error.
     */
    private void enviarMensajeError(ComandoContext context, long chatId)
            throws TelegramApiException {
        String mensaje =
                "❌ Ocurrió un error al procesar tu solicitud.\n\n" +
                        "Por favor, intenta nuevamente más tarde.";

        enviarMensaje(context, chatId, mensaje);
    }

    @Override
    public boolean requiereRegistro() {
        // /registrar NO requiere estar registrado (es el proceso de registro)
        return false;
    }
}