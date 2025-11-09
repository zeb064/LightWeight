package org.example.gimnasioproyect.controllers;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.gimnasioproyect.HelloApplication;
import org.example.gimnasioproyect.Utilidades.TipoPersonal;
import org.example.gimnasioproyect.model.Entrenadores;
import org.example.gimnasioproyect.model.Personal;
import org.example.gimnasioproyect.services.*;

import java.io.IOException;

public class MenuController {

    @FXML private Label lblNombreUsuario;
    @FXML private Label lblTipoUsuario;
    @FXML private VBox menuContainer;
    @FXML private StackPane contentArea;
    @FXML private Button btnPerfil;
    @FXML private Button btnCerrarSesion;
    @FXML private Button btnNotificaciones;

    private LoginService loginService;
    private Personal usuarioActual;


    // Servicios necesarios
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

    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    // Método para inyectar todos los servicios
    public void setAllServices(ClienteServices clienteService,
                               BarrioService barrioService,
                               MembresiaClienteService membresiaClienteService,
                               MembresiaService membresiaService,
                               EntrenadorService entrenadorService,
                               AsistenciaService asistenciaService,
                               RutinaService rutinaService,
                               EstadisticaService estadisticaService,
                               PersonalService personalService,
                               AdministradorService administradorService,
                               RecepcionistaService recepcionistaService) {
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
    }

    public void inicializarMenu(Personal personal) {
        this.usuarioActual = personal;

        // Configurar header
        lblNombreUsuario.setText(personal.getNombreCompleto());
        lblTipoUsuario.setText(personal.getTipoPersonal().toString());

        // Cargar menú según rol
        cargarMenu(personal.getTipoPersonal());
    }

    private void cargarMenu(TipoPersonal tipoPersonal) {
        menuContainer.getChildren().clear();

        switch (tipoPersonal) {
            case ADMINISTRADOR:
                cargarMenuAdministrador();
                break;
            case ENTRENADOR:
                cargarMenuEntrenador();
                break;
            case RECEPCIONISTA:
                cargarMenuRecepcionista();
                break;
        }
    }

    private void cargarMenuAdministrador() {
        agregarMenuItem("📊  Dashboard", this::handleDashboard);
        agregarMenuItem("👥  Clientes", this::handleClientes);
        agregarMenuItem("💳  Membresías", this::handleMembresias);
        agregarMenuItem("🏋️  Entrenadores", this::handleEntrenadores);
        agregarMenuItem("📝  Rutinas", this::handleRutinas);
        agregarMenuItem("✅  Asistencias", this::handleAsistencias);
        agregarMenuItem("👔  Personal", this::handlePersonal);
        agregarMenuItem("📈  Reportes", this::handleReportes);
    }

    private void cargarMenuEntrenador() {
        agregarMenuItem("📊  Dashboard", this::handleDashboard);
        agregarMenuItem("👥  Mis Clientes", this::handleMisClientes);
        agregarMenuItem("📝  Rutinas", this::handleRutinas);
    }

    private void cargarMenuRecepcionista() {
        agregarMenuItem("📊  Dashboard", this::handleDashboard);
        agregarMenuItem("👥  Clientes", this::handleClientes);
        agregarMenuItem("🏋️  Entrenadores", this::handleEntrenadores);
        agregarMenuItem("✅  Asistencias", this::handleAsistencias);
        agregarMenuItem("💳  Membresías", this::handleMembresias);
    }

    private void agregarMenuItem(String texto, Runnable accion) {
        Button btn = new Button(texto);
        btn.getStyleClass().add("menu-item");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        btn.setOnAction(e -> accion.run());

        menuContainer.getChildren().add(btn);
    }

    // Handlers de menú
    @FXML
    private void handleDashboard() {
        try {
            Parent dashboard = HelloApplication.loadFXML("Dashboard");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(dashboard);
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar el dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleClientes() {
        try {
            // Cargar el FXML
            Parent gestionClientes = HelloApplication.loadFXML("GestionClientes");

            // Reemplazar el contenido del StackPane
            contentArea.getChildren().clear();
            contentArea.getChildren().add(gestionClientes);

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar la gestión de clientes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleMembresias() {
        try {
            Parent gestionMembresias = HelloApplication.loadFXML("GestionMembresias");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(gestionMembresias);
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar la gestión de membresías: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleEntrenadores() {
        try {
            Parent gestionEntrenadores = HelloApplication.loadFXML("Entrenadores");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(gestionEntrenadores);
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar la gestion de entrenadores: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleRutinas() {
        try {
            Parent gestionRutinas = HelloApplication.loadFXML("Rutinas");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(gestionRutinas);
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar el registro de rutinas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleAsistencias() {
        try {
            Parent gestionAsistencias = HelloApplication.loadFXML("Asistencias");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(gestionAsistencias);
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar el registro de asistencias: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handlePersonal() {
        try {
            Parent gestionPersonal = HelloApplication.loadFXML("Personal");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(gestionPersonal);
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar la gestión de personal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleReportes() {
        try {
            Parent reportes = HelloApplication.loadFXML("Reportes");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(reportes);
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar el registro de reportes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleMisClientes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gimnasioproyect/MisClientes.fxml"));
            Parent misClientes = loader.load();

            // Obtener el controlador
            MisClientesController controller = loader.getController();

            // Pasar el entrenador logueado
            if (usuarioActual instanceof Entrenadores) {
                controller.setEntrenador((Entrenadores) usuarioActual);
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Usuario no es un entrenador");
                return;
            }

            // Cargar en el contentArea
            contentArea.getChildren().clear();
            contentArea.getChildren().add(misClientes);

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar mis clientes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMiPerfil() {
        System.out.println("Mi Perfil");
        // TODO: Cargar vista de perfil
    }

    @FXML
    private void handleNotificaciones() {
        System.out.println("Notificaciones");
        // TODO: Mostrar notificaciones
    }

    @FXML
    private void handleCerrarSesion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cerrar Sesión");
        alert.setHeaderText("¿Estás seguro?");
        alert.setContentText("¿Deseas cerrar sesión?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                cerrarSesion();
            }
        });
    }

    private void cerrarSesion() {
        try {
            // Guardar referencia al stage ANTES de cambiar la escena
            Stage stage = (Stage) lblNombreUsuario.getScene().getWindow();

            // Hacer logout
            if (loginService != null) {
                loginService.logout();
            }

            // Cambiar a Login (esto inyectará automáticamente el LoginService)
            HelloApplication.setRoot("Login");

            // Configurar el stage
            if (stage != null) {
                stage.setTitle("LightWeight - Login");
                stage.setMaximized(false);
                stage.centerOnScreen();
            }

            System.out.println("✅ Sesión cerrada correctamente");

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cerrar sesión: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error inesperado",
                    "Ocurrió un error al cerrar sesión: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
