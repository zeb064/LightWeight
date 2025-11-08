package org.example.gimnasioproyect.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.gimnasioproyect.HelloApplication;
import org.example.gimnasioproyect.Utilidades.CalculadoraFechas;
import org.example.gimnasioproyect.Utilidades.FormateadorFechas;
import org.example.gimnasioproyect.Utilidades.ServiceFactory;
import org.example.gimnasioproyect.model.*;
import org.example.gimnasioproyect.services.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class DetalleClienteController {

    @FXML private Text lblNombreCliente;
    @FXML private Text lblDocumento;

    // Información Personal
    @FXML private Label lblEdad;
    @FXML private Label lblGenero;
    @FXML private Label lblTelefono;
    @FXML private Label lblCorreo;
    @FXML private Label lblDireccion;
    @FXML private Label lblBarrio;
    @FXML private Label lblFechaRegistro;

    // Membresía
    @FXML private VBox boxMembresia;
    @FXML private Text lblTipoMembresia;
    @FXML private Text lblEstadoMembresia;
    @FXML private Label lblFechaInicioMembresia;
    @FXML private Label lblFechaFinMembresia;
    @FXML private Label lblDiasRestantes;
    @FXML private Label lblPrecioMembresia;
    @FXML private Button btnRenovarMembresia;

    // Entrenador
    @FXML private VBox boxEntrenador;
    @FXML private Button btnAsignarEntrenador;

    // Asistencias
    @FXML private Text lblTotalAsistencias;
    @FXML private Text lblAsistenciasMes;
    @FXML private Label lblUltimaAsistencia;

    // Rutinas
    @FXML private ListView<String> listRutinas;

    private Clientes cliente;
    private StackPane parentContainer;

    private ClienteServices clienteService;
    private MembresiaClienteService membresiaClienteService;
    private EntrenadorService entrenadorService;
    private AsistenciaService asistenciaService;
    private RutinaService rutinaService;
    private EstadisticaService estadisticaService;

    public void initialize() {
        // Obtener servicios
        ServiceFactory factory = ServiceFactory.getInstance();
        this.clienteService = factory.getClienteService();
        this.membresiaClienteService = factory.getMembresiaClienteService();
        this.entrenadorService = factory.getEntrenadorService();
        this.asistenciaService = factory.getAsistenciaService();
        this.rutinaService = factory.getRutinaService();
        this.estadisticaService = factory.getEstadisticaService();
    }

    public void setParentContainer(StackPane container) {
        this.parentContainer = container;
    }

    public void cargarCliente(Clientes cliente) {
        this.cliente = cliente;

        try {
            // Cargar información básica
            cargarInformacionPersonal();

            // Cargar membresía
            cargarMembresia();

            // Cargar entrenador
            cargarEntrenador();

            // Cargar estadísticas de asistencias
            cargarEstadisticasAsistencias();

            // Cargar rutinas
            cargarRutinas();

        } catch (SQLException e) {
            mostrarError("Error al cargar datos", e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarInformacionPersonal() {
        lblNombreCliente.setText(cliente.getNombreCompleto());
        lblDocumento.setText("CC: " + cliente.getDocumento());

        // Calcular edad
        int edad = CalculadoraFechas.calcularEdad(cliente.getFechaNacimiento());
        lblEdad.setText(edad + " años");

        lblGenero.setText(cliente.getGenero().equals("M") ? "Masculino" : "Femenino");
        lblTelefono.setText(cliente.getTelefono());
        lblCorreo.setText(cliente.getCorreo() != null ? cliente.getCorreo() : "No especificado");
        lblDireccion.setText(cliente.getDireccion() != null ? cliente.getDireccion() : "No especificada");
        lblBarrio.setText(cliente.getBarrio() != null ? cliente.getBarrio().getNombreBarrio() : "No especificado");
        lblFechaRegistro.setText(FormateadorFechas.formatearFecha(cliente.getFechaRegistro()));
    }

    private void cargarMembresia() throws SQLException {
        Optional<MembresiaClientes> membresiaOpt = membresiaClienteService.obtenerMembresiaActiva(cliente.getDocumento());

        if (membresiaOpt.isPresent()) {
            MembresiaClientes membresia = membresiaOpt.get();

            lblTipoMembresia.setText(membresia.getMembresia().getTipoMembresia());
            lblFechaInicioMembresia.setText(FormateadorFechas.formatearFecha(membresia.getFechaAsignacion()));
            lblFechaFinMembresia.setText(FormateadorFechas.formatearFecha(membresia.getFechaFinalizacion()));
            lblPrecioMembresia.setText("$" + String.format("%,.0f", membresia.getMembresia().getPrecioMembresia()));

            // Calcular días restantes
            long diasRestantes = estadisticaService.calcularDiasRestantes(cliente.getDocumento());

            if (membresia.estaActiva()) {
                lblEstadoMembresia.setText("✓ ACTIVA");
                lblEstadoMembresia.setStyle("-fx-fill: #27ae60;");
                lblDiasRestantes.setText(diasRestantes + " días");
                lblDiasRestantes.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

                btnRenovarMembresia.setText("🔄 Renovar Membresía");
            } else if (membresia.estaVencida()) {
                lblEstadoMembresia.setText("⚠ VENCIDA");
                lblEstadoMembresia.setStyle("-fx-fill: #e74c3c;");
                lblDiasRestantes.setText("Vencida");
                lblDiasRestantes.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");

                btnRenovarMembresia.setText("💳 Renovar Ahora");
            }

        } else {
            // Sin membresía
            boxMembresia.getChildren().clear();
            Text sinMembresia = new Text("Sin Membresía Activa");
            sinMembresia.setStyle("-fx-fill: #7f8c8d;");
            sinMembresia.setFont(Font.font("System", 18));
            boxMembresia.getChildren().add(sinMembresia);

            lblEstadoMembresia.setText("✗ SIN MEMBRESÍA");
            lblEstadoMembresia.setStyle("-fx-fill: #7f8c8d;");
            lblFechaInicioMembresia.setText("-");
            lblFechaFinMembresia.setText("-");
            lblDiasRestantes.setText("-");
            lblPrecioMembresia.setText("-");

            btnRenovarMembresia.setText("💳 Asignar Membresía");
        }
    }

    private void cargarEntrenador() throws SQLException {
        Optional<AsignacionEntrenadores> asignacionOpt = entrenadorService.obtenerEntrenadorDeCliente(cliente.getDocumento());

        boxEntrenador.getChildren().clear();

        if (asignacionOpt.isPresent()) {
            AsignacionEntrenadores asignacion = asignacionOpt.get();
            Entrenadores entrenador = asignacion.getEntrenador();

            Text nombreEntrenador = new Text(entrenador.getNombreCompleto());
            nombreEntrenador.setStyle("-fx-fill: #ecf0f1;");
            nombreEntrenador.setFont(Font.font("System Bold", 16));

            Text especialidad = new Text(entrenador.getEspecialidad());
            especialidad.setStyle("-fx-fill: #3498db;");
            especialidad.setFont(Font.font("System", 13));

            Text experiencia = new Text(entrenador.getExperiencia() + " años de experiencia");
            experiencia.setStyle("-fx-fill: #bdc3c7;");
            experiencia.setFont(Font.font("System", 12));

            boxEntrenador.getChildren().addAll(nombreEntrenador, especialidad, experiencia);
            btnAsignarEntrenador.setText("🔄 Cambiar Entrenador");

        } else {
            Text sinEntrenador = new Text("Sin Entrenador Asignado");
            sinEntrenador.setStyle("-fx-fill: #7f8c8d;");
            sinEntrenador.setFont(Font.font("System", 14));
            boxEntrenador.getChildren().add(sinEntrenador);

            btnAsignarEntrenador.setText("➕ Asignar Entrenador");
        }
    }

    private void cargarEstadisticasAsistencias() throws SQLException {
        int totalAsistencias = estadisticaService.contarAsistenciasCliente(cliente.getDocumento());
        int asistenciasMes = estadisticaService.contarAsistenciasMesActual(cliente.getDocumento());

        lblTotalAsistencias.setText(String.valueOf(totalAsistencias));
        lblAsistenciasMes.setText(String.valueOf(asistenciasMes));

        // Verificar si asistió hoy
        boolean asistioHoy = estadisticaService.asistioHoy(cliente.getDocumento());

        if (asistioHoy) {
            lblUltimaAsistencia.setText("Hoy");
            lblUltimaAsistencia.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        } else {
            // Obtener última asistencia
            List<Asistencias> historial = asistenciaService.obtenerHistorialCliente(cliente.getDocumento());
            if (!historial.isEmpty()) {
                Asistencias ultima = historial.get(historial.size() - 1);
                lblUltimaAsistencia.setText(FormateadorFechas.formatearFecha(ultima.getFecha()));
                lblUltimaAsistencia.setStyle("-fx-text-fill: #ecf0f1; -fx-font-weight: bold;");
            } else {
                lblUltimaAsistencia.setText("Sin asistencias");
                lblUltimaAsistencia.setStyle("-fx-text-fill: #7f8c8d; -fx-font-weight: bold;");
            }
        }
    }

    private void cargarRutinas() throws SQLException {
        List<RutinaAsignadas> rutinasActivas = rutinaService.obtenerRutinasActivasCliente(cliente.getDocumento());

        listRutinas.getItems().clear();

        if (rutinasActivas.isEmpty()) {
            listRutinas.getItems().add("Sin rutinas asignadas");
        } else {
            for (RutinaAsignadas ra : rutinasActivas) {
                String item = "📝 " + ra.getRutina().getObjetivo() +
                        " (Desde: " + FormateadorFechas.formatearFecha(ra.getFechaAsignacion()) + ")";
                listRutinas.getItems().add(item);
            }
        }
    }

    // Handlers de botones
    @FXML
    private void handleVolver() {
        try {
            javafx.scene.Parent gestionClientes = HelloApplication.loadFXML("GestionClientes");

            if (parentContainer != null) {
                parentContainer.getChildren().clear();
                parentContainer.getChildren().add(gestionClientes);
            } else {
                // Plan B: buscar el contentArea
                StackPane contentArea = (StackPane) lblNombreCliente.getScene().getRoot().lookup("#contentArea");
                if (contentArea != null) {
                    contentArea.getChildren().clear();
                    contentArea.getChildren().add(gestionClientes);
                }
            }
        } catch (IOException e) {
            mostrarError("Error", "No se pudo volver: " + e.getMessage());
            e.printStackTrace();
        }
//        try {
//            Parent gestionClientes = HelloApplication.loadFXML("GestionClientes");
//            if (parentContainer != null) {
//                parentContainer.getChildren().clear();
//                parentContainer.getChildren().add(gestionClientes);
//            }
//        } catch (IOException e) {
//            mostrarError("Error", "No se pudo volver: " + e.getMessage());
//        }
    }

    @FXML
    private void handleEditar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gimnasioproyect/FormularioCliente.fxml"));
            Parent formulario = loader.load();

            FormularioClienteController controller = loader.getController();
            controller.cargarCliente(cliente);
            controller.setVolverADetalle(true);

            // Pasar referencia del contenedor padre
            if (parentContainer != null) {
                controller.setParentContainer(parentContainer);
                parentContainer.getChildren().clear();
                parentContainer.getChildren().add(formulario);
            }

        } catch (IOException e) {
            mostrarError("Error", "No se pudo abrir el formulario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMembresia() {
        abrirDialogoMembresia(false);
    }

    @FXML
    private void handleRenovarMembresia() {
        abrirDialogoMembresia(true);
    }

    private void abrirDialogoMembresia(boolean renovacion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gimnasioproyect/AsignarMembresia.fxml"));
            Parent root = loader.load();

            AsignarMembresiaController controller = loader.getController();
            controller.setCliente(cliente);
            controller.setModoRenovacion(renovacion);

            // Callback para recargar datos al éxito
            controller.setOnSuccess(success -> {
                if (success) {
                    try {
                        cargarCliente(cliente); // Recargar datos del cliente
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            // Crear ventana modal
            Stage stage = new Stage();
            stage.setTitle(renovacion ? "Renovar Membresía" : "Asignar Membresía");
            stage.setScene(new javafx.scene.Scene(root));
            stage.setResizable(false);
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.initOwner(lblNombreCliente.getScene().getWindow());
            stage.showAndWait();

        } catch (IOException e) {
            mostrarError("Error", "No se pudo abrir el diálogo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAsignarEntrenador() {
        // TODO: Asignar entrenador
        System.out.println("Asignar entrenador");
    }

    @FXML
    private void handleAsignarRutina() {
        // TODO: Asignar rutina
        System.out.println("Asignar rutina");
    }

    @FXML
    private void handleVerHistorialAsistencias() {
        // TODO: Ver historial completo
        System.out.println("Ver historial de asistencias");
    }

    @FXML
    private void handleEliminar() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar cliente?");
        confirmacion.setContentText("¿Está seguro de eliminar a " + cliente.getNombreCompleto() + "?");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    clienteService.eliminarCliente(cliente.getDocumento());
                    mostrarExito("Cliente eliminado correctamente");
                    handleVolver();
                } catch (SQLException e) {
                    mostrarError("Error al eliminar", e.getMessage());
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