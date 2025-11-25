package org.example.gimnasioproyect.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.gimnasioproyect.Utilidades.ServiceFactory;
import org.example.gimnasioproyect.model.AsignacionEntrenadores;
import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.model.Entrenadores;
import org.example.gimnasioproyect.services.EntrenadorService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class AsignarEntrenadorController {

    @FXML private Text txtTitulo;
    @FXML private Text txtNombreCliente;

    // Entrenador actual
    @FXML private VBox boxEntrenadorActual;
    @FXML private Text txtEntrenadorActual;
    @FXML private Text txtEspecialidadActual;

    // Selección nuevo entrenador
    @FXML private ComboBox<Entrenadores> comboEntrenadores;
    @FXML private VBox boxInfoEntrenador;
    @FXML private Text txtEspecialidad;
    @FXML private Text txtExperiencia;
    @FXML private Text txtTelefono;
    @FXML private Text txtCorreo;

    @FXML private HBox boxAdvertencia;
    @FXML private Button btnAsignar;

    private Clientes cliente;
    private EntrenadorService entrenadorService;
    private AsignacionEntrenadores asignacionActual;
    private boolean esCambio = false;
    private Consumer<Boolean> onSuccess;

    public void initialize() {
        ServiceFactory factory = ServiceFactory.getInstance();
        this.entrenadorService = factory.getEntrenadorService();

        configurarComboBox();
        configurarListeners();
    }

    private void configurarComboBox() {
        // Configurar cómo se muestra cada entrenador en el ComboBox
        comboEntrenadores.setConverter(new StringConverter<Entrenadores>() {
            @Override
            public String toString(Entrenadores entrenador) {
                if (entrenador == null) return "";
                return entrenador.getNombreCompleto() + " - " + entrenador.getEspecialidad();
            }

            @Override
            public Entrenadores fromString(String string) {
                return null;
            }
        });
    }

    private void configurarListeners() {
        // Listener para cuando se selecciona un entrenador
        comboEntrenadores.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                mostrarInformacionEntrenador(newVal);
                btnAsignar.setDisable(false);
            } else {
                ocultarInformacionEntrenador();
                btnAsignar.setDisable(true);
            }
        });
    }

    public void setCliente(Clientes cliente) {
        this.cliente = cliente;
        txtNombreCliente.setText(cliente.getNombreCompleto() + " (CC: " + cliente.getDocumento() + ")");

        try {
            cargarEntrenadorActual();
            cargarEntrenadores();
        } catch (SQLException e) {
            mostrarError("Error", "No se pudieron cargar los datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarEntrenadorActual() throws SQLException {
        Optional<AsignacionEntrenadores> asignacionOpt =
                entrenadorService.obtenerEntrenadorDeCliente(cliente.getDocumento());

        if (asignacionOpt.isPresent()) {
            asignacionActual = asignacionOpt.get();
            Entrenadores entrenadorActual = asignacionActual.getEntrenador();

            // Mostrar información del entrenador actual
            txtEntrenadorActual.setText(entrenadorActual.getNombreCompleto());
            txtEspecialidadActual.setText(entrenadorActual.getEspecialidad() +
                    " - " + entrenadorActual.getExperiencia() + " años");

            boxEntrenadorActual.setVisible(true);
            boxEntrenadorActual.setManaged(true);

            // Mostrar advertencia y cambiar título
            boxAdvertencia.setVisible(true);
            boxAdvertencia.setManaged(true);
            txtTitulo.setText("Cambiar Entrenador");
            btnAsignar.setText("Cambiar Entrenador");

            esCambio = true;
        } else {
            esCambio = false;
            txtTitulo.setText("Asignar Entrenador");
            btnAsignar.setText("Asignar Entrenador");
        }
    }

    private void cargarEntrenadores() throws SQLException {
        List<Entrenadores> todosLosEntrenadores = entrenadorService.obtenerTodosLosEntrenadores();

        // Si hay entrenador actual, excluirlo de la lista
        if (asignacionActual != null) {
            String documentoActual = asignacionActual.getEntrenador().getDocuEntrenador();
            todosLosEntrenadores.removeIf(e -> e.getDocuEntrenador().equals(documentoActual));
        }

        comboEntrenadores.getItems().setAll(todosLosEntrenadores);

        if (todosLosEntrenadores.isEmpty()) {
            comboEntrenadores.setPromptText("No hay entrenadores disponibles");
            comboEntrenadores.setDisable(true);
            btnAsignar.setDisable(true);
        }
    }

    private void mostrarInformacionEntrenador(Entrenadores entrenador) {
        txtEspecialidad.setText(entrenador.getEspecialidad());
        txtExperiencia.setText(entrenador.getExperiencia() + " años");
        txtTelefono.setText("📱 " + entrenador.getTelefono());
        txtCorreo.setText("📧 " + (entrenador.getCorreo() != null ? entrenador.getCorreo() : "No especificado"));

        boxInfoEntrenador.setVisible(true);
        boxInfoEntrenador.setManaged(true);
    }

    private void ocultarInformacionEntrenador() {
        boxInfoEntrenador.setVisible(false);
        boxInfoEntrenador.setManaged(false);
    }

    @FXML
    private void handleAsignar() {
        Entrenadores entrenadorSeleccionado = comboEntrenadores.getValue();

        if (entrenadorSeleccionado == null) {
            mostrarError("Error", "Debe seleccionar un entrenador");
            return;
        }

        // Confirmación
        String mensaje = esCambio
                ? "¿Está seguro de cambiar el entrenador?\n\n" +
                "Entrenador actual: " + asignacionActual.getEntrenador().getNombreCompleto() + "\n" +
                "Nuevo entrenador: " + entrenadorSeleccionado.getNombreCompleto()
                : "¿Está seguro de asignar a " + entrenadorSeleccionado.getNombreCompleto() +
                " como entrenador de " + cliente.getNombreCompleto() + "?";

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar " + (esCambio ? "cambio" : "asignación"));
        confirmacion.setHeaderText(null);
        confirmacion.setContentText(mensaje);

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            realizarAsignacion(entrenadorSeleccionado);
        }
    }

    private void realizarAsignacion(Entrenadores nuevoEntrenador) {
        try {
            if (esCambio) {
                // Primero finalizar la asignación actual
                entrenadorService.finalizarAsignacion(cliente.getDocumento());
            }

            // Asignar el nuevo entrenador
            entrenadorService.asignarEntrenadorACliente(
                    nuevoEntrenador.getDocuEntrenador(),
                    cliente.getDocumento()
            );

            mostrarExito(esCambio ? "Entrenador cambiado exitosamente" : "Entrenador asignado exitosamente");

            // Notificar éxito al callback
            if (onSuccess != null) {
                onSuccess.accept(true);
            }

            cerrarVentana();

        } catch (SQLException e) {
            mostrarError("Error", "No se pudo " + (esCambio ? "cambiar" : "asignar") +
                    " el entrenador: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            mostrarError("Error", e.getMessage());
        }
    }

    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtNombreCliente.getScene().getWindow();
        stage.close();
    }

    public void setOnSuccess(Consumer<Boolean> callback) {
        this.onSuccess = callback;
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