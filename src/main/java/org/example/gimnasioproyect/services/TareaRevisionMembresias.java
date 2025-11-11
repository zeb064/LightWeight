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

public class TareaRevisionMembresias {
    private final MembresiaClienteRepository membresiaClienteRepository;
    private final NotificacionService notificacionService;
    private final TelegramConfig config;
    private ScheduledExecutorService scheduler;

    public TareaRevisionMembresias(MembresiaClienteRepository membresiaClienteRepository,
                                   NotificacionService notificacionService) {
        this.membresiaClienteRepository = membresiaClienteRepository;
        this.notificacionService = notificacionService;
        this.config = TelegramConfig.getInstance();
    }

    //Inicia la tarea programada
    public void iniciar() {
        if (scheduler != null && !scheduler.isShutdown()) {
            System.out.println("⚠️ La tarea de revisión ya está en ejecución");
            return;
        }

        scheduler = Executors.newScheduledThreadPool(1);

        // Calcular el delay inicial hasta la próxima ejecución
        long delayInicial = calcularDelayHastaProximaEjecucion();

        System.out.println("🕐 Tarea de revisión de membresías programada a las " +
                config.getRevisionHora() + ":" +
                String.format("%02d", config.getRevisionMinuto()) + " cada día");

        // Programar la tarea
        scheduler.scheduleAtFixedRate(
                this::ejecutarRevision,
                delayInicial,
                TimeUnit.DAYS.toSeconds(1), // Se ejecuta cada día
                TimeUnit.SECONDS
        );

        System.out.println("✅ Tarea de revisión iniciada. Próxima ejecución en " +
                (delayInicial / 3600) + " horas");
    }

    // Detiene la tarea programada
    public void detener() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            System.out.println("⏹️ Tarea de revisión detenida");
        }
    }

    // Calcula el delay en segundos hasta la próxima ejecución programada
    private long calcularDelayHastaProximaEjecucion() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalTime horaEjecucion = LocalTime.of(config.getRevisionHora(), config.getRevisionMinuto());

        LocalDateTime proximaEjecucion = ahora.with(horaEjecucion);

        // Si la hora ya pasó hoy, programar para mañana
        if (ahora.isAfter(proximaEjecucion)) {
            proximaEjecucion = proximaEjecucion.plusDays(1);
        }

        return ChronoUnit.SECONDS.between(ahora, proximaEjecucion);
    }

    // Ejecuta la revisión de membresías
    private void ejecutarRevision() {
        System.out.println("\n🔍 ===============================================");
        System.out.println("🔍 Iniciando revisión de membresías - " + LocalDateTime.now());
        System.out.println("🔍 ===============================================\n");

        try {
            int diasAnticipacion = config.getDiasVencimientoProximo();

            // 1. Revisar membresías próximas a vencer
            revisarMembresiasProximasAVencer(diasAnticipacion);

            // 2. Revisar membresías vencidas
            revisarMembresiasVencidas();

            System.out.println("\n✅ Revisión completada exitosamente\n");

        } catch (Exception e) {
            System.err.println("❌ Error durante la revisión: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Revisa y notifica membresías próximas a vencer
    private void revisarMembresiasProximasAVencer(int dias) throws SQLException {
        System.out.println("📅 Revisando membresías que vencen en " + dias + " días...");

        List<MembresiaClientes> membresias = membresiaClienteRepository.findMembresiasProximasAVencer(dias);

        int notificacionesEnviadas = 0;

        for (MembresiaClientes membresia : membresias) {
            Clientes cliente = membresia.getCliente();

            // Calcular días exactos restantes
            long diasRestantes = CalculadoraFechas.calcularDiasRestantes(membresia.getFechaFinalizacion());

            // Solo notificar si está exactamente en el día configurado
            if (diasRestantes == dias) {
                System.out.println("  ⚠️ Notificando a: " + cliente.getNombreCompleto() +
                        " (vence en " + diasRestantes + " días)");

                boolean enviado = notificacionService.enviarMensajeVencimientoProximo(
                        cliente, membresia, diasRestantes
                );

                if (enviado) {
                    notificacionesEnviadas++;
                }
            }
        }

        System.out.println("✅ Notificaciones de vencimiento próximo enviadas: " + notificacionesEnviadas);
    }

    // Revisa y notifica membresías vencidas
    private void revisarMembresiasVencidas() throws SQLException {
        System.out.println("❌ Revisando membresías vencidas...");

        List<MembresiaClientes> membresiasVencidas = membresiaClienteRepository.findMembresiasVencidas();

        int notificacionesEnviadas = 0;

        for (MembresiaClientes membresia : membresiasVencidas) {
            Clientes cliente = membresia.getCliente();

            // Solo notificar si venció exactamente hoy
            if (membresia.getFechaFinalizacion() != null &&
                    membresia.getFechaFinalizacion().equals(LocalDate.now())) {

                System.out.println("  ❌ Notificando vencimiento a: " + cliente.getNombreCompleto());

                boolean enviado = notificacionService.enviarMensajeVencido(cliente, membresia);

                if (enviado) {
                    notificacionesEnviadas++;
                }
            }
        }

        System.out.println("✅ Notificaciones de vencimiento enviadas: " + notificacionesEnviadas);
    }

    // Método para ejecutar la revisión manualmente
    public void ejecutarRevisionManual() {
        System.out.println("🔧 Ejecutando revisión manual...");
        ejecutarRevision();
    }
}