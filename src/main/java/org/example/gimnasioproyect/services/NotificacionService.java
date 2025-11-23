package org.example.gimnasioproyect.services;

import org.example.gimnasioproyect.Utilidades.PlantillaProcesador;
import org.example.gimnasioproyect.model.*;
import org.example.gimnasioproyect.repository.HistorialMensajeTelegramRepository;
import org.example.gimnasioproyect.services.estrategia.EstrategiaNotificacion;
import org.example.gimnasioproyect.services.estrategia.NotificacionFactory;

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

    /**
     * Metodo GENÉRICO para enviar cualquier tipo de notificación.
     *
     * @param tipoMensaje Tipo de mensaje (BIENVENIDA, VENCE_PRONTO, VENCIDO)
     * @param cliente Cliente que recibirá la notificación
     * @param membresia Membresía asociada (puede ser null)
     * @param contexto Información adicional según el tipo (ej: días restantes)
     * @param <T> Tipo del contexto (Long, Void, etc.)
     * @return true si el mensaje fue enviado exitosamente
     */
    public <T> boolean enviarNotificacion(String tipoMensaje,
                                          Clientes cliente,
                                          MembresiaClientes membresia,
                                          T contexto) {
        try {
            //Validar que el cliente tenga chatId
            if (cliente.getChatId() == null || cliente.getChatId().trim().isEmpty()) {
                System.out.println("⚠️ Cliente sin chatId de Telegram: " + cliente.getDocumento());
                return false;
            }

            //Obtener la estrategia correspondiente al tipo de mensaje
            EstrategiaNotificacion<T> estrategia =
                    (EstrategiaNotificacion<T>) NotificacionFactory.obtenerEstrategia(tipoMensaje);

            //Validar el contexto según la estrategia
            if (!estrategia.validarContexto(contexto)) {
                System.err.println("❌ Contexto inválido para " + tipoMensaje);
                return false;
            }

            //Obtener la plantilla de la base de datos
            Optional<MensajesTelegram> plantillaOpt =
                    mensajeTelegramService.obtenerMensajePorTipo(tipoMensaje);

            if (!plantillaOpt.isPresent()) {
                System.err.println("No se encontró la plantilla de " + tipoMensaje);
                return false;
            }

            MensajesTelegram plantilla = plantillaOpt.get();

            //Construir variables usando la estrategia
            Map<String, String> variables = estrategia.construirVariables(cliente, membresia, contexto);

            //Procesar la plantilla (reemplazar variables)
            String mensajeFinal = PlantillaProcesador.procesarPlantilla(plantilla.getContenido(), variables);

            //Enviar el mensaje
            boolean enviado = telegramBotService.enviarMensaje(cliente.getChatId(), mensajeFinal);

            //Guardar en historial
            guardarHistorial(plantilla, cliente, mensajeFinal, enviado);

            if (enviado) {
                System.out.println("Notificación " + tipoMensaje + " enviada a: " + cliente.getNombreCompleto());
            }

            return enviado;

        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("Error de BD al enviar " + tipoMensaje + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ========================================================================
    // MÉTODOS DE COMPATIBILIDAD (para no romper código existente)
    // Estos métodos llaman al método genérico internamente
    // Puedes mantenerlos para backward compatibility o eliminarlos gradualmente
    // ========================================================================

    /**
     * Envía mensaje de BIENVENIDA.
     *
     * @deprecated Usa {@link #enviarNotificacion(String, Clientes, MembresiaClientes, Object)}
     *             con tipoMensaje="BIENVENIDA"
     */
    @Deprecated
    public boolean enviarMensajeBienvenida(Clientes cliente, MembresiaClientes membresia) {
        return enviarNotificacion("BIENVENIDA", cliente, membresia, null);
    }

    /**
     * Envía mensaje de VENCIMIENTO PRÓXIMO.
     *
     * @deprecated Usa {@link #enviarNotificacion(String, Clientes, MembresiaClientes, Object)}
     *             con tipoMensaje="VENCE_PRONTO"
     */
    @Deprecated
    public boolean enviarMensajeVencimientoProximo(Clientes cliente,
                                                   MembresiaClientes membresia,
                                                   long diasRestantes) {
        return enviarNotificacion("VENCE_PRONTO", cliente, membresia, diasRestantes);
    }

    /**
     * Envía mensaje de VENCIDO.
     *
     * @deprecated Usa {@link #enviarNotificacion(String, Clientes, MembresiaClientes, Object)}
     *             con tipoMensaje="VENCIDO"
     */
    @Deprecated
    public boolean enviarMensajeVencido(Clientes cliente, MembresiaClientes membresia) {
        return enviarNotificacion("VENCIDO", cliente, membresia, null);
    }

    /**
     * Guarda el historial del mensaje enviado en la base de datos.
     */
    private void guardarHistorial(MensajesTelegram plantilla,
                                  Clientes cliente,
                                  String mensajeFinal,
                                  boolean enviado) {
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