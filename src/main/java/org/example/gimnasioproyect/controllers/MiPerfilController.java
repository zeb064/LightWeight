package org.example.gimnasioproyect.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.example.gimnasioproyect.Utilidades.FormateadorFechas;
import org.example.gimnasioproyect.Utilidades.ServiceFactory;
import org.example.gimnasioproyect.Utilidades.TipoPersonal;
import org.example.gimnasioproyect.model.Administradores;
import org.example.gimnasioproyect.model.Entrenadores;
import org.example.gimnasioproyect.model.Personal;
import org.example.gimnasioproyect.model.Recepcionistas;
import org.example.gimnasioproyect.services.*;

import java.io.IOException;
import java.sql.SQLException;

public class MiPerfilController {

    // Header
    @FXML private Text lblTipoUsuario;
    @FXML private Button btnEditarPerfil;

    // Información Personal
    @FXML private Label lblDocumento;
    @FXML private Label lblNombres;
    @FXML private Label lblApellidos;
    @FXML private Label lblTipoPersonal;
    @FXML private Label lblTelefono;
    @FXML private Label lblCorreo;
    @FXML private Label lblFechaContratacion;

    // Información específica
    @FXML private VBox boxInfoEspecifica;

    // Sistema
    @FXML private Label lblUsuario;

    // Servicios
    private PersonalService personalService;
    private AdministradorService administradorService;
    private EntrenadorService entrenadorService;
    private RecepcionistaService recepcionistaService;

    // Datos
    private Personal personalActual;

    public void initialize() {
        // Obtener servicios
        ServiceFactory factory = ServiceFactory.getInstance();
        this.personalService = factory.getPersonalService();
        this.administradorService = factory.getAdministradorService();
        this.entrenadorService = factory.getEntrenadorService();
        this.recepcionistaService = factory.getRecepcionistaService();
    }

    public void setPersonal(Personal personal) {
        this.personalActual = personal;
        cargarInformacion();
    }

    private void cargarInformacion() {
        if (personalActual == null) {
            mostrarError("Error", "No se pudo cargar la información del usuario");
            return;
        }

        // Información básica
        lblTipoUsuario.setText(obtenerTituloSegunRol());
        lblDocumento.setText(personalActual.getDocumento());
        lblNombres.setText(personalActual.getNombres());
        lblApellidos.setText(personalActual.getApellidos());
        lblTipoPersonal.setText(personalActual.getTipoPersonal().toString());
        lblTelefono.setText(personalActual.getTelefono() != null ? personalActual.getTelefono() : "No especificado");
        lblCorreo.setText(personalActual.getCorreo() != null ? personalActual.getCorreo() : "No especificado");
        lblFechaContratacion.setText(FormateadorFechas.formatearFecha(personalActual.getFechaContratacion()));
        lblUsuario.setText(personalActual.getUsuarioSistema());

        // Cargar información específica según el rol
        cargarInformacionEspecifica();
    }

    private String obtenerTituloSegunRol() {
        TipoPersonal tipo = personalActual.getTipoPersonal();
        switch (tipo) {
            case ADMINISTRADOR:
                return "Perfil de Administrador";
            case ENTRENADOR:
                return "Perfil de Entrenador";
            case RECEPCIONISTA:
                return "Perfil de Recepcionista";
            default:
                return "Perfil de Usuario";
        }
    }

    private void cargarInformacionEspecifica() {
        boxInfoEspecifica.getChildren().clear();

        // Header
        HBox header = new HBox(10);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Text icono = new Text(obtenerIconoSegunRol());
        icono.setStyle("-fx-fill: #9b59b6;");
        icono.setFont(Font.font(20));

        Text titulo = new Text("Información " + obtenerNombreRol());
        titulo.setStyle("-fx-fill: #ecf0f1;");
        titulo.setFont(Font.font("System Bold", 18));

        header.getChildren().addAll(icono, titulo);
        boxInfoEspecifica.getChildren().add(header);

        // Separator
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #2c3e50;");
        boxInfoEspecifica.getChildren().add(separator);

        // Grid con información específica
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        VBox.setMargin(grid, new Insets(10, 0, 0, 0));

        if (personalActual instanceof Administradores) {
            cargarInfoAdministrador(grid, (Administradores) personalActual);
        } else if (personalActual instanceof Entrenadores) {
            cargarInfoEntrenador(grid, (Entrenadores) personalActual);
        } else if (personalActual instanceof Recepcionistas) {
            cargarInfoRecepcionista(grid, (Recepcionistas) personalActual);
        }

        boxInfoEspecifica.getChildren().add(grid);
    }

    private void cargarInfoAdministrador(GridPane grid, Administradores admin) {
        agregarCampo(grid, "Cargo", admin.getCargo(), 0, 0);
        agregarCampo(grid, "ID Personal", String.valueOf(admin.getIdPersonal()), 1, 0);
    }

    private void cargarInfoEntrenador(GridPane grid, Entrenadores entrenador) {
        agregarCampo(grid, "Especialidad", entrenador.getEspecialidad(), 0, 0);
        agregarCampo(grid, "Experiencia", entrenador.getExperiencia() + " años", 1, 0);
        agregarCampo(grid, "ID Personal", String.valueOf(entrenador.getIdPersonal()), 2, 0);
    }

    private void cargarInfoRecepcionista(GridPane grid, Recepcionistas recepcionista) {
        agregarCampo(grid, "Turno/Horario", recepcionista.getHorarioTurno(), 0, 0);
        agregarCampo(grid, "ID Personal", String.valueOf(recepcionista.getIdPersonal()), 1, 0);
    }

    private void agregarCampo(GridPane grid, String label, String valor, int col, int row) {
        VBox vbox = new VBox(5);

        Label lblCampo = new Label(label);
        lblCampo.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 11px;");

        Label lblValor = new Label(valor != null ? valor : "No especificado");
        lblValor.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 14px; -fx-font-weight: bold;");

        vbox.getChildren().addAll(lblCampo, lblValor);
        grid.add(vbox, col, row);
    }

    private String obtenerIconoSegunRol() {
        TipoPersonal tipo = personalActual.getTipoPersonal();
        switch (tipo) {
            case ADMINISTRADOR: return "👨‍💼";
            case ENTRENADOR: return "🏋️";
            case RECEPCIONISTA: return "📞";
            default: return "👤";
        }
    }

    private String obtenerNombreRol() {
        TipoPersonal tipo = personalActual.getTipoPersonal();
        switch (tipo) {
            case ADMINISTRADOR: return "del Administrador";
            case ENTRENADOR: return "del Entrenador";
            case RECEPCIONISTA: return "del Recepcionista";
            default: return "del Usuario";
        }
    }

    // Handlers
//    @FXML
//    private void handleEditarPerfil() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gimnasioproyect/EditarPerfil.fxml"));
//            Parent root = loader.load();
//
//            EditarPerfilController controller = loader.getController();
//            controller.setPersonal(personalActual);
//            controller.setOnSuccess(success -> {
//                if (success) {
//                    // Recargar información actualizada
//                    recargarPersonal();
//                }
//            });
//
//            Stage stage = new Stage();
//            stage.setTitle("Editar Perfil");
//            stage.setScene(new Scene(root));
//            stage.setResizable(false);
//            stage.initModality(Modality.APPLICATION_MODAL);
//            stage.initOwner(btnEditarPerfil.getScene().getWindow());
//            stage.showAndWait();
//
//        } catch (IOException e) {
//            mostrarError("Error", "No se pudo abrir el formulario de edición: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

    @FXML
    private void handleCambiarContrasena() {
        // Crear diálogo personalizado
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Cambiar Contraseña");
        dialog.setHeaderText("Actualiza tu contraseña de acceso");
        //dialog.initOwner(btnCambiarContraseña.getScene().getWindow());

        // Botones
        ButtonType btnCambiar = new ButtonType("Cambiar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnCambiar, btnCancelar);

        // Crear campos
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        PasswordField txtContrasenaActual = new PasswordField();
        txtContrasenaActual.setPromptText("Contraseña actual");
        txtContrasenaActual.setPrefWidth(250);

        PasswordField txtNuevaContrasena = new PasswordField();
        txtNuevaContrasena.setPromptText("Nueva contraseña");
        txtNuevaContrasena.setPrefWidth(250);

        PasswordField txtConfirmarContrasena = new PasswordField();
        txtConfirmarContrasena.setPromptText("Confirmar contraseña");
        txtConfirmarContrasena.setPrefWidth(250);

        grid.add(new Label("Contraseña Actual:"), 0, 0);
        grid.add(txtContrasenaActual, 1, 0);
        grid.add(new Label("Nueva Contraseña:"), 0, 1);
        grid.add(txtNuevaContrasena, 1, 1);
        grid.add(new Label("Confirmar:"), 0, 2);
        grid.add(txtConfirmarContrasena, 1, 2);

        // Agregar nota de requisitos
        Label lblRequisitos = new Label("Requisitos: Entre 6 y 15 caracteres");
        lblRequisitos.setStyle("-fx-text-fill: gray; -fx-font-size: 10px;");
        grid.add(lblRequisitos, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Validar antes de cerrar
        Button btnConfirmar = (Button) dialog.getDialogPane().lookupButton(btnCambiar);
        btnConfirmar.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String contrasenaActual = txtContrasenaActual.getText();
            String nuevaContrasena = txtNuevaContrasena.getText();
            String confirmarContrasena = txtConfirmarContrasena.getText();

            // Validaciones
            if (contrasenaActual.trim().isEmpty() || nuevaContrasena.trim().isEmpty() || confirmarContrasena.trim().isEmpty()) {
                mostrarError("Error", "Todos los campos son obligatorios");
                event.consume();
                return;
            }

            if (!nuevaContrasena.equals(confirmarContrasena)) {
                mostrarError("Error", "Las contraseñas nuevas no coinciden");
                event.consume();
                return;
            }

            if (nuevaContrasena.length() < 6 || nuevaContrasena.length() > 15) {
                mostrarError("Error", "La contraseña debe tener entre 6 y 15 caracteres");
                event.consume();
                return;
            }

            // Intentar cambiar contraseña
            try {
                personalService.cambiarContrasena(
                        personalActual.getUsuarioSistema(),
                        contrasenaActual,
                        nuevaContrasena
                );
                mostrarExito("Contraseña actualizada correctamente");
            } catch (SQLException e) {
                mostrarError("Error", "Error al cambiar la contraseña: " + e.getMessage());
                event.consume();
            } catch (IllegalArgumentException e) {
                mostrarError("Error", e.getMessage());
                event.consume();
            }
        });

        dialog.showAndWait();
    }

    private void recargarPersonal() {
        try {
            // Buscar el personal actualizado según su tipo
            Personal personalActualizado = null;

            if (personalActual instanceof Administradores) {
                personalActualizado = administradorService.buscarAdministradorPorDocumento(
                        personalActual.getDocumento()
                ).orElse(null);
            } else if (personalActual instanceof Entrenadores) {
                personalActualizado = entrenadorService.buscarEntrenadorPorDocumento(
                        personalActual.getDocumento()
                ).orElse(null);
            } else if (personalActual instanceof Recepcionistas) {
                personalActualizado = recepcionistaService.buscarRecepcionistaPorDocumento(
                        personalActual.getDocumento()
                ).orElse(null);
            }

            if (personalActualizado != null) {
                this.personalActual = personalActualizado;
                cargarInformacion();
                mostrarExito("Perfil actualizado correctamente");
            }

        } catch (SQLException e) {
            mostrarError("Error", "No se pudo recargar la información: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}