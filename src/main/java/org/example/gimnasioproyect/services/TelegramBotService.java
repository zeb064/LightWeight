package org.example.gimnasioproyect.services;

import org.example.gimnasioproyect.Utilidades.TelegramConfig;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TelegramBotService {
    private final TelegramConfig config;
    private org.telegram.telegrambots.bots.DefaultAbsSender bot;

    public TelegramBotService() {
        this.config = TelegramConfig.getInstance();
    }

    // Envía un mensaje a un chatId específico
    public boolean enviarMensaje(String chatId, String mensaje) {
        if (!config.isConfigured()) {
            System.err.println("❌ Bot de Telegram no configurado. Revisa telegram.properties");
            return false;
        }

        if (chatId == null || chatId.trim().isEmpty()) {
            System.err.println("❌ chatId no puede estar vacío");
            return false;
        }

        if (mensaje == null || mensaje.trim().isEmpty()) {
            System.err.println("❌ El mensaje no puede estar vacío");
            return false;
        }

        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(mensaje);
            sendMessage.enableMarkdown(true); // Permite formato Markdown

            // Enviar usando la API directa
            enviarMensajeDirecto(chatId, mensaje);

            System.out.println("✅ Mensaje enviado a chatId: " + chatId);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error al enviar mensaje: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Método para enviar mensaje usando la API HTTP directa
    private void enviarMensajeDirecto(String chatId, String mensaje) throws Exception {
        String urlString = "https://api.telegram.org/bot" + config.getBotToken() + "/sendMessage";

        java.net.URL url = new java.net.URL(urlString);
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String jsonInputString = String.format(
                "{\"chat_id\": \"%s\", \"text\": \"%s\", \"parse_mode\": \"Markdown\"}",
                chatId,
                mensaje.replace("\"", "\\\"").replace("\n", "\\n")
        );

        try (java.io.OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("Error HTTP: " + responseCode);
        }
    }

    // Verifica si el bot está correctamente configurado
    public boolean verificarConfiguracion() {
        if (!config.isConfigured()) {
            System.err.println("❌ Bot no configurado. Token no encontrado o inválido.");
            return false;
        }

        try {
            // Intentar hacer una petición simple para verificar el token
            String urlString = "https://api.telegram.org/bot" + config.getBotToken() + "/getMe";
            java.net.URL url = new java.net.URL(urlString);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                System.out.println("✅ Bot de Telegram configurado correctamente");
                return true;
            } else {
                System.err.println("❌ Token inválido o bot no autorizado");
                return false;
            }

        } catch (Exception e) {
            System.err.println("❌ Error al verificar configuración: " + e.getMessage());
            return false;
        }
    }

    // Obtiene la configuración del bot
    public TelegramConfig getConfig() {
        return config;
    }
}