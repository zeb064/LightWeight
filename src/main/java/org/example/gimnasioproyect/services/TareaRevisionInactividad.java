package org.example.gimnasioproyect.services;

import org.example.gimnasioproyect.model.Asistencias;
import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.model.MembresiaClientes;
import org.example.gimnasioproyect.repository.AsistenciaRepository;
import org.example.gimnasioproyect.repository.ClienteRepository;
import org.example.gimnasioproyect.repository.MembresiaClienteRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Tarea programada para detectar clientes inactivos (7 días sin asistir).
 *
 * Se ejecuta diariamente a las 9:00 AM y notifica a los clientes que:
 * - Tienen membresía activa
 * - Tienen chatId de Telegram registrado
 * - No han asistido en los últimos 7 días
 */
public class TareaRevisionInactividad {

    private static final int DIAS_INACTIVIDAD = 7;
    private static final int HORA_EJECUCION = 9;  // 9:00 AM
    private static final int MINUTO_EJECUCION = 0;

    private final ClienteRepository clienteRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final MembresiaClienteRepository membresiaClienteRepository;
    private final NotificacionService notificacionService;

    private ScheduledExecutorService scheduler;

    public TareaRevisionInactividad(ClienteRepository clienteRepository,
                                    AsistenciaRepository asistenciaRepository,
                                    MembresiaClienteRepository membresiaClienteRepository,
                                    NotificacionService notificacionService) {
        this.clienteRepository = clienteRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.membresiaClienteRepository = membresiaClienteRepository;
        this.notificacionService = notificacionService;
    }

    /**
     * Inicia la tarea programada.
     */
    public void iniciar() {
        if (scheduler != null && !scheduler.isShutdown()) {
            System.out.println("La tarea de revisión de inactividad ya está en ejecución");
            return;
        }

        scheduler = Executors.newScheduledThreadPool(1);

        // Calcular el delay inicial hasta la próxima ejecución
        long delayInicial = calcularDelayHastaProximaEjecucion();

        System.out.println("Tarea de revisión de inactividad programada a las " +
                HORA_EJECUCION + ":" + String.format("%02d", MINUTO_EJECUCION) + " cada día");

        // Programar la tarea
        scheduler.scheduleAtFixedRate(
                this::ejecutarRevision,
                delayInicial,
                TimeUnit.DAYS.toSeconds(1), // Se ejecuta cada día
                TimeUnit.SECONDS
        );

        System.out.println("Tarea de inactividad iniciada. Próxima ejecución en " +
                (delayInicial / 3600) + " horas");
    }

    /**
     * Detiene la tarea programada.
     */
    public void detener() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            System.out.println("Tarea de revisión de inactividad detenida");
        }
    }

    /**
     * Calcula el delay en segundos hasta la próxima ejecución programada.
     */
    private long calcularDelayHastaProximaEjecucion() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalTime horaEjecucion = LocalTime.of(HORA_EJECUCION, MINUTO_EJECUCION);

        LocalDateTime proximaEjecucion = ahora.with(horaEjecucion);

        // Si la hora ya pasó hoy, programar para mañana
        if (ahora.isAfter(proximaEjecucion)) {
            proximaEjecucion = proximaEjecucion.plusDays(1);
        }

        return ChronoUnit.SECONDS.between(ahora, proximaEjecucion);
    }

    /**
     * Ejecuta la revisión de clientes inactivos.
     */
    private void ejecutarRevision() {
        System.out.println("\n===============================================");
        System.out.println("Iniciando revisión de inactividad - " + LocalDateTime.now());
        System.out.println("===============================================\n");

        try {
            revisarClientesInactivos();
            System.out.println("\nRevisión de inactividad completada\n");

        } catch (Exception e) {
            System.err.println("Error durante la revisión de inactividad: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Revisa y notifica a clientes inactivos.
     */
    private void revisarClientesInactivos() throws SQLException {
        System.out.println("Buscando clientes inactivos (más de " + DIAS_INACTIVIDAD + " días)...");

        // Obtener todos los clientes con membresía activa
        List<MembresiaClientes> membresiasActivas = membresiaClienteRepository.findAll()
                .stream()
                .filter(MembresiaClientes::estaActiva)
                .toList();

        int notificacionesEnviadas = 0;
        int clientesRevisados = 0;

        for (MembresiaClientes membresia : membresiasActivas) {
            Clientes cliente = membresia.getCliente();

            // Verificar que tenga chatId registrado
            if (cliente.getChatId() == null || cliente.getChatId().trim().isEmpty()) {
                continue;
            }

            clientesRevisados++;

            // Obtener última asistencia del cliente
            List<Asistencias> historial = asistenciaRepository.findByCliente(cliente.getDocumento());

            if (historial.isEmpty()) {
                // Cliente nunca ha asistido
                System.out.println("Cliente sin asistencias: " + cliente.getNombreCompleto());

                // Verificar si la membresía tiene más de 7 días
                long diasDesdeLaAsignacion = ChronoUnit.DAYS.between(
                        membresia.getFechaAsignacion(),
                        LocalDate.now()
                );

                if (diasDesdeLaAsignacion >= DIAS_INACTIVIDAD) {
                    boolean enviado = notificacionService.enviarNotificacion(
                            "INACTIVIDAD_7_DIAS",
                            cliente,
                            membresia,
                            null
                    );

                    if (enviado) {
                        notificacionesEnviadas++;
                    }
                }

            } else {
                // Obtener la asistencia más reciente
                Optional<Asistencias> ultimaAsistenciaOpt = historial.stream()
                        .max((a1, a2) -> a1.getFecha().compareTo(a2.getFecha()));

                if (ultimaAsistenciaOpt.isPresent()) {
                    LocalDate ultimaAsistencia = ultimaAsistenciaOpt.get().getFecha();
                    long diasInactivo = ChronoUnit.DAYS.between(ultimaAsistencia, LocalDate.now());

                    // Notificar si está inactivo exactamente 7 días (para no enviar todos los días)
                    if (diasInactivo == DIAS_INACTIVIDAD) {
                        System.out.println(" Cliente inactivo " + diasInactivo + " días: " +
                                cliente.getNombreCompleto() +
                                " (última: " + ultimaAsistencia + ")");

                        boolean enviado = notificacionService.enviarNotificacion(
                                "INACTIVIDAD_7_DIAS",
                                cliente,
                                membresia,
                                ultimaAsistencia
                        );

                        if (enviado) {
                            notificacionesEnviadas++;
                        }
                    }
                }
            }
        }

        System.out.println("\nResumen de revisión:");
        System.out.println("  • Clientes revisados: " + clientesRevisados);
        System.out.println("  • Notificaciones enviadas: " + notificacionesEnviadas);
    }

    /**
     * Metodo para ejecutar la revisión manualmente (útil para testing).
     */
    public void ejecutarRevisionManual() {
        System.out.println("Ejecutando revisión manual de inactividad...");
        ejecutarRevision();
    }
}