package org.example.gimnasioproyect.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.gimnasioproyect.Utilidades.ServiceFactory;
import org.example.gimnasioproyect.Utilidades.TipoPersonal;
import org.example.gimnasioproyect.model.Administradores;
import org.example.gimnasioproyect.model.Entrenadores;
import org.example.gimnasioproyect.model.Personal;
import org.example.gimnasioproyect.model.Recepcionistas;
import org.example.gimnasioproyect.services.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class PersonalController {

    // Búsqueda y filtros
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbTipoPersonal;

    // Estadísticas
    @FXML private Text lblTotalPersonal;
    @FXML private Text lblAdministradores;
    @FXML private Text lblEntrenadores;
    @FXML private Text lblRecepcionistas;
    @FXML private Label lblResultados;

    // Tabla
    @FXML private TableView<Personal> tablePersonal;
    @FXML private TableColumn<Personal, String> colTipo;
    @FXML private TableColumn<Personal, String> colDocumento;
    @FXML private TableColumn<Personal, String> colNombre;
    @FXML private TableColumn<Personal, String> colUsuario;
    @FXML private TableColumn<Personal, String> colTelefono;
    @FXML private TableColumn<Personal, String> colCorreo;
    @FXML private TableColumn<Personal, String> colEspecifico;
    @FXML private TableColumn<Personal, Void> colAcciones;

    // Botones
    @FXML private Button btnNuevoPersonal;
    @FXML private Button btnVerDetalle;
    @FXML private Button btnEditar;
    @FXML private Button btnCambiarContrasena;
    @FXML private Button btnEliminar;

    // Servicios
    private PersonalService personalService;
    private AdministradorService administradorService;
    private EntrenadorService entrenadorService;
    private RecepcionistaService recepcionistaService;

    // Datos
    private ObservableList<Personal> listaPersonal;
    private ObservableList<Personal> listaPersonalFiltrada;

    public void initialize() {
        // Obtener servicios
        ServiceFactory factory = ServiceFactory.getInstance();
        this.personalService = factory.getPersonalService();
        this.administradorService = factory.getAdministradorService();
        this.entrenadorService = factory.getEntrenadorService();
        this.recepcionistaService = factory.getRecepcionistaService();

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
        // Tipo
        colTipo.setCellValueFactory(data -> {
            TipoPersonal tipo = data.getValue().getTipoPersonal();
            return new SimpleStringProperty(tipo.toString());
        });

        // Estilo para la columna Tipo
        colTipo.setCellFactory(column -> new TableCell<Personal, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);

                    switch (item) {
                        case "ADMINISTRADOR":
                            setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                            break;
                        case "ENTRENADOR":
                            setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                            break;
                        case "RECEPCIONISTA":
                            setStyle("-fx-text-fill: #9b59b6; -fx-font-weight: bold;");
                            break;
                    }
                }
            }
        });

        // Documento
        colDocumento.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDocumento()));

        // Nombre
        colNombre.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombreCompleto()));

        // Usuario
        colUsuario.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getUsuarioSistema()));

        // Teléfono
        colTelefono.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTelefono()));

        // Correo
        colCorreo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCorreo()));

        // Detalle Específico (según tipo)
        colEspecifico.setCellValueFactory(data -> {
            Personal personal = data.getValue();

            if (personal instanceof Administradores) {
                return new SimpleStringProperty("Cargo: " + ((Administradores) personal).getCargo());
            } else if (personal instanceof Entrenadores) {
                return new SimpleStringProperty("Especialidad: " + ((Entrenadores) personal).getEspecialidad());
            } else if (personal instanceof Recepcionistas) {
                return new SimpleStringProperty("Turno: " + ((Recepcionistas) personal).getHorarioTurno());
            }

            return new SimpleStringProperty("-");
        });

        // Acciones
        colAcciones.setCellFactory(column -> new TableCell<Personal, Void>() {
            private final Button btnVer = new Button("👁");
            private final Button btnEditar = new Button("✏");
            private final Button btnEliminar = new Button("🗑");
            private final HBox hbox = new HBox(5, btnVer, btnEditar, btnEliminar);

            {
                btnVer.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;");
                btnEditar.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;");
                btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;");

                btnVer.setOnAction(e -> {
                    Personal personal = getTableView().getItems().get(getIndex());
                    verDetallePersonal(personal);
                });

                btnEditar.setOnAction(e -> {
                    Personal personal = getTableView().getItems().get(getIndex());
                    editarPersonal(personal);
                });

                btnEliminar.setOnAction(e -> {
                    Personal personal = getTableView().getItems().get(getIndex());
                    eliminarPersonal(personal);
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
        // ComboBox tipo personal
        cmbTipoPersonal.getItems().addAll("Todos", "ADMINISTRADOR", "ENTRENADOR", "RECEPCIONISTA");
        cmbTipoPersonal.setValue("Todos");

        // Listeners
        txtBuscar.textProperty().addListener((obs, old, nuevo) -> aplicarFiltros());
        cmbTipoPersonal.setOnAction(e -> aplicarFiltros());
    }

    private void configurarBotones() {
        tablePersonal.getSelectionModel().selectedItemProperty().addListener((obs, old, nuevo) -> {
            boolean haySeleccion = nuevo != null;
            btnVerDetalle.setDisable(!haySeleccion);
            btnEditar.setDisable(!haySeleccion);
            btnCambiarContrasena.setDisable(!haySeleccion);
            btnEliminar.setDisable(!haySeleccion);
        });
    }

    private void cargarDatos() {
        try {
            // Cargar todo el personal
            List<Personal> personal = personalService.obtenerTodoElPersonal();
            listaPersonal = FXCollections.observableArrayList(personal);
            listaPersonalFiltrada = FXCollections.observableArrayList(personal);
            tablePersonal.setItems(listaPersonalFiltrada);

            lblResultados.setText("Mostrando " + personal.size() + " clientes");

            // Actualizar estadísticas
            actualizarEstadisticas();

        } catch (SQLException e) {
            mostrarError("Error al cargar datos", e.getMessage());
            e.printStackTrace();
        }
    }

    private void actualizarEstadisticas() {
        try {
            int total = listaPersonal.size();
            lblTotalPersonal.setText(String.valueOf(total));

            long admins = personalService.contarPersonalPorTipo("ADMINISTRADOR");
            lblAdministradores.setText(String.valueOf(admins));

            long entrenadores = personalService.contarPersonalPorTipo("ENTRENADOR");
            lblEntrenadores.setText(String.valueOf(entrenadores));

            long recepcionistas = personalService.contarPersonalPorTipo("RECEPCIONISTA");
            lblRecepcionistas.setText(String.valueOf(recepcionistas));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void aplicarFiltros() {
        if (listaPersonal == null) return;

        String busqueda = txtBuscar.getText().toLowerCase().trim();
        String tipoSeleccionado = cmbTipoPersonal.getValue();

        List<Personal> filtrados = listaPersonal.stream()
                .filter(personal -> {
                    // Filtro por búsqueda
                    if (!busqueda.isEmpty()) {
                        String nombre = personal.getNombreCompleto().toLowerCase();
                        String documento = personal.getDocumento().toLowerCase();
                        String usuario = personal.getUsuarioSistema().toLowerCase();

                        if (!nombre.contains(busqueda) &&
                                !documento.contains(busqueda) &&
                                !usuario.contains(busqueda)) {
                            return false;
                        }
                    }

                    // Filtro por tipo
                    if (!"Todos".equals(tipoSeleccionado)) {
                        if (!personal.getTipoPersonal().toString().equals(tipoSeleccionado)) {
                            return false;
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());

        listaPersonalFiltrada.setAll(filtrados);
        lblResultados.setText("Mostrando " + filtrados.size() + " registros");
    }

    @FXML
    private void handleNuevoPersonal() {
        abrirFormulario(null);
    }

    private void abrirFormulario(Personal personal) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/org/example/gimnasioproyect/FormularioPersonal.fxml"));
            Parent formulario = loader.load();

            FormularioPersonalController controller = loader.getController();

            if (personal != null) {
                controller.cargarPersonal(personal);
            }

            // Buscar el contentArea
            if (tablePersonal.getScene() != null) {
                Parent root = tablePersonal.getScene().getRoot();
                StackPane contentArea = (StackPane) root.lookup("#contentArea");

                if (contentArea != null) {
                    StackPane loadingPane = null;
                    for (javafx.scene.Node node : contentArea.getChildren()) {
                        if (node.getId() != null && node.getId().equals("loadingPane")) {
                            loadingPane = (StackPane) node;
                            break;
                        }
                    }
                    System.out.println("✅ ContentArea encontrado!");
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

    @FXML
    private void handleVerDetalle() {
        Personal seleccionado = tablePersonal.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            verDetallePersonal(seleccionado);
        }
    }

    private void verDetallePersonal(Personal personal) {
        StringBuilder detalle = new StringBuilder();
        detalle.append("Información del Personal\n\n");
        detalle.append("Tipo: ").append(personal.getTipoPersonal()).append("\n");
        detalle.append("Documento: ").append(personal.getDocumento()).append("\n");
        detalle.append("Nombre: ").append(personal.getNombreCompleto()).append("\n");
        detalle.append("Usuario: ").append(personal.getUsuarioSistema()).append("\n");
        detalle.append("Teléfono: ").append(personal.getTelefono()).append("\n");
        detalle.append("Correo: ").append(personal.getCorreo()).append("\n");
        detalle.append("Fecha Contratación: ").append(personal.getFechaContratacion()).append("\n\n");

        // Detalles específicos
        if (personal instanceof Administradores) {
            Administradores admin = (Administradores) personal;
            detalle.append("Cargo: ").append(admin.getCargo());
        } else if (personal instanceof Entrenadores) {
            Entrenadores entrenador = (Entrenadores) personal;
            detalle.append("Especialidad: ").append(entrenador.getEspecialidad()).append("\n");
            detalle.append("Experiencia: ").append(entrenador.getExperiencia()).append(" años");
        } else if (personal instanceof Recepcionistas) {
            Recepcionistas recepcionista = (Recepcionistas) personal;
            String turno = recepcionista.getHorarioTurno();
            detalle.append("Horario Turno: ").append(turno != null ? turno : "No asignado");
        }

        mostrarInfo("Detalle del Personal", detalle.toString());
    }

    @FXML
    private void handleEditar() {
        Personal seleccionado = tablePersonal.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            editarPersonal(seleccionado);
        }
    }

    private void editarPersonal(Personal personal) {

        abrirFormulario(personal);
    }

    @FXML
    private void handleCambiarContrasena() {
        Personal seleccionado = tablePersonal.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;

        // Diálogo para cambiar contraseña
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Cambiar Contraseña");
        dialog.setHeaderText("Cambiar contraseña de: " + seleccionado.getNombreCompleto());

        ButtonType cambiarButton = new ButtonType("Cambiar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(cambiarButton, ButtonType.CANCEL);

        PasswordField txtNuevaContrasena = new PasswordField();
        txtNuevaContrasena.setPromptText("Nueva contraseña");

        PasswordField txtConfirmar = new PasswordField();
        txtConfirmar.setPromptText("Confirmar contraseña");

        VBox content = new VBox(10);
        content.getChildren().addAll(
                new Label("Nueva contraseña:"), txtNuevaContrasena,
                new Label("Confirmar contraseña:"), txtConfirmar
        );

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == cambiarButton) {
                return txtNuevaContrasena.getText();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(nuevaContrasena -> {
            if (!nuevaContrasena.equals(txtConfirmar.getText())) {
                mostrarError("Error", "Las contraseñas no coinciden");
                return;
            }

            try {
                personalService.resetearContrasena(seleccionado.getUsuarioSistema(), nuevaContrasena);
                mostrarExito("Contraseña cambiada correctamente");
            } catch (SQLException e) {
                mostrarError("Error al cambiar contraseña", e.getMessage());
            } catch (IllegalArgumentException e) {
                mostrarError("Error de validación", e.getMessage());
            }
        });
    }

    @FXML
    private void handleEliminar() {
        Personal seleccionado = tablePersonal.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            eliminarPersonal(seleccionado);
        }
    }

    private void eliminarPersonal(Personal personal) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar personal?");
        confirmacion.setContentText("¿Está seguro de eliminar a " + personal.getNombreCompleto() + "?");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String documento = personal.getDocumento();

                    if (personal instanceof Administradores) {
                        administradorService.eliminarAdministrador(documento);
                    } else if (personal instanceof Entrenadores) {
                        entrenadorService.eliminarEntrenador(documento);
                    } else if (personal instanceof Recepcionistas) {
                        recepcionistaService.eliminarRecepcionista(documento);
                    }

                    mostrarExito("Personal eliminado correctamente");
                    cargarDatos();

                } catch (SQLException e) {
                    mostrarError("Error al eliminar", e.getMessage());
                } catch (IllegalArgumentException e) {
                    mostrarError("Error", e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleLimpiarFiltros() {
        txtBuscar.clear();
        cmbTipoPersonal.setValue("Todos");
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