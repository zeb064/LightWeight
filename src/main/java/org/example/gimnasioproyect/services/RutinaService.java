package org.example.gimnasioproyect.services;

import org.example.gimnasioproyect.Utilidades.Constantes;
import org.example.gimnasioproyect.Utilidades.EstadoRutina;
import org.example.gimnasioproyect.Utilidades.Validador;
import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.model.DetalleRutinas;
import org.example.gimnasioproyect.model.RutinaAsignadas;
import org.example.gimnasioproyect.model.Rutinas;
import org.example.gimnasioproyect.repository.ClienteRepository;
import org.example.gimnasioproyect.repository.DetalleRutinaRepository;
import org.example.gimnasioproyect.repository.RutinaAsignadaRepository;
import org.example.gimnasioproyect.repository.RutinaRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RutinaService {
    private final RutinaRepository rutinaRepository;
    private final DetalleRutinaRepository detalleRutinaRepository;
    private final RutinaAsignadaRepository rutinaAsignadaRepository;
    private final ClienteRepository clienteRepository;
    private final NotificacionService notificacionService;

    public RutinaService(RutinaRepository rutinaRepository,
                         DetalleRutinaRepository detalleRutinaRepository,
                         RutinaAsignadaRepository rutinaAsignadaRepository,
                         ClienteRepository clienteRepository,
                         NotificacionService notificacionService) {
        this.rutinaRepository = rutinaRepository;
        this.detalleRutinaRepository = detalleRutinaRepository;
        this.rutinaAsignadaRepository = rutinaAsignadaRepository;
        this.clienteRepository = clienteRepository;
        this.notificacionService = notificacionService;
    }

    //Crea una nueva rutina
    public Rutinas crearRutina(String objetivo) throws SQLException {
        Validador.validarTexto(objetivo, "Objetivo", 30, true);

        Rutinas rutina = new Rutinas();
        rutina.setObjetivo(objetivo);

        rutinaRepository.save(rutina);
        return rutina;
    }

    //Agrega detalles a una rutina existente
    public void agregarDetalleRutina(Integer idRutina, String diaSemana, Integer orden,
                                     String ejercicio, Integer series, Integer repeticiones,
                                     Double peso, String notas) throws SQLException {
        // Validaciones
        if (idRutina == null) {
            throw new IllegalArgumentException("El ID de la rutina es obligatorio");
        }

        // Verificar que existe la rutina
        Optional<Rutinas> rutinaOpt = rutinaRepository.findById(idRutina);
        if (!rutinaOpt.isPresent()) {
            throw new IllegalArgumentException("No existe una rutina con el ID: " + idRutina);
        }

        Validador.validarTexto(diaSemana, "Día de la semana", 10, true);
        Validador.validarTexto(ejercicio, "Ejercicio", 15, true);
        Validador.validarNumeroPositivo(orden, "Orden");
        Validador.validarNumeroPositivo(series, "Series");
        Validador.validarNumeroPositivo(repeticiones, "Repeticiones");
        Validador.validarNumeroPositivo(peso, "Peso");

        DetalleRutinas detalle = new DetalleRutinas();
        detalle.setRutina(rutinaOpt.get());
        detalle.setDiaSemana(diaSemana.toUpperCase());
        detalle.setOrden(orden);
        detalle.setEjercicio(ejercicio);
        detalle.setSeries(series);
        detalle.setRepeticiones(repeticiones);
        detalle.setPeso(peso);
        detalle.setNotas(notas);

        detalleRutinaRepository.save(detalle);
    }

    //Asigna una rutina a un cliente
    public void asignarRutinaACliente(Integer idRutina, String documento, LocalDate fechaInicio) throws SQLException {
        // Validaciones
        if (idRutina == null) {
            throw new IllegalArgumentException("El ID de la rutina es obligatorio");
        }
        Validador.validarDocumento(documento);

        if (fechaInicio == null) {
            fechaInicio = LocalDate.now();
        }

        // Verificar que existe la rutina
        Optional<Rutinas> rutinaOpt = rutinaRepository.findById(idRutina);
        if (!rutinaOpt.isPresent()) {
            throw new IllegalArgumentException("No existe una rutina con el ID: " + idRutina);
        }

        // Verificar que existe el cliente
        Optional<Clientes> clienteOpt = clienteRepository.findByDocumento(documento);
        if (!clienteOpt.isPresent()) {
            throw new IllegalArgumentException("No existe un cliente con el documento: " + documento);
        }

        // Crear la asignación
        RutinaAsignadas rutinaAsignada = new RutinaAsignadas();
        rutinaAsignada.setRutina(rutinaOpt.get());
        rutinaAsignada.setCliente(clienteOpt.get());
        rutinaAsignada.setFechaAsignacion(fechaInicio);
        rutinaAsignada.setEstado(Constantes.RUTINA_ACTIVA);

        try {
            if (clienteOpt.get().getChatId() != null && !clienteOpt.get().getChatId().trim().isEmpty()) {
                notificacionService.enviarNotificacion(
                        "RUTINA_ACTUALIZADA",
                        clienteOpt.get(),
                        null,
                        rutinaAsignada
                );
            }
        } catch (Exception e) {
            System.err.println("Error al enviar notificación de rutina: " + e.getMessage());
        }

        rutinaAsignadaRepository.save(rutinaAsignada);
    }

    //Cambiar el estado de una rutina asignada
    public void cambiarEstadoRutina(Integer idRutinaCliente, String nuevoEstado) throws SQLException {
        if (idRutinaCliente == null) {
            throw new IllegalArgumentException("El ID de rutina cliente es obligatorio");
        }

        // Validar estado usando el enum
        EstadoRutina estadoNuevo = EstadoRutina.from(nuevoEstado);

        Optional<RutinaAsignadas> rutinaOpt = rutinaAsignadaRepository.findById(idRutinaCliente);
        if (!rutinaOpt.isPresent()) {
            throw new IllegalArgumentException("No existe la rutina asignada");
        }

        RutinaAsignadas rutina = rutinaOpt.get();

        // Validar que la transición sea válida
        EstadoRutina estadoActual = EstadoRutina.from(rutina.getEstado());
        if (!estadoActual.puedeTransicionarA(estadoNuevo)) {
            throw new IllegalArgumentException(
                    String.format("No se puede cambiar de %s a %s", estadoActual, estadoNuevo)
            );
        }

        rutina.setEstado(estadoNuevo.name());
        // Manejar fecha de finalización si es necesario
        if (estadoNuevo.requiereFechaFinalizacion()) {
            rutina.setFechaFinalizacion(LocalDate.now());
        } else if (estadoNuevo == EstadoRutina.ACTIVA && rutina.getFechaFinalizacion() != null) {
            // Si se reactiva, limpiar fecha de finalización
            rutina.setFechaFinalizacion(null);
        }

        rutinaAsignadaRepository.update(rutina);
    }

    //Obtiene todas las rutinas disponibles
    public List<Rutinas> obtenerTodasLasRutinas() throws SQLException {
        return rutinaRepository.findAll();
    }

    //Obtiene los detalles de una rutina específica
    public List<DetalleRutinas> obtenerDetallesRutina(Integer idRutina) throws SQLException {
        if (idRutina == null) {
            throw new IllegalArgumentException("El ID de la rutina es obligatorio");
        }
        return detalleRutinaRepository.findByRutina(idRutina);
    }

    //Obtiene las rutinas activas de un cliente
    public List<RutinaAsignadas> obtenerRutinasActivasCliente(String documentoCliente) throws SQLException {
        Validador.validarDocumento(documentoCliente);
        return rutinaAsignadaRepository.findRutinasActivasByCliente(documentoCliente);
    }

    //Obtiene el historial de rutinas asignadas a un cliente
    public List<RutinaAsignadas> obtenerHistorialRutinas(String documentoCliente) throws SQLException {
        Validador.validarDocumento(documentoCliente);
        return rutinaAsignadaRepository.findByCliente(documentoCliente);
    }

    //Actualiza un detalle de rutina existente
    public void actualizarDetalleRutina(DetalleRutinas detalle) throws SQLException {
        if (detalle == null) {
            throw new IllegalArgumentException("El detalle de rutina no puede ser nulo");
        }

        if (detalle.getIdDetalle() == null) {
            throw new IllegalArgumentException("El ID del detalle es obligatorio");
        }

        // Validaciones
        Validador.validarTexto(detalle.getDiaSemana(), "Día de la semana", 10, true);
        Validador.validarTexto(detalle.getEjercicio(), "Ejercicio", 15, true);
        Validador.validarNumeroPositivo(detalle.getOrden(), "Orden");
        Validador.validarNumeroPositivo(detalle.getSeries(), "Series");
        Validador.validarNumeroPositivo(detalle.getRepeticiones(), "Repeticiones");
        Validador.validarNumeroPositivo(detalle.getPeso(), "Peso");

        detalleRutinaRepository.update(detalle);
    }

    //Elimina un detalle de rutina
    public void eliminarDetalleRutina(Integer idDetalle) throws SQLException {
        if (idDetalle == null) {
            throw new IllegalArgumentException("El ID del detalle es obligatorio");
        }

        // Verificar que existe
        Optional<DetalleRutinas> detalleOpt = detalleRutinaRepository.findById(idDetalle);
        if (!detalleOpt.isPresent()) {
            throw new IllegalArgumentException("No existe el detalle de rutina con ID: " + idDetalle);
        }

        detalleRutinaRepository.delete(idDetalle);
    }

    //Obtiene un detalle de rutina por su ID
    public Optional<DetalleRutinas> obtenerDetallePorId(Integer idDetalle) throws SQLException {
        if (idDetalle == null) {
            throw new IllegalArgumentException("El ID del detalle es obligatorio");
        }
        return detalleRutinaRepository.findById(idDetalle);
    }

    //Elimina una rutina del sistema
    public void eliminarRutina(Integer idRutina) throws SQLException {
        if (idRutina == null) {
            throw new IllegalArgumentException("El ID de la rutina es obligatorio");
        }

        // Verificar si está asignada
        List<RutinaAsignadas> asignaciones = rutinaAsignadaRepository.findByRutina(idRutina);
        if (!asignaciones.isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar. La rutina está asignada a clientes");
        }

        // Eliminar detalles primero
        detalleRutinaRepository.deleteByRutina(idRutina);

        // Eliminar rutina
        rutinaRepository.delete(idRutina);
    }

    //Obtiene todos los estados disponibles para una rutina asignada
    public List<String> obtenerEstadosDisponibles() {
        return Arrays.stream(EstadoRutina.values())
                .map(EstadoRutina::name)
                .collect(Collectors.toList());
    }

    //Obtiene los estados disponibles para una rutina asignada específica
    public List<String> obtenerEstadosDisponiblesParaRutina(Integer idRutinaCliente) throws SQLException {
        Optional<RutinaAsignadas> rutinaOpt = rutinaAsignadaRepository.findById(idRutinaCliente);

        if (!rutinaOpt.isPresent()) {
            return Collections.emptyList();
        }

        EstadoRutina estadoActual = EstadoRutina.from(rutinaOpt.get().getEstado());

        return Arrays.stream(EstadoRutina.values())
                .filter(estadoActual::puedeTransicionarA)
                .map(EstadoRutina::name)
                .collect(Collectors.toList());
    }
}
