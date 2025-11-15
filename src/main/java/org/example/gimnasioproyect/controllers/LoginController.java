package org.example.gimnasioproyect.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.gimnasioproyect.HelloApplication;
import org.example.gimnasioproyect.model.Personal;
import org.example.gimnasioproyect.services.LoginService;

import java.io.IOException;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private Label lblError;
    @FXML private Button btnLogin;
    @FXML private StackPane loadingPane;

    private LoginService loginService;

    public void initialize() {
        // El LoginService se inyectará desde fuera
        lblError.setText("");

        // Enter key para login
        txtContrasena.setOnAction(event -> handleLogin());
    }

    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    @FXML
    private void handleLogin() {
        String usuario = txtUsuario.getText().trim();
        String contrasena = txtContrasena.getText();

        // Validaciones básicas
        if (usuario.isEmpty()) {
            mostrarError("Por favor ingrese su usuario");
            return;
        }

        if (contrasena.isEmpty()) {
            mostrarError("Por favor ingrese su contraseña");
            return;
        }

        // Mostrar loading
        mostrarLoading(true);
        lblError.setText("");

        // Ejecutar en hilo separado para no bloquear UI
        new Thread(() -> {
            try {
                // Intentar login
                Personal personal = loginService.login(usuario, contrasena);

                // Login exitoso - cambiar a dashboard en el hilo de JavaFX
                Platform.runLater(() -> {
                    try {
                        abrirMenu(personal);
                    } catch (Exception e) {
                        mostrarError("Error al cargar el menu: " + e.getMessage());
                        mostrarLoading(false);
                    }
                });

            } catch (Exception e) {
                // Login fallido
                Platform.runLater(() -> {
                    mostrarError(e.getMessage());
                    mostrarLoading(false);
                });
            }
        }).start();
    }

    private void abrirMenu(Personal personal) throws Exception {
        try {
            // Cambiar raíz y obtener controlador del menú
            MenuController controller = HelloApplication.setRootAndGetController("Menu");

            // Pasar dependencias y datos
            controller.setLoginService(loginService);
            controller.inicializarMenu(personal);

            // Configurar ventana
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setTitle("LightWeight - Menú");
            stage.setMaximized(true);

        } catch (IOException e) {
            mostrarError("Error al cargar el menú: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
    }

    private void mostrarLoading(boolean mostrar) {
        loadingPane.setVisible(mostrar);
        btnLogin.setDisable(mostrar);
        txtUsuario.setDisable(mostrar);
        txtContrasena.setDisable(mostrar);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo, titulo, ButtonType.OK);
        alerta.setContentText(mensaje);
        alerta.show();
    }
}
