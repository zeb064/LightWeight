package org.example.gimnasioproyect.Utilidades;

import org.example.gimnasioproyect.services.*;
import org.example.gimnasioproyect.services.LightWeightBot;

public class ServiceFactory {
    private static ServiceFactory instance;

    // Servicios existentes
    private ClienteServices clienteService;
    private BarrioService barrioService;
    private MembresiaClienteService membresiaClienteService;
    private MembresiaService membresiaService;
    private EntrenadorService entrenadorService;
    private AsistenciaService asistenciaService;
    private RutinaService rutinaService;
    private EstadisticaService estadisticaService;
    private PersonalService personalService;
    private AdministradorService administradorService;
    private RecepcionistaService recepcionistaService;
    private LoginService loginService;
    private MensajeTelegramService mensajeTelegramService;
    private TelegramBotService telegramBotService;
    private NotificacionService notificacionService;
    private LightWeightBot telegramBot;
    private TareaRevisionMembresias tareaRevision;
    private HistorialNotificacionService historialService;

    private ServiceFactory() {}

    public static ServiceFactory getInstance() {
        if (instance == null) {
            instance = new ServiceFactory();
        }
        return instance;
    }

    // Método para inicializar todos los servicios
    public void initializeServices(ClienteServices clienteService,
                                   BarrioService barrioService,
                                   MembresiaClienteService membresiaClienteService,
                                   MembresiaService membresiaService,
                                   EntrenadorService entrenadorService,
                                   AsistenciaService asistenciaService,
                                   RutinaService rutinaService,
                                   EstadisticaService estadisticaService,
                                   PersonalService personalService,
                                   AdministradorService administradorService,
                                   RecepcionistaService recepcionistaService,
                                   LoginService loginService,
                                   MensajeTelegramService mensajeTelegramService,
                                   TelegramBotService telegramBotService,
                                   NotificacionService notificacionService,
                                   LightWeightBot telegramBot,
                                   TareaRevisionMembresias tareaRevision,
                                    HistorialNotificacionService historialService) {
        this.clienteService = clienteService;
        this.barrioService = barrioService;
        this.membresiaClienteService = membresiaClienteService;
        this.membresiaService = membresiaService;
        this.entrenadorService = entrenadorService;
        this.asistenciaService = asistenciaService;
        this.rutinaService = rutinaService;
        this.estadisticaService = estadisticaService;
        this.personalService = personalService;
        this.administradorService = administradorService;
        this.recepcionistaService = recepcionistaService;
        this.loginService = loginService;
        this.mensajeTelegramService = mensajeTelegramService;
        this.telegramBotService = telegramBotService;
        this.notificacionService = notificacionService;
        this.telegramBot = telegramBot;
        this.tareaRevision = tareaRevision;
        this.historialService = historialService;
    }

    // Getters existentes
    public ClienteServices getClienteService() { return clienteService; }
    public BarrioService getBarrioService() { return barrioService; }
    public MembresiaClienteService getMembresiaClienteService() { return membresiaClienteService; }
    public MembresiaService getMembresiaService() { return membresiaService; }
    public EntrenadorService getEntrenadorService() { return entrenadorService; }
    public AsistenciaService getAsistenciaService() { return asistenciaService; }
    public RutinaService getRutinaService() { return rutinaService; }
    public EstadisticaService getEstadisticaService() { return estadisticaService; }
    public PersonalService getPersonalService() { return personalService; }
    public AdministradorService getAdministradorService() { return administradorService; }
    public RecepcionistaService getRecepcionistaService() { return recepcionistaService; }
    public LoginService getLoginService() { return loginService; }
    public MensajeTelegramService getMensajeTelegramService() { return mensajeTelegramService; }
    public TelegramBotService getTelegramBotService() { return telegramBotService; }
    public NotificacionService getNotificacionService() { return notificacionService; }
    public LightWeightBot getTelegramBot() { return telegramBot; }
    public TareaRevisionMembresias getTareaRevision() { return tareaRevision; }
    public HistorialNotificacionService getHistorialService() { return historialService; }
}