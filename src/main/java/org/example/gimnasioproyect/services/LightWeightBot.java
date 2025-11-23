package org.example.gimnasioproyect.services;

import org.example.gimnasioproyect.Utilidades.TelegramConfig;
import org.example.gimnasioproyect.repository.ClienteRepository;
import org.example.gimnasioproyect.services.bot.ComandoHandler;
import org.example.gimnasioproyect.services.bot.comandos.ComandoContext;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Bot de Telegram para LightWeight Gym (REFACTORIZADO).
 *
 * ANTES: 150+ líneas con lógica compleja de comandos hardcodeada
 * AHORA: Bot limpio que solo delega al ComandoHandler
 *
 * Cambios principales:
 * - Eliminado el if-else gigante de comandos
 * - Toda la lógica movida a comandos individuales
 * - Usa patrón Command para procesar mensajes
 *
 * Cumple con SOLID:
 * - S (Single Responsibility): Solo recibe updates y delega
 * - O (Open/Closed): Agregar comandos no requiere modificar esta clase
 * - D (Dependency Inversion): Depende de abstracciones (ComandoHandler)
 *
 * Responsabilidades:
 * - Conectarse a Telegram API
 * - Recibir updates
 * - Delegar al handler
 */
public class LightWeightBot extends TelegramLongPollingBot {

    private final TelegramConfig config;
    private final ComandoHandler comandoHandler;

    /**
     * Constructor del bot.
     *
     * @param clienteRepository Repositorio de clientes
     * @param membresiaClienteService Servicio de membresías
     * @param notificacionService Servicio de notificaciones
     * @param rutinaService Servicio de rutinas
     * @param entrenadorService Servicio de entrenadores
     * @param estadisticaService Servicio de estadísticas
     */
    public LightWeightBot(ClienteRepository clienteRepository,
                          MembresiaClienteService membresiaClienteService,
                          NotificacionService notificacionService,
                          RutinaService rutinaService,
                          EntrenadorService entrenadorService,
                          EstadisticaService estadisticaService) {
        this.config = TelegramConfig.getInstance();

        // Crear el contexto con todas las dependencias
        ComandoContext context = new ComandoContext(
                this,  // this implementa DefaultAbsSender
                clienteRepository,
                membresiaClienteService,
                notificacionService,
                rutinaService,
                entrenadorService,
                estadisticaService
        );

        // Crear el handler con el contexto
        this.comandoHandler = new ComandoHandler(context);

        System.out.println("✅ LightWeightBot inicializado correctamente");
    }

    @Override
    public String getBotUsername() {
        return config.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    /**
     * Método principal que recibe todos los updates de Telegram.
     *
     * ANTES: Tenía un if-else gigante con toda la lógica
     * AHORA: Delega todo al ComandoHandler
     *
     * @param update Update recibido de Telegram
     */
    @Override
    public void onUpdateReceived(Update update) {
        // Logging básico para debugging
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String firstName = update.getMessage().getFrom().getFirstName();

            System.out.println("📨 Mensaje recibido de " + firstName +
                    " (chatId: " + chatId + "): " + messageText);
        }
        // Delegar el procesamiento al handler
        comandoHandler.procesarComando(update);
    }
}