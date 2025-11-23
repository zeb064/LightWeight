package org.example.gimnasioproyect.services.bot.comandos;

import org.example.gimnasioproyect.repository.ClienteRepository;
import org.example.gimnasioproyect.services.*;
import org.telegram.telegrambots.bots.DefaultAbsSender;

/**
 * Contexto compartido para todos los comandos del bot.
 * Contiene todas las dependencias que los comandos pueden necesitar:
 * - Repositorios para consultar datos
 * - Servicios de negocio
 * - Bot sender para enviar mensajes
 * PATRÓN CONTEXT: Evita que cada comando tenga que recibir múltiples parámetros.
 * En lugar de pasar 5-6 dependencias, pasamos un solo objeto contexto.
 */
public class ComandoContext {

    private final DefaultAbsSender botSender;
    private final ClienteRepository clienteRepository;
    private final MembresiaClienteService membresiaClienteService;
    private final NotificacionService notificacionService;
    private final RutinaService rutinaService;
    private final EntrenadorService entrenadorService;
    private final EstadisticaService estadisticaService;

    /**
     * Constructor con todas las dependencias necesarias.
     *
     * @param botSender               Para enviar mensajes a través del bot
     * @param clienteRepository       Para consultar/actualizar clientes
     * @param membresiaClienteService Para consultar membresías
     * @param notificacionService     Para enviar notificaciones
     * @param rutinaService           Para gestionar rutinas de entrenamiento
     * @param entrenadorService       Para gestionar entrenadores
     * @param estadisticaService      Para consultar estadísticas de asistencia
     */
    public ComandoContext(DefaultAbsSender botSender,
                          ClienteRepository clienteRepository,
                          MembresiaClienteService membresiaClienteService,
                          NotificacionService notificacionService,
                          RutinaService rutinaService,
                          EntrenadorService entrenadorService,
                          EstadisticaService estadisticaService){
        this.botSender = botSender;
        this.clienteRepository = clienteRepository;
        this.membresiaClienteService = membresiaClienteService;
        this.notificacionService = notificacionService;
        this.rutinaService = rutinaService;
        this.entrenadorService = entrenadorService;
        this.estadisticaService = estadisticaService;
    }

    public DefaultAbsSender getBotSender() {
        return botSender;
    }

    public ClienteRepository getClienteRepository() {
        return clienteRepository;
    }

    public MembresiaClienteService getMembresiaClienteService() {
        return membresiaClienteService;
    }

    public NotificacionService getNotificacionService() {
        return notificacionService;
    }

    public RutinaService getRutinaService() {
        return rutinaService;
    }

    public EntrenadorService getEntrenadorService() {
        return entrenadorService;
    }

    public EstadisticaService getEstadisticaService() {
        return estadisticaService;
    }
}