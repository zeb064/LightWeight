package org.example.gimnasioproyect.services;

import org.example.gimnasioproyect.Utilidades.CalculadoraFechas;
import org.example.gimnasioproyect.Utilidades.PlantillaProcesador;
import org.example.gimnasioproyect.model.*;
import org.example.gimnasioproyect.repository.HistorialMensajeTelegramRepository;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;

public class NotificacionService {
    private final TelegramBotService telegramBotService;
    private final MensajeTelegramService mensajeTelegramService;
    private final HistorialMensajeTelegramRepository historialRepository;

    public NotificacionService(TelegramBotService telegramBotService,
                               MensajeTelegramService mensajeTelegramService,
                               HistorialMensajeTelegramRepository historialRepository) {
        this.telegramBotService = telegramBotService;
        this.mensajeTelegramService = mensajeTelegramService;
        this.historialRepository = historialRepository;
    }

    // Envía mensaje de BIENVENIDA
    public boolean enviarMensajeBienvenida(Clientes cliente, MembresiaClientes membresia) {
        try {
            // Verificar que el cliente tenga chatId
            if (cliente.getChatId() == null || cliente.getChatId().trim().isEmpty()) {
                System.out.println("⚠️ Cliente sin chatId de Telegram: " + cliente.getDocumento());
                return false;
            }

            // Obtener plantilla
            Optional<MensajesTelegram> plantillaOpt = mensajeTelegramService.obtenerMensajePorTipo("BIENVENIDA");
            if (!plantillaOpt.isPresent()) {
                System.err.println("❌ No se encontró la plantilla de BIENVENIDA");
                return false;
            }

            MensajesTelegram plantilla = plantillaOpt.get();

            // Procesar plantilla
            Map<String, String> variables = PlantillaProcesador.crearVariablesBienvenida(cliente, membresia);
            String mensajeFinal = PlantillaProcesador.procesarPlantilla(plantilla.getContenido(), variables);

            // Enviar mensaje
            boolean enviado = telegramBotService.enviarMensaje(cliente.getChatId(), mensajeFinal);

            // Guardar en historial
            guardarHistorial(plantilla, cliente, mensajeFinal, enviado);

            return enviado;

        } catch (SQLException e) {
            System.err.println("❌ Error al enviar mensaje de bienvenida: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Envía mensaje de VENCE_PRONTO
    public boolean enviarMensajeVencimientoProximo(Clientes cliente, MembresiaClientes membresia, long diasRestantes) {
        try {
            // Verificar que el cliente tenga chatId
            if (cliente.getChatId() == null || cliente.getChatId().trim().isEmpty()) {
                System.out.println("⚠️ Cliente sin chatId de Telegram: " + cliente.getDocumento());
                return false;
            }

            // Obtener plantilla
            Optional<MensajesTelegram> plantillaOpt = mensajeTelegramService.obtenerMensajePorTipo("VENCE_PRONTO");
            if (!plantillaOpt.isPresent()) {
                System.err.println("❌ No se encontró la plantilla de VENCE_PRONTO");
                return false;
            }

            MensajesTelegram plantilla = plantillaOpt.get();

            // Procesar plantilla
            Map<String, String> variables = PlantillaProcesador.crearVariablesVencimientoProximo(cliente, membresia, diasRestantes);
            String mensajeFinal = PlantillaProcesador.procesarPlantilla(plantilla.getContenido(), variables);

            // Enviar mensaje
            boolean enviado = telegramBotService.enviarMensaje(cliente.getChatId(), mensajeFinal);

            // Guardar en historial
            guardarHistorial(plantilla, cliente, mensajeFinal, enviado);

            return enviado;

        } catch (SQLException e) {
            System.err.println("❌ Error al enviar mensaje de vencimiento próximo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Envía mensaje de VENCIDO
    public boolean enviarMensajeVencido(Clientes cliente, MembresiaClientes membresia) {
        try {
            // Verificar que el cliente tenga chatId
            if (cliente.getChatId() == null || cliente.getChatId().trim().isEmpty()) {
                System.out.println("⚠️ Cliente sin chatId de Telegram: " + cliente.getDocumento());
                return false;
            }

            // Obtener plantilla
            Optional<MensajesTelegram> plantillaOpt = mensajeTelegramService.obtenerMensajePorTipo("VENCIDO");
            if (!plantillaOpt.isPresent()) {
                System.err.println("❌ No se encontró la plantilla de VENCIDO");
                return false;
            }

            MensajesTelegram plantilla = plantillaOpt.get();

            // Procesar plantilla
            Map<String, String> variables = PlantillaProcesador.crearVariablesVencido(cliente, membresia);
            String mensajeFinal = PlantillaProcesador.procesarPlantilla(plantilla.getContenido(), variables);

            // Enviar mensaje
            boolean enviado = telegramBotService.enviarMensaje(cliente.getChatId(), mensajeFinal);

            // Guardar en historial
            guardarHistorial(plantilla, cliente, mensajeFinal, enviado);

            return enviado;

        } catch (SQLException e) {
            System.err.println("❌ Error al enviar mensaje de vencimiento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Guarda el historial del mensaje enviado
    private void guardarHistorial(MensajesTelegram plantilla, Clientes cliente, String mensajeFinal, boolean enviado) {
        try {
            HistorialMensajeTelegram historial = new HistorialMensajeTelegram();
            historial.setMensaje(plantilla);
            historial.setClientes(cliente);
            historial.setMensajeFinal(mensajeFinal);
            historial.setFechaEnvio(new Timestamp(System.currentTimeMillis()));
            historial.setEstado(enviado ? "ENVIADO" : "FALLIDO");
            historial.setChatId(cliente.getChatId());

            historialRepository.save(historial);

        } catch (SQLException e) {
            System.err.println("⚠️ Error al guardar historial: " + e.getMessage());
        }
    }
}