package org.example.gimnasioproyect.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.gimnasioproyect.HelloApplication;
import org.example.gimnasioproyect.Utilidades.ServiceFactory;
import org.example.gimnasioproyect.model.AsignacionEntrenadores;
import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.model.Entrenadores;
import org.example.gimnasioproyect.services.ClienteServices;
import org.example.gimnasioproyect.services.EntrenadorService;
import org.example.gimnasioproyect.services.EstadisticaService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class EntrenadoresController {

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbEspecialidad;
    @FXML private ToggleButton btnTodos;
    @FXML private ToggleButton btnConClientes;
    @FXML private ToggleButton btnSinClientes;
    @FXML private ToggleGroup filtroEstado;

    @FXML private Text lblTotalEntrenadores;
    @FXML private Text lblConClientes;
    @FXML private Text lblSinClientes;
    @FXML private Text lblTotalClientesAsignados;
    @FXML private Label lblResultados;

    @FXML private TableView<Entrenadores> tableEntrenadores;
    @FXML private TableColumn<Entrenadores, String> colDocumento;
    @FXML private TableColumn<Entrenadores, String> colNombre;
    @FXML private TableColumn<Entrenadores, String> colEspecialidad;
    @FXML private TableColumn<Entrenadores, String> colExperiencia;
    @FXML private TableColumn<Entrenadores, String> colTelefono;
    @FXML private TableColumn<Entrenadores, String> colCorreo;
    @FXML private TableColumn<Entrenadores, String> colClientesAsignados;
    @FXML private TableColumn<Entrenadores, Void> colAcciones;

    @FXML private Button btnNuevoEntrenador;
    @FXML private Button btnVerDetalle;
    @FXML private Button btnEditar;
    @FXML private Button btnAsignarCliente;
    @FXML private Button btnEliminar;

    private EntrenadorService entrenadorService;
    private EstadisticaService estadisticaService;

    private ObservableList<Entrenadores> listaEntrenadores;
    private ObservableList<Entrenadores> listaEntrenadoresFiltrada;

    public void initialize() {
        configurarTabla();
        configurarFiltros();
        configurarBotones();

        // Obtener servicios
        ServiceFactory factory = ServiceFactory.getInstance();
        this.entrenadorService = factory.getEntrenadorService();
        this.estadisticaService = factory.getEstadisticaService();

        // Cargar datos
        cargarDatos();
    }

    private void configurarTabla() {
        // Columna Documento
        colDocumento.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDocuEntrenador()));

        // Columna Nombre
        colNombre.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombreCompleto()));

        // Columna Especialidad
        colEspecialidad.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEspecialidad()));

        // Columna Experiencia
        colExperiencia.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getExperiencia() + " años"));

        // Columna Teléfono
        colTelefono.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTelefono()));

        // Columna Correo
        colCorreo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCorreo()));

        // Columna Clientes Asignados
        colClientesAsignados.setCellValueFactory(data -> {
            try {
                List<AsignacionEntrenadores> clientesActivos =
                        entrenadorService.obtenerClientesActivos(data.getValue().getDocuEntrenador());
                return new SimpleStringProperty(clientesActivos.size() + " clientes");
            } catch (SQLException e) {
                return new SimpleStringProperty("0 clientes");
            }
        });

        // Estilo para la columna de clientes
        colClientesAsignados.setCellFactory(column -> new TableCell<Entrenadores, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.startsWith("0")) {
                        setStyle("-fx-text-fill: #95a5a6;");
                    } else {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Columna Acciones
        colAcciones.setCellFactory(column -> new TableCell<Entrenadores, Void>() {
            private final Button btnVer = new Button("Ver");
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox hbox = new HBox(5, btnVer, btnEditar, btnEliminar);

            {
                btnVer.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;");
                btnEditar.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;");
                btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;");

                btnVer.setOnAction(e -> {
                    Entrenadores entrenador = getTableView().getItems().get(getIndex());
                    verDetalleEntrenador(entrenador);
                });

                btnEditar.setOnAction(e -> {
                    Entrenadores entrenador = getTableView().getItems().get(getIndex());
                    editarEntrenador(entrenador);
                });

                btnEliminar.setOnAction(e -> {
                    Entrenadores entrenador = getTableView().getItems().get(getIndex());
                    eliminarEntrenador(entrenador);
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
        // Listener para búsqueda
        txtBuscar.textProperty().addListener((obs, old, nuevo) -> aplicarFiltros());

        // Listener para especialidad
        cmbEspecialidad.setOnAction(e -> aplicarFiltros());

        // Listener para estado
        filtroEstado.selectedToggleProperty().addListener((obs, old, nuevo) -> aplicarFiltros());
    }

    private void configurarBotones() {
        tableEntrenadores.getSelectionModel().selectedItemProperty().addListener((obs, old, nuevo) -> {
            boolean haySeleccion = nuevo != null;
            btnVerDetalle.setDisable(!haySeleccion);
            btnEditar.setDisable(!haySeleccion);
            btnAsignarCliente.setDisable(!haySeleccion);
            btnEliminar.setDisable(!haySeleccion);
        });
    }

    private void cargarDatos() {
        try {
            // Cargar entrenadores
            List<Entrenadores> entrenadores = entrenadorService.obtenerTodosLosEntrenadores();
            listaEntrenadores = FXCollections.observableArrayList(entrenadores);
            listaEntrenadoresFiltrada = FXCollections.observableArrayList(entrenadores);
            tableEntrenadores.setItems(listaEntrenadoresFiltrada);

            // Cargar especialidades en el ComboBox
            cargarEspecialidades(entrenadores);

            lblResultados.setText("Mostrando " + entrenadores.size() + " clientes");

            // Actualizar estadísticas
            actualizarEstadisticas();

        } catch (SQLException e) {
            mostrarError("Error al cargar datos", e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarEspecialidades(List<Entrenadores> entrenadores) {
        Set<String> especialidades = entrenadores.stream()
                .map(Entrenadores::getEspecialidad)
                .collect(Collectors.toSet());

        cmbEspecialidad.getItems().clear();
        cmbEspecialidad.getItems().add(null); // Opción "Todas"
        cmbEspecialidad.getItems().addAll(especialidades);

        // Configurar cómo se muestra
        cmbEspecialidad.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Todas las especialidades" : item);
            }
        });

        cmbEspecialidad.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Todas las especialidades" : item);
            }
        });
    }

    private void aplicarFiltros() {
        if (listaEntrenadores == null) return;

        String busqueda = txtBuscar.getText().toLowerCase().trim();
        String especialidadSeleccionada = cmbEspecialidad.getValue();
        Toggle estadoSeleccionado = filtroEstado.getSelectedToggle();

        List<Entrenadores> filtrados = listaEntrenadores.stream()
                .filter(entrenador -> {
                    // Filtro por búsqueda
                    if (!busqueda.isEmpty()) {
                        String nombre = entrenador.getNombreCompleto().toLowerCase();
                        String documento = entrenador.getDocuEntrenador().toLowerCase();
                        if (!nombre.contains(busqueda) && !documento.contains(busqueda)) {
                            return false;
                        }
                    }

                    // Filtro por especialidad
                    if (especialidadSeleccionada != null) {
                        if (!entrenador.getEspecialidad().equals(especialidadSeleccionada)) {
                            return false;
                        }
                    }

                    // Filtro por estado (con/sin clientes)
                    if (estadoSeleccionado != null) {
                        try {
                            int clientesActivos = entrenadorService.obtenerClientesActivos(
                                    entrenador.getDocuEntrenador()).size();

                            if (estadoSeleccionado == btnConClientes && clientesActivos == 0) {
                                return false;
                            } else if (estadoSeleccionado == btnSinClientes && clientesActivos > 0) {
                                return false;
                            }
                        } catch (SQLException e) {
                            return true;
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());

        listaEntrenadoresFiltrada.setAll(filtrados);
        lblResultados.setText("Mostrando " + filtrados.size() + " entrenadores");
    }

    private void actualizarEstadisticas() {
        try {
            int total = listaEntrenadores.size();
            lblTotalEntrenadores.setText(String.valueOf(total));

            // Contar entrenadores con/sin clientes
            int conClientes = 0;
            int totalClientesAsignados = 0;

            for (Entrenadores entrenador : listaEntrenadores) {
                int clientesActivos = entrenadorService.obtenerClientesActivos(
                        entrenador.getDocuEntrenador()).size();

                if (clientesActivos > 0) {
                    conClientes++;
                }
                totalClientesAsignados += clientesActivos;
            }

            lblConClientes.setText(String.valueOf(conClientes));
            lblSinClientes.setText(String.valueOf(total - conClientes));
            lblTotalClientesAsignados.setText(String.valueOf(totalClientesAsignados));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Handlers
    @FXML
    private void handleNuevoEntrenador() {
        abrirFormulario(null);
    }

    @FXML
    private void handleVerDetalle() {
        Entrenadores seleccionado = tableEntrenadores.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            verDetalleEntrenador(seleccionado);
        }
    }

    @FXML
    private void handleEditar() {
        Entrenadores seleccionado = tableEntrenadores.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            abrirFormulario(seleccionado);
        }
    }

    @FXML
    private void handleAsignarCliente() {
        Entrenadores seleccionado = tableEntrenadores.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            abrirDialogoAsignarCliente(seleccionado);
        }
    }

    private void abrirDialogoAsignarCliente(Entrenadores entrenador) {
        // Crear diálogo personalizado
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Asignar Cliente");
        dialog.setHeaderText("Asignar cliente a: " + entrenador.getNombreCompleto() +
                "\nEspecialidad: " + entrenador.getEspecialidad());

        // Botones
        ButtonType asignarButtonType = new ButtonType("Asignar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(asignarButtonType, ButtonType.CANCEL);

        // Crear contenido
        VBox content = new VBox(15);
        content.setPrefWidth(500);
        content.setPrefHeight(400);

        // Campo de búsqueda
        HBox searchBox = new HBox(10);
        TextField txtBuscarCliente = new TextField();
        txtBuscarCliente.setPromptText("Buscar cliente por nombre o documento...");
        txtBuscarCliente.setPrefWidth(400);
        Button btnBuscar = new Button("🔍");
        searchBox.getChildren().addAll(txtBuscarCliente, btnBuscar);

        // Tabla de clientes disponibles
        TableView<Clientes> tableClientes = new TableView<>();
        tableClientes.setPrefHeight(300);

        TableColumn<Clientes, String> colDoc = new TableColumn<>("Documento");
        colDoc.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getDocumento()));
        colDoc.setPrefWidth(120);

        TableColumn<Clientes, String> colNom = new TableColumn<>("Nombre");
        colNom.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getNombreCompleto()));
        colNom.setPrefWidth(200);

        TableColumn<Clientes, String> colTel = new TableColumn<>("Teléfono");
        colTel.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getTelefono()));
        colTel.setPrefWidth(120);

        tableClientes.getColumns().addAll(colDoc, colNom, colTel);

        // Label de información
        Label lblInfo = new Label("Seleccione un cliente de la lista");
        lblInfo.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");

        content.getChildren().addAll(
                new Label("Buscar cliente:"),
                searchBox,
                new Label("Clientes disponibles (sin entrenador):"),
                tableClientes,
                lblInfo
        );

        dialog.getDialogPane().setContent(content);

        // Cargar clientes sin entrenador
        try {
            ClienteServices clienteService = ServiceFactory.getInstance().getClienteService();
            List<Clientes> todosClientes = clienteService.obtenerTodosLosClientes();

            // Filtrar clientes sin entrenador activo
            List<Clientes> clientesDisponibles = new ArrayList<>();
            for (Clientes cliente : todosClientes) {
                try {
                    Optional<AsignacionEntrenadores> asignacion =
                            entrenadorService.obtenerEntrenadorDeCliente(cliente.getDocumento());

                    // Si no tiene asignación activa, está disponible
                    if (!asignacion.isPresent()) {
                        clientesDisponibles.add(cliente);
                    }
                } catch (SQLException e) {
                    // Si hay error, incluirlo por si acaso
                    clientesDisponibles.add(cliente);
                }
            }

            ObservableList<Clientes> listaClientes =
                    FXCollections.observableArrayList(clientesDisponibles);
            tableClientes.setItems(listaClientes);

            lblInfo.setText("Clientes disponibles: " + clientesDisponibles.size());

            // Funcionalidad de búsqueda
            txtBuscarCliente.textProperty().addListener((obs, old, nuevo) -> {
                if (nuevo == null || nuevo.isEmpty()) {
                    tableClientes.setItems(listaClientes);
                } else {
                    String busqueda = nuevo.toLowerCase();
                    List<Clientes> filtrados = clientesDisponibles.stream()
                            .filter(c -> c.getNombreCompleto().toLowerCase().contains(busqueda) ||
                                    c.getDocumento().toLowerCase().contains(busqueda))
                            .collect(Collectors.toList());
                    tableClientes.setItems(FXCollections.observableArrayList(filtrados));
                }
            });

            btnBuscar.setOnAction(e -> {
                String busqueda = txtBuscarCliente.getText();
                if (busqueda != null && !busqueda.isEmpty()) {
                    txtBuscarCliente.clear();
                }
            });

        } catch (SQLException e) {
            mostrarError("Error", "No se pudieron cargar los clientes: " + e.getMessage());
            return;
        }

        // Deshabilitar botón Asignar si no hay selección
        Node asignarButton = dialog.getDialogPane().lookupButton(asignarButtonType);
        asignarButton.setDisable(true);

        tableClientes.getSelectionModel().selectedItemProperty().addListener((obs, old, nuevo) -> {
            asignarButton.setDisable(nuevo == null);
        });

        // Convertir resultado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == asignarButtonType) {
                Clientes clienteSeleccionado = tableClientes.getSelectionModel().getSelectedItem();
                if (clienteSeleccionado != null) {
                    return clienteSeleccionado.getDocumento();
                }
            }
            return null;
        });

        // Mostrar diálogo y procesar resultado
        dialog.showAndWait().ifPresent(documentoCliente -> {
            try {
                entrenadorService.asignarEntrenadorACliente(
                        entrenador.getDocuEntrenador(),
                        documentoCliente
                );

                mostrarExito("Cliente asignado correctamente al entrenador");
                cargarDatos(); // Recargar para actualizar contadores

            } catch (SQLException e) {
                mostrarError("Error al asignar", e.getMessage());
            } catch (IllegalArgumentException e) {
                mostrarError("Error de validación", e.getMessage());
            }
        });
    }

    @FXML
    private void handleEliminar() {
        Entrenadores seleccionado = tableEntrenadores.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            eliminarEntrenador(seleccionado);
        }
    }

    @FXML
    private void handleLimpiarFiltros() {
        txtBuscar.clear();
        cmbEspecialidad.setValue(null);
        btnTodos.setSelected(true);
        aplicarFiltros();
    }

    // Métodos auxiliares
    private void abrirFormulario(Entrenadores entrenador) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/org/example/gimnasioproyect/FormularioEntrenadores.fxml"));
            Parent formulario = loader.load();

            FormularioEntrenadoresController controller = loader.getController();

            if (entrenador != null) {
                controller.cargarEntrenador(entrenador);
            }

            // Buscar el contentArea
            if (tableEntrenadores.getScene() != null) {
                Parent root = tableEntrenadores.getScene().getRoot();
                StackPane contentArea = (StackPane) root.lookup("#contentArea");

                if (contentArea != null) {
                    StackPane loadingPane = null;
                    for (javafx.scene.Node node : contentArea.getChildren()) {
                        if (node.getId() != null && node.getId().equals("loadingPane")) {
                            loadingPane = (StackPane) node;
                            break;
                        }
                    }
                    controller.setParentContainer(contentArea);
                    contentArea.getChildren().clear();
                    contentArea.getChildren().add(formulario);
                    if (loadingPane != null) {
                        contentArea.getChildren().add(loadingPane);
                        loadingPane.toFront();
                    }
                } else {
                    System.err.println("No se encontró el contentArea");
                    mostrarError("Error de navegación", "No se pudo encontrar el contenedor principal");
                }
            }

        } catch (IOException e) {
            mostrarError("Error", "No se pudo abrir el formulario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void verDetalleEntrenador(Entrenadores entrenador) {
        // TODO: Implementar vista de detalle
        mostrarInfo("Detalle del Entrenador",
                "Vista de detalle en desarrollo.\n\n" +
                        "Entrenador: " + entrenador.getNombreCompleto() + "\n" +
                        "Especialidad: " + entrenador.getEspecialidad());
    }

    private void editarEntrenador(Entrenadores entrenador) {
        abrirFormulario(entrenador);
    }

    private void eliminarEntrenador(Entrenadores entrenador) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar entrenador?");
        confirmacion.setContentText("¿Está seguro de eliminar a " +
                entrenador.getNombreCompleto() + "?");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    entrenadorService.eliminarEntrenador(entrenador.getDocuEntrenador());
                    cargarDatos();
                    mostrarExito("Entrenador eliminado correctamente");
                } catch (SQLException e) {
                    mostrarError("Error al eliminar", e.getMessage());
                } catch (IllegalArgumentException e) {
                    mostrarError("No se puede eliminar", e.getMessage());
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

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}