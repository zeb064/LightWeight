package org.example.gimnasioproyect.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.example.gimnasioproyect.HelloApplication;
import org.example.gimnasioproyect.Utilidades.ServiceFactory;
import org.example.gimnasioproyect.services.EstadisticaService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class DashboardController {

    // Header
    @FXML private Text lblFechaHora;
    @FXML private Button btnActualizar;

    // Tarjetas Principales
    @FXML private Text lblTotalClientes;
    @FXML private Text lblClientesActivos;
    @FXML private Text lblAsistenciasHoy;
    @FXML private Text lblIngresosMes;

    // Alertas de Membresías
    @FXML private Text lblMembresiasVencidas;
    @FXML private Text lblMembresiasProximas;
    @FXML private Text lblMembresiasActivas;

    // Nuevos Clientes
    @FXML private Text lblNuevosClientes;
    @FXML private Text lblAsistenciasMes;

    // Top Clientes
    @FXML private BarChart<String, Number> barChartTopClientes;
    @FXML private ListView<String> listTopClientes;

    // Distribución Membresías
    @FXML private VBox boxMembresias;

    // Servicio
    private EstadisticaService estadisticaService;
    private Timeline relojTimeline;

    public void initialize() {
        // Obtener servicios
        ServiceFactory factory = ServiceFactory.getInstance();
        this.estadisticaService = factory.getEstadisticaService();
        this.

        // Iniciar reloj
        inicializarReloj();

        // Cargar datos
        cargarDatos();
    }

    private void inicializarReloj() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern(
                "EEEE, dd 'de' MMMM yyyy - hh:mm a",
                new Locale("es", "ES")
        );

        // Actualizar cada segundo
        relojTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalDateTime ahora = LocalDateTime.now();
            lblFechaHora.setText(ahora.format(formato));
        }));

        relojTimeline.setCycleCount(Animation.INDEFINITE);
        relojTimeline.play();

        // Establecer hora inicial
        lblFechaHora.setText(LocalDateTime.now().format(formato));
    }

    private void cargarDatos() {
        try {
            cargarEstadisticasPrincipales();
            cargarAlertasMembresias();
            cargarNuevosClientes();
            cargarTopClientes();
            cargarDistribucionMembresias();

        } catch (SQLException e) {
            mostrarError("Error al cargar datos", e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarEstadisticasPrincipales() throws SQLException {
        // Total Clientes
        int totalClientes = estadisticaService.obtenerTotalClientes();
        lblTotalClientes.setText(String.valueOf(totalClientes));

        // Clientes Activos (con membresía)
        int clientesActivos = estadisticaService.obtenerClientesActivos();
        lblClientesActivos.setText(String.valueOf(clientesActivos));

        // Asistencias Hoy
        int asistenciasHoy = estadisticaService.obtenerAsistenciasHoy();
        lblAsistenciasHoy.setText(String.valueOf(asistenciasHoy));

        // Ingresos del Mes
        double ingresos = estadisticaService.obtenerIngresoTotalMembresias();
        lblIngresosMes.setText("$" + String.format("%,.0f", ingresos));
    }

    private void cargarAlertasMembresias() throws SQLException {
        // Membresías Vencidas
        int vencidas = estadisticaService.obtenerTotalMembresiasVencidas();
        lblMembresiasVencidas.setText(vencidas + " membresías vencidas o finalizadas");

        // Próximas a Vencer (7 días)
        int proximas = estadisticaService.obtenerMembresiasProximasAVencer();
        lblMembresiasProximas.setText(proximas + " próximas a vencer (7 días)");

        // Activas
        int activas = estadisticaService.obtenerTotalMembresiasActivas();
        lblMembresiasActivas.setText(activas + " membresías activas");
    }

    private void cargarNuevosClientes() throws SQLException {
        // Nuevos del mes
        int nuevos = estadisticaService.obtenerNuevosClientesMes();
        lblNuevosClientes.setText(String.valueOf(nuevos));

        // Asistencias del mes
        int asistenciasMes = estadisticaService.obtenerAsistenciasMesActual();
        lblAsistenciasMes.setText(String.valueOf(asistenciasMes));
    }

    private void cargarTopClientes() throws SQLException {
        Map<String, Integer> topClientes = estadisticaService.obtenerClientesMasFrecuentes(5);

        // Limpiar gráfico anterior
        barChartTopClientes.getData().clear();

        if (topClientes.isEmpty()) {
            return;
        }

        // Crear serie de datos
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Asistencias");

        // Agregar datos al gráfico (ya vienen con nombres completos)
        for (Map.Entry<String, Integer> entry : topClientes.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChartTopClientes.getData().add(series);
    }

    private String obtenerEmojiPosicion(int posicion) {
        switch (posicion) {
            case 1: return "🥇";
            case 2: return "🥈";
            case 3: return "🥉";
            default: return "  " + posicion + ".";
        }
    }

    private void cargarDistribucionMembresias() throws SQLException {
        Map<String, Integer> distribucion = estadisticaService.obtenerClientesActivosPorTipoMembresia();

        boxMembresias.getChildren().clear();

        if (distribucion.isEmpty()) {
            Text sinDatos = new Text("Sin membresías registradas");
            sinDatos.setStyle("-fx-fill: #7f8c8d;");
            boxMembresias.getChildren().add(sinDatos);
        } else {
            for (Map.Entry<String, Integer> entry : distribucion.entrySet()) {
                HBox item = crearItemMembresia(entry.getKey(), entry.getValue());
                boxMembresias.getChildren().add(item);
            }
        }
    }

    private HBox crearItemMembresia(String tipo, int cantidad) {
        HBox hbox = new HBox(10);
        hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Tipo
        Text txtTipo = new Text(tipo);
        txtTipo.setStyle("-fx-fill: #ecf0f1;");
        txtTipo.setFont(Font.font("System Bold", 13));
        HBox.setHgrow(txtTipo, javafx.scene.layout.Priority.ALWAYS);

        // Cantidad
        Text txtCantidad = new Text(String.valueOf(cantidad));
        txtCantidad.setStyle("-fx-fill: #3498db;");
        txtCantidad.setFont(Font.font("System Bold", 16));

        hbox.getChildren().addAll(txtTipo, txtCantidad);
        return hbox;
    }

    @FXML
    private void handleActualizar() {
        cargarDatos();
        mostrarExito("Datos actualizados correctamente");
    }

    // Accesos Rápidos
    @FXML
    private void handleNuevoCliente() {
        navegarA("FormularioCliente");
    }

    @FXML
    private void handleRegistrarAsistencia() {
        navegarA("Asistencias");
    }

    @FXML
    private void handleGestionarMembresias() {
        navegarA("GestionMembresias");
    }

    @FXML
    private void handleVerReportes() {
        navegarA("Reportes");
    }

    @FXML
    private void handleVerMembresias() {
        navegarA("GestionMembresias");
    }

    private void navegarA(String vista) {
        try {
            Parent contenido = HelloApplication.loadFXML(vista);

            // Buscar el contentArea
            StackPane contentArea = buscarContentArea();
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(contenido);
            } else {
                mostrarError("Error", "No se pudo encontrar el área de contenido");
            }

        } catch (IOException e) {
            mostrarError("Error de navegación", "No se pudo cargar " + vista + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private StackPane buscarContentArea() {
        if (lblFechaHora.getScene() != null) {
            return (StackPane) lblFechaHora.getScene().getRoot().lookup("#contentArea");
        }
        return null;
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

    // Detener el reloj al cerrar
    public void detenerReloj() {
        if (relojTimeline != null) {
            relojTimeline.stop();
        }
    }
}