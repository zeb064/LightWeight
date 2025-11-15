package org.example.gimnasioproyect.services;

import org.example.gimnasioproyect.Utilidades.TelegramConfig;
import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.model.MembresiaClientes;
import org.example.gimnasioproyect.repository.ClienteRepository;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.Optional;

public class LightWeightBot extends TelegramLongPollingBot {
    private final TelegramConfig config;
    private final ClienteRepository clienteRepository;
    private final MembresiaClienteService membresiaClienteService;
    private final NotificacionService notificacionService;

    public LightWeightBot(ClienteRepository clienteRepository,
                          MembresiaClienteService membresiaClienteService,
                          NotificacionService notificacionService) {
        this.config = TelegramConfig.getInstance();
        this.clienteRepository = clienteRepository;
        this.membresiaClienteService = membresiaClienteService;
        this.notificacionService = notificacionService;
    }

    @Override
    public String getBotUsername() {
        return config.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Verificar que sea un mensaje de texto
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String firstName = update.getMessage().getFrom().getFirstName();

            System.out.println("📨 Mensaje recibido de " + firstName + " (chatId: " + chatId + "): " + messageText);

            // Procesar comandos
            if (messageText.startsWith("/registrar")) {
                procesarComandoRegistrar(chatId, messageText, firstName);
            } else if (messageText.equals("/start")) {
                procesarComandoStart(chatId, firstName);
            } else if (messageText.equals("/ayuda") || messageText.equals("/help")) {
                procesarComandoAyuda(chatId);
            } else {
                enviarMensaje(chatId, "⚠️ Comando no reconocido. Escribe /ayuda para ver los comandos disponibles.");
            }
        }
    }

    //Procesa el comando /registrar DOCUMENTO
    private void procesarComandoRegistrar(long chatId, String messageText, String firstName) {
        // Extraer documento del comando
        String[] partes = messageText.split(" ");

        if (partes.length < 2) {
            enviarMensaje(chatId,
                    "❌ Formato incorrecto.\n\n" +
                            "Uso correcto: `/registrar TU_DOCUMENTO`\n\n" +
                            "Ejemplo: `/registrar 1234567890`");
            return;
        }

        String documento = partes[1].trim();

        try {
            // Buscar el cliente en la base de datos
            Optional<Clientes> clienteOpt = clienteRepository.findByDocumento(documento);

            if (!clienteOpt.isPresent()) {
                enviarMensaje(chatId,
                        "❌ No encontramos un cliente registrado con el documento: " + documento + "\n\n" +
                                "Por favor, verifica que tu documento esté registrado en el gimnasio.");
                return;
            }

            Clientes cliente = clienteOpt.get();

            // Verificar si ya tiene un chatId registrado
            if (cliente.getChatId() != null && !cliente.getChatId().trim().isEmpty()) {
                enviarMensaje(chatId,
                        "⚠️ Tu cuenta ya está vinculada a Telegram.\n\n" +
                                "Si necesitas cambiar tu vinculación, contacta con recepción.");
                return;
            }

            // Registrar el chatId
            cliente.setChatId(String.valueOf(chatId));
            clienteRepository.update(cliente);

            System.out.println("✅ ChatId registrado para cliente: " + cliente.getDocumento());

            // Obtener membresía activa
            Optional<MembresiaClientes> membresiaOpt =
                    membresiaClienteService.obtenerMembresiaActiva(documento);

            // Enviar mensaje de bienvenida
            notificacionService.enviarMensajeBienvenida(
                    cliente,
                    membresiaOpt.orElse(null)
            );

        } catch (SQLException e) {
            System.err.println("❌ Error al registrar chatId: " + e.getMessage());
            e.printStackTrace();
            enviarMensaje(chatId,
                    "❌ Ocurrió un error al procesar tu solicitud. " +
                            "Por favor, intenta nuevamente más tarde.");
        }
    }

    //Procesa el comando /start
    private void procesarComandoStart(long chatId, String firstName) {
        String mensaje =
                "👋 ¡Hola " + firstName + "!\n\n" +
                        "Bienvenido al bot de *LightWeight Gym* 💪\n\n" +
                        "Para vincular tu cuenta de Telegram con el gimnasio, " +
                        "usa el comando:\n\n" +
                        "`/registrar TU_DOCUMENTO`\n\n" +
                        "Ejemplo: `/registrar 1234567890`\n\n" +
                        "Si necesitas ayuda, escribe /ayuda";

        enviarMensaje(chatId, mensaje);
    }

    //Procesa el comando /ayuda
    private void procesarComandoAyuda(long chatId) {
        String mensaje =
                "📖 *Comandos disponibles:*\n\n" +
                        "• `/start` - Inicia la conversación\n" +
                        "• `/registrar DOCUMENTO` - Vincula tu cuenta\n" +
                        "• `/ayuda` - Muestra esta ayuda\n\n" +
                        "ℹ️ *Importante:*\n" +
                        "Debes estar registrado en el gimnasio para usar este bot.\n\n" +
                        "📞 Si tienes problemas, contacta con recepción.";

        enviarMensaje(chatId, mensaje);
    }

    //Envía un mensaje de texto
    private void enviarMensaje(long chatId, String texto) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(texto);
        message.enableMarkdown(true);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("❌ Error al enviar mensaje: " + e.getMessage());
            e.printStackTrace();
        }
    }
}