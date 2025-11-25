package org.example.gimnasioproyect.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.gimnasioproyect.Utilidades.FormateadorFechas;
import org.example.gimnasioproyect.Utilidades.ServiceFactory;
import org.example.gimnasioproyect.model.*;
import org.example.gimnasioproyect.services.RutinaService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AsignarRutinaController {

    @FXML private Text txtTitulo;
    @FXML private Text txtNombreCliente;

    // Rutinas actuales
    @FXML private VBox boxRutinasActuales;
    @FXML private VBox containerRutinasActuales;

    // Selección nueva rutina
    @FXML private ComboBox<Rutinas> comboRutinas;
    @FXML private VBox boxInfoRutina;
    @FXML private Text txtObjetivo;

    // Tabla de ejercicios
    @FXML private TableView<DetalleRutinas> tableEjercicios;
    @FXML private TableColumn<DetalleRutinas, String> colDia;
    @FXML private TableColumn<DetalleRutinas, Integer> colOrden;
    @FXML private TableColumn<DetalleRutinas, String> colEjercicio;
    @FXML private TableColumn<DetalleRutinas, Integer> colSeries;
    @FXML private TableColumn<DetalleRutinas, Integer> colRepeticiones;
    @FXML private TableColumn<DetalleRutinas, Double> colPeso;
    @FXML private TableColumn<DetalleRutinas, String> colNotas;

    @FXML private HBox boxAdvertencia;
    @FXML private Button btnAsignar;

    private Clientes cliente;
    private RutinaService rutinaService;
    private List<RutinaAsignadas> rutinasActivas;
    private boolean esCambio = false;
    private Consumer<Boolean> onSuccess;

    public void initialize() {
        ServiceFactory factory = ServiceFactory.getInstance();
        this.rutinaService = factory.getRutinaService();

        configurarComboBox();
        configurarTabla();
        configurarListeners();
    }

    private void configurarComboBox() {
        comboRutinas.setConverter(new StringConverter<Rutinas>() {
            @Override
            public String toString(Rutinas rutina) {
                if (rutina == null) return "";
                return rutina.getObjetivo();
            }

            @Override
            public Rutinas fromString(String string) {
                return null;
            }
        });
    }

    private void configurarTabla() {
        // Configurar columnas
        colDia.setCellValueFactory(new PropertyValueFactory<>("diaSemana"));
        colOrden.setCellValueFactory(new PropertyValueFactory<>("orden"));
        colEjercicio.setCellValueFactory(new PropertyValueFactory<>("ejercicio"));
        colSeries.setCellValueFactory(new PropertyValueFactory<>("series"));
        colRepeticiones.setCellValueFactory(new PropertyValueFactory<>("repeticiones"));
        colPeso.setCellValueFactory(new PropertyValueFactory<>("peso"));
        colNotas.setCellValueFactory(new PropertyValueFactory<>("notas"));

        // Estilo para las celdas
        tableEjercicios.setRowFactory(tv -> {
            TableRow<DetalleRutinas> row = new TableRow<>();
            row.setStyle("-fx-background-color: #34495e; -fx-text-fill: #ecf0f1;");
            return row;
        });

        // Formato especial para peso (mostrar guiones si es null)
        colPeso.setCellFactory(column -> new TableCell<DetalleRutinas, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("-");
                } else {
                    setText(String.format("%.1f", item));
                }
                setStyle("-fx-text-fill: #ecf0f1;");
            }
        });

        // Formato para notas
        colNotas.setCellFactory(column -> new TableCell<DetalleRutinas, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.trim().isEmpty()) {
                    setText("-");
                } else {
                    setText(item);
                }
                setStyle("-fx-text-fill: #95a5a6;");
            }
        });
    }

    private void configurarListeners() {
        comboRutinas.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                mostrarInformacionRutina(newVal);
                btnAsignar.setDisable(false);
            } else {
                ocultarInformacionRutina();
                btnAsignar.setDisable(true);
            }
        });
    }

    public void setCliente(Clientes cliente) {
        this.cliente = cliente;
        txtNombreCliente.setText(cliente.getNombreCompleto() + " (CC: " + cliente.getDocumento() + ")");

        try {
            cargarRutinasActuales();
            cargarRutinasDisponibles();
        } catch (SQLException e) {
            mostrarError("Error", "No se pudieron cargar los datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarRutinasActuales() throws SQLException {
        rutinasActivas = rutinaService.obtenerRutinasActivasCliente(cliente.getDocumento());

        if (!rutinasActivas.isEmpty()) {
            containerRutinasActuales.getChildren().clear();

            for (RutinaAsignadas ra : rutinasActivas) {
                HBox rutinaBox = crearItemRutinaActual(ra);
                containerRutinasActuales.getChildren().add(rutinaBox);
            }

            boxRutinasActuales.setVisible(true);
            boxRutinasActuales.setManaged(true);
            boxAdvertencia.setVisible(true);
            boxAdvertencia.setManaged(true);

            // Cambiar título y texto del botón
            txtTitulo.setText("Cambiar Rutina");
            btnAsignar.setText("Cambiar Rutina");
            esCambio = true;
        } else {
            txtTitulo.setText("Asignar Rutina");
            btnAsignar.setText("Asignar Rutina");
            esCambio = false;
        }
    }

    private HBox crearItemRutinaActual(RutinaAsignadas ra) {
        HBox box = new HBox(15.0);
        box.setStyle("-fx-background-color: #1a252f; -fx-background-radius: 5; -fx-padding: 10;");
        box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Text icono = new Text("📋");
        icono.setStyle("-fx-font-size: 20px;");

        VBox info = new VBox(5.0);
        Text nombre = new Text(ra.getRutina().getObjetivo());
        nombre.setStyle("-fx-fill: #3498db; -fx-font-size: 14px; -fx-font-weight: bold;");

        Text fecha = new Text("Desde: " + FormateadorFechas.formatearFecha(ra.getFechaAsignacion()));
        fecha.setStyle("-fx-fill: #95a5a6; -fx-font-size: 12px;");

        info.getChildren().addAll(nombre, fecha);

        Text estado = new Text("● Activa");
        estado.setStyle("-fx-fill: #27ae60; -fx-font-size: 12px; -fx-font-weight: bold;");

        box.getChildren().addAll(icono, info, estado);
        return box;
    }

    private void cargarRutinasDisponibles() throws SQLException {
        List<Rutinas> todasLasRutinas = rutinaService.obtenerTodasLasRutinas();

        // Si hay rutinas activas, excluirlas de la lista
        if (!rutinasActivas.isEmpty()) {
            // Obtener IDs de rutinas activas
            List<Integer> idsActivas = rutinasActivas.stream()
                    .map(ra -> ra.getRutina().getIdRutina())
                    .collect(Collectors.toList());

            // Filtrar rutinas que no estén activas
            todasLasRutinas.removeIf(r -> idsActivas.contains(r.getIdRutina()));
        }

        comboRutinas.getItems().setAll(todasLasRutinas);

        if (todasLasRutinas.isEmpty()) {
            comboRutinas.setPromptText("No hay rutinas disponibles");
            comboRutinas.setDisable(true);
            btnAsignar.setDisable(true);
        }
    }

    private void mostrarInformacionRutina(Rutinas rutina) {
        try {
            txtObjetivo.setText(rutina.getObjetivo());

            // Cargar detalles de la rutina (ejercicios)
            List<DetalleRutinas> detalles = rutinaService.obtenerDetallesRutina(rutina.getIdRutina());
            tableEjercicios.getItems().setAll(detalles);

            boxInfoRutina.setVisible(true);
            boxInfoRutina.setManaged(true);

        } catch (SQLException e) {
            mostrarError("Error", "No se pudieron cargar los detalles de la rutina: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void ocultarInformacionRutina() {
        boxInfoRutina.setVisible(false);
        boxInfoRutina.setManaged(false);
        tableEjercicios.getItems().clear();
    }

    @FXML
    private void handleAsignar() {
        Rutinas rutinaSeleccionada = comboRutinas.getValue();

        if (rutinaSeleccionada == null) {
            mostrarError("Error", "Debe seleccionar una rutina");
            return;
        }

        // Construir mensaje de confirmación
        String mensaje;
        if (esCambio) {
            StringBuilder rutinasActualesStr = new StringBuilder();
            for (RutinaAsignadas ra : rutinasActivas) {
                rutinasActualesStr.append("  • ").append(ra.getRutina().getObjetivo()).append("\n");
            }

            mensaje = "¿Está seguro de cambiar la rutina?\n\n" +
                    "Rutina(s) actual(es):\n" + rutinasActualesStr.toString() +
                    "\nNueva rutina: " + rutinaSeleccionada.getObjetivo() + "\n\n" +
                    "Las rutinas actuales se finalizarán automáticamente.";
        } else {
            mensaje = "¿Está seguro de asignar la rutina \"" + rutinaSeleccionada.getObjetivo() +
                    "\" a " + cliente.getNombreCompleto() + "?";
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar " + (esCambio ? "cambio" : "asignación"));
        confirmacion.setHeaderText(null);
        confirmacion.setContentText(mensaje);

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            realizarAsignacion(rutinaSeleccionada);
        }
    }

    private void realizarAsignacion(Rutinas rutina) {
        try {
            if (esCambio) {
                for (RutinaAsignadas ra : rutinasActivas) {
                    rutinaService.cambiarEstadoRutina(ra.getIdRutinaCliente(), "COMPLETADA");
                }
            }

            // Asignar la nueva rutina
            rutinaService.asignarRutinaACliente(
                    rutina.getIdRutina(),
                    cliente.getDocumento(),
                    LocalDate.now()
            );

            mostrarExito(esCambio ? "Rutina cambiada exitosamente" : "Rutina asignada exitosamente");

            // Notificar éxito al callback
            if (onSuccess != null) {
                onSuccess.accept(true);
            }

            cerrarVentana();

        } catch (SQLException e) {
            mostrarError("Error", "No se pudo " + (esCambio ? "cambiar" : "asignar") +
                    " la rutina: " + e.getMessage());
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