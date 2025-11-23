package org.example.gimnasioproyect.services;

import org.example.gimnasioproyect.Utilidades.CalculadoraFechas;
import org.example.gimnasioproyect.Utilidades.TelegramConfig;
import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.model.MembresiaClientes;
import org.example.gimnasioproyect.repository.MembresiaClienteRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Tarea programada para revisar membresías y enviar notificaciones automáticas.
 * <p>
 * Esta clase se encarga de ejecutar una revisión diaria de las membresías de los clientes,
 * identificando aquellas que están próximas a vencer o que ya han vencido, y enviando
 * las notificaciones correspondientes a través del servicio de notificaciones.
 * <p>
 * La tarea se ejecuta automáticamente en un horario configurado mediante {@link TelegramConfig}.
 *
 * @author Sistema de Gestión de Gimnasio
 * @version 1.0
 */
public class TareaRevisionMembresias {
    private final MembresiaClienteRepository membresiaClienteRepository;
    private final NotificacionService notificacionService;
    private final TelegramConfig config;
    private ScheduledExecutorService scheduler;

    /**
     * Constructor de la tarea de revisión de membresías.
     *
     * @param membresiaClienteRepository Repositorio para acceder a las membresías de los clientes
     * @param notificacionService Servicio para enviar notificaciones a los clientes
     */
    public TareaRevisionMembresias(MembresiaClienteRepository membresiaClienteRepository,
                                   NotificacionService notificacionService) {
        this.membresiaClienteRepository = membresiaClienteRepository;
        this.notificacionService = notificacionService;
        this.config = TelegramConfig.getInstance();
    }

    /**
     * Inicia la tarea programada de revisión de membresías.
     * <p>
     * La tarea se ejecutará diariamente en el horario configurado en {@link TelegramConfig}.
     * Si la tarea ya está en ejecución, se mostrará un mensaje de advertencia y no se
     * iniciará una nueva instancia.
     * <p>
     * La primera ejecución se programa calculando el tiempo restante hasta la próxima
     * hora de ejecución configurada. Las ejecuciones subsecuentes ocurren cada 24 horas.
     */
    public void iniciar() {
        if (scheduler != null && !scheduler.isShutdown()) {
            System.out.println("La tarea de revisión ya está en ejecución");
            return;
        }

        scheduler = Executors.newScheduledThreadPool(1);

        long delayInicial = calcularDelayHastaProximaEjecucion();

        System.out.println("Tarea de revisión de membresías programada a las " +
                config.getRevisionHora() + ":" +
                String.format("%02d", config.getRevisionMinuto()) + " cada día");

        scheduler.scheduleAtFixedRate(
                this::ejecutarRevision,
                delayInicial,
                TimeUnit.DAYS.toSeconds(1),
                TimeUnit.SECONDS
        );

        System.out.println("✅ Tarea de revisión iniciada. Próxima ejecución en " +
                (delayInicial / 3600) + " horas");
    }

    /**
     * Detiene la tarea programada de revisión de membresías.
     * <p>
     * Si la tarea está en ejecución, se detendrá el scheduler de forma ordenada.
     * Si la tarea no está activa, no se realizará ninguna acción.
     */
    public void detener() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            System.out.println("Tarea de revisión detenida");
        }
    }

    /**
     * Calcula el tiempo en segundos hasta la próxima ejecución programada.
     * <p>
     * El cálculo se basa en la hora y minuto configurados en {@link TelegramConfig}.
     * Si la hora de ejecución ya pasó en el día actual, se programa para el día siguiente.
     *
     * @return Número de segundos hasta la próxima ejecución programada
     */
    private long calcularDelayHastaProximaEjecucion() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalTime horaEjecucion = LocalTime.of(config.getRevisionHora(), config.getRevisionMinuto());

        LocalDateTime proximaEjecucion = ahora.with(horaEjecucion);

        if (ahora.isAfter(proximaEjecucion)) {
            proximaEjecucion = proximaEjecucion.plusDays(1);
        }

        return ChronoUnit.SECONDS.between(ahora, proximaEjecucion);
    }

    /**
     * Ejecuta la revisión completa de membresías.
     * <p>
     * Este método coordina dos tipos de revisiones:
     * <ul>
     *   <li>Revisión de membresías próximas a vencer</li>
     *   <li>Revisión de membresías que vencieron hoy</li>
     * </ul>
     * <p>
     * En caso de error durante la ejecución, se captura la excepción y se registra
     * en la consola sin interrumpir futuras ejecuciones programadas.
     */
    private void ejecutarRevision() {
        System.out.println("\n===============================================");
        System.out.println("Iniciando revisión de membresías - " + LocalDateTime.now());
        System.out.println("===============================================\n");

        try {
            int diasAnticipacion = config.getDiasVencimientoProximo();

            revisarMembresiasProximasAVencer(diasAnticipacion);
            revisarMembresiasVencidas();

            System.out.println("\nRevisión completada exitosamente\n");

        } catch (Exception e) {
            System.err.println("Error durante la revisión: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Revisa y notifica las membresías que están próximas a vencer.
     * <p>
     * Busca todas las membresías que vencerán en exactamente el número de días
     * especificado por el parámetro y envía notificaciones a los clientes correspondientes.
     * <p>
     * Solo se envían notificaciones para membresías que vencen exactamente en la
     * cantidad de días configurada, evitando notificaciones duplicadas.
     *
     * @param dias Número de días de anticipación para considerar una membresía
     *             como próxima a vencer (obtenido de la configuración)
     * @throws SQLException Si ocurre un error al acceder a la base de datos
     */
    private void revisarMembresiasProximasAVencer(int dias) throws SQLException {
        System.out.println("📋 Revisando membresías que vencen en " + dias + " días...");

        List<MembresiaClientes> membresias = membresiaClienteRepository.findMembresiasProximasAVencer(dias);

        int notificacionesEnviadas = 0;

        for (MembresiaClientes membresia : membresias) {
            Clientes cliente = membresia.getCliente();

            long diasRestantes = CalculadoraFechas.calcularDiasRestantes(membresia.getFechaFinalizacion());

            if (diasRestantes == dias) {
                System.out.println("Notificando a: " + cliente.getNombreCompleto() +
                        " (vence en " + diasRestantes + " días)");

                boolean enviado = notificacionService.enviarNotificacion(
                        "VENCE_PRONTO",
                        cliente,
                        membresia,
                        diasRestantes
                );

                if (enviado) {
                    notificacionesEnviadas++;
                }
            }
        }

        System.out.println("Notificaciones de vencimiento próximo enviadas: " + notificacionesEnviadas);
    }

    /**
     * Revisa y notifica las membresías que han vencido hoy.
     * <p>
     * Busca todas las membresías vencidas y envía notificaciones únicamente
     * para aquellas cuya fecha de finalización coincide con la fecha actual,
     * evitando notificaciones repetidas para membresías que vencieron en días anteriores.
     *
     * @throws SQLException Si ocurre un error al acceder a la base de datos
     */
    private void revisarMembresiasVencidas() throws SQLException {
        System.out.println("Revisando membresías vencidas...");

        List<MembresiaClientes> membresiasVencidas = membresiaClienteRepository.findMembresiasVencidas();

        int notificacionesEnviadas = 0;

        for (MembresiaClientes membresia : membresiasVencidas) {
            Clientes cliente = membresia.getCliente();

            if (membresia.getFechaFinalizacion() != null &&
                    membresia.getFechaFinalizacion().equals(LocalDate.now())) {

                System.out.println("Notificando vencimiento a: " + cliente.getNombreCompleto());

                boolean enviado = notificacionService.enviarNotificacion(
                        "VENCIDO",
                        cliente,
                        membresia,
                        null
                );

                if (enviado) {
                    notificacionesEnviadas++;
                }
            }
        }

        System.out.println("✅ Notificaciones de vencimiento enviadas: " + notificacionesEnviadas);
    }

    /**
     * Ejecuta la revisión de membresías de forma manual.
     * <p>
     * Este método permite realizar una revisión inmediata sin esperar a la
     * ejecución programada. Es útil para realizar pruebas o verificar el
     * funcionamiento del sistema.
     */
    public void ejecutarRevisionManual() {
        System.out.println("Ejecutando revisión manual...");
        ejecutarRevision();
    }
}