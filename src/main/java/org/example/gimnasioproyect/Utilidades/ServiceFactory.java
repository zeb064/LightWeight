package org.example.gimnasioproyect.Utilidades;

import org.example.gimnasioproyect.services.*;

public class ServiceFactory {
    private static ServiceFactory instance;

    // Servicios
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
                                   LoginService loginService) {
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
    }

    // Getters
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
}
