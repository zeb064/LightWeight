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
    private VBox opcionesAvanzadasContainer;
    private Button btnMostrarMasOpciones;
    private boolean opcionesAvanzadasVisibles = false;

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

        // ✅ Guardar referencia después de que todo esté cargado
        Platform.runLater(() -> {
            if (contentArea != null && contentArea.getScene() != null) {
                javafx.scene.Node root = contentArea.getScene().getRoot();
                root.setUserData(this);
                System.out.println("✅ MenuController guardado en root");
            }
        });

        cargarMenu(personal.getTipoPersonal());
    }

    public static MenuController obtenerMenuController(javafx.scene.Node node) {
        if (node == null || node.getScene() == null) {
            return null;
        }

        javafx.scene.Node root = node.getScene().getRoot();

        // Buscar en el root
        if (root.getUserData() instanceof MenuController) {
            return (MenuController) root.getUserData();
        }

        // Buscar en el contentArea si existe
        if (root.lookup("#contentArea") != null) {
            javafx.scene.Node contentArea = root.lookup("#contentArea");
            if (contentArea.getUserData() instanceof MenuController) {
                return (MenuController) contentArea.getUserData();
            }
        }

        return null;
    }

    /**
     * Obtiene la instancia del MenuController desde el StackPane contentArea
     */
    public static MenuController getInstance(StackPane contentArea) {
        if (contentArea != null && contentArea.getScene() != null) {
            javafx.scene.Node root = contentArea.getScene().getRoot();
            Object userData = root.getUserData();

            if (userData instanceof MenuController) {
                return (MenuController) userData;
            }
        }
        return null;
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
        agregarMenuItem("✅  Asistencias", this::handleAsistencias);

        btnMostrarMasOpciones = new Button("▼  Más opciones");
        btnMostrarMasOpciones.getStyleClass().addAll("menu-item", "menu-item-expandible");
        btnMostrarMasOpciones.setMaxWidth(Double.MAX_VALUE);
        btnMostrarMasOpciones.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        btnMostrarMasOpciones.setOnAction(e -> toggleOpcionesAvanzadas());
        menuContainer.getChildren().add(btnMostrarMasOpciones);

        opcionesAvanzadasContainer = new VBox(5);
        opcionesAvanzadasContainer.setManaged(false);
        opcionesAvanzadasContainer.setVisible(false);
        opcionesAvanzadasContainer.getStyleClass().add("opciones-avanzadas");

        agregarMenuItemAvanzado("🏋️  Entrenadores", this::handleEntrenadores);
        agregarMenuItemAvanzado("📋  Rutinas", this::handleRutinas);
        agregarMenuItemAvanzado("👔  Personal", this::handlePersonal);
        agregarMenuItemAvanzado("📈  Reportes", this::handleReportes);
        agregarMenuItemAvanzado("📱  Plantillas Telegram", this::handlePlantillasTelegram);
        agregarMenuItemAvanzado("📨  Historial Notificaciones", this::handleHistorialNotificaciones);

        menuContainer.getChildren().add(opcionesAvanzadasContainer);
    }

    private void agregarMenuItemAvanzado(String texto, Runnable accion) {
        Button btn = new Button(texto);
        btn.getStyleClass().addAll("menu-item", "menu-item-secundario");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        btn.setOnAction(e -> accion.run());
        opcionesAvanzadasContainer.getChildren().add(btn);
    }

    private void toggleOpcionesAvanzadas() {
        opcionesAvanzadasVisibles = !opcionesAvanzadasVisibles;

        if (opcionesAvanzadasVisibles) {
            btnMostrarMasOpciones.setText("▲  Menos opciones");
            expandirOpciones();
        } else {
            btnMostrarMasOpciones.setText("▼  Más opciones");
            colapsarOpciones();
        }
    }

    private void expandirOpciones() {
        opcionesAvanzadasContainer.setVisible(true);
        opcionesAvanzadasContainer.setManaged(true);

        javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(
                javafx.util.Duration.millis(300), opcionesAvanzadasContainer
        );
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }

    private void colapsarOpciones() {
        javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(
                javafx.util.Duration.millis(200), opcionesAvanzadasContainer
        );
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> {
            opcionesAvanzadasContainer.setVisible(false);
            opcionesAvanzadasContainer.setManaged(false);
        });
        fade.play();
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
        agregarMenuItem("📱  Plantillas Telegram", this::handlePlantillasTelegram);
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

    private void handleClientes() {
        mostrarLoading(true);

        new Thread(() -> {
            try {
                Thread.sleep(300); // simula carga
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Platform.runLater(() -> {
                try {
                    Parent gestionClientes = HelloApplication.loadFXML("GestionClientes");
                    cargarContenido(gestionClientes); // ✅ Usar el nuevo método

                } catch (Exception e) {
                    mostrarLoading(false);
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "No se pudo cargar la gestión de clientes: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }).start();
    }

    @FXML
    private void handleDashboard() {
        mostrarLoading(true);
        new Thread(() -> {
            try {
                Thread.sleep(300);
                Parent dashboard = HelloApplication.loadFXML("Dashboard");
                cargarContenido(dashboard); // ✅
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

    private void handleMembresias() {
        mostrarLoading(true);
        new Thread(() -> {
            try {
                Thread.sleep(300);
                Parent gestionMembresias = HelloApplication.loadFXML("GestionMembresias");
                cargarContenido(gestionMembresias); // ✅
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
                cargarContenido(gestionEntrenadores); // ✅
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
                cargarContenido(gestionRutinas); // ✅
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
                cargarContenido(gestionAsistencias); // ✅
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Platform.runLater(() -> {
                try {
                    Parent gestionPersonal = HelloApplication.loadFXML("Personal");
                    cargarContenido(gestionPersonal); // ✅
                } catch (Exception e) {
                    mostrarLoading(false);
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "No se pudo cargar la gestión de personal: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }).start();
    }

    private void handleReportes() {
        mostrarLoading(true);
        new Thread(() -> {
            try {
                Thread.sleep(300);
                Parent reportes = HelloApplication.loadFXML("Reportes");
                cargarContenido(reportes); // ✅
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
                    MisClientesController controller = loader.getController();

                    if (usuarioActual instanceof Entrenadores) {
                        controller.setEntrenador((Entrenadores) usuarioActual);
                        cargarContenido(misClientes); // ✅
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
                    MiPerfilController controller = loader.getController();
                    controller.setPersonal(usuarioActual);
                    cargarContenido(miPerfil); // ✅
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
    private void handlePlantillasTelegram() {
        mostrarLoading(true);
        new Thread(() -> {
            try {
                Thread.sleep(300);
                Parent telegram = HelloApplication.loadFXML("GestionPlantillas");
                cargarContenido(telegram); // ✅
            } catch (Exception e) {
                Platform.runLater(() -> {
                    mostrarLoading(false);
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "No se pudo cargar la gestion de plantillas telegram: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
    }

    @FXML
    private void handleHistorialNotificaciones() {
        mostrarLoading(true);
        new Thread(() -> {
            try {
                Thread.sleep(300);
                Parent historialNotificaciones = HelloApplication.loadFXML("HistorialNotificaciones");
                cargarContenido(historialNotificaciones);
            } catch (Exception e) {
                Platform.runLater(() -> {
                    mostrarLoading(false);
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "No se pudo cargar el historial de notificaciones: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
    }
    @FXML
    private void handleNotificaciones() {
        handleHistorialNotificaciones();
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

    /**
     * Carga contenido asegurando que el loadingPane siempre esté presente
     */
    private void cargarContenido(Parent contenido) {
        Platform.runLater(() -> {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(contenido);

            // Siempre re-agregar el loadingPane encima
            if (loadingPane != null) {
                contentArea.getChildren().add(loadingPane);
                loadingPane.toFront();
            }

            mostrarLoading(false);
        });
    }

    /**
     * Método público para navegación desde otros controladores
     */
    public void navegarA(String fxmlName) {
        mostrarLoading(true);
        new Thread(() -> {
            try {
                Thread.sleep(300);
                Parent contenido = HelloApplication.loadFXML(fxmlName);
                cargarContenido(contenido);
            } catch (Exception e) {
                Platform.runLater(() -> {
                    mostrarLoading(false);
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "No se pudo cargar la vista: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}