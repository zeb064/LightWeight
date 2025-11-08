package org.example.gimnasioproyect.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.gimnasioproyect.Utilidades.CalculadoraFechas;
import org.example.gimnasioproyect.Utilidades.FormateadorFechas;
import org.example.gimnasioproyect.Utilidades.ServiceFactory;
import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.model.Membresias;
import org.example.gimnasioproyect.services.MembresiaClienteService;
import org.example.gimnasioproyect.services.MembresiaService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

public class AsignarMembresiaController {

    @FXML private Text lblTitulo;
    @FXML private Text lblSubtitulo;
    @FXML private Label lblNombreCliente;
    @FXML private Label lblDocumentoCliente;

    @FXML private ComboBox<Membresias> cmbTipoMembresia;
    @FXML private DatePicker dpFechaInicio;

    @FXML private Label lblErrorMembresia;
    @FXML private Label lblErrorFecha;

    @FXML private Label lblPrecio;
    @FXML private Label lblDuracion;
    @FXML private Label lblFechaInicioResumen;
    @FXML private Label lblFechaFinResumen;

    @FXML private Button btnCancelar;
    @FXML private Button btnAsignar;

    private MembresiaService membresiaService;
    private MembresiaClienteService membresiaClienteService;

    private Clientes cliente;
    private boolean modoRenovacion = false;
    private Consumer<Boolean> onSuccess;

    public void initialize() {
        // Obtener servicios
        ServiceFactory factory = ServiceFactory.getInstance();
        this.membresiaService = factory.getMembresiaService();
        this.membresiaClienteService = factory.getMembresiaClienteService();

        // Configurar fecha de inicio por defecto (hoy)
        dpFechaInicio.setValue(LocalDate.now());

        // Configurar ComboBox
        configurarComboMembresias();

        // Listeners para actualizar resumen
        cmbTipoMembresia.setOnAction(e -> actualizarResumen());
        dpFechaInicio.valueProperty().addListener((obs, old, nuevo) -> actualizarResumen());

        // Cargar tipos de membresía
        cargarMembresias();
    }

    private void configurarComboMembresias() {
        cmbTipoMembresia.setButtonCell(new ListCell<Membresias>() {
            @Override
            protected void updateItem(Membresias item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTipoMembresia() + " - $" +
                            String.format("%,.0f", item.getPrecioMembresia()));
                }
            }
        });

        cmbTipoMembresia.setCellFactory(param -> new ListCell<Membresias>() {
            @Override
            protected void updateItem(Membresias item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTipoMembresia() + " - $" +
                            String.format("%,.0f", item.getPrecioMembresia()));
                }
            }
        });
    }

    private void cargarMembresias() {
        try {
            List<Membresias> membresias = membresiaService.obtenerTodasLasMembresias();
            cmbTipoMembresia.getItems().addAll(membresias);
        } catch (SQLException e) {
            mostrarError("Error al cargar membresías", e.getMessage());
            e.printStackTrace();
        }
    }

    public void setCliente(Clientes cliente) {
        this.cliente = cliente;
        lblNombreCliente.setText(cliente.getNombreCompleto());
        lblDocumentoCliente.setText("CC: " + cliente.getDocumento());
    }

    public void setModoRenovacion(boolean renovacion) {
        this.modoRenovacion = renovacion;
        if (renovacion) {
            lblTitulo.setText("Renovar Membresía");
            lblSubtitulo.setText("Renueva la membresía del cliente");
            btnAsignar.setText("🔄 Renovar Membresía");
        }
    }

    public void setOnSuccess(Consumer<Boolean> callback) {
        this.onSuccess = callback;
    }

    private void actualizarResumen() {
        Membresias membresia = cmbTipoMembresia.getValue();
        LocalDate fechaInicio = dpFechaInicio.getValue();

        if (membresia != null) {
            // Precio
            lblPrecio.setText("$" + String.format("%,.0f", membresia.getPrecioMembresia()));

            // Duración
            String duracion = obtenerDuracionTexto(membresia.getTipoMembresia());
            lblDuracion.setText(duracion);

            if (fechaInicio != null) {
                // Fecha de inicio
                lblFechaInicioResumen.setText(FormateadorFechas.formatearFecha(fechaInicio));

                // Calcular y mostrar fecha de fin
                LocalDate fechaFin = CalculadoraFechas.calcularFechaFinalizacion(
                        fechaInicio, membresia.getTipoMembresia()
                );
                lblFechaFinResumen.setText(FormateadorFechas.formatearFecha(fechaFin));
            } else {
                lblFechaInicioResumen.setText("-");
                lblFechaFinResumen.setText("-");
            }
        } else {
            lblPrecio.setText("-");
            lblDuracion.setText("-");
            lblFechaInicioResumen.setText("-");
            lblFechaFinResumen.setText("-");
        }
    }

    private String obtenerDuracionTexto(String tipo) {
        switch (tipo.toUpperCase()) {
            case "DIARIA": return "1 día";
            case "SEMANAL": return "7 días";
            case "MENSUAL": return "1 mes";
            case "TRIMESTRAL": return "3 meses";
            case "SEMESTRAL": return "6 meses";
            case "ANUAL": return "12 meses";
            default: return tipo;
        }
    }

    @FXML
    private void handleAsignar() {
        if (!validarFormulario()) {
            return;
        }

        try {
            Membresias membresia = cmbTipoMembresia.getValue();
            LocalDate fechaInicio = dpFechaInicio.getValue();

            if (modoRenovacion) {
                // Renovar membresía existente
                membresiaClienteService.renovarMembresia(
                        cliente.getDocumento(),
                        membresia.getIdMembresia()
                );
                mostrarExito("Membresía renovada correctamente");
            } else {
                // Asignar nueva membresía
                membresiaClienteService.asignarMembresiaACliente(
                        cliente.getDocumento(),
                        membresia.getIdMembresia(),
                        fechaInicio
                );
                mostrarExito("Membresía asignada correctamente");
            }

            // Notificar éxito
            if (onSuccess != null) {
                onSuccess.accept(true);
            }

            // Cerrar ventana
            cerrarVentana();

        } catch (SQLException e) {
            mostrarError("Error al asignar membresía", e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            mostrarError("Error de validación", e.getMessage());
        }
    }

    private boolean validarFormulario() {
        boolean valido = true;

        // Validar tipo de membresía
        if (cmbTipoMembresia.getValue() == null) {
            lblErrorMembresia.setText("Debe seleccionar un tipo de membresía");
            valido = false;
        } else {
            lblErrorMembresia.setText("");
        }

        // Validar fecha
        if (dpFechaInicio.getValue() == null) {
            lblErrorFecha.setText("Debe seleccionar una fecha de inicio");
            valido = false;
        } else {
            lblErrorFecha.setText("");
        }

        return valido;
    }

    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
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