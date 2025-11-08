package org.example.gimnasioproyect.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.gimnasioproyect.Utilidades.ServiceFactory;
import org.example.gimnasioproyect.Utilidades.TipoPersonal;
import org.example.gimnasioproyect.Utilidades.Validador;
import org.example.gimnasioproyect.model.Administradores;
import org.example.gimnasioproyect.model.Entrenadores;
import org.example.gimnasioproyect.model.Personal;
import org.example.gimnasioproyect.model.Recepcionistas;
import org.example.gimnasioproyect.services.AdministradorService;
import org.example.gimnasioproyect.services.EntrenadorService;
import org.example.gimnasioproyect.services.RecepcionistaService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class FormularioPersonalController {

    @FXML private Text lblTitulo;
    @FXML private Button btnVolver;
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;

    // Tipo de personal
    @FXML private ComboBox<String> cmbTipoPersonal;
    @FXML private Label lblErrorTipoPersonal;

    // Secciones del formulario
    @FXML private VBox seccionInformacionPersonal;
    @FXML private VBox seccionContacto;
    @FXML private VBox seccionCredenciales;
    @FXML private VBox seccionAdministrador;
    @FXML private VBox seccionEntrenador;
    @FXML private VBox seccionRecepcionista;

    // Campos comunes
    @FXML private TextField txtDocumento;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private DatePicker dpFechaContratacion;

    // Campos específicos - Administrador
    @FXML private TextField txtCargo;

    // Campos específicos - Entrenador
    @FXML private TextField txtEspecialidad;
    @FXML private TextField txtExperiencia;

    // Campos específicos - Recepcionista
    @FXML private ComboBox<String> cmbHorarioTurno;

    // Labels de error
    @FXML private Label lblErrorDocumento;
    @FXML private Label lblErrorNombres;
    @FXML private Label lblErrorApellidos;
    @FXML private Label lblErrorTelefono;
    @FXML private Label lblErrorCorreo;
    @FXML private Label lblErrorUsuario;
    @FXML private Label lblErrorContrasena;
    @FXML private Label lblErrorFechaContratacion;
    @FXML private Label lblErrorCargo;
    @FXML private Label lblErrorEspecialidad;
    @FXML private Label lblErrorExperiencia;
    @FXML private Label lblErrorHorarioTurno;

    // Servicios
    private AdministradorService administradorService;
    private EntrenadorService entrenadorService;
    private RecepcionistaService recepcionistaService;

    private StackPane parentContainer;
    private Personal personalActual;
    private boolean modoEdicion = false;

    public void initialize() {
        // Obtener servicios
        ServiceFactory factory = ServiceFactory.getInstance();
        this.administradorService = factory.getAdministradorService();
        this.entrenadorService = factory.getEntrenadorService();
        this.recepcionistaService = factory.getRecepcionistaService();

        // Configurar ComboBox de tipo de personal
        cmbTipoPersonal.getItems().addAll("ADMINISTRADOR", "ENTRENADOR", "RECEPCIONISTA");

        // Configurar ComboBox de horario turno
        cmbHorarioTurno.getItems().addAll("MAÑANA", "TARDE", "NOCHE", "MADRUGADA");

        // Listener para cambio de tipo
        cmbTipoPersonal.setOnAction(e -> onTipoPersonalChanged());

        // Establecer fecha de contratación por defecto
        dpFechaContratacion.setValue(LocalDate.now());

        // Configurar validaciones
        configurarValidaciones();
    }

    private void onTipoPersonalChanged() {
        String tipoSeleccionado = cmbTipoPersonal.getValue();

        if (tipoSeleccionado == null) {
            ocultarTodasLasSecciones();
            return;
        }

        // Mostrar secciones comunes
        mostrarSeccionesComunes();

        // Ocultar todas las secciones específicas primero
        seccionAdministrador.setVisible(false);
        seccionAdministrador.setManaged(false);
        seccionEntrenador.setVisible(false);
        seccionEntrenador.setManaged(false);
        seccionRecepcionista.setVisible(false);
        seccionRecepcionista.setManaged(false);

        // Mostrar la sección específica según el tipo
        switch (tipoSeleccionado) {
            case "ADMINISTRADOR":
                seccionAdministrador.setVisible(true);
                seccionAdministrador.setManaged(true);
                break;
            case "ENTRENADOR":
                seccionEntrenador.setVisible(true);
                seccionEntrenador.setManaged(true);
                break;
            case "RECEPCIONISTA":
                seccionRecepcionista.setVisible(true);
                seccionRecepcionista.setManaged(true);
                break;
        }

        limpiarError(lblErrorTipoPersonal);
    }

    private void mostrarSeccionesComunes() {
        seccionInformacionPersonal.setVisible(true);
        seccionInformacionPersonal.setManaged(true);
        seccionContacto.setVisible(true);
        seccionContacto.setManaged(true);
        seccionCredenciales.setVisible(true);
        seccionCredenciales.setManaged(true);
    }

    private void ocultarTodasLasSecciones() {
        seccionInformacionPersonal.setVisible(false);
        seccionInformacionPersonal.setManaged(false);
        seccionContacto.setVisible(false);
        seccionContacto.setManaged(false);
        seccionCredenciales.setVisible(false);
        seccionCredenciales.setManaged(false);
        seccionAdministrador.setVisible(false);
        seccionAdministrador.setManaged(false);
        seccionEntrenador.setVisible(false);
        seccionEntrenador.setManaged(false);
        seccionRecepcionista.setVisible(false);
        seccionRecepcionista.setManaged(false);
    }

    private void configurarValidaciones() {
        // Validaciones en tiempo real
        txtDocumento.textProperty().addListener((obs, old, nuevo) -> {
            if (!nuevo.matches("\\d*")) {
                txtDocumento.setText(nuevo.replaceAll("[^\\d]", ""));
            }
            validarDocumento();
        });

        txtNombres.textProperty().addListener((obs, old, nuevo) -> validarNombres());
        txtApellidos.textProperty().addListener((obs, old, nuevo) -> validarApellidos());

        txtTelefono.textProperty().addListener((obs, old, nuevo) -> {
            if (!nuevo.matches("\\d*")) {
                txtTelefono.setText(nuevo.replaceAll("[^\\d]", ""));
            }
            validarTelefono();
        });

        txtCorreo.textProperty().addListener((obs, old, nuevo) -> validarCorreo());
        txtUsuario.textProperty().addListener((obs, old, nuevo) -> validarUsuario());
        txtContrasena.textProperty().addListener((obs, old, nuevo) -> validarContrasena());
        dpFechaContratacion.valueProperty().addListener((obs, old, nuevo) -> validarFechaContratacion());

        // Específicos
        txtCargo.textProperty().addListener((obs, old, nuevo) -> validarCargo());
        txtEspecialidad.textProperty().addListener((obs, old, nuevo) -> validarEspecialidad());

        txtExperiencia.textProperty().addListener((obs, old, nuevo) -> {
            if (!nuevo.matches("\\d*")) {
                txtExperiencia.setText(nuevo.replaceAll("[^\\d]", ""));
            }
            validarExperiencia();
        });
    }

    // Métodos de validación
    private boolean validarTipoPersonal() {
        if (cmbTipoPersonal.getValue() == null) {
            mostrarError(lblErrorTipoPersonal, "Debe seleccionar un tipo de personal");
            return false;
        }
        limpiarError(lblErrorTipoPersonal);
        return true;
    }

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

    private boolean validarFechaContratacion() {
        try {
            LocalDate fecha = dpFechaContratacion.getValue();
            if (fecha == null) {
                mostrarError(lblErrorFechaContratacion, "La fecha de contratación es obligatoria");
                return false;
            }
            if (fecha.isAfter(LocalDate.now())) {
                mostrarError(lblErrorFechaContratacion, "La fecha no puede ser futura");
                return false;
            }
            limpiarError(lblErrorFechaContratacion);
            return true;
        } catch (Exception e) {
            mostrarError(lblErrorFechaContratacion, "Fecha inválida");
            return false;
        }
    }

    private boolean validarCargo() {
        try {
            String cargo = txtCargo.getText();
            if (cargo == null || cargo.trim().isEmpty()) {
                mostrarError(lblErrorCargo, "El cargo es obligatorio");
                return false;
            }
            Validador.validarTexto(cargo, "Cargo", 20, true);
            limpiarError(lblErrorCargo);
            return true;
        } catch (IllegalArgumentException e) {
            mostrarError(lblErrorCargo, e.getMessage());
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

    private boolean validarFormularioCompleto() {
        boolean tipoValido = validarTipoPersonal();
        boolean documentoValido = validarDocumento();
        boolean nombresValido = validarNombres();
        boolean apellidosValido = validarApellidos();
        boolean telefonoValido = validarTelefono();
        boolean correoValido = validarCorreo();
        boolean usuarioValido = validarUsuario();
        boolean contrasenaValida = validarContrasena();
        boolean fechaValida = validarFechaContratacion();

        String tipoPersonal = cmbTipoPersonal.getValue();
        boolean camposEspecificosValidos = true;

        if ("ADMINISTRADOR".equals(tipoPersonal)) {
            camposEspecificosValidos = validarCargo();
        } else if ("ENTRENADOR".equals(tipoPersonal)) {
            camposEspecificosValidos = validarEspecialidad() && validarExperiencia();
        }
        // Recepcionista no tiene campos obligatorios específicos

        return tipoValido && documentoValido && nombresValido && apellidosValido &&
                telefonoValido && correoValido && usuarioValido && contrasenaValida &&
                fechaValida && camposEspecificosValidos;
    }

    // Cargar personal para edición
    public void cargarPersonal(Personal personal) {
        if (personal == null) return;

        this.personalActual = personal;
        this.modoEdicion = true;

        lblTitulo.setText("Editar Personal");

        // Determinar tipo y cargar datos comunes
        String tipo = personal.getTipoPersonal().toString();
        cmbTipoPersonal.setValue(tipo);
        cmbTipoPersonal.setDisable(true);

        // Datos comunes
        txtDocumento.setText(personal.getDocumento());
        txtNombres.setText(personal.getNombres());
        txtApellidos.setText(personal.getApellidos());
        txtTelefono.setText(personal.getTelefono());
        txtCorreo.setText(personal.getCorreo());
        txtUsuario.setText(personal.getUsuarioSistema());
        txtContrasena.setText(personal.getContrasena());
        dpFechaContratacion.setValue(personal.getFechaContratacion());

        // Deshabilitar documento
        txtDocumento.setDisable(true);
        txtDocumento.setStyle("-fx-background-color: #1a252f; -fx-text-fill: #95a5a6; -fx-opacity: 0.6;");

        // Cargar datos específicos
        if (personal instanceof Administradores) {
            Administradores admin = (Administradores) personal;
            txtCargo.setText(admin.getCargo());
        } else if (personal instanceof Entrenadores) {
            Entrenadores entrenador = (Entrenadores) personal;
            txtEspecialidad.setText(entrenador.getEspecialidad());
            txtExperiencia.setText(String.valueOf(entrenador.getExperiencia()));
        } else if (personal instanceof Recepcionistas) {
            Recepcionistas recepcionista = (Recepcionistas) personal;
            cmbHorarioTurno.setValue(recepcionista.getHorarioTurno());
        }
    }

    @FXML
    private void handleGuardar() {
        if (!validarFormularioCompleto()) {
            mostrarAlerta("Error de validación",
                    "Por favor corrija los errores en el formulario",
                    Alert.AlertType.ERROR);
            return;
        }

        try {
            if (modoEdicion) {
                actualizarPersonal();
            } else {
                registrarNuevoPersonal();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error de base de datos",
                    "Error al guardar: " + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Error de validación",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void registrarNuevoPersonal() throws SQLException {
        String tipoPersonal = cmbTipoPersonal.getValue();

        switch (tipoPersonal) {
            case "ADMINISTRADOR":
                registrarAdministrador();
                break;
            case "ENTRENADOR":
                registrarEntrenador();
                break;
            case "RECEPCIONISTA":
                registrarRecepcionista();
                break;
        }

        mostrarAlerta("Éxito",
                "Personal registrado correctamente",
                Alert.AlertType.INFORMATION);

        volverALista();
    }

    private void registrarAdministrador() throws SQLException {
        Administradores admin = new Administradores();
        admin.setDocuAdministrador(txtDocumento.getText().trim());
        admin.setNombres(txtNombres.getText().trim());
        admin.setApellidos(txtApellidos.getText().trim());
        admin.setTelefono(txtTelefono.getText().trim());
        admin.setCorreo(txtCorreo.getText().trim());
        admin.setUsuarioSistema(txtUsuario.getText().trim());
        admin.setContrasena(txtContrasena.getText());
        admin.setFechaContratacion(dpFechaContratacion.getValue());
        admin.setCargo(txtCargo.getText().trim());
        admin.setTipoPersonal(TipoPersonal.ADMINISTRADOR);

        administradorService.registrarAdministrador(admin);
    }

    private void registrarEntrenador() throws SQLException {
        Entrenadores entrenador = new Entrenadores();
        entrenador.setDocuEntrenador(txtDocumento.getText().trim());
        entrenador.setNombres(txtNombres.getText().trim());
        entrenador.setApellidos(txtApellidos.getText().trim());
        entrenador.setTelefono(txtTelefono.getText().trim());
        entrenador.setCorreo(txtCorreo.getText().trim());
        entrenador.setUsuarioSistema(txtUsuario.getText().trim());
        entrenador.setContrasena(txtContrasena.getText());
        entrenador.setFechaContratacion(dpFechaContratacion.getValue());
        entrenador.setEspecialidad(txtEspecialidad.getText().trim());
        entrenador.setExperiencia(Integer.parseInt(txtExperiencia.getText().trim()));
        entrenador.setTipoPersonal(TipoPersonal.ENTRENADOR);

        entrenadorService.registrarEntrenador(entrenador);
    }

    private void registrarRecepcionista() throws SQLException {
        Recepcionistas recepcionista = new Recepcionistas();
        recepcionista.setDocuRecepcionista(txtDocumento.getText().trim());
        recepcionista.setNombres(txtNombres.getText().trim());
        recepcionista.setApellidos(txtApellidos.getText().trim());
        recepcionista.setTelefono(txtTelefono.getText().trim());
        recepcionista.setCorreo(txtCorreo.getText().trim());
        recepcionista.setUsuarioSistema(txtUsuario.getText().trim());
        recepcionista.setContrasena(txtContrasena.getText());
        recepcionista.setFechaContratacion(dpFechaContratacion.getValue());
        recepcionista.setHorarioTurno(cmbHorarioTurno.getValue());
        recepcionista.setTipoPersonal(TipoPersonal.RECEPCIONISTA);

        recepcionistaService.registrarRecepcionista(recepcionista);
    }

    private void actualizarPersonal() throws SQLException {
        if (personalActual instanceof Administradores) {
            actualizarAdministrador();
        } else if (personalActual instanceof Entrenadores) {
            actualizarEntrenador();
        } else if (personalActual instanceof Recepcionistas) {
            actualizarRecepcionista();
        }

        mostrarAlerta("Éxito",
                "Personal actualizado correctamente",
                Alert.AlertType.INFORMATION);

        volverALista();
    }

    private void actualizarAdministrador() throws SQLException {
        Administradores admin = (Administradores) personalActual;
        admin.setNombres(txtNombres.getText().trim());
        admin.setApellidos(txtApellidos.getText().trim());
        admin.setTelefono(txtTelefono.getText().trim());
        admin.setCorreo(txtCorreo.getText().trim());
        admin.setUsuarioSistema(txtUsuario.getText().trim());
        admin.setContrasena(txtContrasena.getText());
        admin.setFechaContratacion(dpFechaContratacion.getValue());
        admin.setCargo(txtCargo.getText().trim());

        administradorService.actualizarAdministrador(admin);
    }

    private void actualizarEntrenador() throws SQLException {
        Entrenadores entrenador = (Entrenadores) personalActual;
        entrenador.setNombres(txtNombres.getText().trim());
        entrenador.setApellidos(txtApellidos.getText().trim());
        entrenador.setTelefono(txtTelefono.getText().trim());
        entrenador.setCorreo(txtCorreo.getText().trim());
        entrenador.setUsuarioSistema(txtUsuario.getText().trim());
        entrenador.setContrasena(txtContrasena.getText());
        entrenador.setFechaContratacion(dpFechaContratacion.getValue());
        entrenador.setEspecialidad(txtEspecialidad.getText().trim());
        entrenador.setExperiencia(Integer.parseInt(txtExperiencia.getText().trim()));

        entrenadorService.actualizarEntrenador(entrenador);
    }

    private void actualizarRecepcionista() throws SQLException {
        Recepcionistas recepcionista = (Recepcionistas) personalActual;
        recepcionista.setNombres(txtNombres.getText().trim());
        recepcionista.setApellidos(txtApellidos.getText().trim());
        recepcionista.setTelefono(txtTelefono.getText().trim());
        recepcionista.setCorreo(txtCorreo.getText().trim());
        recepcionista.setUsuarioSistema(txtUsuario.getText().trim());
        recepcionista.setContrasena(txtContrasena.getText());
        recepcionista.setFechaContratacion(dpFechaContratacion.getValue());
        recepcionista.setHorarioTurno(cmbHorarioTurno.getValue());

        recepcionistaService.actualizarRecepcionista(recepcionista);
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
                    "/org/example/gimnasioproyect/Personal.fxml"));
            Parent vistaPersonal = loader.load();

            if (parentContainer != null) {
                parentContainer.getChildren().clear();
                parentContainer.getChildren().add(vistaPersonal);
            }
        } catch (IOException e) {
            mostrarAlerta("Error",
                    "No se pudo volver a la lista: " + e.getMessage(),
                    Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

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