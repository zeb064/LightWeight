package org.example.gimnasioproyect.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.example.gimnasioproyect.Utilidades.ServiceFactory;
import org.example.gimnasioproyect.model.HistorialMensajeTelegram;
import org.example.gimnasioproyect.repository.HistorialMensajeTelegramRepository;
import org.example.gimnasioproyect.services.HistorialNotificacionService;
import org.example.gimnasioproyect.services.NotificacionService;
import org.example.gimnasioproyect.services.TelegramBotService;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class HistorialNotificacionesController {

    @FXML private Text lblTotalEnviados;
    @FXML private Text lblExitosos;
    @FXML private Text lblFallidos;
    @FXML private Text lblHoy;

    @FXML private TextField txtBuscarCliente;
    @FXML private ComboBox<String> cmbTipoMensaje;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private DatePicker dpFechaDesde;
    @FXML private DatePicker dpFechaHasta;

    @FXML private TableView<HistorialMensajeTelegram> tableHistorial;
    @FXML private TableColumn<HistorialMensajeTelegram, String> colFecha;
    @FXML private TableColumn<HistorialMensajeTelegram, String> colTipo;
    @FXML private TableColumn<HistorialMensajeTelegram, String> colCliente;
    @FXML private TableColumn<HistorialMensajeTelegram, String> colDocumento;
    @FXML private TableColumn<HistorialMensajeTelegram, String> colEstado;
    @FXML private TableColumn<HistorialMensajeTelegram, String> colMensaje;
    @FXML private TableColumn<HistorialMensajeTelegram, Void> colAcciones;

    @FXML private Label lblResultados;

    @FXML private Button btnVerDetalle;
    @FXML private Button btnReenviar;
    @FXML private Button btnExportar;

    private HistorialNotificacionService historialService;
    private NotificacionService notificacionService;
    private TelegramBotService telegramBotService;

    // === Datos ===
    private ObservableList<HistorialMensajeTelegram> historialCompleto;
    private ObservableList<HistorialMensajeTelegram> historialFiltrado;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        // Obtener servicios
        historialService = ServiceFactory.getInstance().getHistorialService();
        notificacionService = ServiceFactory.getInstance().getNotificacionService();
        telegramBotService = ServiceFactory.getInstance().getTelegramBotService();

        historialCompleto = FXCollections.observableArrayList();
        historialFiltrado = FXCollections.observableArrayList();

        // Configurar tabla
        configurarTabla();

        // Configurar ComboBoxes
        configurarComboBoxes();

        // Configurar listeners
        configurarListeners();

        // Cargar datos
        cargarHistorial();
    }

    private void configurarTabla() {
        // Columna Fecha
        colFecha.setCellValueFactory(cellData -> {
            Timestamp fecha = cellData.getValue().getFechaEnvio();
            if (fecha != null) {
                String fechaFormateada = fecha.toLocalDateTime().format(FORMATTER);
                return new SimpleStringProperty(fechaFormateada);
            }
            return new SimpleStringProperty("");
        });

        // Columna Tipo
        colTipo.setCellValueFactory(cellData -> {
            String tipo = cellData.getValue().getMensaje().getTipoMensaje();
            return new SimpleStringProperty(formatearTipo(tipo));
        });

        // Columna Cliente
        colCliente.setCellValueFactory(cellData -> {
            String nombre = cellData.getValue().getClientes().getNombreCompleto();
            return new SimpleStringProperty(nombre);
        });

        // Columna Documento
        colDocumento.setCellValueFactory(cellData -> {
            String documento = cellData.getValue().getClientes().getDocumento();
            return new SimpleStringProperty(documento);
        });

        // Columna Estado
        colEstado.setCellValueFactory(cellData -> {
            String estado = cellData.getValue().getEstado();
            return new SimpleStringProperty(estado);
        });

        // Estilo personalizado para estado
        colEstado.setCellFactory(column -> new TableCell<HistorialMensajeTelegram, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);

                    if (item.equals("ENVIADO")) {
                        setStyle("-fx-background-color: rgba(46, 204, 113, 0.2); " +
                                "-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-background-color: rgba(231, 76, 60, 0.2); " +
                                "-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Columna Mensaje (Vista previa)
        colMensaje.setCellValueFactory(cellData -> {
            String mensaje = cellData.getValue().getMensajeFinal();
            if (mensaje != null && mensaje.length() > 50) {
                mensaje = mensaje.substring(0, 50) + "...";
            }
            return new SimpleStringProperty(mensaje);
        });

        // Columna Acciones
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnVer = new Button("👁️");

            {
                btnVer.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                        "-fx-background-radius: 5; -fx-cursor: hand; -fx-padding: 5 10;");
                btnVer.setOnAction(event -> {
                    HistorialMensajeTelegram historial = getTableView().getItems().get(getIndex());
                    mostrarDetalleCompleto(historial);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnVer);
                }
            }
        });

        tableHistorial.setItems(historialFiltrado);
    }

    private void configurarComboBoxes() {
        cmbTipoMensaje.setItems(FXCollections.observableArrayList(
                "TODOS",
                "BIENVENIDA",
                "VENCE_PRONTO",
                "VENCIDO",
                "INACTIVIDAD_7_DIAS",
                "RUTINA_ACTUALIZADA",
                "NUEVO_ENTRENADOR"
        ));
        cmbTipoMensaje.setValue("TODOS");

        // Estado
        cmbEstado.setItems(FXCollections.observableArrayList(
                "TODOS", "ENVIADO", "FALLIDO"
        ));
        cmbEstado.setValue("TODOS");
    }

    private void configurarListeners() {
        // Listener para habilitar/deshabilitar botones
        tableHistorial.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    boolean haySeleccion = newSelection != null;
                    btnVerDetalle.setDisable(!haySeleccion);

                    // Habilitar reenviar solo si el mensaje falló
                    if (haySeleccion && newSelection.getEstado().equals("FALLIDO")) {
                        btnReenviar.setDisable(false);
                    } else {
                        btnReenviar.setDisable(true);
                    }
                }
        );

        // Listeners para búsqueda en tiempo real
        txtBuscarCliente.textProperty().addListener((obs, old, newVal) -> aplicarFiltros());
        cmbTipoMensaje.valueProperty().addListener((obs, old, newVal) -> aplicarFiltros());
        cmbEstado.valueProperty().addListener((obs, old, newVal) -> aplicarFiltros());
    }

    private void cargarHistorial() {
        new Thread(() -> {
            try {
                List<HistorialMensajeTelegram> historial = historialService.obtenerTodoElHistorial();

                Platform.runLater(() -> {
                    historialCompleto.clear();
                    historialCompleto.addAll(historial);
                    aplicarFiltros();
                    actualizarEstadisticas();
                });

            } catch (SQLException e) {
                Platform.runLater(() ->
                        mostrarAlerta(Alert.AlertType.ERROR, "Error",
                                "No se pudo cargar el historial: " + e.getMessage())
                );
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void handleBuscar() {
        aplicarFiltros();
    }

    @FXML
    private void handleLimpiarFiltros() {
        txtBuscarCliente.clear();
        cmbTipoMensaje.setValue("TODOS");
        cmbEstado.setValue("TODOS");
        dpFechaDesde.setValue(null);
        dpFechaHasta.setValue(null);
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        historialFiltrado.clear();

        List<HistorialMensajeTelegram> resultado = historialCompleto.stream()
                .filter(h -> filtrarPorCliente(h))
                .filter(h -> filtrarPorTipo(h))
                .filter(h -> filtrarPorEstado(h))
                .filter(h -> filtrarPorFechas(h))
                .collect(Collectors.toList());

        historialFiltrado.addAll(resultado);

        lblResultados.setText("Mostrando " + historialFiltrado.size() + " registros");
    }

    private boolean filtrarPorCliente(HistorialMensajeTelegram h) {
        String busqueda = txtBuscarCliente.getText();
        if (busqueda == null || busqueda.trim().isEmpty()) {
            return true;
        }

        busqueda = busqueda.toLowerCase();
        String nombre = h.getClientes().getNombreCompleto().toLowerCase();
        String documento = h.getClientes().getDocumento().toLowerCase();

        return nombre.contains(busqueda) || documento.contains(busqueda);
    }

    private boolean filtrarPorTipo(HistorialMensajeTelegram h) {
        String tipo = cmbTipoMensaje.getValue();
        if (tipo == null || tipo.equals("TODOS")) {
            return true;
        }

        return h.getMensaje().getTipoMensaje().equals(tipo);
    }

    private boolean filtrarPorEstado(HistorialMensajeTelegram h) {
        String estado = cmbEstado.getValue();
        if (estado == null || estado.equals("TODOS")) {
            return true;
        }

        return h.getEstado().equals(estado);
    }

    private boolean filtrarPorFechas(HistorialMensajeTelegram h) {
        LocalDate desde = dpFechaDesde.getValue();
        LocalDate hasta = dpFechaHasta.getValue();

        if (desde == null && hasta == null) {
            return true;
        }

        LocalDate fechaMensaje = h.getFechaEnvio().toLocalDateTime().toLocalDate();

        if (desde != null && hasta != null) {
            return !fechaMensaje.isBefore(desde) && !fechaMensaje.isAfter(hasta);
        } else if (desde != null) {
            return !fechaMensaje.isBefore(desde);
        } else {
            return !fechaMensaje.isAfter(hasta);
        }
    }

    private void actualizarEstadisticas() {
        int total = historialCompleto.size();
        int exitosos = (int) historialCompleto.stream()
                .filter(h -> h.getEstado().equals("ENVIADO"))
                .count();
        int fallidos = total - exitosos;

        LocalDate hoy = LocalDate.now();
        int hoyCount = (int) historialCompleto.stream()
                .filter(h -> h.getFechaEnvio().toLocalDateTime().toLocalDate().equals(hoy))
                .count();

        lblTotalEnviados.setText(String.valueOf(total));
        lblExitosos.setText(String.valueOf(exitosos));
        lblFallidos.setText(String.valueOf(fallidos));
        lblHoy.setText(String.valueOf(hoyCount));
    }

    @FXML
    private void handleVerDetalle() {
        HistorialMensajeTelegram seleccionado = tableHistorial.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            mostrarDetalleCompleto(seleccionado);
        }
    }

    private void mostrarDetalleCompleto(HistorialMensajeTelegram historial) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Detalle del Mensaje");
        dialog.setHeaderText("Información Completa");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        int row = 0;

        // Fecha/Hora
        grid.add(new Label("Fecha/Hora:"), 0, row);
        grid.add(new Label(historial.getFechaEnvio().toLocalDateTime().format(FORMATTER)), 1, row++);

        // Tipo
        grid.add(new Label("Tipo:"), 0, row);
        grid.add(new Label(formatearTipo(historial.getMensaje().getTipoMensaje())), 1, row++);

        // Cliente
        grid.add(new Label("Cliente:"), 0, row);
        grid.add(new Label(historial.getClientes().getNombreCompleto()), 1, row++);

        // Documento
        grid.add(new Label("Documento:"), 0, row);
        grid.add(new Label(historial.getClientes().getDocumento()), 1, row++);

        // Estado
        grid.add(new Label("Estado:"), 0, row);
        Label lblEstado = new Label(historial.getEstado());
        if (historial.getEstado().equals("ENVIADO")) {
            lblEstado.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
        } else {
            lblEstado.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        }
        grid.add(lblEstado, 1, row++);

        // Chat ID
        grid.add(new Label("Chat ID:"), 0, row);
        grid.add(new Label(historial.getChatId()), 1, row++);

        // Mensaje Completo
        grid.add(new Label("Mensaje:"), 0, row);
        TextArea txtMensaje = new TextArea(historial.getMensajeFinal());
        txtMensaje.setEditable(false);
        txtMensaje.setWrapText(true);
        txtMensaje.setPrefRowCount(10);
        txtMensaje.setStyle("-fx-control-inner-background: #f5f5f5;");
        grid.add(txtMensaje, 1, row++);

        dialog.getDialogPane().setContent(grid);
        dialog.showAndWait();
    }

    @FXML
    private void handleReenviar() {
        HistorialMensajeTelegram seleccionado = tableHistorial.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            return;
        }

        if (!seleccionado.getEstado().equals("FALLIDO")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia",
                    "Solo se pueden reenviar mensajes fallidos");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Reenviar Mensaje");
        confirmacion.setHeaderText("¿Reenviar mensaje?");
        confirmacion.setContentText("Se intentará enviar nuevamente el mensaje a " +
                seleccionado.getClientes().getNombreCompleto());

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                reenviarMensaje(seleccionado);
            }
        });
    }

    private void reenviarMensaje(HistorialMensajeTelegram historial) {
        new Thread(() -> {
            boolean enviado = telegramBotService.enviarMensaje(
                    historial.getChatId(),
                    historial.getMensajeFinal()
            );

            Platform.runLater(() -> {
                if (enviado) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                            "Mensaje reenviado correctamente");
                    cargarHistorial(); // Recargar para actualizar
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "No se pudo reenviar el mensaje");
                }
            });
        }).start();
    }

    @FXML
    private void handleExportar() {
        mostrarAlerta(Alert.AlertType.INFORMATION, "Próximamente",
                "Funcionalidad de exportación en desarrollo");
        // TODO: Implementar exportación a CSV/Excel
    }

    @FXML
    private void handleActualizar() {
        cargarHistorial();
    }

    private String formatearTipo(String tipo) {
        switch (tipo) {
            case "BIENVENIDA":
                return "🎉 Bienvenida";
            case "VENCE_PRONTO":
                return "⚠️ Próximo a Vencer";
            case "VENCIDO":
                return "❌ Vencido";
            case "INACTIVIDAD_7_DIAS":
                return "😔 Inactividad";
            case "RUTINA_ACTUALIZADA":
                return "🎯 Rutina Actualizada";
            case "NUEVO_ENTRENADOR":
                return "👨‍🏫 Nuevo Entrenador";
            default:
                return tipo;
        }
    }
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

}