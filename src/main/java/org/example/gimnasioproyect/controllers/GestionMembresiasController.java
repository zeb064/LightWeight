package org.example.gimnasioproyect.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.gimnasioproyect.Utilidades.ServiceFactory;
import org.example.gimnasioproyect.model.Membresias;
import org.example.gimnasioproyect.services.EstadisticaService;
import org.example.gimnasioproyect.services.MembresiaService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class GestionMembresiasController {

    @FXML private Text lblTotalTipos;
    @FXML private Text lblMembresiasActivas;
    @FXML private Text lblIngresosMensuales;

    @FXML private TableView<Membresias> tableMembresias;
    @FXML private TableColumn<Membresias, String> colTipo;
    @FXML private TableColumn<Membresias, String> colPrecio;
    @FXML private TableColumn<Membresias, String> colDuracion;
    @FXML private TableColumn<Membresias, String> colClientesActivos;
    @FXML private TableColumn<Membresias, String> colIngresos;
    @FXML private TableColumn<Membresias, Void> colAcciones;

    @FXML private Button btnNuevaMembresia;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;

    private MembresiaService membresiaService;
    private EstadisticaService estadisticaService;

    private ObservableList<Membresias> listaMembresias;

    public void initialize() {
        // Obtener servicios
        ServiceFactory factory = ServiceFactory.getInstance();
        this.membresiaService = factory.getMembresiaService();
        this.estadisticaService = factory.getEstadisticaService();

        configurarTabla();
        configurarBotones();
        cargarDatos();
    }

    private void configurarTabla() {
        // Columna Tipo
        colTipo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTipoMembresia()));

        // Columna Precio
        colPrecio.setCellValueFactory(data ->
                new SimpleStringProperty("$" + String.format("%,.0f", data.getValue().getPrecioMembresia())));

        // Columna Duración
        colDuracion.setCellValueFactory(data -> {
            String tipo = data.getValue().getTipoMembresia();
            String duracion = obtenerDuracion(tipo);
            return new SimpleStringProperty(duracion);
        });

        // Columna Clientes Activos
        colClientesActivos.setCellValueFactory(data -> {
            try {
                Map<String, Integer> clientesActivos =
                        estadisticaService.obtenerClientesActivosPorTipoMembresia();

                Integer cantidad = clientesActivos.getOrDefault(
                        data.getValue().getTipoMembresia(), 0
                );

                return new SimpleStringProperty(cantidad + " clientes");
            } catch (SQLException e) {
                return new SimpleStringProperty("Error");
            }
        });

        // Columna Ingresos
        colIngresos.setCellValueFactory(data -> {
            try {
                Map<String, Double> ingresos =
                        estadisticaService.obtenerIngresosMembresiasActivas();

                Double ingreso = ingresos.getOrDefault(
                        data.getValue().getTipoMembresia(), 0.0
                );

                return new SimpleStringProperty("$" + String.format("%,.0f", ingreso));
            } catch (SQLException e) {
                return new SimpleStringProperty("Error");
            }
        });

        // Columna Acciones
        colAcciones.setCellFactory(column -> new TableCell<Membresias, Void>() {
            private final Button btnEditar = new Button("✏️");
            private final Button btnEliminar = new Button("🗑️");
            private final HBox hbox = new HBox(5, btnEditar, btnEliminar);

            {
                hbox.setAlignment(Pos.CENTER);
                btnEditar.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;");
                btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;");

                btnEditar.setOnAction(e -> {
                    Membresias membresia = getTableView().getItems().get(getIndex());
                    editarPrecio(membresia);
                });

                btnEliminar.setOnAction(e -> {
                    Membresias membresia = getTableView().getItems().get(getIndex());
                    eliminarMembresia(membresia);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }

    private String obtenerDuracion(String tipo) {
        switch (tipo.toUpperCase()) {
            case "DIARIO": return "1 día";
            case "SEMANAL": return "7 días";
            case "MENSUAL": return "1 mes";
            case "TRIMESTRAL": return "3 meses";
            case "SEMESTRAL": return "6 meses";
            case "ANUAL": return "12 meses";
            default: return tipo;
        }
    }

    private void configurarBotones() {
        tableMembresias.getSelectionModel().selectedItemProperty().addListener((obs, old, nuevo) -> {
            boolean haySeleccion = nuevo != null;
            btnEditar.setDisable(!haySeleccion);
            btnEliminar.setDisable(!haySeleccion);
        });
    }

    private void cargarDatos() {
        try {
            // Cargar membresías
            List<Membresias> membresias = membresiaService.obtenerTodasLasMembresias();
            listaMembresias = FXCollections.observableArrayList(membresias);
            tableMembresias.setItems(listaMembresias);

            // Actualizar estadísticas
            actualizarEstadisticas();

        } catch (SQLException e) {
            mostrarError("Error al cargar datos", e.getMessage());
            e.printStackTrace();
        }
    }

    private void actualizarEstadisticas() {
        try {
            // Total de tipos
            lblTotalTipos.setText(String.valueOf(listaMembresias.size()));

            // Membresías activas
            int activas = estadisticaService.obtenerTotalMembresiasActivas();
            lblMembresiasActivas.setText(String.valueOf(activas));

            // Ingresos estimados
            double ingresos = estadisticaService.obtenerIngresoTotalMembresias();
            lblIngresosMensuales.setText("$" + String.format("%,.0f", ingresos));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNuevaMembresia() {
        // Diálogo para crear nueva membresía
        Dialog<Membresias> dialog = new Dialog<>();
        dialog.setTitle("Nueva Membresía");
        dialog.setHeaderText("Crear nuevo tipo de membresía");

        // Botones
        ButtonType crearButtonType = new ButtonType("Crear", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(crearButtonType, ButtonType.CANCEL);

        // ComboBox con tipos predefinidos
        ComboBox<String> cmbTipo = new ComboBox<>();
        cmbTipo.getItems().addAll("DIARIO", "SEMANAL", "MENSUAL", "TRIMESTRAL", "SEMESTRAL", "ANUAL");
        cmbTipo.setPromptText("Seleccione el tipo");
        cmbTipo.setPrefWidth(250);

        TextField txtPrecio = new TextField();
        txtPrecio.setPromptText("Ingrese el precio");
        txtPrecio.setPrefWidth(250);

        Label lblPrecioSugerido = new Label();
        lblPrecioSugerido.setStyle("-fx-text-fill: #3498db; -fx-font-size: 11px;");

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new javafx.geometry.Insets(20));

        grid.add(new Label("Tipo de Membresía:"), 0, 0);
        grid.add(cmbTipo, 1, 0);
        grid.add(new Label("Precio:"), 0, 1);
        grid.add(txtPrecio, 1, 1);
        grid.add(lblPrecioSugerido, 1, 2);

        // Información de duraciones
        VBox infoBox = new VBox(5);
        infoBox.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 10; -fx-background-radius: 5;");
        Label lblInfo = new Label("💡 Información:");
        lblInfo.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

        VBox duracionesBox = new VBox(3);
        duracionesBox.getChildren().addAll(
                new Label("• DIARIO: 1 día"),
                new Label("• SEMANAL: 7 días"),
                new Label("• MENSUAL: 30 días"),
                new Label("• TRIMESTRAL: 90 días"),
                new Label("• SEMESTRAL: 180 días"),
                new Label("• ANUAL: 365 días")
        );
        duracionesBox.getChildren().forEach(node ->
                ((Label)node).setStyle("-fx-font-size: 11px;"));

        infoBox.getChildren().addAll(lblInfo, duracionesBox);
        grid.add(infoBox, 0, 3, 2, 1);

        dialog.getDialogPane().setContent(grid);

        // Sugerencias de precio según el tipo
        cmbTipo.valueProperty().addListener((obs, old, nuevo) -> {
            if (nuevo != null) {
                String sugerencia = obtenerPrecioSugerido(nuevo);
                lblPrecioSugerido.setText("💡 Precio sugerido: " + sugerencia);
            } else {
                lblPrecioSugerido.setText("");
            }
        });

        // Validación
        Button crearButton = (Button) dialog.getDialogPane().lookupButton(crearButtonType);
        crearButton.setDisable(true);

        cmbTipo.valueProperty().addListener((obs, old, nuevo) ->
                crearButton.setDisable(nuevo == null || txtPrecio.getText().trim().isEmpty()));

        txtPrecio.textProperty().addListener((obs, old, nuevo) -> {
            crearButton.setDisable(cmbTipo.getValue() == null || nuevo.trim().isEmpty());

            // Solo permitir números y punto decimal
            if (!nuevo.matches("\\d*\\.?\\d*")) {
                txtPrecio.setText(old);
            }
        });

        // Resultado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == crearButtonType) {
                Membresias membresia = new Membresias();
                membresia.setTipoMembresia(cmbTipo.getValue());
                membresia.setPrecioMembresia(Double.parseDouble(txtPrecio.getText().trim()));
                return membresia;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(membresia -> {
            try {
                membresiaService.registrarMembresia(membresia);
                mostrarExito("Membresía creada correctamente");
                cargarDatos();
            } catch (SQLException e) {
                mostrarError("Error al crear membresía", e.getMessage());
            } catch (IllegalArgumentException e) {
                mostrarError("Error de validación", e.getMessage());
            }
        });
    }

    // Método auxiliar para sugerir precios
    private String obtenerPrecioSugerido(String tipo) {
        switch (tipo) {
            case "DIARIO":
                return "$5,000 - $8,000";
            case "SEMANAL":
                return "$25,000 - $35,000";
            case "MENSUAL":
                return "$80,000 - $120,000";
            case "TRIMESTRAL":
                return "$200,000 - $300,000";
            case "SEMESTRAL":
                return "$350,000 - $500,000";
            case "ANUAL":
                return "$600,000 - $900,000";
            default:
                return "";
        }
    }
    @FXML
    private void handleEditarPrecio() {
        Membresias seleccionada = tableMembresias.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            editarPrecio(seleccionada);
        }
    }

    private void editarPrecio(Membresias membresia) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(membresia.getPrecioMembresia()));
        dialog.setTitle("Editar Precio");
        dialog.setHeaderText("Actualizar precio de " + membresia.getTipoMembresia());
        dialog.setContentText("Nuevo precio:");

        dialog.showAndWait().ifPresent(precioStr -> {
            try {
                double nuevoPrecio = Double.parseDouble(precioStr);
                membresiaService.actualizarPrecio(membresia.getIdMembresia(), nuevoPrecio);
                mostrarExito("Precio actualizado correctamente");
                cargarDatos();
            } catch (NumberFormatException e) {
                mostrarError("Error", "El precio debe ser un número válido");
            } catch (SQLException e) {
                mostrarError("Error al actualizar precio", e.getMessage());
            }
        });
    }

    @FXML
    private void handleEliminar() {
        Membresias seleccionada = tableMembresias.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            eliminarMembresia(seleccionada);
        }
    }

    private void eliminarMembresia(Membresias membresia) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar membresía?");
        confirmacion.setContentText("¿Está seguro de eliminar el tipo de membresía " +
                membresia.getTipoMembresia() + "?");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    membresiaService.eliminarMembresia(membresia.getIdMembresia());
                    mostrarExito("Membresía eliminada correctamente");
                    cargarDatos();
                } catch (SQLException e) {
                    mostrarError("Error al eliminar",
                            "No se puede eliminar porque hay clientes con esta membresía.\n" + e.getMessage());
                }
            }
        });
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