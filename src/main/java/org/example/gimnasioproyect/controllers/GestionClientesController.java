package org.example.gimnasioproyect.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.example.gimnasioproyect.Utilidades.ServiceFactory;
import org.example.gimnasioproyect.model.Barrios;
import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.model.MembresiaClientes;
import org.example.gimnasioproyect.services.BarrioService;
import org.example.gimnasioproyect.services.ClienteServices;
import org.example.gimnasioproyect.services.MembresiaClienteService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GestionClientesController {

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<Barrios> cmbBarrio;
    @FXML private ToggleButton btnTodos;
    @FXML private ToggleButton btnActivos;
    @FXML private ToggleButton btnInactivos;
    @FXML private ToggleGroup filtroEstado;

    @FXML private javafx.scene.text.Text lblTotalClientes;
    @FXML private javafx.scene.text.Text lblClientesActivos;
    @FXML private javafx.scene.text.Text lblClientesInactivos;
    @FXML private Label lblResultados;

    @FXML private TableView<Clientes> tableClientes;
    @FXML private TableColumn<Clientes, String> colDocumento;
    @FXML private TableColumn<Clientes, String> colNombre;
    @FXML private TableColumn<Clientes, String> colTelefono;
    @FXML private TableColumn<Clientes, String> colCorreo;
    @FXML private TableColumn<Clientes, String> colBarrio;
    @FXML private TableColumn<Clientes, String> colEstadoMembresia;
    @FXML private TableColumn<Clientes, Void> colAcciones;

    @FXML private Button btnNuevoCliente;
    @FXML private Button btnVerDetalle;
    @FXML private Button btnEditar;
    @FXML private Button btnAsignarMembresia;
    @FXML private Button btnEliminar;

    private ClienteServices clienteService;
    private BarrioService barrioService;
    private MembresiaClienteService membresiaClienteService;

    private ObservableList<Clientes> listaClientes;
    private ObservableList<Clientes> listaClientesFiltrada;

    public void initialize() {
        configurarTabla();
        configurarFiltros();
        configurarBotones();

        // Obtener servicios del ServiceFactory
        ServiceFactory factory = ServiceFactory.getInstance();
        this.clienteService = factory.getClienteService();
        this.barrioService = factory.getBarrioService();
        this.membresiaClienteService = factory.getMembresiaClienteService();

        // Cargar datos
        cargarDatos();
    }

    private void configurarTabla() {
        // Configurar columnas
        colDocumento.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDocumento()));

        colNombre.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombreCompleto()));

        colTelefono.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTelefono()));

        colCorreo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCorreo()));

        colBarrio.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getBarrio() != null ?
                                data.getValue().getBarrio().getNombreBarrio() : "N/A"
                ));

        // Columna de estado con indicador visual
        colEstadoMembresia.setCellValueFactory(data -> {
            try {
                Optional<MembresiaClientes> membresiaOpt =
                        membresiaClienteService.obtenerMembresiaActiva(data.getValue().getDocumento());

                if (membresiaOpt.isPresent() && membresiaOpt.get().estaActiva()) {
                    return new SimpleStringProperty("✓ Activa");
                } else if (membresiaOpt.isPresent() && membresiaOpt.get().estaVencida()) {
                    return new SimpleStringProperty("⚠ Vencida");
                } else {
                    return new SimpleStringProperty("✗ Sin Membresía");
                }
            } catch (SQLException e) {
                return new SimpleStringProperty("Error");
            }
        });

        // Estilo personalizado para la columna de estado
        colEstadoMembresia.setCellFactory(column -> new TableCell<Clientes, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    setText(item);

                    if (item.contains("Activa")) {
                        setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                    } else if (item.contains("Vencida")) {
                        setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #95a5a6; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Columna de acciones con botones
        colAcciones.setCellFactory(column -> new TableCell<Clientes, Void>() {
            private final Button btnVer = new Button("👁");
            private final Button btnEditar = new Button("✏");
            private final Button btnEliminar = new Button("🗑");
            private final HBox hbox = new HBox(5, btnVer, btnEditar, btnEliminar);

            {
                btnVer.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;");
                btnEditar.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;");
                btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;");

                btnVer.setOnAction(e -> {
                    Clientes cliente = getTableView().getItems().get(getIndex());
                    verDetalleCliente(cliente);
                });

                btnEditar.setOnAction(e -> {
                    Clientes cliente = getTableView().getItems().get(getIndex());
                    abrirFormulario(cliente);
                });

                btnEliminar.setOnAction(e -> {
                    Clientes cliente = getTableView().getItems().get(getIndex());
                    eliminarCliente(cliente);
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
        // Listener para búsqueda en tiempo real
        txtBuscar.textProperty().addListener((obs, old, nuevo) -> aplicarFiltros());

        // Listener para cambio de barrio
        cmbBarrio.setOnAction(e -> aplicarFiltros());

        // Listener para cambio de estado
        filtroEstado.selectedToggleProperty().addListener((obs, old, nuevo) -> aplicarFiltros());
    }

    private void configurarBotones() {
        // Habilitar/deshabilitar botones según selección
        tableClientes.getSelectionModel().selectedItemProperty().addListener((obs, old, nuevo) -> {
            boolean haySeleccion = nuevo != null;
            btnVerDetalle.setDisable(!haySeleccion);
            btnEditar.setDisable(!haySeleccion);
            btnAsignarMembresia.setDisable(!haySeleccion);
            btnEliminar.setDisable(!haySeleccion);
        });
    }

    private void cargarDatos() {
        try {
            // Cargar clientes
            List<Clientes> clientes = clienteService.obtenerTodosLosClientes();
            listaClientes = FXCollections.observableArrayList(clientes);
            listaClientesFiltrada = FXCollections.observableArrayList(clientes);
            tableClientes.setItems(listaClientesFiltrada);

            // Cargar barrios en el ComboBox
            List<Barrios> barrios = barrioService.obtenerTodosLosBarrios();
            cmbBarrio.getItems().clear();
            cmbBarrio.getItems().add(null); // Opción "Todos"
            cmbBarrio.getItems().addAll(barrios);

            // Configurar cómo se muestra cada barrio
            cmbBarrio.setButtonCell(new ListCell<Barrios>() {
                @Override
                protected void updateItem(Barrios item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "Todos los barrios" : item.getNombreBarrio());
                }
            });

            cmbBarrio.setCellFactory(param -> new ListCell<Barrios>() {
                @Override
                protected void updateItem(Barrios item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "Todos los barrios" : item.getNombreBarrio());
                }
            });

            // Actualizar estadísticas
            actualizarEstadisticas();

        } catch (SQLException e) {
            mostrarError("Error al cargar datos", e.getMessage());
            e.printStackTrace();
        }
    }

    private void aplicarFiltros() {
        if (listaClientes == null) return;

        String busqueda = txtBuscar.getText().toLowerCase().trim();
        Barrios barrioSeleccionado = cmbBarrio.getValue();
        Toggle estadoSeleccionado = filtroEstado.getSelectedToggle();

        List<Clientes> filtrados = listaClientes.stream()
                .filter(cliente -> {
                    // Filtro por búsqueda
                    if (!busqueda.isEmpty()) {
                        String nombre = cliente.getNombreCompleto().toLowerCase();
                        String documento = cliente.getDocumento().toLowerCase();
                        if (!nombre.contains(busqueda) && !documento.contains(busqueda)) {
                            return false;
                        }
                    }

                    // Filtro por barrio
                    if (barrioSeleccionado != null) {
                        if (cliente.getBarrio() == null ||
                                !cliente.getBarrio().getIdBarrio().equals(barrioSeleccionado.getIdBarrio())) {
                            return false;
                        }
                    }

                    // Filtro por estado de membresía
                    if (estadoSeleccionado != null) {
                        try {
                            boolean tieneMembresia = membresiaClienteService.tieneMembresiaActiva(cliente.getDocumento());

                            if (estadoSeleccionado == btnActivos && !tieneMembresia) {
                                return false;
                            } else if (estadoSeleccionado == btnInactivos && tieneMembresia) {
                                return false;
                            }
                        } catch (SQLException e) {
                            return true; // En caso de error, incluir el cliente
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());

        listaClientesFiltrada.setAll(filtrados);
        lblResultados.setText("Mostrando " + filtrados.size() + " clientes");
    }

    private void actualizarEstadisticas() {
        try {
            int total = listaClientes.size();

            // Contar clientes con membresía activa
            int activos = 0;
            for (Clientes cliente : listaClientes) {
                if (membresiaClienteService.tieneMembresiaActiva(cliente.getDocumento())) {
                    activos++;
                }
            }

            int inactivos = total - activos;

            lblTotalClientes.setText(String.valueOf(total));
            lblClientesActivos.setText(String.valueOf(activos));
            lblClientesInactivos.setText(String.valueOf(inactivos));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Handlers de botones
    @FXML
    private void handleNuevoCliente() {
        abrirFormulario(null);
    }

    @FXML
    private void handleVerDetalle() {
        Clientes seleccionado = tableClientes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            verDetalleCliente(seleccionado);
        }
    }

    @FXML
    private void handleEditar() {
        Clientes seleccionado = tableClientes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            abrirFormulario(seleccionado);
        }
    }

    private void abrirFormulario(Clientes cliente) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gimnasioproyect/FormularioCliente.fxml"));
            Parent formulario = loader.load();

            FormularioClienteController controller = loader.getController();

            // Si hay un cliente, cargarlo (modo edición)
            if (cliente != null) {
                controller.cargarCliente(cliente);
            }

            // Buscar el StackPane contentArea
            if (tableClientes.getScene() != null) {
                StackPane contentArea = encontrarContentArea(tableClientes.getScene().getRoot());

                if (contentArea != null) {
                    System.out.println("✅ ContentArea encontrado!");
                    controller.setParentContainer(contentArea);
                    contentArea.getChildren().clear();
                    contentArea.getChildren().add(formulario);
                } else {
                    System.err.println("❌ No se encontró el contentArea");
                    mostrarError("Error de navegación", "No se pudo encontrar el contenedor principal");
                }
            }

        } catch (IOException e) {
            mostrarError("Error", "No se pudo abrir el formulario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private StackPane encontrarContentArea(Parent root) {
        // Si el root es BorderPane, buscar en su center
        if (root instanceof BorderPane) {
            BorderPane borderPane = (BorderPane) root;
            if (borderPane.getCenter() instanceof StackPane) {
                return (StackPane) borderPane.getCenter();
            }
        }

        // Buscar recursivamente en la jerarquía de padres
        Parent current = root;
        while (current != null) {
            if (current instanceof BorderPane) {
                BorderPane bp = (BorderPane) current;
                if (bp.getCenter() instanceof StackPane) {
                    return (StackPane) bp.getCenter();
                }
            }

            // Subir en la jerarquía
            if (current.getParent() != null) {
                current = current.getParent();
            } else {
                break;
            }
        }

        return null;
    }

    @FXML
    private void handleAsignarMembresia() {
        Clientes seleccionado = tableClientes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            abrirDialogoMembresia(seleccionado);
        }
    }

    private void abrirDialogoMembresia(Clientes cliente) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/gimnasioproyect/AsignarMembresia.fxml")
            );
            Parent root = loader.load();

            AsignarMembresiaController controller = loader.getController();
            controller.setCliente(cliente);

            // Callback para recargar datos al éxito
            controller.setOnSuccess(success -> {
                if (success) {
                    cargarDatos(); // Recargar tabla
                }
            });

            // Crear ventana modal
            Stage stage = new Stage();
            stage.setTitle("Asignar Membresía");
            stage.setScene(new javafx.scene.Scene(root));
            stage.setResizable(false);
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.initOwner(tableClientes.getScene().getWindow());
            stage.showAndWait();

        } catch (IOException e) {
            mostrarError("Error", "No se pudo abrir el diálogo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEliminar() {
        Clientes seleccionado = tableClientes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            eliminarCliente(seleccionado);
        }
    }

    @FXML
    private void handleLimpiarFiltros() {
        txtBuscar.clear();
        cmbBarrio.setValue(null);
        btnTodos.setSelected(true);
        aplicarFiltros();
    }

    // Métodos auxiliares
    private void verDetalleCliente(Clientes cliente) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gimnasioproyect/DetalleCliente.fxml"));
            Parent detalleView = loader.load();

            DetalleClienteController controller = loader.getController();
            controller.cargarCliente(cliente);

            // Buscar el contentArea
            if (tableClientes.getScene() != null) {
                Parent root = tableClientes.getScene().getRoot();
                StackPane contentArea = (StackPane) root.lookup("#contentArea");

                if (contentArea != null) {
                    controller.setParentContainer(contentArea);
                    contentArea.getChildren().clear();
                    contentArea.getChildren().add(detalleView);
                } else {
                    mostrarError("Error", "No se pudo encontrar el área de contenido");
                }
            }

        } catch (IOException e) {
            mostrarError("Error", "No se pudo abrir el detalle: " + e.getMessage());
            e.printStackTrace();
        }
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gimnasioproyect/DetalleCliente.fxml"));
//            Parent detalle = loader.load();
//
//            DetalleClienteController controller = loader.getController();
//            controller.cargarCliente(cliente);
//
//            // Pasar referencia del contenedor padre
//            if (tableClientes.getScene() != null && tableClientes.getScene().getRoot() instanceof StackPane) {
//                StackPane parent = (StackPane) tableClientes.getScene().getRoot().getParent();
//                if (parent != null) {
//                    controller.setParentContainer(parent);
//                    parent.getChildren().clear();
//                    parent.getChildren().add(detalle);
//                }
//            }
//
//        } catch (IOException e) {
//            mostrarError("Error", "No se pudo abrir el detalle: " + e.getMessage());
//            e.printStackTrace();
//        }

    }

    private void eliminarCliente(Clientes cliente) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar cliente?");
        confirmacion.setContentText("¿Está seguro de eliminar a " + cliente.getNombreCompleto() + "?");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    clienteService.eliminarCliente(cliente.getDocumento());
                    cargarDatos(); // Recargar tabla
                    mostrarExito("Cliente eliminado correctamente");
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
