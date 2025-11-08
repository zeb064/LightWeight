package org.example.gimnasioproyect.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.example.gimnasioproyect.Utilidades.ServiceFactory;
import org.example.gimnasioproyect.Utilidades.TipoPersonal;
import org.example.gimnasioproyect.Utilidades.Validador;
import org.example.gimnasioproyect.model.Entrenadores;
import org.example.gimnasioproyect.services.EntrenadorService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class FormularioEntrenadoresController {

    @FXML private Text lblTitulo;
    @FXML private Button btnVolver;
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;

    // Campos del formulario
    @FXML private TextField txtDocumento;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private TextField txtEspecialidad;
    @FXML private TextField txtExperiencia;
    @FXML private DatePicker dpFechaContratacion;

    // Labels de error
    @FXML private Label lblErrorDocumento;
    @FXML private Label lblErrorNombres;
    @FXML private Label lblErrorApellidos;
    @FXML private Label lblErrorTelefono;
    @FXML private Label lblErrorCorreo;
    @FXML private Label lblErrorUsuario;
    @FXML private Label lblErrorContrasena;
    @FXML private Label lblErrorEspecialidad;
    @FXML private Label lblErrorExperiencia;
    @FXML private Label lblErrorFechaContratacion;

    private EntrenadorService entrenadorService;
    private StackPane parentContainer;
    private Entrenadores entrenadorActual;
    private boolean modoEdicion = false;

    public void initialize() {
        // Obtener servicios
        ServiceFactory factory = ServiceFactory.getInstance();
        this.entrenadorService = factory.getEntrenadorService();

        // Establecer fecha de contratación por defecto como hoy
        dpFechaContratacion.setValue(LocalDate.now());

        // Configurar validaciones en tiempo real
        configurarValidaciones();
    }

    private void configurarValidaciones() {
        // Validación de documento
        txtDocumento.textProperty().addListener((obs, old, nuevo) -> {
            validarDocumento();
        });

        // Validación de nombres
        txtNombres.textProperty().addListener((obs, old, nuevo) -> {
            validarNombres();
        });

        // Validación de apellidos
        txtApellidos.textProperty().addListener((obs, old, nuevo) -> {
            validarApellidos();
        });

        // Validación de teléfono
        txtTelefono.textProperty().addListener((obs, old, nuevo) -> {
            validarTelefono();
        });

        // Validación de correo
        txtCorreo.textProperty().addListener((obs, old, nuevo) -> {
            validarCorreo();
        });

        // Validación de usuario
        txtUsuario.textProperty().addListener((obs, old, nuevo) -> {
            validarUsuario();
        });

        // Validación de contraseña
        txtContrasena.textProperty().addListener((obs, old, nuevo) -> {
            validarContrasena();
        });

        // Validación de especialidad
        txtEspecialidad.textProperty().addListener((obs, old, nuevo) -> {
            validarEspecialidad();
        });

        // Validación de experiencia
        txtExperiencia.textProperty().addListener((obs, old, nuevo) -> {
            validarExperiencia();
        });

        // Validación de fecha de contratación
        dpFechaContratacion.valueProperty().addListener((obs, old, nuevo) -> {
            validarFechaContratacion();
        });

        // Permitir solo números en experiencia
        txtExperiencia.textProperty().addListener((obs, old, nuevo) -> {
            if (!nuevo.matches("\\d*")) {
                txtExperiencia.setText(nuevo.replaceAll("[^\\d]", ""));
            }
        });

        // Permitir solo números en documento
        txtDocumento.textProperty().addListener((obs, old, nuevo) -> {
            if (!nuevo.matches("\\d*")) {
                txtDocumento.setText(nuevo.replaceAll("[^\\d]", ""));
            }
        });

        // Permitir solo números en teléfono
        txtTelefono.textProperty().addListener((obs, old, nuevo) -> {
            if (!nuevo.matches("\\d*")) {
                txtTelefono.setText(nuevo.replaceAll("[^\\d]", ""));
            }
        });
    }

    // Métodos de validación individual
    private boolean validarDocumento() {
        try {
            String documento = txtDocumento.getText();
            if (documento == null || documento.trim().isEmpty()) {
                mostrarError(lblErrorDocumento, "El documento es obligatorio");
                return false;
            }
            Validador.validarDocumento(documento);
            limpiarError(lblErrorDocumento);
            return true;
        } catch (IllegalArgumentException e) {
            mostrarError(lblErrorDocumento, e.getMessage());
            return false;
        }
    }

    private boolean validarNombres() {
        try {
            String nombres = txtNombres.getText();
            if (nombres == null || nombres.trim().isEmpty()) {
                mostrarError(lblErrorNombres, "Los nombres son obligatorios");
                return false;
            }
            Validador.validarNombre(nombres, "Nombres");
            limpiarError(lblErrorNombres);
            return true;
        } catch (IllegalArgumentException e) {
            mostrarError(lblErrorNombres, e.getMessage());
            return false;
        }
    }

    private boolean validarApellidos() {
        try {
            String apellidos = txtApellidos.getText();
            if (apellidos == null || apellidos.trim().isEmpty()) {
                mostrarError(lblErrorApellidos, "Los apellidos son obligatorios");
                return false;
            }
            Validador.validarNombre(apellidos, "Apellidos");
            limpiarError(lblErrorApellidos);
            return true;
        } catch (IllegalArgumentException e) {
            mostrarError(lblErrorApellidos, e.getMessage());
            return false;
        }
    }

    private boolean validarTelefono() {
        try {
            String telefono = txtTelefono.getText();
            if (telefono == null || telefono.trim().isEmpty()) {
                mostrarError(lblErrorTelefono, "El teléfono es obligatorio");
                return false;
            }
            Validador.validarTelefono(telefono);
            limpiarError(lblErrorTelefono);
            return true;
        } catch (IllegalArgumentException e) {
            mostrarError(lblErrorTelefono, e.getMessage());
            return false;
        }
    }

    private boolean validarCorreo() {
        try {
            String correo = txtCorreo.getText();
            if (correo == null || correo.trim().isEmpty()) {
                mostrarError(lblErrorCorreo, "El correo es obligatorio");
                return false;
            }
            Validador.validarCorreo(correo);
            limpiarError(lblErrorCorreo);
            return true;
        } catch (IllegalArgumentException e) {
            mostrarError(lblErrorCorreo, e.getMessage());
            return false;
        }
    }

    private boolean validarUsuario() {
        try {
            String usuario = txtUsuario.getText();
            if (usuario == null || usuario.trim().isEmpty()) {
                mostrarError(lblErrorUsuario, "El usuario es obligatorio");
                return false;
            }
            Validador.validarUsuarioSistema(usuario);
            limpiarError(lblErrorUsuario);
            return true;
        } catch (IllegalArgumentException e) {
            mostrarError(lblErrorUsuario, e.getMessage());
            return false;
        }
    }

    private boolean validarContrasena() {
        try {
            String contrasena = txtContrasena.getText();
            if (contrasena == null || contrasena.trim().isEmpty()) {
                mostrarError(lblErrorContrasena, "La contraseña es obligatoria");
                return false;
            }
            Validador.validarContrasena(contrasena);
            limpiarError(lblErrorContrasena);
            return true;
        } catch (IllegalArgumentException e) {
            mostrarError(lblErrorContrasena, e.getMessage());
            return false;
        }
    }

    private boolean validarEspecialidad() {
        try {
            String especialidad = txtEspecialidad.getText();
            if (especialidad == null || especialidad.trim().isEmpty()) {
                mostrarError(lblErrorEspecialidad, "La especialidad es obligatoria");
                return false;
            }
            Validador.validarTexto(especialidad, "Especialidad", 20, true);
            limpiarError(lblErrorEspecialidad);
            return true;
        } catch (IllegalArgumentException e) {
            mostrarError(lblErrorEspecialidad, e.getMessage());
            return false;
        }
    }

    private boolean validarExperiencia() {
        try {
            String experienciaStr = txtExperiencia.getText();
            if (experienciaStr == null || experienciaStr.trim().isEmpty()) {
                mostrarError(lblErrorExperiencia, "La experiencia es obligatoria");
                return false;
            }
            Integer experiencia = Integer.parseInt(experienciaStr);
            Validador.validarNumeroPositivo(experiencia, "Experiencia");
            if (experiencia > 50) {
                mostrarError(lblErrorExperiencia, "La experiencia no puede ser mayor a 50 años");
                return false;
            }
            limpiarError(lblErrorExperiencia);
            return true;
        } catch (NumberFormatException e) {
            mostrarError(lblErrorExperiencia, "La experiencia debe ser un número válido");
            return false;
        } catch (IllegalArgumentException e) {
            mostrarError(lblErrorExperiencia, e.getMessage());
            return false;
        }
    }

    private boolean validarFechaContratacion() {
        try {
            LocalDate fecha = dpFechaContratacion.getValue();
            if (fecha == null) {
                mostrarError(lblErrorFechaContratacion, "La fecha de contratación es obligatoria");
                return false;
            }
            if (fecha.isAfter(LocalDate.now())) {
                mostrarError(lblErrorFechaContratacion, "La fecha de contratación no puede ser futura");
                return false;
            }
            limpiarError(lblErrorFechaContratacion);
            return true;
        } catch (Exception e) {
            mostrarError(lblErrorFechaContratacion, "Fecha inválida");
            return false;
        }
    }

    // Validación completa del formulario
    private boolean validarFormularioCompleto() {
        boolean documentoValido = validarDocumento();
        boolean nombresValido = validarNombres();
        boolean apellidosValido = validarApellidos();
        boolean telefonoValido = validarTelefono();
        boolean correoValido = validarCorreo();
        boolean usuarioValido = validarUsuario();
        boolean contrasenaValida = validarContrasena();
        boolean especialidadValida = validarEspecialidad();
        boolean experienciaValida = validarExperiencia();
        boolean fechaValida = validarFechaContratacion();

        return documentoValido && nombresValido && apellidosValido && telefonoValido &&
                correoValido && usuarioValido && contrasenaValida && especialidadValida &&
                experienciaValida && fechaValida;
    }

    // Cargar entrenador para edición
    public void cargarEntrenador(Entrenadores entrenador) {
        if (entrenador == null) return;

        this.entrenadorActual = entrenador;
        this.modoEdicion = true;

        // Cambiar título
        lblTitulo.setText("Editar Entrenador");

        // Cargar datos
        txtDocumento.setText(entrenador.getDocuEntrenador());
        txtNombres.setText(entrenador.getNombres());
        txtApellidos.setText(entrenador.getApellidos());
        txtTelefono.setText(entrenador.getTelefono());
        txtCorreo.setText(entrenador.getCorreo());
        txtUsuario.setText(entrenador.getUsuarioSistema());
        txtContrasena.setText(entrenador.getContrasena());
        txtEspecialidad.setText(entrenador.getEspecialidad());
        txtExperiencia.setText(String.valueOf(entrenador.getExperiencia()));
        dpFechaContratacion.setValue(entrenador.getFechaContratacion());

        // Deshabilitar edición del documento
        txtDocumento.setDisable(true);
        txtDocumento.setStyle("-fx-background-color: #1a252f; -fx-text-fill: #95a5a6; -fx-opacity: 0.6;");
    }

    // Handlers
    @FXML
    private void handleGuardar() {
        if (!validarFormularioCompleto()) {
            mostrarAlerta("Error de validación",
                    "Por favor corrija los errores en el formulario antes de continuar",
                    Alert.AlertType.ERROR);
            return;
        }

        try {
            if (modoEdicion) {
                actualizarEntrenador();
            } else {
                registrarNuevoEntrenador();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error de base de datos",
                    "Error al guardar el entrenador: " + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Error de validación",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void registrarNuevoEntrenador() throws SQLException {
        Entrenadores nuevoEntrenador = new Entrenadores();

        // El idPersonal será el documento
        nuevoEntrenador.setDocuEntrenador(txtDocumento.getText().trim());
        nuevoEntrenador.setNombres(txtNombres.getText().trim());
        nuevoEntrenador.setApellidos(txtApellidos.getText().trim());
        nuevoEntrenador.setTelefono(txtTelefono.getText().trim());
        nuevoEntrenador.setCorreo(txtCorreo.getText().trim());
        nuevoEntrenador.setUsuarioSistema(txtUsuario.getText().trim());
        nuevoEntrenador.setContrasena(txtContrasena.getText());
        nuevoEntrenador.setEspecialidad(txtEspecialidad.getText().trim());
        nuevoEntrenador.setExperiencia(Integer.parseInt(txtExperiencia.getText().trim()));
        nuevoEntrenador.setFechaContratacion(dpFechaContratacion.getValue());
        nuevoEntrenador.setTipoPersonal(TipoPersonal.ENTRENADOR);

        entrenadorService.registrarEntrenador(nuevoEntrenador);

        mostrarAlerta("Éxito",
                "Entrenador registrado correctamente",
                Alert.AlertType.INFORMATION);

        volverALista();
    }

    private void actualizarEntrenador() throws SQLException {
        // Mantener el documento original
        entrenadorActual.setNombres(txtNombres.getText().trim());
        entrenadorActual.setApellidos(txtApellidos.getText().trim());
        entrenadorActual.setTelefono(txtTelefono.getText().trim());
        entrenadorActual.setCorreo(txtCorreo.getText().trim());
        entrenadorActual.setUsuarioSistema(txtUsuario.getText().trim());
        entrenadorActual.setContrasena(txtContrasena.getText());
        entrenadorActual.setEspecialidad(txtEspecialidad.getText().trim());
        entrenadorActual.setExperiencia(Integer.parseInt(txtExperiencia.getText().trim()));
        entrenadorActual.setFechaContratacion(dpFechaContratacion.getValue());

        entrenadorService.actualizarEntrenador(entrenadorActual);

        mostrarAlerta("Éxito",
                "Entrenador actualizado correctamente",
                Alert.AlertType.INFORMATION);

        volverALista();
    }

    @FXML
    private void handleCancelar() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar cancelación");
        confirmacion.setHeaderText("¿Desea cancelar?");
        confirmacion.setContentText("Los cambios no guardados se perderán");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                volverALista();
            }
        });
    }

    @FXML
    private void handleVolver() {
        handleCancelar();
    }

    private void volverALista() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/org/example/gimnasioproyect/Entrenadores.fxml"));
            Parent vistaEntrenadores = loader.load();

            if (parentContainer != null) {
                parentContainer.getChildren().clear();
                parentContainer.getChildren().add(vistaEntrenadores);
            }
        } catch (IOException e) {
            mostrarAlerta("Error",
                    "No se pudo volver a la lista de entrenadores: " + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // Métodos auxiliares
    private void mostrarError(Label label, String mensaje) {
        label.setText(mensaje);
        label.setVisible(true);
    }

    private void limpiarError(Label label) {
        label.setText("");
        label.setVisible(false);
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void setParentContainer(StackPane parentContainer) {
        this.parentContainer = parentContainer;
    }
}