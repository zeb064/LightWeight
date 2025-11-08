package org.example.gimnasioproyect.controllers;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.example.gimnasioproyect.HelloApplication;
import org.example.gimnasioproyect.Utilidades.CalculadoraFechas;
import org.example.gimnasioproyect.Utilidades.ServiceFactory;
import org.example.gimnasioproyect.model.Barrios;
import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.services.BarrioService;
import org.example.gimnasioproyect.services.ClienteServices;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class FormularioClienteController {

    @FXML private Text lblTitulo;
    @FXML private Button btnVolver;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    // Campos del formulario
    @FXML private TextField txtDocumento;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private RadioButton rbMasculino;
    @FXML private RadioButton rbFemenino;
    @FXML private ToggleGroup grupoGenero;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtDireccion;
    @FXML private ComboBox<Barrios> cmbBarrio;

    // Labels de error
    @FXML private Label lblErrorDocumento;
    @FXML private Label lblErrorNombres;
    @FXML private Label lblErrorApellidos;
    @FXML private Label lblErrorFechaNacimiento;
    @FXML private Label lblErrorGenero;
    @FXML private Label lblErrorTelefono;
    @FXML private Label lblErrorCorreo;
    @FXML private Label lblErrorDireccion;
    @FXML private Label lblErrorBarrio;
    @FXML private Label lblEdad;

    private ClienteServices clienteService;
    private BarrioService barrioService;

    private boolean modoEdicion = false;
    private String documentoOriginal;
    private StackPane parentContainer;
    private boolean volverADetalle = false;// Para volver a la vista anterior

    public void initialize() {
        // Obtener servicios
        ServiceFactory factory = ServiceFactory.getInstance();
        this.clienteService = factory.getClienteService();
        this.barrioService = factory.getBarrioService();

        // Configurar validaciones en tiempo real
        configurarValidaciones();

        // Cargar barrios
        cargarBarrios();

        // Configurar DatePicker para calcular edad
        dpFechaNacimiento.valueProperty().addListener((obs, old, nuevo) -> {
            if (nuevo != null) {
                int edad = CalculadoraFechas.calcularEdad(nuevo);
                lblEdad.setText("(" + edad + " años)");
            } else {
                lblEdad.setText("");
            }
        });

        // Configurar ComboBox de barrios
        configurarComboBarrios();
    }

    public void setParentContainer(StackPane container) {
        this.parentContainer = container;
    }

    public void setVolverADetalle(boolean volverADetalle) {
        this.volverADetalle = volverADetalle;
    }

    private void configurarComboBarrios() {
        cmbBarrio.setButtonCell(new ListCell<Barrios>() {
            @Override
            protected void updateItem(Barrios item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombreBarrio());
            }
        });

        cmbBarrio.setCellFactory(param -> new ListCell<Barrios>() {
            @Override
            protected void updateItem(Barrios item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombreBarrio());
            }
        });
    }

    private void cargarBarrios() {
        try {
            List<Barrios> barrios = barrioService.obtenerTodosLosBarrios();
            cmbBarrio.getItems().addAll(barrios);
        } catch (SQLException e) {
            mostrarError("Error al cargar barrios", e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarValidaciones() {
        // Validación de documento (solo números)
        txtDocumento.textProperty().addListener((obs, old, nuevo) -> {
            if (nuevo != null && !nuevo.matches("\\d*")) {
                txtDocumento.setText(old);
            }
            validarCampo(txtDocumento, lblErrorDocumento, "Documento");
        });

        // Validación de nombres (solo letras)
        txtNombres.textProperty().addListener((obs, old, nuevo) ->
                validarCampo(txtNombres, lblErrorNombres, "Nombres"));

        txtApellidos.textProperty().addListener((obs, old, nuevo) ->
                validarCampo(txtApellidos, lblErrorApellidos, "Apellidos"));

        // Validación de teléfono (solo números)
        txtTelefono.textProperty().addListener((obs, old, nuevo) -> {
            if (nuevo != null && !nuevo.matches("\\d*")) {
                txtTelefono.setText(old);
            }
            validarCampo(txtTelefono, lblErrorTelefono, "Teléfono");
        });

        // Validación de correo
        txtCorreo.textProperty().addListener((obs, old, nuevo) ->
                validarCorreo());

        // Validación de fecha
        dpFechaNacimiento.valueProperty().addListener((obs, old, nuevo) ->
                validarFechaNacimiento());
    }

    private void validarCampo(TextField campo, Label lblError, String nombreCampo) {
        String valor = campo.getText().trim();

        if (valor.isEmpty()) {
            lblError.setText(nombreCampo + " es obligatorio");
            campo.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2; -fx-border-radius: 8; " +
                    "-fx-background-color: #2c3e50; -fx-text-fill: #ecf0f1; -fx-background-radius: 8; -fx-padding: 10;");
        } else if (valor.length() > 15 && (nombreCampo.equals("Nombres") || nombreCampo.equals("Apellidos"))) {
            lblError.setText(nombreCampo + " no puede exceder 15 caracteres");
            campo.setStyle("-fx-border-color: #f39c12; -fx-border-width: 2; -fx-border-radius: 8; " +
                    "-fx-background-color: #2c3e50; -fx-text-fill: #ecf0f1; -fx-background-radius: 8; -fx-padding: 10;");
        } else {
            lblError.setText("");
            campo.setStyle("-fx-border-color: #27ae60; -fx-border-width: 2; -fx-border-radius: 8; " +
                    "-fx-background-color: #2c3e50; -fx-text-fill: #ecf0f1; -fx-background-radius: 8; -fx-padding: 10;");
        }
    }

    private void validarCorreo() {
        String correo = txtCorreo.getText().trim();

        if (correo.isEmpty()) {
            lblErrorCorreo.setText("");
            txtCorreo.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: #ecf0f1; -fx-background-radius: 8; -fx-padding: 10;");
            return;
        }

        if (!correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            lblErrorCorreo.setText("Formato de correo inválido");
            txtCorreo.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2; -fx-border-radius: 8; " +
                    "-fx-background-color: #2c3e50; -fx-text-fill: #ecf0f1; -fx-background-radius: 8; -fx-padding: 10;");
        } else {
            lblErrorCorreo.setText("");
            txtCorreo.setStyle("-fx-border-color: #27ae60; -fx-border-width: 2; -fx-border-radius: 8; " +
                    "-fx-background-color: #2c3e50; -fx-text-fill: #ecf0f1; -fx-background-radius: 8; -fx-padding: 10;");
        }
    }

    private void validarFechaNacimiento() {
        LocalDate fecha = dpFechaNacimiento.getValue();

        if (fecha == null) {
            lblErrorFechaNacimiento.setText("Fecha de nacimiento es obligatoria");
            return;
        }

        if (fecha.isAfter(LocalDate.now())) {
            lblErrorFechaNacimiento.setText("La fecha no puede ser futura");
            return;
        }

        int edad = CalculadoraFechas.calcularEdad(fecha);
        if (edad < 12) {
            lblErrorFechaNacimiento.setText("El cliente debe tener al menos 12 años");
            return;
        }

        lblErrorFechaNacimiento.setText("");
    }

    // Método para cargar datos en modo edición
    public void cargarCliente(Clientes cliente) {
        this.modoEdicion = true;
        this.documentoOriginal = cliente.getDocumento();

        lblTitulo.setText("Editar Cliente");
        btnGuardar.setText("💾 Actualizar Cliente");

        // Cargar datos
        txtDocumento.setText(cliente.getDocumento());
        txtDocumento.setDisable(true); // No se puede cambiar el documento
        txtNombres.setText(cliente.getNombres());
        txtApellidos.setText(cliente.getApellidos());
        dpFechaNacimiento.setValue(cliente.getFechaNacimiento());

        if ("M".equals(cliente.getGenero())) {
            rbMasculino.setSelected(true);
        } else if ("F".equals(cliente.getGenero())) {
            rbFemenino.setSelected(true);
        }

        txtTelefono.setText(cliente.getTelefono());
        txtCorreo.setText(cliente.getCorreo());
        txtDireccion.setText(cliente.getDireccion());

        // Seleccionar barrio
        if (cliente.getBarrio() != null) {
            for (Barrios barrio : cmbBarrio.getItems()) {
                if (barrio.getIdBarrio().equals(cliente.getBarrio().getIdBarrio())) {
                    cmbBarrio.setValue(barrio);
                    break;
                }
            }
        }
    }

    @FXML
    private void handleGuardar() {
        // Validar todos los campos
        if (!validarFormulario()) {
            mostrarError("Formulario incompleto", "Por favor complete todos los campos obligatorios correctamente");
            return;
        }

        try {
            // Crear o actualizar cliente
            Clientes cliente = new Clientes();
            cliente.setDocumento(txtDocumento.getText().trim());
            cliente.setNombres(txtNombres.getText().trim());
            cliente.setApellidos(txtApellidos.getText().trim());
            cliente.setFechaNacimiento(dpFechaNacimiento.getValue());
            cliente.setGenero(rbMasculino.isSelected() ? "M" : "F");
            cliente.setTelefono(txtTelefono.getText().trim());
            cliente.setCorreo(txtCorreo.getText().trim());
            cliente.setDireccion(txtDireccion.getText().trim());
            cliente.setBarrio(cmbBarrio.getValue());

            if (modoEdicion) {
                clienteService.actualizarCliente(cliente);
                mostrarExito("Cliente actualizado correctamente");
            } else {
                cliente.setFechaRegistro(LocalDate.now());
                clienteService.registrarCliente(cliente);
                mostrarExito("Cliente registrado correctamente");
            }

            // Volver según el contexto
            if (volverADetalle && modoEdicion) {
                volverADetalle(cliente);
            } else {
                volverALista();
            }


        } catch (SQLException e) {
            mostrarError("Error al guardar", e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            mostrarError("Error de validación", e.getMessage());
        }
    }

    private boolean validarFormulario() {
        boolean valido = true;

        // Validar documento
        if (txtDocumento.getText().trim().isEmpty()) {
            lblErrorDocumento.setText("Documento es obligatorio");
            valido = false;
        }

        // Validar nombres
        if (txtNombres.getText().trim().isEmpty()) {
            lblErrorNombres.setText("Nombres es obligatorio");
            valido = false;
        }

        // Validar apellidos
        if (txtApellidos.getText().trim().isEmpty()) {
            lblErrorApellidos.setText("Apellidos es obligatorio");
            valido = false;
        }

        // Validar fecha nacimiento
        if (dpFechaNacimiento.getValue() == null) {
            lblErrorFechaNacimiento.setText("Fecha de nacimiento es obligatoria");
            valido = false;
        }

        // Validar género
        if (grupoGenero.getSelectedToggle() == null) {
            lblErrorGenero.setText("Debe seleccionar un género");
            valido = false;
        } else {
            lblErrorGenero.setText("");
        }

        // Validar teléfono
        if (txtTelefono.getText().trim().isEmpty()) {
            lblErrorTelefono.setText("Teléfono es obligatorio");
            valido = false;
        }

        // Validar barrio
        if (cmbBarrio.getValue() == null) {
           lblErrorBarrio.setText("Debe seleccionar un barrio");
            valido = false;
        } else {
            lblErrorBarrio.setText("");
        }

        return valido;
    }

    @FXML
    private void handleCancelar() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Cancelar");
        confirmacion.setHeaderText("¿Está seguro?");
        confirmacion.setContentText("Se perderán los cambios no guardados");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Volver según el contexto
                if (volverADetalle && modoEdicion) {
                    // Recargar el cliente original desde la BD
                    try {
                        Clientes clienteOriginal = clienteService.buscarClientePorDocumento(documentoOriginal)
                                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
                        volverADetalle(clienteOriginal);
                    } catch (SQLException e) {
                        mostrarError("Error", "No se pudo cargar el cliente: " + e.getMessage());
                        volverALista();
                    }
                } else {
                    volverALista();
                }
            }
        });
    }

    @FXML
    private void handleVolver() {
        handleCancelar();
    }

    private void volverALista() {
        try {
            Parent gestionClientes = HelloApplication.loadFXML("GestionClientes");

            if (parentContainer != null) {
                parentContainer.getChildren().clear();
                parentContainer.getChildren().add(gestionClientes);
            } else {
                StackPane contentArea = (StackPane) btnVolver.getScene().getRoot().lookup("#contentArea");
                if (contentArea != null) {
                    contentArea.getChildren().clear();
                    contentArea.getChildren().add(gestionClientes);
                }
            }
        } catch (IOException e) {
            mostrarError("Error", "No se pudo volver a la lista: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void volverADetalle(Clientes cliente) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gimnasioproyect/DetalleCliente.fxml"));
            Parent detalle = loader.load();

            DetalleClienteController controller = loader.getController();
            controller.setParentContainer(parentContainer);
            controller.cargarCliente(cliente);

            if (parentContainer != null) {
                parentContainer.getChildren().clear();
                parentContainer.getChildren().add(detalle);
            }
        } catch (IOException e) {
            mostrarError("Error", "No se pudo volver al detalle: " + e.getMessage());
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
