package org.example.gimnasioproyect.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.example.gimnasioproyect.HelloApplication;
import org.example.gimnasioproyect.Utilidades.FormateadorFechas;
import org.example.gimnasioproyect.Utilidades.ServiceFactory;
import org.example.gimnasioproyect.model.*;
import org.example.gimnasioproyect.services.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class MisClientesController {

    // Header
    @FXML private Text lblTitulo;
    @FXML private Text lblSubtitulo;

    // Estadísticas
    @FXML private Text lblTotalClientes;
    @FXML private Text lblAsistenciasHoy;
    @FXML private Text lblPromedioMes;

    // Filtros
    @FXML private TextField txtBuscar;
    @FXML private ToggleButton btnTodos;
    @FXML private ToggleButton btnActivos;
    @FXML private ToggleButton btnVencidos;
    @FXML private ToggleGroup filtroEstado;
    @FXML private Label lblResultados;

    // Tabla
    @FXML private TableView<AsignacionEntrenadores> tableClientes;
    @FXML private TableColumn<AsignacionEntrenadores, String> colDocumento;
    @FXML private TableColumn<AsignacionEntrenadores, String> colNombre;
    @FXML private TableColumn<AsignacionEntrenadores, String> colTelefono;
    @FXML private TableColumn<AsignacionEntrenadores, String> colEstadoMembresia;
    @FXML private TableColumn<AsignacionEntrenadores, String> colDiasRestantes;
    @FXML private TableColumn<AsignacionEntrenadores, String> colUltimaAsistencia;
    @FXML private TableColumn<AsignacionEntrenadores, String> colAsistenciasMes;
    @FXML private TableColumn<AsignacionEntrenadores, Void> colAcciones;

    // Botones inferiores
    @FXML private Button btnVerDetalle;
    @FXML private Button btnVerHistorial;
    @FXML private Button btnFinalizarAsignacion;

    // Servicios
    private EntrenadorService entrenadorService;
    private EstadisticaService estadisticaService;
    private MembresiaClienteService membresiaClienteService;
    private AsistenciaService asistenciaService;
    private ClienteServices clienteService;

    // Datos
    private Entrenadores entrenadorActual;
    private ObservableList<AsignacionEntrenadores> listaCompleta;
    private ObservableList<AsignacionEntrenadores> listaFiltrada;

    public void initialize() {
        // Obtener servicios
        ServiceFactory factory = ServiceFactory.getInstance();
        this.entrenadorService = factory.getEntrenadorService();
        this.estadisticaService = factory.getEstadisticaService();
        this.membresiaClienteService = factory.getMembresiaClienteService();
        this.asistenciaService = factory.getAsistenciaService();
        this.clienteService = factory.getClienteService();

        // Configurar tabla
        configurarTabla();

        // Configurar listeners
        configurarListeners();

        // Inicializar listas
        listaCompleta = FXCollections.observableArrayList();
        listaFiltrada = FXCollections.observableArrayList();
    }

    public void setEntrenador(Entrenadores entrenador) {
        this.entrenadorActual = entrenador;

        // Actualizar UI con nombre del entrenador
        lblSubtitulo.setText("Entrenador: " + entrenador.getNombreCompleto());

        // Cargar datos
        cargarDatos();
    }

    private void configurarTabla() {
        // Documento
        colDocumento.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCliente().getDocumento())
        );

        // Nombre
        colNombre.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCliente().getNombreCompleto())
        );

        // Teléfono
        colTelefono.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCliente().getTelefono())
        );

        // Estado Membresía
        colEstadoMembresia.setCellValueFactory(cellData -> {
            try {
                String documento = cellData.getValue().getCliente().getDocumento();
                boolean tieneMembresia = membresiaClienteService.tieneMembresiaActiva(documento);
                return new SimpleStringProperty(tieneMembresia ? "✓ ACTIVA" : "✗ SIN MEMBRESÍA");
            } catch (SQLException e) {
                return new SimpleStringProperty("Error");
            }
        });

        // Días Restantes
        colDiasRestantes.setCellValueFactory(cellData -> {
            try {
                String documento = cellData.getValue().getCliente().getDocumento();
                long dias = estadisticaService.calcularDiasRestantes(documento);
                return new SimpleStringProperty(dias > 0 ? dias + " días" : "-");
            } catch (SQLException e) {
                return new SimpleStringProperty("-");
            }
        });

        // Última Asistencia
        colUltimaAsistencia.setCellValueFactory(cellData -> {
            try {
                String documento = cellData.getValue().getCliente().getDocumento();
                boolean asistioHoy = estadisticaService.asistioHoy(documento);

                if (asistioHoy) {
                    return new SimpleStringProperty("Hoy");
                } else {
                    List<Asistencias> historial = asistenciaService.obtenerHistorialCliente(documento);
                    if (!historial.isEmpty()) {
                        Asistencias ultima = historial.get(historial.size() - 1);
                        return new SimpleStringProperty(FormateadorFechas.formatearFecha(ultima.getFecha()));
                    }
                    return new SimpleStringProperty("Sin registro");
                }
            } catch (SQLException e) {
                return new SimpleStringProperty("-");
            }
        });

        // Asistencias del Mes
        colAsistenciasMes.setCellValueFactory(cellData -> {
            try {
                String documento = cellData.getValue().getCliente().getDocumento();
                int asistencias = estadisticaService.contarAsistenciasMesActual(documento);
                return new SimpleStringProperty(String.valueOf(asistencias));
            } catch (SQLException e) {
                return new SimpleStringProperty("0");
            }
        });

        // Columna de acciones
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnHistorialCell = new Button("📊");
            private final Button btnFinalizar = new Button("👁️");
            private final HBox hbox = new HBox(5, btnHistorialCell,btnFinalizar);

            {
                btnFinalizar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 5 10;");
                btnHistorialCell.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 5 10;");

                btnFinalizar.setOnAction(e -> {
                    AsignacionEntrenadores asignacion = getTableView().getItems().get(getIndex());
                    finalizarAsignacion(asignacion.getCliente());
                });

                btnHistorialCell.setOnAction(e -> {
                    AsignacionEntrenadores asignacion = getTableView().getItems().get(getIndex());
                    verHistorialAsistencias(asignacion.getCliente());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });

        // Estilo para estado de membresía
        colEstadoMembresia.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("ACTIVA")) {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void configurarListeners() {
        // Búsqueda en tiempo real
        txtBuscar.textProperty().addListener((obs, old, nuevo) -> aplicarFiltros());

        // Filtros de estado
        filtroEstado.selectedToggleProperty().addListener((obs, old, nuevo) -> aplicarFiltros());

        // Selección de tabla
        tableClientes.getSelectionModel().selectedItemProperty().addListener((obs, old, nuevo) -> {
            boolean haySeleccion = nuevo != null;
            btnVerDetalle.setDisable(!haySeleccion);
            btnVerHistorial.setDisable(!haySeleccion);
            btnFinalizarAsignacion.setDisable(!haySeleccion);
        });
    }

    private void cargarDatos() {
        if (entrenadorActual == null) {
            mostrarError("Error", "No se pudo identificar al entrenador");
            return;
        }

        try {
            // Cargar clientes activos
            List<AsignacionEntrenadores> clientes = entrenadorService.obtenerClientesActivos(
                    entrenadorActual.getDocuEntrenador()
            );

            listaCompleta.setAll(clientes);
            aplicarFiltros();

            // Actualizar estadísticas
            actualizarEstadisticas();

        } catch (SQLException e) {
            mostrarError("Error al cargar datos", e.getMessage());
            e.printStackTrace();
        }
    }

    private void actualizarEstadisticas() throws SQLException {
        // Total de clientes
        lblTotalClientes.setText(String.valueOf(listaCompleta.size()));

        // Clientes que asistieron hoy
        int asistenciasHoy = 0;
        for (AsignacionEntrenadores asignacion : listaCompleta) {
            if (estadisticaService.asistioHoy(asignacion.getCliente().getDocumento())) {
                asistenciasHoy++;
            }
        }
        lblAsistenciasHoy.setText(String.valueOf(asistenciasHoy));

        // Promedio de asistencias del mes
        if (!listaCompleta.isEmpty()) {
            double totalAsistencias = 0;
            for (AsignacionEntrenadores asignacion : listaCompleta) {
                totalAsistencias += estadisticaService.contarAsistenciasMesActual(
                        asignacion.getCliente().getDocumento()
                );
            }
            double promedio = totalAsistencias / listaCompleta.size();
            lblPromedioMes.setText(String.format("%.1f", promedio));
        } else {
            lblPromedioMes.setText("0.0");
        }
    }

    private void aplicarFiltros() {
        if (listaCompleta == null) return;

        String busqueda = txtBuscar.getText().toLowerCase().trim();
        String filtroSeleccionado = obtenerFiltroSeleccionado();

        List<AsignacionEntrenadores> filtrados = listaCompleta.stream()
                .filter(asignacion -> {
                    Clientes cliente = asignacion.getCliente();

                    // Filtro de búsqueda
                    boolean cumpleBusqueda = busqueda.isEmpty() ||
                            cliente.getNombreCompleto().toLowerCase().contains(busqueda) ||
                            cliente.getDocumento().contains(busqueda);

                    if (!cumpleBusqueda) return false;

                    // Filtro de estado
                    try {
                        boolean tieneMembresia = membresiaClienteService.tieneMembresiaActiva(cliente.getDocumento());

                        switch (filtroSeleccionado) {
                            case "ACTIVOS":
                                return tieneMembresia;
                            case "VENCIDOS":
                                return !tieneMembresia;
                            default: // TODOS
                                return true;
                        }
                    } catch (SQLException e) {
                        return true; // En caso de error, mostrar el cliente
                    }
                })
                .collect(Collectors.toList());

        listaFiltrada.setAll(filtrados);
        tableClientes.setItems(listaFiltrada);
        lblResultados.setText("Mostrando " + filtrados.size() + " cliente(s)");
    }

    private String obtenerFiltroSeleccionado() {
        Toggle seleccionado = filtroEstado.getSelectedToggle();
        if (seleccionado == btnActivos) return "ACTIVOS";
        if (seleccionado == btnVencidos) return "VENCIDOS";
        return "TODOS";
    }

    // Handlers
    @FXML
    private void handleLimpiarFiltros() {
        txtBuscar.clear();
        btnTodos.setSelected(true);
        aplicarFiltros();
    }

    @FXML
    private void handleVerHistorial() {
        AsignacionEntrenadores seleccionada = tableClientes.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            verHistorialAsistencias(seleccionada.getCliente());
        }
    }

    private void verHistorialAsistencias(Clientes cliente) {
        try {
            List<Asistencias> historial = asistenciaService.obtenerHistorialCliente(cliente.getDocumento());

            // Crear diálogo para mostrar historial
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Historial de Asistencias");
            dialog.setHeaderText("Cliente: " + cliente.getNombreCompleto());

            ListView<String> listView = new ListView<>();

            if (historial.isEmpty()) {
                listView.getItems().add("No hay registros de asistencias");
            } else {
                for (Asistencias asistencia : historial) {
                    String item = "📅 " + FormateadorFechas.formatearFecha(asistencia.getFecha());
                    listView.getItems().add(item);
                }
            }

            listView.setPrefHeight(300);
            listView.setPrefWidth(400);

            dialog.getDialogPane().setContent(listView);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.showAndWait();

        } catch (SQLException e) {
            mostrarError("Error", "No se pudo cargar el historial: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFinalizarAsignacion(){
        AsignacionEntrenadores seleccionada = tableClientes.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            finalizarAsignacion(seleccionada.getCliente());
        }
    }

    @FXML
    private void finalizarAsignacion(Clientes cliente) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Finalización");
        confirmacion.setHeaderText("¿Finalizar asignación?");
        confirmacion.setContentText("¿Está seguro de finalizar la asignación del cliente " +
                cliente.getNombreCompleto() + "?");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    entrenadorService.finalizarAsignacion(cliente.getDocumento());

                    mostrarExito("Asignación finalizada correctamente");

                    // Recargar datos
                    cargarDatos();

                } catch (SQLException e) {
                    mostrarError("Error al finalizar", e.getMessage());
                    e.printStackTrace();
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