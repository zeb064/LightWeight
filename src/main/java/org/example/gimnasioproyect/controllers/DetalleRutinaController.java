package org.example.gimnasioproyect.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.example.gimnasioproyect.Utilidades.ServiceFactory;
import org.example.gimnasioproyect.model.DetalleRutinas;
import org.example.gimnasioproyect.model.Rutinas;
import org.example.gimnasioproyect.services.RutinaService;
import org.example.gimnasioproyect.services.EstadisticaService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class DetalleRutinaController {

    @FXML private Button btnVolver;
    @FXML private Text lblTitulo;
    @FXML private Text lblObjetivo;

    // Estadísticas
    @FXML private Text lblTotalEjercicios;
    @FXML private Text lblDiasEntrenamiento;
    @FXML private Text lblClientesAsignados;

    // Tabs y contenedores por día
    @FXML private TabPane tabPaneDias;
    @FXML private VBox vboxLunes;
    @FXML private VBox vboxMartes;
    @FXML private VBox vboxMiercoles;
    @FXML private VBox vboxJueves;
    @FXML private VBox vboxViernes;
    @FXML private VBox vboxSabado;
    @FXML private VBox vboxDomingo;

    // Botones de acción
    @FXML private Button btnAgregarEjercicio;
    @FXML private Button btnAsignarCliente;
    @FXML private Button btnEliminarRutina;

    private RutinaService rutinaService;
    private EstadisticaService estadisticaService;
    private StackPane parentContainer;
    private Rutinas rutinaActual;
    private List<DetalleRutinas> detallesRutina;

    public void initialize() {
        // Obtener servicios
        ServiceFactory factory = ServiceFactory.getInstance();
        this.rutinaService = factory.getRutinaService();
        this.estadisticaService = factory.getEstadisticaService();
    }

    public void cargarRutina(Rutinas rutina) {
        if (rutina == null) return;

        this.rutinaActual = rutina;

        // Establecer información básica
        lblTitulo.setText("Detalle de Rutina #" + rutina.getIdRutina());
        lblObjetivo.setText("Objetivo: " + rutina.getObjetivo());

        // Cargar detalles
        cargarDetalles();
    }

    private void cargarDetalles() {
        try {
            // Obtener todos los detalles de la rutina
            detallesRutina = rutinaService.obtenerDetallesRutina(rutinaActual.getIdRutina());

            // Actualizar estadísticas
            actualizarEstadisticas();

            // Cargar ejercicios por día
            cargarEjerciciosPorDia();

        } catch (SQLException e) {
            mostrarError("Error al cargar detalles", e.getMessage());
            e.printStackTrace();
        }
    }

    private void actualizarEstadisticas() {
        // Total de ejercicios
        lblTotalEjercicios.setText(String.valueOf(detallesRutina.size()));

        // Días con entrenamiento
        Set<String> diasUnicos = detallesRutina.stream()
                .map(DetalleRutinas::getDiaSemana)
                .collect(Collectors.toSet());
        lblDiasEntrenamiento.setText(String.valueOf(diasUnicos.size()));

        // Clientes asignados
        try {
            int clientesAsignados = estadisticaService.obtenerClientesAsignadosARutina(rutinaActual.getIdRutina());
            lblClientesAsignados.setText(String.valueOf(clientesAsignados));
        } catch (SQLException e) {
            lblClientesAsignados.setText("-");
            e.printStackTrace();
        }
    }

    private void cargarEjerciciosPorDia() {
        // Limpiar todos los contenedores
        vboxLunes.getChildren().clear();
        vboxMartes.getChildren().clear();
        vboxMiercoles.getChildren().clear();
        vboxJueves.getChildren().clear();
        vboxViernes.getChildren().clear();
        vboxSabado.getChildren().clear();
        vboxDomingo.getChildren().clear();

        // Agrupar ejercicios por día
        Map<String, List<DetalleRutinas>> ejerciciosPorDia = detallesRutina.stream()
                .collect(Collectors.groupingBy(DetalleRutinas::getDiaSemana));

        // Cargar cada día
        cargarEjerciciosDia("LUNES", vboxLunes, ejerciciosPorDia.get("LUNES"));
        cargarEjerciciosDia("MARTES", vboxMartes, ejerciciosPorDia.get("MARTES"));
        cargarEjerciciosDia("MIERCOLES", vboxMiercoles, ejerciciosPorDia.get("MIERCOLES"));
        cargarEjerciciosDia("JUEVES", vboxJueves, ejerciciosPorDia.get("JUEVES"));
        cargarEjerciciosDia("VIERNES", vboxViernes, ejerciciosPorDia.get("VIERNES"));
        cargarEjerciciosDia("SABADO", vboxSabado, ejerciciosPorDia.get("SABADO"));
        cargarEjerciciosDia("DOMINGO", vboxDomingo, ejerciciosPorDia.get("DOMINGO"));
    }

    private void cargarEjerciciosDia(String dia, VBox container, List<DetalleRutinas> ejercicios) {
        if (ejercicios == null || ejercicios.isEmpty()) {
            // Mostrar mensaje de día de descanso
            VBox emptyBox = new VBox(10);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(50));

            Text iconoDescanso = new Text("😴");
            iconoDescanso.setStyle("-fx-font-size: 48px;");

            Text textoDescanso = new Text("Día de descanso");
            textoDescanso.setStyle("-fx-fill: #7f8c8d; -fx-font-size: 16px; -fx-font-weight: bold;");

            emptyBox.getChildren().addAll(iconoDescanso, textoDescanso);
            container.getChildren().add(emptyBox);
            return;
        }

        // Ordenar ejercicios por orden
        ejercicios.sort(Comparator.comparing(DetalleRutinas::getOrden));

        // Crear tarjeta para cada ejercicio
        for (DetalleRutinas detalle : ejercicios) {
            VBox tarjetaEjercicio = crearTarjetaEjercicio(detalle);
            container.getChildren().add(tarjetaEjercicio);
        }
    }

    private VBox crearTarjetaEjercicio(DetalleRutinas detalle) {
        VBox tarjeta = new VBox(10);
        tarjeta.setStyle("-fx-background-color: #34495e; -fx-background-radius: 10; -fx-padding: 15;");

        // Header de la tarjeta
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        // Número de orden
        Text numeroOrden = new Text(String.valueOf(detalle.getOrden()));
        numeroOrden.setStyle("-fx-fill: #3498db; -fx-font-size: 24px; -fx-font-weight: bold;");

        VBox circleContainer = new VBox();
        circleContainer.setAlignment(Pos.CENTER);
        circleContainer.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 25; -fx-min-width: 50; -fx-min-height: 50;");
        circleContainer.getChildren().add(numeroOrden);

        // Nombre del ejercicio
        Text nombreEjercicio = new Text(detalle.getEjercicio());
        nombreEjercicio.setStyle("-fx-fill: #ecf0f1; -fx-font-size: 18px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Botón editar
        Button btnEditar = new Button("✏️");
        btnEditar.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5; -fx-padding: 5 10;");
        btnEditar.setOnAction(e -> editarEjercicio(detalle));

        // Botón eliminar
        Button btnEliminar = new Button("🗑️");
        btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5; -fx-padding: 5 10;");
        btnEliminar.setOnAction(e -> eliminarEjercicio(detalle));

        header.getChildren().addAll(circleContainer, nombreEjercicio, spacer, btnEditar, btnEliminar);

        // Información del ejercicio
        GridPane info = new GridPane();
        info.setHgap(20);
        info.setVgap(8);
        info.setPadding(new Insets(10, 0, 0, 0));

        // Series
        Text lblSeries = new Text("Series:");
        lblSeries.setStyle("-fx-fill: #bdc3c7; -fx-font-size: 13px;");
        Text valSeries = new Text(String.valueOf(detalle.getSeries()));
        valSeries.setStyle("-fx-fill: #3498db; -fx-font-size: 16px; -fx-font-weight: bold;");

        // Repeticiones
        Text lblRepeticiones = new Text("Repeticiones:");
        lblRepeticiones.setStyle("-fx-fill: #bdc3c7; -fx-font-size: 13px;");
        Text valRepeticiones = new Text(String.valueOf(detalle.getRepeticiones()));
        valRepeticiones.setStyle("-fx-fill: #27ae60; -fx-font-size: 16px; -fx-font-weight: bold;");

        // Peso
        Text lblPeso = new Text("Peso:");
        lblPeso.setStyle("-fx-fill: #bdc3c7; -fx-font-size: 13px;");
        Text valPeso = new Text(String.format("%.1f kg", detalle.getPeso()));
        valPeso.setStyle("-fx-fill: #f39c12; -fx-font-size: 16px; -fx-font-weight: bold;");

        info.add(lblSeries, 0, 0);
        info.add(valSeries, 0, 1);
        info.add(lblRepeticiones, 1, 0);
        info.add(valRepeticiones, 1, 1);
        info.add(lblPeso, 2, 0);
        info.add(valPeso, 2, 1);

        tarjeta.getChildren().addAll(header, info);

        // Notas (si existen)
        if (detalle.getNotas() != null && !detalle.getNotas().isEmpty()) {
            VBox notasBox = new VBox(5);
            notasBox.setPadding(new Insets(10, 0, 0, 0));

            Text lblNotas = new Text("💡 Notas:");
            lblNotas.setStyle("-fx-fill: #f39c12; -fx-font-size: 12px; -fx-font-weight: bold;");

            Text textoNotas = new Text(detalle.getNotas());
            textoNotas.setStyle("-fx-fill: #ecf0f1; -fx-font-size: 12px;");
            textoNotas.setWrappingWidth(600);

            notasBox.getChildren().addAll(lblNotas, textoNotas);
            tarjeta.getChildren().add(notasBox);
        }

        return tarjeta;
    }

    private void eliminarEjercicio(DetalleRutinas detalle) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar ejercicio?");
        confirmacion.setContentText("¿Está seguro de eliminar: " + detalle.getEjercicio() + "?");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    rutinaService.eliminarDetalleRutina(detalle.getIdDetalle());
                    mostrarExito("Ejercicio eliminado correctamente");
                    cargarDetalles();

                } catch (SQLException | IllegalArgumentException e) {
                    mostrarError("Error al eliminar", e.getMessage());
                }
            }
        });
    }

    private void editarEjercicio(DetalleRutinas detalle) {
        // Crear diálogo para editar ejercicio
        Dialog<DetalleRutinas> dialog = new Dialog<>();
        dialog.setTitle("Editar Ejercicio");
        dialog.setHeaderText("Editar ejercicio: " + detalle.getEjercicio());

        ButtonType guardarButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Cargar datos actuales
        ComboBox<String> cmbDia = new ComboBox<>();
        cmbDia.getItems().addAll("LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO");
        cmbDia.setValue(detalle.getDiaSemana());

        TextField txtOrden = new TextField(String.valueOf(detalle.getOrden()));
        TextField txtEjercicio = new TextField(detalle.getEjercicio());
        TextField txtSeries = new TextField(String.valueOf(detalle.getSeries()));
        TextField txtRepeticiones = new TextField(String.valueOf(detalle.getRepeticiones()));
        TextField txtPeso = new TextField(String.valueOf(detalle.getPeso()));
        TextArea txtNotas = new TextArea(detalle.getNotas() != null ? detalle.getNotas() : "");
        txtNotas.setPrefRowCount(2);

        grid.add(new Label("Día de la semana:"), 0, 0);
        grid.add(cmbDia, 1, 0);
        grid.add(new Label("Orden:"), 0, 1);
        grid.add(txtOrden, 1, 1);
        grid.add(new Label("Ejercicio:"), 0, 2);
        grid.add(txtEjercicio, 1, 2);
        grid.add(new Label("Series:"), 0, 3);
        grid.add(txtSeries, 1, 3);
        grid.add(new Label("Repeticiones:"), 0, 4);
        grid.add(txtRepeticiones, 1, 4);
        grid.add(new Label("Peso (kg):"), 0, 5);
        grid.add(txtPeso, 1, 5);
        grid.add(new Label("Notas:"), 0, 6);
        grid.add(txtNotas, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == guardarButtonType) {
                try {
                    // Actualizar los datos del detalle
                    detalle.setDiaSemana(cmbDia.getValue());
                    detalle.setOrden(Integer.parseInt(txtOrden.getText()));
                    detalle.setEjercicio(txtEjercicio.getText());
                    detalle.setSeries(Integer.parseInt(txtSeries.getText()));
                    detalle.setRepeticiones(Integer.parseInt(txtRepeticiones.getText()));
                    detalle.setPeso(Double.parseDouble(txtPeso.getText()));
                    detalle.setNotas(txtNotas.getText());

//                    // Aquí necesitarías un método en el servicio para actualizar
//                    // Por ahora mostramos mensaje
//                    mostrarInfo("Funcionalidad pendiente",
//                            "La actualización de ejercicios se implementará próximamente");


                     rutinaService.actualizarDetalleRutina(detalle);
                     cargarDetalles();

                    mostrarExito("Ejercicio actualizado correctamente");
                    cargarDetalles();

                } catch (NumberFormatException e) {
                    mostrarError("Error", "Valores numéricos inválidos");
                } catch (Exception e) {
                    mostrarError("Error", e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleAgregarEjercicio() {
        // Diálogo para agregar ejercicio
        Dialog<DetalleRutinas> dialog = new Dialog<>();
        dialog.setTitle("Agregar Ejercicio");
        dialog.setHeaderText("Agregar ejercicio a: " + rutinaActual.getObjetivo());

        ButtonType agregarButtonType = new ButtonType("Agregar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(agregarButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<String> cmbDia = new ComboBox<>();
        cmbDia.getItems().addAll("LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO");
        cmbDia.setValue("LUNES");

        TextField txtOrden = new TextField("1");
        TextField txtEjercicio = new TextField();
        TextField txtSeries = new TextField("3");
        TextField txtRepeticiones = new TextField("12");
        TextField txtPeso = new TextField("0");
        TextArea txtNotas = new TextArea();
        txtNotas.setPrefRowCount(2);

        grid.add(new Label("Día de la semana:"), 0, 0);
        grid.add(cmbDia, 1, 0);
        grid.add(new Label("Orden:"), 0, 1);
        grid.add(txtOrden, 1, 1);
        grid.add(new Label("Ejercicio:"), 0, 2);
        grid.add(txtEjercicio, 1, 2);
        grid.add(new Label("Series:"), 0, 3);
        grid.add(txtSeries, 1, 3);
        grid.add(new Label("Repeticiones:"), 0, 4);
        grid.add(txtRepeticiones, 1, 4);
        grid.add(new Label("Peso (kg):"), 0, 5);
        grid.add(txtPeso, 1, 5);
        grid.add(new Label("Notas:"), 0, 6);
        grid.add(txtNotas, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == agregarButtonType) {
                try {
                    rutinaService.agregarDetalleRutina(
                            rutinaActual.getIdRutina(),
                            cmbDia.getValue(),
                            Integer.parseInt(txtOrden.getText()),
                            txtEjercicio.getText(),
                            Integer.parseInt(txtSeries.getText()),
                            Integer.parseInt(txtRepeticiones.getText()),
                            Double.parseDouble(txtPeso.getText()),
                            txtNotas.getText()
                    );

                    mostrarExito("Ejercicio agregado correctamente");
                    cargarDetalles();

                } catch (NumberFormatException e) {
                    mostrarError("Error", "Valores numéricos inválidos");
                } catch (SQLException | IllegalArgumentException e) {
                    mostrarError("Error", e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleAsignarCliente() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Asignar Rutina");
        dialog.setHeaderText("Asignar rutina: " + rutinaActual.getObjetivo());
        dialog.setContentText("Documento del cliente:");

        dialog.showAndWait().ifPresent(documento -> {
            if (documento.trim().isEmpty()) {
                mostrarError("Error", "Debe ingresar un documento");
                return;
            }

            try {
                rutinaService.asignarRutinaACliente(
                        rutinaActual.getIdRutina(),
                        documento.trim(),
                        null
                );

                mostrarExito("Rutina asignada correctamente al cliente");
                cargarDetalles();

            } catch (SQLException | IllegalArgumentException e) {
                mostrarError("Error", e.getMessage());
            }
        });
    }

    @FXML
    private void handleEliminarRutina() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar rutina completa?");
        confirmacion.setContentText("¿Está seguro de eliminar la rutina: " + rutinaActual.getObjetivo() + "?");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    rutinaService.eliminarRutina(rutinaActual.getIdRutina());
                    mostrarExito("Rutina eliminada correctamente");
                    volverALista();
                } catch (SQLException | IllegalArgumentException e) {
                    mostrarError("Error", e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleVolver() {
        volverALista();
    }

    private void volverALista() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/org/example/gimnasioproyect/Rutinas.fxml"));
            Parent vistaRutinas = loader.load();

            if (parentContainer != null) {
                parentContainer.getChildren().clear();
                parentContainer.getChildren().add(vistaRutinas);
            }
        } catch (IOException e) {
            mostrarError("Error", "No se pudo volver a la lista: " + e.getMessage());
            e.printStackTrace();
        }
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

    public void setParentContainer(StackPane parentContainer) {
        this.parentContainer = parentContainer;
    }
}