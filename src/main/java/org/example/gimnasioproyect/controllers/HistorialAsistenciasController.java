package org.example.gimnasioproyect.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.gimnasioproyect.Utilidades.FormateadorFechas;
import org.example.gimnasioproyect.Utilidades.ServiceFactory;
import org.example.gimnasioproyect.model.Asistencias;
import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.services.AsistenciaService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

public class HistorialAsistenciasController {

    @FXML private Text lblNombreCliente;
    @FXML private Text lblTotalAsistencias;
    @FXML private Text lblPrimeraAsistencia;
    @FXML private Text lblUltimaAsistencia;
    @FXML private Text lblPromedioMensual;

    @FXML private TableView<AsistenciaRow> tableHistorial;
    @FXML private TableColumn<AsistenciaRow, Integer> colNumero;
    @FXML private TableColumn<AsistenciaRow, String> colFecha;
    @FXML private TableColumn<AsistenciaRow, String> colDiaSemana;
    @FXML private TableColumn<AsistenciaRow, String> colTiempoTranscurrido;

    @FXML private Button btnCerrar;

    private Clientes cliente;
    private AsistenciaService asistenciaService;

    public void initialize() {
        ServiceFactory factory = ServiceFactory.getInstance();
        this.asistenciaService = factory.getAsistenciaService();

        configurarTabla();
        cargarEstilos();
    }

    private void cargarEstilos() {
        try {
            String css = getClass().getResource("/org/example/gimnasioproyect/styles/historial-asistencias.css").toExternalForm();
            tableHistorial.getStylesheets().add(css);
        } catch (Exception e) {
            System.err.println("No se pudo cargar el CSS del historial: " + e.getMessage());
        }
    }

    private void configurarTabla() {
        // Configurar columnas
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colDiaSemana.setCellValueFactory(new PropertyValueFactory<>("diaSemana"));
        colTiempoTranscurrido.setCellValueFactory(new PropertyValueFactory<>("tiempoTranscurrido"));

        // Centrar contenido de columnas
        colNumero.setStyle("-fx-alignment: CENTER;");
        colDiaSemana.setStyle("-fx-alignment: CENTER;");
        colTiempoTranscurrido.setStyle("-fx-alignment: CENTER;");

        // Hacer que las columnas se ajusten al ancho de la tabla
        tableHistorial.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void setCliente(Clientes cliente) {
        this.cliente = cliente;
        lblNombreCliente.setText("Historial de " + cliente.getNombreCompleto());
        cargarDatos();
    }

    private void cargarDatos() {
        try {
            List<Asistencias> historial = asistenciaService.obtenerHistorialCliente(cliente.getDocumento());

            if (historial.isEmpty()) {
                mostrarSinDatos();
                return;
            }

            // Ordenar por fecha descendente (más reciente primero)
            historial.sort((a1, a2) -> a2.getFecha().compareTo(a1.getFecha()));

            // Cargar estadísticas superiores
            cargarEstadisticas(historial);

            // Cargar tabla
            cargarTabla(historial);

        } catch (SQLException e) {
            mostrarError("Error al cargar historial", e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarEstadisticas(List<Asistencias> historial) {
        // Total de asistencias
        lblTotalAsistencias.setText(String.valueOf(historial.size()));

        // Primera asistencia (la más antigua)
        Asistencias primera = historial.get(historial.size() - 1);
        lblPrimeraAsistencia.setText(FormateadorFechas.formatearFecha(primera.getFecha()));

        // Última asistencia (la más reciente)
        Asistencias ultima = historial.get(0);
        lblUltimaAsistencia.setText(FormateadorFechas.formatearFecha(ultima.getFecha()));

        // Calcular promedio mensual
        long diasTranscurridos = ChronoUnit.DAYS.between(primera.getFecha(), LocalDate.now());
        double mesesTranscurridos = diasTranscurridos / 30.0;

        if (mesesTranscurridos < 1) {
            mesesTranscurridos = 1; // Mínimo 1 mes para evitar división por cero
        }

        double promedioMensual = historial.size() / mesesTranscurridos;
        lblPromedioMensual.setText(String.format("%.1f", promedioMensual));
    }

    private void cargarTabla(List<Asistencias> historial) {
        ObservableList<AsistenciaRow> rows = FXCollections.observableArrayList();

        int numero = 1;
        for (Asistencias asistencia : historial) {
            String fecha = FormateadorFechas.formatearFecha(asistencia.getFecha());
            String diaSemana = obtenerDiaSemana(asistencia.getFecha());
            String tiempoTranscurrido = calcularTiempoTranscurrido(asistencia.getFecha());

            rows.add(new AsistenciaRow(numero, fecha, diaSemana, tiempoTranscurrido));
            numero++;
        }

        tableHistorial.setItems(rows);
    }

    private String obtenerDiaSemana(LocalDate fecha) {
        return fecha.getDayOfWeek()
                .getDisplayName(TextStyle.FULL, new Locale("es", "ES"))
                .toUpperCase();
    }

    private String calcularTiempoTranscurrido(LocalDate fecha) {
        LocalDate hoy = LocalDate.now();

        if (fecha.equals(hoy)) {
            return "Hoy";
        }

        long dias = ChronoUnit.DAYS.between(fecha, hoy);

        if (dias == 1) {
            return "Ayer";
        } else if (dias < 7) {
            return "Hace " + dias + " días";
        } else if (dias < 30) {
            long semanas = dias / 7;
            return "Hace " + semanas + (semanas == 1 ? " semana" : " semanas");
        } else if (dias < 365) {
            long meses = dias / 30;
            return "Hace " + meses + (meses == 1 ? " mes" : " meses");
        } else {
            long años = dias / 365;
            return "Hace " + años + (años == 1 ? " año" : " años");
        }
    }

    private void mostrarSinDatos() {
        lblTotalAsistencias.setText("0");
        lblPrimeraAsistencia.setText("-");
        lblUltimaAsistencia.setText("-");
        lblPromedioMensual.setText("-");
    }

    @FXML
    private void handleCerrar() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Clase interna para representar las filas de la tabla
    public static class AsistenciaRow {
        private final Integer numero;
        private final String fecha;
        private final String diaSemana;
        private final String tiempoTranscurrido;

        public AsistenciaRow(Integer numero, String fecha, String diaSemana, String tiempoTranscurrido) {
            this.numero = numero;
            this.fecha = fecha;
            this.diaSemana = diaSemana;
            this.tiempoTranscurrido = tiempoTranscurrido;
        }

        public Integer getNumero() { return numero; }
        public String getFecha() { return fecha; }
        public String getDiaSemana() { return diaSemana; }
        public String getTiempoTranscurrido() { return tiempoTranscurrido; }
    }
}