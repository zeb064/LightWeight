package org.example.gimnasioproyect;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.gimnasioproyect.Utilidades.ServiceFactory;
import org.example.gimnasioproyect.Utilidades.TipoPersonal;
import org.example.gimnasioproyect.confi.DatabaseConfig;
import org.example.gimnasioproyect.confi.OracleDatabaseConnection;
import org.example.gimnasioproyect.controllers.LoginController;
import org.example.gimnasioproyect.controllers.MenuController;
import org.example.gimnasioproyect.model.Administradores;
import org.example.gimnasioproyect.model.Personal;
import org.example.gimnasioproyect.repository.*;
import org.example.gimnasioproyect.services.*;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.sql.SQLException;

public class HelloApplication extends Application {
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        DatabaseConfig dbConfig = DatabaseConfig.builder()
                .host("localhost")
                .port("1521")
                .service("XEPDB1")
                .user("gymAdmin")
                .password("12345")
                .build();

        OracleDatabaseConnection dbConnection = new OracleDatabaseConnection(dbConfig);

        ClienteRepository clienteRepo = new ClienteRepositoryImpl(dbConnection);
        BarrioRepository barrioRepo = new BarrioRepositoryImpl(dbConnection);
        MembresiaRepository membresiaRepo = new MembresiaRepositoryImpl(dbConnection);
        MembresiaClienteRepository membresiaClienteRepo = new MembresiaClienteRepositoryImpl(dbConnection);
        EntrenadorRepository entrenadorRepo = new EntrenadorRepositoryImpl(dbConnection);
        AsistenciaRepository asistenciaRepo = new AsistenciaRepositoryImpl(dbConnection);
        RutinaRepository rutinaRepo = new RutinaRepositoryImpl(dbConnection);
        DetalleRutinaRepository detalleRutinaRepo = new DetalleRutinaRepositoryImpl(dbConnection);
        RutinaAsignadaRepository rutinaAsignadaRepo = new RutinaAsignadaRepositoryImpl(dbConnection);
        AsignacionEntrenadorRepository asignacionEntrenadorRepo = new AsignacionEntrenadorRepositoryImpl(dbConnection);
        PersonalRepository personalRepo = new PersonalRepositoryImpl(dbConnection);
        AdministradorRepository adminRepo = new AdministradorRepositoryImpl(dbConnection);
        RecepcionistaRepository recepcionistaRepo = new RecepcionistaRepositoryImpl(dbConnection);
        MensajeTelegramRepository mensajeTelegramRepo = new MensajeTelegramRepositoryImpl(dbConnection);
        HistorialMensajeTelegramRepository historialMensajeTelegramRepo = new HistorialMensajeTelegramRepositoryImpl(dbConnection, clienteRepo, mensajeTelegramRepo);
        System.out.println("Repositorios inicializados");

        //Services
        ClienteServices clienteService = new ClienteServices(clienteRepo, membresiaClienteRepo);
        BarrioService barrioService = new BarrioService(barrioRepo);
        MembresiaService membresiaService = new MembresiaService(membresiaRepo);
        MembresiaClienteService membresiaClienteService = new MembresiaClienteService(
                membresiaClienteRepo, clienteRepo, membresiaRepo
        );
        EntrenadorService entrenadorService = new EntrenadorService(
                entrenadorRepo, asignacionEntrenadorRepo, clienteRepo, personalRepo
        );
        AsistenciaService asistenciaService = new AsistenciaService(
                asistenciaRepo, clienteRepo, membresiaClienteRepo
        );
        RutinaService rutinaService = new RutinaService(
                rutinaRepo, detalleRutinaRepo, rutinaAsignadaRepo, clienteRepo
        );
        EstadisticaService estadisticaService = new EstadisticaService(
                asistenciaRepo, membresiaClienteRepo, asignacionEntrenadorRepo,
                clienteRepo, membresiaRepo, rutinaAsignadaRepo
        );
        PersonalService personalService = new PersonalService(personalRepo );
        AdministradorService administradorService = new AdministradorService(
                adminRepo, personalRepo
        );
        RecepcionistaService recepcionistaService = new RecepcionistaService(
                recepcionistaRepo, personalRepo
        );
        MensajeTelegramService mensajeTelegramService = new MensajeTelegramService(
                mensajeTelegramRepo
        );
        TelegramBotService telegramBotService = new TelegramBotService(
        );
        NotificacionService notificacionService = new NotificacionService(
                telegramBotService, mensajeTelegramService, historialMensajeTelegramRepo
        );
        HistorialNotificacionService historialService = new HistorialNotificacionService(
                historialMensajeTelegramRepo
        );
        LoginService loginService = new LoginService(personalRepo);
        System.out.println("Servicios inicializados");

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            LightWeightBot bot = new LightWeightBot(
                    clienteRepo,
                    membresiaClienteService,
                    notificacionService
            );
            botsApi.registerBot(bot);
            System.out.println("Bot de Telegram registrado y en funcionamiento");

            // Iniciar tarea de revisión automática
            TareaRevisionMembresias tareaRevision = new TareaRevisionMembresias(
                    membresiaClienteRepo,
                    notificacionService
            );
            tareaRevision.iniciar();
            System.out.println("Tarea de revisión de membresías iniciada");

        } catch (TelegramApiException e) {
            System.err.println("Error al inicializar bot: " + e.getMessage());
            // Continuar sin bot si falla
        }

        ServiceFactory.getInstance().initializeServices(
                clienteService,
                barrioService,
                membresiaClienteService,
                membresiaService,
                entrenadorService,
                asistenciaService,
                rutinaService,
                estadisticaService,
                personalService,
                administradorService,
                recepcionistaService,
                loginService,
                mensajeTelegramService,
                telegramBotService,
                notificacionService,
                null, // LightWeightBot ya está manejado por TelegramBotsApi
                null, // TareaRevisionMembresias ya está iniciada arriba
                historialService
        );



        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = loader.load();

        LoginController loginController = loader.getController();
        loginController.setLoginService(loginService);

        scene = new Scene(root);
        stage.setTitle("LightWeight - Login");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        // Detener la tarea de revisión al cerrar la aplicación
        if (ServiceFactory.getInstance().getTareaRevision() != null) {
            ServiceFactory.getInstance().getTareaRevision().detener();
        }
        super.stop();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));

        // Si es Login, inyectar LoginService
        if (fxml.equals("Login")) {
            // Obtener el controlador del Login recién cargado
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(fxml + ".fxml"));
            Parent root = loader.load();
            scene.setRoot(root);

            // Inyectar LoginService
            LoginController loginController = loader.getController();
            loginController.setLoginService(ServiceFactory.getInstance().getLoginService());
        }
    }

    public static <T> T setRootAndGetController(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(fxml + ".fxml"));
        Parent root = loader.load();
        scene.setRoot(root);
        return loader.getController();
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }


}
