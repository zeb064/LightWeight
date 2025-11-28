package org.example.gimnasioproyect.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.example.gimnasioproyect.Utilidades.ServiceFactory;
import org.example.gimnasioproyect.model.DetalleRutinas;
import org.example.gimnasioproyect.model.RutinaAsignadas;
import org.example.gimnasioproyect.model.Rutinas;
import org.example.gimnasioproyect.services.EstadisticaService;
import org.example.gimnasioproyect.services.RutinaService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class RutinasController {

    @FXML private TextField txtBuscar;

    // Estadísticas
    @FXML private Text lblTotalRutinas;
    @FXML private Text lblRutinasActivas;
    @FXML private Text lblClientesConRutina;
    @FXML private Label lblResultados;

    // Tabla
    @FXML private TableView<Rutinas> tableRutinas;
    @FXML private TableColumn<Rutinas, String> colId;
    @FXML private TableColumn<Rutinas, String> colObjetivo;
    @FXML private TableColumn<Rutinas, String> colEjercicios;
    @FXML private TableColumn<Rutinas, String> colAsignaciones;
    @FXML private TableColumn<Rutinas, Void> colAcciones;

    // Botones
    @FXML private Button btnNuevaRutina;
    @FXML private Button btnVerDetalle;
    @FXML private Button btnAgregarEjercicios;
    @FXML private Button btnAsignarCliente;
    @FXML private Button btnEliminar;

    // Servicio
    private RutinaService rutinaService;
    private EstadisticaService estadisticaService;

    // Datos
    private ObservableList<Rutinas> listaRutinas;
    private ObservableList<Rutinas> listaRutinasFiltrada;

    public void initialize() {
        // Obtener servicios
        ServiceFactory factory = ServiceFactory.getInstance();
        this.rutinaService = factory.getRutinaService();
        this.estadisticaService = factory.getEstadisticaService();

        // Configurar tabla
        configurarTabla();

        // Configurar filtros
        configurarFiltros();

        // Configurar botones
        configurarBotones();

        // Cargar datos
        cargarDatos();
    }

    private void configurarTabla() {
        // ID
        colId.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getIdRutina())));

        // Objetivo
        colObjetivo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getObjetivo()));

        // Ejercicios (contar cuántos tiene)
        colEjercicios.setCellValueFactory(data -> {
            try {
                List<DetalleRutinas> detalles = rutinaService.obtenerDetallesRutina(
                        data.getValue().getIdRutina());
                return new SimpleStringProperty(detalles.size() + " ejercicios");
            } catch (SQLException e) {
                return new SimpleStringProperty("0 ejercicios");
            }
        });

        // Asignaciones (contar cuántos clientes la tienen)
        colAsignaciones.setCellValueFactory(data -> {
            try {
                int clientesAsignados = estadisticaService.obtenerClientesAsignadosARutina(
                        data.getValue().getIdRutina());
                return new SimpleStringProperty(String.valueOf(clientesAsignados));
            } catch (Exception e) {
                return new SimpleStringProperty("0");
            }
        });

        // Acciones
        colAcciones.setCellFactory(column -> new TableCell<Rutinas, Void>() {
            private final Button btnVer = new Button("Ver");
            private final Button btnAgregar = new Button("+ Ejercicios");
            private final Button btnAsignar = new Button("Asignar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox hbox = new HBox(5, btnVer, btnAgregar, btnAsignar, btnEliminar);

            {
                btnVer.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5; -fx-font-size: 11px;");
                btnAgregar.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5; -fx-font-size: 11px;");
                btnAsignar.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5; -fx-font-size: 11px;");
                btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5; -fx-font-size: 11px;");

                btnVer.setOnAction(e -> {
                    Rutinas rutina = getTableView().getItems().get(getIndex());
                    verDetalleRutina(rutina);
                });

                btnAgregar.setOnAction(e -> {
                    Rutinas rutina = getTableView().getItems().get(getIndex());
                    agregarEjerciciosRutina(rutina);
                });

                btnAsignar.setOnAction(e -> {
                    Rutinas rutina = getTableView().getItems().get(getIndex());
                    asignarRutinaACliente(rutina);
                });

                btnEliminar.setOnAction(e -> {
                    Rutinas rutina = getTableView().getItems().get(getIndex());
                    eliminarRutina(rutina);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }

    private void configurarFiltros() {
        txtBuscar.textProperty().addListener((obs, old, nuevo) -> aplicarFiltros());
    }

    private void configurarBotones() {
        tableRutinas.getSelectionModel().selectedItemProperty().addListener((obs, old, nuevo) -> {
            boolean haySeleccion = nuevo != null;
            btnVerDetalle.setDisable(!haySeleccion);
            btnAgregarEjercicios.setDisable(!haySeleccion);
            btnAsignarCliente.setDisable(!haySeleccion);
            btnEliminar.setDisable(!haySeleccion);
        });
    }

    private void cargarDatos() {
        try {
            // Cargar todas las rutinas
            List<Rutinas> rutinas = rutinaService.obtenerTodasLasRutinas();
            listaRutinas = FXCollections.observableArrayList(rutinas);
            listaRutinasFiltrada = FXCollections.observableArrayList(rutinas);
            tableRutinas.setItems(listaRutinasFiltrada);

            lblResultados.setText("Mostrando " + rutinas.size() + " rutinas");

            // Actualizar estadísticas
            actualizarEstadisticas();

        } catch (SQLException e) {
            mostrarError("Error al cargar datos", e.getMessage());
            e.printStackTrace();
        }
    }

    private void actualizarEstadisticas() {
        lblTotalRutinas.setText(String.valueOf(listaRutinas.size()));

        try {
            // Contar rutinas activas (que tienen al menos un cliente asignado)
            int rutinasActivas = 0;
            for (Rutinas rutina : listaRutinas) {
                int clientesAsignados = estadisticaService.obtenerClientesAsignadosARutina(rutina.getIdRutina());
                if (clientesAsignados > 0) {
                    rutinasActivas++;
                }
            }
            lblRutinasActivas.setText(String.valueOf(rutinasActivas));

            // Total de clientes con rutina
            int clientesConRutina = estadisticaService.obtenerTotalClientesConRutina();
            lblClientesConRutina.setText(String.valueOf(clientesConRutina));

        } catch (SQLException e) {
            lblRutinasActivas.setText("0");
            lblClientesConRutina.setText("0");
            e.printStackTrace();
        }
    }

    private void aplicarFiltros() {
        if (listaRutinas == null) return;

        String busqueda = txtBuscar.getText().toLowerCase().trim();

        List<Rutinas> filtradas = listaRutinas.stream()
                .filter(rutina -> {
                    if (!busqueda.isEmpty()) {
                        String objetivo = rutina.getObjetivo().toLowerCase();
                        return objetivo.contains(busqueda);
                    }
                    return true;
                })
                .collect(Collectors.toList());

        listaRutinasFiltrada.setAll(filtradas);
        lblResultados.setText("Mostrando " + filtradas.size() + " rutinas");
    }

    @FXML
    private void handleNuevaRutina() {
        // Diálogo para crear nueva rutina
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nueva Rutina");
        dialog.setHeaderText("Crear Nueva Rutina");
        dialog.setContentText("Objetivo de la rutina:");

        dialog.showAndWait().ifPresent(objetivo -> {
            if (objetivo.trim().isEmpty()) {
                mostrarError("Error", "El objetivo no puede estar vacío");
                return;
            }

            try {
                Rutinas nuevaRutina = rutinaService.crearRutina(objetivo.trim());
                mostrarExito("Rutina creada correctamente. ID: " + nuevaRutina.getIdRutina());
                cargarDatos();

                // Preguntar si desea agregar ejercicios ahora
                Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION);
                confirmar.setTitle("Agregar Ejercicios");
                confirmar.setHeaderText("¿Desea agregar ejercicios ahora?");
                confirmar.setContentText("Puede agregar ejercicios a esta rutina");

                confirmar.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        agregarEjerciciosRutina(nuevaRutina);
                    }
                });

            } catch (SQLException e) {
                mostrarError("Error al crear rutina", e.getMessage());
            } catch (IllegalArgumentException e) {
                mostrarError("Error de validación", e.getMessage());
            }
        });
    }

    @FXML
    private void handleVerDetalle() {
        Rutinas seleccionada = tableRutinas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            verDetalleRutina(seleccionada);
        }
    }

    private void verDetalleRutina(Rutinas rutina) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/org/example/gimnasioproyect/DetalleRutina.fxml"));
            Parent vistaDetalle = loader.load();

            DetalleRutinaController controller = loader.getController();
            controller.cargarRutina(rutina);

            // Buscar el contentArea
            if (tableRutinas.getScene() != null) {
                Parent root = tableRutinas.getScene().getRoot();
                StackPane contentArea = (StackPane) root.lookup("#contentArea");

                if (contentArea != null) {
                    controller.setParentContainer(contentArea);
                    contentArea.getChildren().clear();
                    contentArea.getChildren().add(vistaDetalle);
                } else {
                    mostrarError("Error", "No se pudo encontrar el área de contenido");
                }
            }

        } catch (IOException e) {
            mostrarError("Error", "No se pudo abrir el detalle: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAgregarEjercicios() {
        Rutinas seleccionada = tableRutinas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            agregarEjerciciosRutina(seleccionada);
        }
    }

    private void agregarEjerciciosRutina(Rutinas rutina) {
        // Crear diálogo personalizado
        Dialog<DetalleRutinas> dialog = new Dialog<>();
        dialog.setTitle("Agregar Ejercicio");
        dialog.setHeaderText("Agregar ejercicio a: " + rutina.getObjetivo());

        // Botones
        ButtonType agregarButtonType = new ButtonType("Agregar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(agregarButtonType, ButtonType.CANCEL);

        // Crear formulario
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<String> cmbDia = new ComboBox<>();
        cmbDia.getItems().addAll("LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO");
        cmbDia.setValue("LUNES");

        TextField txtOrden = new TextField("1");
        TextField txtEjercicio = new TextField();
        TextField txtSeries = new TextField("3");
        TextField txtRepeticiones = new TextField("12");
        TextField txtPeso = new TextField("0");
        TextArea txtNotas = new TextArea();
        txtNotas.setPrefRowCount(2);

        grid.add(new Label("Día de la semana:"), 0, 0);
        grid.add(cmbDia, 1, 0);
        grid.add(new Label("Orden:"), 0, 1);
        grid.add(txtOrden, 1, 1);
        grid.add(new Label("Ejercicio:"), 0, 2);
        grid.add(txtEjercicio, 1, 2);
        grid.add(new Label("Series:"), 0, 3);
        grid.add(txtSeries, 1, 3);
        grid.add(new Label("Repeticiones:"), 0, 4);
        grid.add(txtRepeticiones, 1, 4);
        grid.add(new Label("Peso (kg):"), 0, 5);
        grid.add(txtPeso, 1, 5);
        grid.add(new Label("Notas:"), 0, 6);
        grid.add(txtNotas, 1, 6);

        dialog.getDialogPane().setContent(grid);

        // Convertir resultado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == agregarButtonType) {
                try {
                    String dia = cmbDia.getValue();
                    Integer orden = Integer.parseInt(txtOrden.getText());
                    String ejercicio = txtEjercicio.getText();
                    Integer series = Integer.parseInt(txtSeries.getText());
                    Integer repeticiones = Integer.parseInt(txtRepeticiones.getText());
                    Double peso = Double.parseDouble(txtPeso.getText());
                    String notas = txtNotas.getText();

                    rutinaService.agregarDetalleRutina(
                            rutina.getIdRutina(), dia, orden, ejercicio,
                            series, repeticiones, peso, notas);

                    return null;
                } catch (NumberFormatException e) {
                    mostrarError("Error", "Valores numéricos inválidos");
                    return null;
                } catch (SQLException e) {
                    mostrarError("Error", e.getMessage());
                    return null;
                } catch (IllegalArgumentException e) {
                    mostrarError("Error de validación", e.getMessage());
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait();

        // Preguntar si desea agregar otro
        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION);
        confirmar.setTitle("Continuar");
        confirmar.setHeaderText("¿Desea agregar otro ejercicio?");

        confirmar.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                agregarEjerciciosRutina(rutina);
            }
        });
    }

    @FXML
    private void handleAsignarCliente() {
        Rutinas seleccionada = tableRutinas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            asignarRutinaACliente(seleccionada);
        }
    }

    private void asignarRutinaACliente(Rutinas rutina) {
        // Verificar que la rutina tenga ejercicios
        try {
            List<DetalleRutinas> detalles = rutinaService.obtenerDetallesRutina(rutina.getIdRutina());
            if (detalles.isEmpty()) {
                mostrarError("Error", "Esta rutina no tiene ejercicios asignados. " +
                        "Agregue ejercicios antes de asignarla a un cliente.");
                return;
            }
        } catch (SQLException e) {
            mostrarError("Error", e.getMessage());
            return;
        }

        // Diálogo para ingresar documento del cliente
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Asignar Rutina");
        dialog.setHeaderText("Asignar rutina: " + rutina.getObjetivo());
        dialog.setContentText("Documento del cliente:");

        dialog.showAndWait().ifPresent(documento -> {
            if (documento.trim().isEmpty()) {
                mostrarError("Error", "Debe ingresar un documento");
                return;
            }

            try {
                rutinaService.asignarRutinaACliente(
                        rutina.getIdRutina(),
                        documento.trim(),
                        null // fecha actual
                );

                mostrarExito("Rutina asignada correctamente al cliente");
                cargarDatos();

            } catch (SQLException e) {
                mostrarError("Error al asignar rutina", e.getMessage());
            } catch (IllegalArgumentException e) {
                mostrarError("Error de validación", e.getMessage());
            }
        });
    }

    @FXML
    private void handleEliminar() {
        Rutinas seleccionada = tableRutinas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            eliminarRutina(seleccionada);
        }
    }

    private void eliminarRutina(Rutinas rutina) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar rutina?");
        confirmacion.setContentText("¿Está seguro de eliminar la rutina: " + rutina.getObjetivo() + "?");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    rutinaService.eliminarRutina(rutina.getIdRutina());
                    mostrarExito("Rutina eliminada correctamente");
                    cargarDatos();
                } catch (SQLException e) {
                    mostrarError("Error al eliminar", e.getMessage());
                } catch (IllegalArgumentException e) {
                    mostrarError("No se puede eliminar", e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleLimpiarFiltros() {
        txtBuscar.clear();
        aplicarFiltros();
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

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}