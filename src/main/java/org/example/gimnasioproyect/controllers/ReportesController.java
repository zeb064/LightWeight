package org.example.gimnasioproyect.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.example.gimnasioproyect.Utilidades.*;
import org.example.gimnasioproyect.services.EstadisticaService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public class ReportesController {

    // Header
    @FXML private Button btnExportar;
    @FXML private Button btnActualizar;

    // Filtros
    @FXML private ComboBox<String> cmbPeriodo;
    @FXML private DatePicker dpFechaDesde;
    @FXML private DatePicker dpFechaHasta;

    // Tab Resumen General
    @FXML private Text lblResumenTotalClientes;
    @FXML private Text lblResumenActivos;
    @FXML private Text lblResumenInactivos;
    @FXML private Text lblResumenIngresos;
    @FXML private Label lblTasaRenovacion;
    @FXML private Label lblNuevosMes;
    @FXML private Label lblPromedioAsistencias;
    @FXML private Label lblAlertaVencidas;
    @FXML private Label lblAlertaProximas;
    @FXML private Label lblClientesConEntrenador;

    // Tab Asistencias
    @FXML private Text lblAsistenciasHoy;
    @FXML private Text lblAsistenciasMes;
    @FXML private Text lblPromedioAsistenciasDia;
    @FXML private TableView<TopClienteData> tableTopClientes;
    @FXML private TableColumn<TopClienteData, String> colPosicion;
    @FXML private TableColumn<TopClienteData, String> colDocumentoTop;
    @FXML private TableColumn<TopClienteData, String> colNombreTop;
    @FXML private TableColumn<TopClienteData, String> colAsistenciasTop;
    @FXML private VBox boxAsistenciasSemana;

    // Tab Membresías
    @FXML private Text lblMembresiasActivas;
    @FXML private Text lblMembresiasVencidas;
    @FXML private Text lblMembresiasProximas;
    @FXML private TableView<MembresiaData> tableMembresias;
    @FXML private TableColumn<MembresiaData, String> colTipoMembresia;
    @FXML private TableColumn<MembresiaData, String> colCantidad;
    @FXML private TableColumn<MembresiaData, String> colIngresosMembresia;
    @FXML private TableColumn<MembresiaData, String> colPorcentaje;
    @FXML private VBox boxIngresos;
    @FXML private Text lblIngresoTotal;

    // Tab Entrenadores
    @FXML private Text lblTotalConEntrenador;
    @FXML private TableView<EntrenadorData> tableEntrenadores;
    @FXML private TableColumn<EntrenadorData, String> colEntrenador;
    @FXML private TableColumn<EntrenadorData, String> colClientesAsignados;

    // Tab Barrios
    @FXML private TableView<BarrioData> tableBarrios;
    @FXML private TableColumn<BarrioData, String> colBarrio;
    @FXML private TableColumn<BarrioData, String> colClientesBarrio;
    @FXML private TableColumn<BarrioData, String> colPorcentajeBarrio;

    // Servicio
    private EstadisticaService estadisticaService;


    public void initialize() {
        // Obtener servicios
        ServiceFactory factory = ServiceFactory.getInstance();
        this.estadisticaService = factory.getEstadisticaService();

        // Configurar ComboBox de períodos
        configurarComboBoxPeriodo();

        // Configurar tablas
        configurarTablas();

        // Cargar datos
        cargarDatos();
    }

    private void configurarComboBoxPeriodo() {
        cmbPeriodo.getItems().addAll(
                "Hoy",
                "Esta Semana",
                "Este Mes",
                "Último Trimestre",
                "Este Año",
                "Personalizado"
        );

        cmbPeriodo.setValue("Este Mes");

        // Listener para cambio de período
        cmbPeriodo.setOnAction(e -> {
            String periodo = cmbPeriodo.getValue();
            if (!"Personalizado".equals(periodo)) {
                configurarFechasPorPeriodo(periodo);
                cargarDatos();
            }
        });

        // Configurar fechas iniciales (Este Mes)
        configurarFechasPorPeriodo("Este Mes");
    }

    private void configurarFechasPorPeriodo(String periodo) {
        LocalDate hoy = LocalDate.now();

        switch (periodo) {
            case "Hoy":
                dpFechaDesde.setValue(hoy);
                dpFechaHasta.setValue(hoy);
                break;
            case "Esta Semana":
                dpFechaDesde.setValue(hoy.minusDays(hoy.getDayOfWeek().getValue() - 1));
                dpFechaHasta.setValue(hoy);
                break;
            case "Este Mes":
                dpFechaDesde.setValue(hoy.withDayOfMonth(1));
                dpFechaHasta.setValue(hoy);
                break;
            case "Último Trimestre":
                dpFechaDesde.setValue(hoy.minusMonths(3));
                dpFechaHasta.setValue(hoy);
                break;
            case "Este Año":
                dpFechaDesde.setValue(hoy.withDayOfYear(1));
                dpFechaHasta.setValue(hoy);
                break;
        }
    }

    private void configurarTablas() {
        // Tabla Top Clientes
        colPosicion.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPosicion()));
        colDocumentoTop.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDocumento()));
        colNombreTop.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombre()));
        colAsistenciasTop.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getAsistencias())));

        // Tabla Membresías
        colTipoMembresia.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTipo()));
        colCantidad.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getCantidad())));
        colIngresosMembresia.setCellValueFactory(data ->
                new SimpleStringProperty("$" + String.format("%,.0f", data.getValue().getIngresos())));
        colPorcentaje.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%.1f%%", data.getValue().getPorcentaje())));

        // Tabla Entrenadores
        colEntrenador.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombre()));
        colClientesAsignados.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getClientes())));

        // Tabla Barrios
        colBarrio.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombre()));
        colClientesBarrio.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getClientes())));
        colPorcentajeBarrio.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%.1f%%", data.getValue().getPorcentaje())));
    }

    private void cargarDatos() {
        try {
            cargarResumenGeneral();
            cargarAsistencias();
            cargarMembresias();
            cargarEntrenadores();
            cargarBarrios();

        } catch (SQLException e) {
            mostrarError("Error al cargar datos", e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarResumenGeneral() throws SQLException {
        Map<String, Object> resumen = estadisticaService.obtenerResumenGeneral();

        // Clientes
        lblResumenTotalClientes.setText(String.valueOf(resumen.get("totalClientes")));
        lblResumenActivos.setText(String.valueOf(resumen.get("clientesActivos")));
        lblResumenInactivos.setText(String.valueOf(resumen.get("clientesInactivos")));

        // Ingresos
        double ingresos = (double) resumen.get("ingresosTotales");
        lblResumenIngresos.setText("$" + String.format("%,.0f", ingresos));

        // Tendencias
        double tasaRenovacion = (double) resumen.get("tasaRenovacion");
        lblTasaRenovacion.setText(String.format("Tasa de renovación: %.1f%%", tasaRenovacion));

        lblNuevosMes.setText("Nuevos este mes: " + resumen.get("nuevosClientesMes"));

        int asistenciasMes = (int) resumen.get("asistenciasMes");
        LocalDate hoy = LocalDate.now();
        int diasDelMes = YearMonth.from(hoy).lengthOfMonth();
        double promedio = (double) asistenciasMes / diasDelMes;
        lblPromedioAsistencias.setText(String.format("Promedio asistencias/día: %.1f", promedio));

        // Alertas
        lblAlertaVencidas.setText("Membresías vencidas: " + resumen.get("membresiasVencidas"));
        lblAlertaProximas.setText("Próximas a vencer: " + resumen.get("membresiasProximasVencer"));
        lblClientesConEntrenador.setText("Con entrenador: " + resumen.get("clientesConEntrenador"));
    }

    private void cargarAsistencias() throws SQLException {
        // Estadísticas básicas
        int asistenciasHoy = estadisticaService.obtenerAsistenciasHoy();
        lblAsistenciasHoy.setText(String.valueOf(asistenciasHoy));

        int asistenciasMes = estadisticaService.obtenerAsistenciasMesActual();
        lblAsistenciasMes.setText(String.valueOf(asistenciasMes));

        // Promedio por día
        LocalDate hoy = LocalDate.now();
        double promedio = estadisticaService.obtenerPromedioAsistenciasPorDia(
                hoy.getMonthValue(), hoy.getYear()
        );
        lblPromedioAsistenciasDia.setText(String.format("%.1f", promedio));

        // Top Clientes
        cargarTopClientes();

        // Asistencias por día de la semana
        cargarAsistenciasPorDiaSemana();
    }

    private void cargarTopClientes() throws SQLException {
        // ✅ SOLUCIÓN: Usar el nuevo método que ya incluye los nombres
        List<TopClienteData> topClientes = estadisticaService.obtenerTopClientesConNombres(10);

        ObservableList<TopClienteData> datos = FXCollections.observableArrayList(topClientes);
        tableTopClientes.setItems(datos);
    }

    private void cargarAsistenciasPorDiaSemana() throws SQLException {
        Map<String, Integer> asistenciasSemana = estadisticaService.obtenerAsistenciasPorDiaSemana();

        boxAsistenciasSemana.getChildren().clear();

        String[] dias = {"LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO"};

        for (String dia : dias) {
            int cantidad = asistenciasSemana.getOrDefault(dia, 0);
            HBox item = crearBarraDia(dia, cantidad);
            boxAsistenciasSemana.getChildren().add(item);
        }
    }

    private HBox crearBarraDia(String dia, int cantidad) {
        HBox hbox = new HBox(15);
        hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Nombre del día
        Text txtDia = new Text(dia);
        txtDia.setStyle("-fx-fill: #ecf0f1;");
        txtDia.setFont(Font.font("System Bold", 13));
        txtDia.setWrappingWidth(100);

        // Barra de progreso visual
        double maxAsistencias = 100.0; // Ajusta según tus datos
        double porcentaje = (cantidad / maxAsistencias) * 100;

        ProgressBar progressBar = new ProgressBar(porcentaje / 100);
        progressBar.setPrefWidth(300);
        progressBar.setStyle("-fx-accent: #3498db;");

        // Cantidad
        Text txtCantidad = new Text(String.valueOf(cantidad));
        txtCantidad.setStyle("-fx-fill: #3498db;");
        txtCantidad.setFont(Font.font("System Bold", 16));

        hbox.getChildren().addAll(txtDia, progressBar, txtCantidad);
        return hbox;
    }

    private void cargarMembresias() throws SQLException {
        // Estadísticas básicas
        int activas = estadisticaService.obtenerTotalMembresiasActivas();
        lblMembresiasActivas.setText(String.valueOf(activas));

        int vencidas = estadisticaService.obtenerTotalMembresiasVencidas();
        lblMembresiasVencidas.setText(String.valueOf(vencidas));

        int proximas = estadisticaService.obtenerMembresiasProximasAVencer();
        lblMembresiasProximas.setText(String.valueOf(proximas));

        // Tabla de distribución
        cargarDistribucionMembresias();

        // Ingresos por tipo
        cargarIngresosPorTipo();
    }

    private void cargarDistribucionMembresias() throws SQLException {
        // ✅ SOLUCIÓN: Usar el nuevo método que calcula correctamente los porcentajes sobre ingresos
        List<MembresiaData> distribucion = estadisticaService.obtenerDistribucionMembresiasConPorcentajes();

        ObservableList<MembresiaData> datos = FXCollections.observableArrayList(distribucion);
        tableMembresias.setItems(datos);
    }

    private void cargarIngresosPorTipo() throws SQLException {
        Map<String, Double> ingresos = estadisticaService.obtenerIngresosPorTipoMembresia();

        boxIngresos.getChildren().clear();

        double total = 0;

        for (Map.Entry<String, Double> entry : ingresos.entrySet()) {
            HBox item = crearItemIngreso(entry.getKey(), entry.getValue());
            boxIngresos.getChildren().add(item);
            total += entry.getValue();
        }

        lblIngresoTotal.setText("$" + String.format("%,.0f", total));
    }

    private HBox crearItemIngreso(String tipo, double monto) {
        HBox hbox = new HBox(10);
        hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Tipo
        Text txtTipo = new Text(tipo);
        txtTipo.setStyle("-fx-fill: #ecf0f1;");
        txtTipo.setFont(Font.font("System Bold", 14));
        HBox.setHgrow(txtTipo, javafx.scene.layout.Priority.ALWAYS);

        // Monto
        Text txtMonto = new Text("$" + String.format("%,.0f", monto));
        txtMonto.setStyle("-fx-fill: #27ae60;");
        txtMonto.setFont(Font.font("System Bold", 18));

        hbox.getChildren().addAll(txtTipo, txtMonto);
        return hbox;
    }

    private void cargarEntrenadores() throws SQLException {
        int totalConEntrenador = estadisticaService.obtenerTotalClientesConEntrenador();
        lblTotalConEntrenador.setText(String.valueOf(totalConEntrenador));

        // Distribución por entrenador
        Map<String, Integer> distribucion = estadisticaService.obtenerDistribucionClientesPorEntrenador();

        ObservableList<EntrenadorData> datos = FXCollections.observableArrayList();

        for (Map.Entry<String, Integer> entry : distribucion.entrySet()) {
            datos.add(new EntrenadorData(entry.getKey(), entry.getValue()));
        }

        tableEntrenadores.setItems(datos);
    }

    private void cargarBarrios() throws SQLException {
        Map<String, Integer> distribucion = estadisticaService.obtenerClientesPorBarrio();

        ObservableList<BarrioData> datos = FXCollections.observableArrayList();

        int total = distribucion.values().stream().mapToInt(Integer::intValue).sum();

        for (Map.Entry<String, Integer> entry : distribucion.entrySet()) {
            int cantidad = entry.getValue();
            double porcentaje = (total > 0) ? ((double) cantidad / total) * 100 : 0;
            datos.add(new BarrioData(entry.getKey(), cantidad, porcentaje));
        }

        tableBarrios.setItems(datos);
    }

    @FXML
    private void handleFiltrar() {
        cargarDatos();
    }

    @FXML
    private void handleLimpiarFiltros() {
        cmbPeriodo.setValue("Este Mes");
        configurarFechasPorPeriodo("Este Mes");
        cargarDatos();
    }

    @FXML
    private void handleActualizar() {
        cargarDatos();
        mostrarExito("Datos actualizados correctamente");
    }

    @FXML
    private void handleExportar() {
        // TODO: Implementar exportación a PDF/Excel
        mostrarInfo("Exportar", "Funcionalidad de exportación en desarrollo");
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