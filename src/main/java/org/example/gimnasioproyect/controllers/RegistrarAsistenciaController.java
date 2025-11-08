package org.example.gimnasioproyect.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.example.gimnasioproyect.Utilidades.FormateadorFechas;
import org.example.gimnasioproyect.Utilidades.ServiceFactory;
import org.example.gimnasioproyect.model.Asistencias;
import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.model.MembresiaClientes;
import org.example.gimnasioproyect.services.AsistenciaService;
import org.example.gimnasioproyect.services.ClienteServices;
import org.example.gimnasioproyect.services.EstadisticaService;
import org.example.gimnasioproyect.services.MembresiaClienteService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class RegistrarAsistenciaController {

    // Estadísticas del día
    @FXML private Text lblAsistenciasHoy;
    @FXML private Text lblAsistenciasMes;
    @FXML private Text lblHoraActual;

    // Búsqueda de cliente
    @FXML private TextField txtDocumento;
    @FXML private Button btnBuscar;
    @FXML private Label lblMensaje;

    // Panel de información del cliente
    @FXML private VBox panelCliente;
    @FXML private Label lblNombre;
    @FXML private Label lblDocumentoCliente;
    @FXML private Label lblEstadoMembresia;
    @FXML private Label lblVencimiento;
    @FXML private Label lblUltimaAsistencia;
    @FXML private Button btnRegistrarEntrada;

    // Tabla de asistencias
    @FXML private TableView<Asistencias> tableAsistencias;
    @FXML private TableColumn<Asistencias, String> colHora;
    @FXML private TableColumn<Asistencias, String> colDocumento;
    @FXML private TableColumn<Asistencias, String> colNombre;
    @FXML private TableColumn<Asistencias, String> colMembresia;

    // Servicios
    private AsistenciaService asistenciaService;
    private ClienteServices clienteService;
    private MembresiaClienteService membresiaClienteService;
    private EstadisticaService estadisticaService;

    // Datos
    private Clientes clienteSeleccionado;
    private ObservableList<Asistencias> listaAsistencias;
    private Timeline relojTimeline;

    public void initialize() {
        // Obtener servicios
        ServiceFactory factory = ServiceFactory.getInstance();
        this.asistenciaService = factory.getAsistenciaService();
        this.clienteService = factory.getClienteService();
        this.membresiaClienteService = factory.getMembresiaClienteService();
        this.estadisticaService = factory.getEstadisticaService();

        // Configurar tabla
        configurarTabla();

        // Inicializar reloj en tiempo real
        inicializarReloj();

        // Cargar estadísticas
        actualizarEstadisticas();

        // Cargar asistencias de hoy
        cargarAsistenciasHoy();

        // Ocultar panel de cliente al inicio
        panelCliente.setVisible(false);
        panelCliente.setManaged(false);

        // Configurar Enter en el campo de búsqueda
        txtDocumento.setOnAction(e -> handleBuscar());

        // Limpiar mensaje cuando se escriba
        txtDocumento.textProperty().addListener((obs, old, nuevo) -> {
            lblMensaje.setText("");
            lblMensaje.setStyle("");
        });
    }

    private void configurarTabla() {
        // Columna Hora
        colHora.setCellValueFactory(data -> {
            if (data.getValue().getFecha() != null) {
                // Como solo tenemos LocalDate, mostramos la fecha
                String hora = FormateadorFechas.formatearFecha(data.getValue().getFecha());
                return new SimpleStringProperty(hora);
            }
            return new SimpleStringProperty("--:--");
        });

        // Columna Documento
        colDocumento.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCliente().getDocumento()));

        // Columna Nombre
        colNombre.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCliente().getNombreCompleto()));

        // Columna Membresía
        colMembresia.setCellValueFactory(data -> {
            try {
                String documento = data.getValue().getCliente().getDocumento();
                Optional<MembresiaClientes> membresiaOpt =
                        membresiaClienteService.obtenerMembresiaActiva(documento);

                if (membresiaOpt.isPresent()) {
                    return new SimpleStringProperty(
                            membresiaOpt.get().getMembresia().getTipoMembresia()
                    );
                }
                return new SimpleStringProperty("Sin membresía");
            } catch (SQLException e) {
                return new SimpleStringProperty("Error");
            }
        });

        // Estilo de la columna Membresía
        colMembresia.setCellFactory(column -> new TableCell<Asistencias, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);

                    if (item.equals("Sin membresía")) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void inicializarReloj() {
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm:ss");

        // Actualizar cada segundo
        relojTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalTime horaActual = LocalTime.now();
            lblHoraActual.setText(horaActual.format(formatoHora));
        }));

        relojTimeline.setCycleCount(Animation.INDEFINITE);
        relojTimeline.play();

        // Establecer hora inicial
        lblHoraActual.setText(LocalTime.now().format(formatoHora));
    }

    private void actualizarEstadisticas() {
        try {
            // Asistencias de hoy
            int asistenciasHoy = estadisticaService.obtenerAsistenciasHoy();
            lblAsistenciasHoy.setText(String.valueOf(asistenciasHoy));

            // Asistencias del mes
            int asistenciasMes = estadisticaService.obtenerAsistenciasMesActual();
            lblAsistenciasMes.setText(String.valueOf(asistenciasMes));

        } catch (SQLException e) {
            mostrarError("Error al cargar estadísticas", e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarAsistenciasHoy() {
        try {
            List<Asistencias> asistencias = estadisticaService.obtenerAsistenciasDeHoy();
            listaAsistencias = FXCollections.observableArrayList(asistencias);
            tableAsistencias.setItems(listaAsistencias);

        } catch (SQLException e) {
            mostrarError("Error al cargar asistencias", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBuscar() {
        String documento = txtDocumento.getText().trim();

        // Validación básica
        if (documento.isEmpty()) {
            mostrarMensajeError("Ingrese un documento para buscar");
            return;
        }

        if (!documento.matches("\\d+")) {
            mostrarMensajeError("El documento solo puede contener números");
            return;
        }

        try {
            // Buscar cliente
            Optional<Clientes> clienteOpt = clienteService.buscarClientePorDocumento(documento);

            if (!clienteOpt.isPresent()) {
                mostrarMensajeError("Cliente no encontrado con documento: " + documento);
                ocultarPanelCliente();
                return;
            }

            clienteSeleccionado = clienteOpt.get();

            // Verificar membresía
            Optional<MembresiaClientes> membresiaOpt =
                    membresiaClienteService.obtenerMembresiaActiva(documento);

            if (!membresiaOpt.isPresent()) {
                mostrarMensajeAdvertencia(
                        "⚠️ Cliente encontrado, pero NO tiene membresía activa. " +
                                "No puede registrar asistencia."
                );
                mostrarDatosCliente(clienteSeleccionado, null);
                btnRegistrarEntrada.setDisable(true);
                return;
            }

            MembresiaClientes membresia = membresiaOpt.get();

            // Verificar si ya asistió hoy
            boolean yaAsistioHoy = estadisticaService.asistioHoy(documento);

            if (yaAsistioHoy) {
                mostrarMensajeAdvertencia(
                        "ℹ️ " + clienteSeleccionado.getNombreCompleto() +
                                " ya registró su entrada hoy."
                );
                mostrarDatosCliente(clienteSeleccionado, membresia);
                btnRegistrarEntrada.setDisable(true);
                return;
            }

            // Todo OK - mostrar cliente y habilitar registro
            mostrarMensajeExito("✅ Cliente encontrado. Puede registrar entrada.");
            mostrarDatosCliente(clienteSeleccionado, membresia);
            btnRegistrarEntrada.setDisable(false);

        } catch (SQLException e) {
            mostrarError("Error al buscar cliente", e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarDatosCliente(Clientes cliente, MembresiaClientes membresia) {
        // Mostrar panel
        panelCliente.setVisible(true);
        panelCliente.setManaged(true);

        // Datos básicos
        lblNombre.setText(cliente.getNombreCompleto());
        lblDocumentoCliente.setText(cliente.getDocumento());

        if (membresia != null) {
            // Estado de membresía
            if (membresia.estaActiva()) {
                lblEstadoMembresia.setText("✓ ACTIVA");
                lblEstadoMembresia.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            } else if (membresia.estaVencida()) {
                lblEstadoMembresia.setText("✗ VENCIDA");
                lblEstadoMembresia.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            }

            // Fecha de vencimiento
            lblVencimiento.setText(FormateadorFechas.formatearFecha(membresia.getFechaFinalizacion()));

        } else {
            lblEstadoMembresia.setText("✗ SIN MEMBRESÍA");
            lblEstadoMembresia.setStyle("-fx-text-fill: #7f8c8d; -fx-font-weight: bold;");
            lblVencimiento.setText("-");
        }

        // Última asistencia
        try {
            List<Asistencias> historial = asistenciaService.obtenerHistorialCliente(cliente.getDocumento());

            if (!historial.isEmpty()) {
                Asistencias ultima = historial.get(0); // Ya viene ordenado DESC
                lblUltimaAsistencia.setText(FormateadorFechas.formatearFecha(ultima.getFecha()));
            } else {
                lblUltimaAsistencia.setText("Sin asistencias previas");
            }
        } catch (SQLException e) {
            lblUltimaAsistencia.setText("Error al cargar");
        }


    }

    private void ocultarPanelCliente() {
        panelCliente.setVisible(false);
        panelCliente.setManaged(false);
        clienteSeleccionado = null;
    }

    @FXML
    private void handleRegistrarEntrada() {
        if (clienteSeleccionado == null) {
            mostrarError("Error", "No hay cliente seleccionado");
            return;
        }

        try {
            // Registrar asistencia
            asistenciaService.registrarAsistencia(clienteSeleccionado.getDocumento());

            // Mostrar confirmación
            mostrarExito(
                    "✅ Entrada registrada correctamente para:\n" +
                            clienteSeleccionado.getNombreCompleto()
            );

            // Actualizar estadísticas
            actualizarEstadisticas();

            // Recargar tabla de asistencias
            cargarAsistenciasHoy();

            // Limpiar formulario
            limpiarFormulario();

        } catch (SQLException e) {
            mostrarError("Error al registrar asistencia", e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            mostrarError("Error de validación", e.getMessage());
        }
    }

    @FXML
    private void handleCancelar() {
        limpiarFormulario();
    }

    @FXML
    private void handleActualizar() {
        actualizarEstadisticas();
        cargarAsistenciasHoy();
        mostrarMensajeExito("Datos actualizados correctamente");
    }

    private void limpiarFormulario() {
        txtDocumento.clear();
        lblMensaje.setText("");
        lblMensaje.setStyle("");
        ocultarPanelCliente();
        txtDocumento.requestFocus();
    }

    private void mostrarMensajeError(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
    }

    private void mostrarMensajeAdvertencia(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
    }

    private void mostrarMensajeExito(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
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

    // Detener el reloj al cerrar la vista
    public void detenerReloj() {
        if (relojTimeline != null) {
            relojTimeline.stop();
        }
    }
}