package org.example.gimnasioproyect.controllers;

import javafx.application.Platform;
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
    @FXML private StackPane loadingPane;
    @FXML private Button btnPerfil;
    @FXML private Button btnCerrarSesion;
    @FXML private Button btnNotificaciones;

    private LoginService loginService;
    private Personal usuarioActual;

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
        lblNombreUsuario.setText(personal.getNombreCompleto());
        lblTipoUsuario.setText(personal.getTipoPersonal().toString());
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
        agregarMenuItem("📋  Rutinas", this::handleRutinas);
        agregarMenuItem("✅  Asistencias", this::handleAsistencias);
        agregarMenuItem("👔  Personal", this::handlePersonal);
        agregarMenuItem("📈  Reportes", this::handleReportes);
    }

    private void cargarMenuEntrenador() {
        agregarMenuItem("📊  Dashboard", this::handleDashboard);
        agregarMenuItem("👥  Mis Clientes", this::handleMisClientes);
        agregarMenuItem("📋  Rutinas", this::handleRutinas);
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


    private void mostrarLoading(boolean mostrar) {
        if (loadingPane != null) {
            loadingPane.setVisible(mostrar);
            if (mostrar) {
                loadingPane.toFront();
            }
        }
    }

    @FXML
    private void handleDashboard() {
        mostrarLoading(true);
        new Thread(() -> {
            try {
                Thread.sleep(300);
                Parent dashboard = HelloApplication.loadFXML("Dashboard");

                Platform.runLater(() -> {
                    contentArea.getChildren().clear();
                    contentArea.getChildren().addAll(dashboard, loadingPane);
                    mostrarLoading(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    mostrarLoading(false);
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "No se pudo cargar el dashboard: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
    }

    private void handleClientes() {
        mostrarLoading(true);
        new Thread(() -> {
            try {
                Thread.sleep(300);
                Parent gestionClientes = HelloApplication.loadFXML("GestionClientes");

                Platform.runLater(() -> {
                    contentArea.getChildren().clear();
                    contentArea.getChildren().addAll(gestionClientes, loadingPane);
                    mostrarLoading(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    mostrarLoading(false);
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "No se pudo cargar la gestión de clientes: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
    }

    private void handleMembresias() {
        mostrarLoading(true);
        new Thread(() -> {
            try {
                Thread.sleep(300);
                Parent gestionMembresias = HelloApplication.loadFXML("GestionMembresias");

                Platform.runLater(() -> {
                    contentArea.getChildren().clear();
                    contentArea.getChildren().addAll(gestionMembresias, loadingPane);
                    mostrarLoading(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    mostrarLoading(false);
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "No se pudo cargar la gestión de membresías: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
    }

    private void handleEntrenadores() {
        mostrarLoading(true);
        new Thread(() -> {
            try {
                Thread.sleep(300);
                Parent gestionEntrenadores = HelloApplication.loadFXML("Entrenadores");

                Platform.runLater(() -> {
                    contentArea.getChildren().clear();
                    contentArea.getChildren().addAll(gestionEntrenadores, loadingPane);
                    mostrarLoading(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    mostrarLoading(false);
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "No se pudo cargar la gestion de entrenadores: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
    }

    private void handleRutinas() {
        mostrarLoading(true);
        new Thread(() -> {
            try {
                Thread.sleep(300);
                Parent gestionRutinas = HelloApplication.loadFXML("Rutinas");

                Platform.runLater(() -> {
                    contentArea.getChildren().clear();
                    contentArea.getChildren().addAll(gestionRutinas, loadingPane);
                    mostrarLoading(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    mostrarLoading(false);
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "No se pudo cargar el registro de rutinas: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
    }

    private void handleAsistencias() {
        mostrarLoading(true);
        new Thread(() -> {
            try {
                Thread.sleep(300);
                Parent gestionAsistencias = HelloApplication.loadFXML("Asistencias");

                Platform.runLater(() -> {
                    contentArea.getChildren().clear();
                    contentArea.getChildren().addAll(gestionAsistencias, loadingPane);
                    mostrarLoading(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    mostrarLoading(false);
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "No se pudo cargar el registro de asistencias: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
    }

    private void handlePersonal() {
        mostrarLoading(true);
        new Thread(() -> {
            try {
                Thread.sleep(300);
                Parent gestionPersonal = HelloApplication.loadFXML("Personal");

                Platform.runLater(() -> {
                    contentArea.getChildren().clear();
                    contentArea.getChildren().addAll(gestionPersonal, loadingPane);
                    mostrarLoading(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    mostrarLoading(false);
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "No se pudo cargar la gestión de personal: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
    }

    private void handleReportes() {
        mostrarLoading(true);
        new Thread(() -> {
            try {
                Thread.sleep(300);
                Parent reportes = HelloApplication.loadFXML("Reportes");

                Platform.runLater(() -> {
                    contentArea.getChildren().clear();
                    contentArea.getChildren().addAll(reportes, loadingPane);
                    mostrarLoading(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    mostrarLoading(false);
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "No se pudo cargar el registro de reportes: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
    }

    private void handleMisClientes() {
        mostrarLoading(true);
        new Thread(() -> {
            try {
                Thread.sleep(300);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gimnasioproyect/MisClientes.fxml"));
                Parent misClientes = loader.load();

                Platform.runLater(() -> {
                    // Obtener el controlador
                    MisClientesController controller = loader.getController();

                    // Pasar el entrenador logueado
                    if (usuarioActual instanceof Entrenadores) {
                        controller.setEntrenador((Entrenadores) usuarioActual);

                        // Cargar en el contentArea
                        contentArea.getChildren().clear();
                        contentArea.getChildren().addAll(misClientes, loadingPane);
                        mostrarLoading(false);
                    } else {
                        mostrarLoading(false);
                        mostrarAlerta(Alert.AlertType.ERROR, "Error", "Usuario no es un entrenador");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    mostrarLoading(false);
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "No se pudo cargar mis clientes: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
    }

    @FXML
    private void handleMiPerfil() {
        mostrarLoading(true);
        new Thread(() -> {
            try {
                Thread.sleep(300);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gimnasioproyect/MiPerfil.fxml"));
                Parent miPerfil = loader.load();

                Platform.runLater(() -> {
                    // Obtener el controlador
                    MiPerfilController controller = loader.getController();

                    // Pasar el usuario logueado
                    controller.setPersonal(usuarioActual);

                    // Cargar en el contentArea
                    contentArea.getChildren().clear();
                    contentArea.getChildren().addAll(miPerfil, loadingPane);
                    mostrarLoading(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    mostrarLoading(false);
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "No se pudo cargar mi perfil: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
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
            Stage stage = (Stage) lblNombreUsuario.getScene().getWindow();

            if (loginService != null) {
                loginService.logout();
            }

            HelloApplication.setRoot("Login");

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