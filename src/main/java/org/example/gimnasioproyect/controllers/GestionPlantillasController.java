package org.example.gimnasioproyect.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.example.gimnasioproyect.Utilidades.PlantillaProcesador;
import org.example.gimnasioproyect.Utilidades.ServiceFactory;
import org.example.gimnasioproyect.Utilidades.TelegramConfig;
import org.example.gimnasioproyect.model.MensajesTelegram;
import org.example.gimnasioproyect.services.MensajeTelegramService;
import org.example.gimnasioproyect.services.TelegramBotService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GestionPlantillasController {

    @FXML private Text lblEstadoBot;
    @FXML private Text lblEstadoBotTexto;
    @FXML private Text lblHoraRevision;
    @FXML private Text lblDiasAnticipacion;
    @FXML private CheckBox chkActivoBienvenida;
    @FXML private TextArea txtBienvenida;
    @FXML private TextArea txtVistaPreviaBienvenida;
    @FXML private CheckBox chkActivoVenceProno;
    @FXML private TextArea txtVenceProno;
    @FXML private TextArea txtVistaPreviaVenceProno;
    @FXML private CheckBox chkActivoVencido;
    @FXML private TextArea txtVencido;
    @FXML private TextArea txtVistaPreviaVencido;
    @FXML private CheckBox chkActivoInactividad;
    @FXML private TextArea txtInactividad;
    @FXML private TextArea txtVistaPreviaInactividad;
    @FXML private CheckBox chkActivoRutinaActualizada;
    @FXML private TextArea txtRutinaActualizada;
    @FXML private TextArea txtVistaPreviaRutinaActualizada;
    @FXML private CheckBox chkActivoNuevoEntrenador;
    @FXML private TextArea txtNuevoEntrenador;
    @FXML private TextArea txtVistaPreviaNuevoEntrenador;

    private MensajeTelegramService mensajeTelegramService;
    private TelegramBotService telegramBotService;
    private TelegramConfig telegramConfig;

    private Integer idBienvenida;
    private Integer idVenceProno;
    private Integer idVencido;
    private Integer idInactividad;
    private Integer idRutinaActualizada;
    private Integer idNuevoEntrenador;

    private static final String PLANTILLA_ORIGINAL_BIENVENIDA =
            "¡Hola {nombre}! 👋\n\n" +
                    "Bienvenido a *LightWeight Gym* 💪\n\n" +
                    "Tu membresía *{tipo_membresia}* ha sido activada exitosamente.\n\n" +
                    "📅 *Fecha de inicio:* {fecha_inicio}\n" +
                    "📅 *Fecha de vencimiento:* {fecha_fin}\n\n" +
                    "¡Nos vemos en el gimnasio! 🏋️‍♂️";

    private static final String PLANTILLA_ORIGINAL_VENCE_PRONTO =
            "⚠️ ¡Hola {nombre}!\n\n" +
                    "Tu membresía está próxima a vencer.\n\n" +
                    "⏰ *Días restantes:* {dias}\n" +
                    "📅 *Fecha de vencimiento:* {fecha_fin}\n\n" +
                    "Recuerda renovar tu membresía para seguir disfrutando del gimnasio. 💪";

    private static final String PLANTILLA_ORIGINAL_VENCIDO =
            "❌ Hola {nombre},\n\n" +
                    "Tu membresía ha vencido.\n\n" +
                    "📅 *Fecha de vencimiento:* {fecha_fin}\n\n" +
                    "Para continuar entrenando, renueva tu membresía en recepción. 🏋️‍♂️\n\n" +
                    "¡Te esperamos! 💪";

    private static final String PLANTILLA_ORIGINAL_INACTIVIDAD =
            "😔 Hola {nombre},\n\n" +
                    "Hemos notado que llevas *{dias_inactivo} días* sin asistir al gimnasio.\n" +
                    "📅 *Última asistencia:* {ultima_asistencia}\n\n" +
                    "¡Te extrañamos! 💪\n" +
                    "Recuerda que la constancia es clave para alcanzar tus objetivos.\n\n" +
                    "¿Algún problema con tu rutina? Habla con tu entrenador.";

    private static final String PLANTILLA_ORIGINAL_RUTINA_ACTUALIZADA =
            "🎯 ¡Hola {nombre}!\n\n" +
                    "Tu entrenador *{nombre_entrenador}* ha actualizado tu rutina.\n\n" +
                    "📋 *Nuevo objetivo:* {objetivo}\n\n" +
                    "Usa /mirutina para ver los detalles completos.\n\n" +
                    "¡Es hora de entrenar con tu nueva rutina! 💪";

    private static final String PLANTILLA_ORIGINAL_NUEVO_ENTRENADOR =
            "👨‍🏫 ¡Hola {nombre}!\n\n" +
                    "Te hemos asignado un entrenador personal.\n\n" +
                    "*Entrenador:* {nombre_entrenador}\n" +
                    "*Especialidad:* {especialidad}\n\n" +
                    "Usa /mientrenador para ver su información de contacto.\n\n" +
                    "¡Prepárate para llevar tu entrenamiento al siguiente nivel! 💪";

    @FXML
    public void initialize() {
        // Obtener servicios del ServiceFactory
        mensajeTelegramService = ServiceFactory.getInstance().getMensajeTelegramService();
        telegramBotService = ServiceFactory.getInstance().getTelegramBotService();
        telegramConfig = TelegramConfig.getInstance();

        // Cargar configuración
        cargarConfiguracion();

        // Cargar plantillas desde la BD
        cargarPlantillas();

        // Configurar listeners para vista previa en tiempo real
        configurarListeners();
    }

    private void cargarConfiguracion() {
        // Verificar estado del bot
        boolean botActivo = telegramBotService.verificarConfiguracion();

        if (botActivo) {
            lblEstadoBot.setText("●");
            lblEstadoBot.setStyle("-fx-fill: #2ecc71;");
            lblEstadoBotTexto.setText("Activo");
        } else {
            lblEstadoBot.setText("●");
            lblEstadoBot.setStyle("-fx-fill: #e74c3c;");
            lblEstadoBotTexto.setText("Inactivo");
        }

        // Cargar configuración
        int hora = telegramConfig.getRevisionHora();
        int minuto = telegramConfig.getRevisionMinuto();
        lblHoraRevision.setText(String.format("%02d:%02d", hora, minuto));

        int dias = telegramConfig.getDiasVencimientoProximo();
        lblDiasAnticipacion.setText(dias + " días");
    }

    private void cargarPlantillas() {
        try {
            Optional<MensajesTelegram> bienvenidaOpt =
                    mensajeTelegramService.obtenerMensajePorTipo("BIENVENIDA");

            if (bienvenidaOpt.isPresent()) {
                MensajesTelegram bienvenida = bienvenidaOpt.get();
                idBienvenida = bienvenida.getIdMensaje();
                txtBienvenida.setText(bienvenida.getContenido());
                chkActivoBienvenida.setSelected(bienvenida.isActivo());
                actualizarVistaPreviaBienvenida();
            } else {
                txtBienvenida.setText(PLANTILLA_ORIGINAL_BIENVENIDA);
                actualizarVistaPreviaBienvenida();
            }

            Optional<MensajesTelegram> vencePronoOpt =
                    mensajeTelegramService.obtenerMensajePorTipo("VENCE_PRONTO");

            if (vencePronoOpt.isPresent()) {
                MensajesTelegram venceProno = vencePronoOpt.get();
                idVenceProno = venceProno.getIdMensaje();
                txtVenceProno.setText(venceProno.getContenido());
                chkActivoVenceProno.setSelected(venceProno.isActivo());
                actualizarVistaPreviaVenceProno();
            } else {
                txtVenceProno.setText(PLANTILLA_ORIGINAL_VENCE_PRONTO);
                actualizarVistaPreviaVenceProno();
            }

            Optional<MensajesTelegram> vencidoOpt =
                    mensajeTelegramService.obtenerMensajePorTipo("VENCIDO");

            if (vencidoOpt.isPresent()) {
                MensajesTelegram vencido = vencidoOpt.get();
                idVencido = vencido.getIdMensaje();
                txtVencido.setText(vencido.getContenido());
                chkActivoVencido.setSelected(vencido.isActivo());
                actualizarVistaPreviaVencido();
            } else {
                txtVencido.setText(PLANTILLA_ORIGINAL_VENCIDO);
                actualizarVistaPreviaVencido();
            }

            Optional<MensajesTelegram> inactividadOpt =
                    mensajeTelegramService.obtenerMensajePorTipo("INACTIVIDAD");

            if (inactividadOpt.isPresent()) {
                MensajesTelegram inactividad = inactividadOpt.get();
                idInactividad = inactividad.getIdMensaje();
                txtInactividad.setText(inactividad.getContenido());
                chkActivoInactividad.setSelected(inactividad.isActivo());
                actualizarVistaPreviaInactividad();
            } else {
                txtInactividad.setText(PLANTILLA_ORIGINAL_INACTIVIDAD);
                actualizarVistaPreviaInactividad();
            }

            // Cargar RUTINA_ACTUALIZADA
            Optional<MensajesTelegram> rutinaActualizadaOpt =
                    mensajeTelegramService.obtenerMensajePorTipo("RUTINA_ACTUALIZADA");

            if (rutinaActualizadaOpt.isPresent()) {
                MensajesTelegram rutinaActualizada = rutinaActualizadaOpt.get();
                idRutinaActualizada = rutinaActualizada.getIdMensaje();
                txtRutinaActualizada.setText(rutinaActualizada.getContenido());
                chkActivoRutinaActualizada.setSelected(rutinaActualizada.isActivo());
                actualizarVistaPreviaRutinaActualizada();
            } else {
                txtRutinaActualizada.setText(PLANTILLA_ORIGINAL_RUTINA_ACTUALIZADA);
                actualizarVistaPreviaRutinaActualizada();
            }

            // Cargar NUEVO_ENTRENADOR
            Optional<MensajesTelegram> nuevoEntrenadorOpt =
                    mensajeTelegramService.obtenerMensajePorTipo("NUEVO_ENTRENADOR");

            if (nuevoEntrenadorOpt.isPresent()) {
                MensajesTelegram nuevoEntrenador = nuevoEntrenadorOpt.get();
                idNuevoEntrenador = nuevoEntrenador.getIdMensaje();
                txtNuevoEntrenador.setText(nuevoEntrenador.getContenido());
                chkActivoNuevoEntrenador.setSelected(nuevoEntrenador.isActivo());
                actualizarVistaPreviaNuevoEntrenador();
            } else {
                txtNuevoEntrenador.setText(PLANTILLA_ORIGINAL_NUEVO_ENTRENADOR);
                actualizarVistaPreviaNuevoEntrenador();
            }

        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudieron cargar las plantillas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarListeners() {
        // Listener para actualizar vista previa automáticamente
        txtBienvenida.textProperty().addListener((obs, old, newVal) -> {
            actualizarVistaPreviaBienvenida();
        });

        txtVenceProno.textProperty().addListener((obs, old, newVal) -> {
            actualizarVistaPreviaVenceProno();
        });

        txtVencido.textProperty().addListener((obs, old, newVal) -> {
            actualizarVistaPreviaVencido();
        });

        txtInactividad.textProperty().addListener((obs, old, newVal) -> {
            actualizarVistaPreviaInactividad();
        });

        txtRutinaActualizada.textProperty().addListener((obs, old, newVal) -> {
            actualizarVistaPreviaRutinaActualizada();
        });

        txtNuevoEntrenador.textProperty().addListener((obs, old, newVal) -> {
            actualizarVistaPreviaNuevoEntrenador();
        });

//        txtInactividad.textProperty().addListener((obs, old, newVal) -> actualizarVistaPreviaInactividad());
//        txtRutinaActualizada.textProperty().addListener((obs, old, newVal) -> actualizarVistaPreviaRutinaActualizada());
//        txtNuevoEntrenador.textProperty().addListener((obs, old, newVal) -> actualizarVistaPreviaNuevoEntrenador());
    }

    @FXML
    private void handleGuardarBienvenida() {
        guardarPlantilla("BIENVENIDA", idBienvenida,
                txtBienvenida.getText(), chkActivoBienvenida.isSelected());
    }

    @FXML
    private void handleRestaurarBienvenida() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Restaurar Plantilla");
        confirmacion.setHeaderText("¿Restaurar plantilla original?");
        confirmacion.setContentText("Se perderán los cambios actuales");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                txtBienvenida.setText(PLANTILLA_ORIGINAL_BIENVENIDA);
                actualizarVistaPreviaBienvenida();
            }
        });
    }

    @FXML
    private void handleActualizarVistaPreviaBienvenida() {
        actualizarVistaPreviaBienvenida();
    }

    private void actualizarVistaPreviaBienvenida() {
        Map<String, String> variables = new HashMap<>();
        variables.put("nombre", "Juan Pérez");
        variables.put("tipo_membresia", "MENSUAL");
        variables.put("fecha_inicio", LocalDate.now().toString());
        variables.put("fecha_fin", LocalDate.now().plusMonths(1).toString());

        String vistaPrevia = PlantillaProcesador.procesarPlantilla(
                txtBienvenida.getText(), variables);

        txtVistaPreviaBienvenida.setText(vistaPrevia);
    }

    @FXML
    private void handleGuardarVenceProno() {
        guardarPlantilla("VENCE_PRONTO", idVenceProno,
                txtVenceProno.getText(), chkActivoVenceProno.isSelected());
    }

    @FXML
    private void handleRestaurarVenceProno() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Restaurar Plantilla");
        confirmacion.setHeaderText("¿Restaurar plantilla original?");
        confirmacion.setContentText("Se perderán los cambios actuales");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                txtVenceProno.setText(PLANTILLA_ORIGINAL_VENCE_PRONTO);
                actualizarVistaPreviaVenceProno();
            }
        });
    }

    @FXML
    private void handleActualizarVistaPreviaVenceProno() {
        actualizarVistaPreviaVenceProno();
    }

    private void actualizarVistaPreviaVenceProno() {
        Map<String, String> variables = new HashMap<>();
        variables.put("nombre", "María García");
        variables.put("dias", "5");
        variables.put("fecha_fin", LocalDate.now().plusDays(5).toString());

        String vistaPrevia = PlantillaProcesador.procesarPlantilla(
                txtVenceProno.getText(), variables);

        txtVistaPreviaVenceProno.setText(vistaPrevia);
    }

    @FXML
    private void handleGuardarVencido() {
        guardarPlantilla("VENCIDO", idVencido,
                txtVencido.getText(), chkActivoVencido.isSelected());
    }

    @FXML
    private void handleRestaurarVencido() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Restaurar Plantilla");
        confirmacion.setHeaderText("¿Restaurar plantilla original?");
        confirmacion.setContentText("Se perderán los cambios actuales");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                txtVencido.setText(PLANTILLA_ORIGINAL_VENCIDO);
                actualizarVistaPreviaVencido();
            }
        });
    }

    @FXML
    private void handleActualizarVistaPreviaVencido() {
        actualizarVistaPreviaVencido();
    }

    private void actualizarVistaPreviaVencido() {
        Map<String, String> variables = new HashMap<>();
        variables.put("nombre", "Carlos Rodríguez");
        variables.put("fecha_fin", LocalDate.now().minusDays(1).toString());

        String vistaPrevia = PlantillaProcesador.procesarPlantilla(
                txtVencido.getText(), variables);

        txtVistaPreviaVencido.setText(vistaPrevia);
    }

    @FXML
    private void handleGuardarInactividad() {
        guardarPlantilla("INACTIVIDAD", idInactividad,
                txtInactividad.getText(), chkActivoInactividad.isSelected());
    }

    @FXML
    private void handleRestaurarInactividad() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Restaurar Plantilla");
        confirmacion.setHeaderText("¿Restaurar plantilla original?");
        confirmacion.setContentText("Se perderán los cambios actuales");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                txtInactividad.setText(PLANTILLA_ORIGINAL_INACTIVIDAD);
                actualizarVistaPreviaInactividad();
            }
        });
    }

    @FXML
    private void handleActualizarVistaPreviaInactividad() {
        actualizarVistaPreviaInactividad();
    }

    private void actualizarVistaPreviaInactividad() {
        Map<String, String> variables = new HashMap<>();
        variables.put("nombre", "Pedro López");
        variables.put("dias_inactivo", "7");
        variables.put("ultima_asistencia", LocalDate.now().minusDays(7).toString());

        String vistaPrevia = PlantillaProcesador.procesarPlantilla(
                txtInactividad.getText(), variables);

        txtVistaPreviaInactividad.setText(vistaPrevia);
    }

    @FXML
    private void handleGuardarRutinaActualizada() {
        guardarPlantilla("RUTINA_ACTUALIZADA", idRutinaActualizada,
                txtRutinaActualizada.getText(), chkActivoRutinaActualizada.isSelected());
    }

    @FXML
    private void handleRestaurarRutinaActualizada() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Restaurar Plantilla");
        confirmacion.setHeaderText("¿Restaurar plantilla original?");
        confirmacion.setContentText("Se perderán los cambios actuales");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                txtRutinaActualizada.setText(PLANTILLA_ORIGINAL_RUTINA_ACTUALIZADA);
                actualizarVistaPreviaRutinaActualizada();
            }
        });
    }

    @FXML
    private void handleActualizarVistaPreviaRutinaActualizada() {
        actualizarVistaPreviaRutinaActualizada();
    }

    private void actualizarVistaPreviaRutinaActualizada() {
        Map<String, String> variables = new HashMap<>();
        variables.put("nombre", "Ana Martínez");
        variables.put("nombre_entrenador", "Carlos Rodríguez");
        variables.put("objetivo", "Ganancia de masa muscular");

        String vistaPrevia = PlantillaProcesador.procesarPlantilla(
                txtRutinaActualizada.getText(), variables);

        txtVistaPreviaRutinaActualizada.setText(vistaPrevia);
    }

    @FXML
    private void handleGuardarNuevoEntrenador() {
        guardarPlantilla("NUEVO_ENTRENADOR", idNuevoEntrenador,
                txtNuevoEntrenador.getText(), chkActivoNuevoEntrenador.isSelected());
    }

    @FXML
    private void handleRestaurarNuevoEntrenador() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Restaurar Plantilla");
        confirmacion.setHeaderText("¿Restaurar plantilla original?");
        confirmacion.setContentText("Se perderán los cambios actuales");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                txtNuevoEntrenador.setText(PLANTILLA_ORIGINAL_NUEVO_ENTRENADOR);
                actualizarVistaPreviaNuevoEntrenador();
            }
        });
    }

    @FXML
    private void handleActualizarVistaPreviaNuevoEntrenador() {
        actualizarVistaPreviaNuevoEntrenador();
    }

    private void actualizarVistaPreviaNuevoEntrenador() {
        Map<String, String> variables = new HashMap<>();
        variables.put("nombre", "Luis Fernández");
        variables.put("nombre_entrenador", "María González");
        variables.put("especialidad", "Entrenamiento funcional");

        String vistaPrevia = PlantillaProcesador.procesarPlantilla(
                txtNuevoEntrenador.getText(), variables);

        txtVistaPreviaNuevoEntrenador.setText(vistaPrevia);
    }

    private void guardarPlantilla(String tipo, Integer id, String contenido, boolean activo) {
        // Validaciones
        if (contenido == null || contenido.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia",
                    "El contenido no puede estar vacío");
            return;
        }

        // Validar que las variables estén presentes
        if (!validarVariables(tipo, contenido)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Variables Faltantes",
                    "Faltan variables requeridas en la plantilla.\n" +
                            "Verifica que todas las variables necesarias estén presentes.");
            return;
        }

        try {
            MensajesTelegram mensaje = new MensajesTelegram();
            mensaje.setIdMensaje(id);
            mensaje.setTipoMensaje(tipo);
            mensaje.setContenido(contenido);
            mensaje.setActivo(activo);

            if (id == null) {
                // Crear nueva plantilla
                mensajeTelegramService.crearMensaje(mensaje);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                        "Plantilla creada correctamente");

                // Recargar para obtener el ID generado
                cargarPlantillas();
            } else {
                // Actualizar plantilla existente
                mensajeTelegramService.actualizarMensaje(mensaje);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                        "Plantilla actualizada correctamente");
            }

        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo guardar la plantilla: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validarVariables(String tipo, String contenido) {
        switch (tipo) {
            case "BIENVENIDA":
                return contenido.contains("{nombre}") &&
                        contenido.contains("{tipo_membresia}") &&
                        contenido.contains("{fecha_inicio}") &&
                        contenido.contains("{fecha_fin}");
            case "VENCE_PRONTO":
                return contenido.contains("{nombre}") &&
                        contenido.contains("{dias}") &&
                        contenido.contains("{fecha_fin}");
            case "VENCIDO":
                return contenido.contains("{nombre}") &&
                        contenido.contains("{fecha_fin}");
            case "INACTIVIDAD_7_DIAS":
                return contenido.contains("{nombre}") &&
                        contenido.contains("{dias_inactivo}") &&
                        contenido.contains("{ultima_asistencia}");
            case "RUTINA_ACTUALIZADA":
                return contenido.contains("{nombre}") &&
                        contenido.contains("{nombre_entrenador}") &&
                        contenido.contains("{objetivo}");
            case "NUEVO_ENTRENADOR":
                return contenido.contains("{nombre}") &&
                        contenido.contains("{nombre_entrenador}") &&
                        contenido.contains("{especialidad}");
            default:
                return false;
        }
    }

    @FXML
    private void handleProbarBot() {
        Alert loading = new Alert(Alert.AlertType.INFORMATION);
        loading.setTitle("Probando Bot");
        loading.setHeaderText("Verificando conexión...");
        loading.setContentText("Por favor espere...");
        loading.show();

        new Thread(() -> {
            boolean conectado = telegramBotService.verificarConfiguracion();

            Platform.runLater(() -> {
                loading.close();

                if (conectado) {
                    lblEstadoBot.setText("●");
                    lblEstadoBot.setStyle("-fx-fill: #2ecc71;");
                    lblEstadoBotTexto.setText("Activo");

                    mostrarAlerta(Alert.AlertType.INFORMATION, "Bot Activo",
                            "✅ El bot de Telegram está funcionando correctamente");
                } else {
                    lblEstadoBot.setText("●");
                    lblEstadoBot.setStyle("-fx-fill: #e74c3c;");
                    lblEstadoBotTexto.setText("Inactivo");

                    mostrarAlerta(Alert.AlertType.ERROR, "Bot Inactivo",
                            "❌ No se pudo conectar con el bot de Telegram.\n\n" +
                                    "Verifica:\n" +
                                    "• Token configurado en telegram.properties\n" +
                                    "• Conexión a internet\n" +
                                    "• Bot activado en @BotFather");
                }
            });
        }).start();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}