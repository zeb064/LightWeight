package org.example.gimnasioproyect.Utilidades;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TelegramConfig {
    private static TelegramConfig instance;
    private Properties properties;

    private TelegramConfig() {
        properties = new Properties();
        cargarConfiguracion();
    }

    public static TelegramConfig getInstance() {
        if (instance == null) {
            instance = new TelegramConfig();
        }
        return instance;
    }

    private void cargarConfiguracion() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("telegram.properties")) {
            if (input == null) {
                System.err.println("No se encontró el archivo telegram.properties");
                return;
            }
            properties.load(input);
            System.out.println("Configuración de Telegram cargada correctamente");
        } catch (IOException e) {
            System.err.println("Error al cargar telegram.properties: " + e.getMessage());
        }
    }

    public String getBotToken() {
        return properties.getProperty("telegram.bot.token", "");
    }

    public String getBotUsername() {
        return properties.getProperty("telegram.bot.username", "");
    }

    public int getDiasVencimientoProximo() {
        return Integer.parseInt(properties.getProperty("telegram.dias.vencimiento.proximo", "5"));
    }

    public int getRevisionHora() {
        return Integer.parseInt(properties.getProperty("telegram.revision.hora", "8"));
    }

    public int getRevisionMinuto() {
        return Integer.parseInt(properties.getProperty("telegram.revision.minuto", "0"));
    }

    public boolean isConfigured() {
        String token = getBotToken();
        return token != null && !token.isEmpty() && !token.equals("TU_BOT_TOKEN_AQUI");
    }
}