package org.example.gimnasioproyect.services;

import org.example.gimnasioproyect.Utilidades.Validador;
import org.example.gimnasioproyect.model.AsignacionEntrenadores;
import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.model.Entrenadores;
import org.example.gimnasioproyect.repository.AsignacionEntrenadorRepository;
import org.example.gimnasioproyect.repository.ClienteRepository;
import org.example.gimnasioproyect.repository.EntrenadorRepository;
import org.example.gimnasioproyect.repository.PersonalRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EntrenadorService {
    private final EntrenadorRepository entrenadorRepository;
    private final AsignacionEntrenadorRepository asignacionRepository;
    private final ClienteRepository clienteRepository;
    private final PersonalRepository personalRepository;
    private final NotificacionService notificacionService;

    public EntrenadorService(EntrenadorRepository entrenadorRepository,
                             AsignacionEntrenadorRepository asignacionRepository,
                             ClienteRepository clienteRepository,
                             PersonalRepository personalRepository,
                             NotificacionService notificacionService) {
        this.entrenadorRepository = entrenadorRepository;
        this.asignacionRepository = asignacionRepository;
        this.clienteRepository = clienteRepository;
        this.personalRepository = personalRepository;
        this.notificacionService = notificacionService;
    }

    // Registra un nuevo entrenador en el sistema
    public void registrarEntrenador(Entrenadores entrenador) throws SQLException {
        validarDatosEntrenador(entrenador);

        // Verificar que no exista el documento
        if (entrenadorRepository.findByDocumento(entrenador.getDocuEntrenador()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un entrenador con el documento: " +
                    entrenador.getDocuEntrenador());
        }

        // Verificar que el usuario no exista
        if (personalRepository.existeUsuario(entrenador.getUsuarioSistema())) {
            throw new IllegalArgumentException("El usuario ya existe: " + entrenador.getUsuarioSistema());
        }

        // Establecer fecha de contratación si no tiene
        if (entrenador.getFechaContratacion() == null) {
            entrenador.setFechaContratacion(LocalDate.now());
        }

        entrenadorRepository.save(entrenador);
    }

    // Actualiza los datos de un entrenador existente
    public void actualizarEntrenador(Entrenadores entrenador) throws SQLException {
        validarDatosEntrenador(entrenador);

        // Verificar que exista
        if (!entrenadorRepository.findByDocumento(entrenador.getDocuEntrenador()).isPresent()) {
            throw new IllegalArgumentException("No existe el entrenador con documento: " +
                    entrenador.getDocuEntrenador());
        }

        entrenadorRepository.update(entrenador);
    }

    // Busca un entrenador por su documento
    public Optional<Entrenadores> buscarEntrenadorPorDocumento(String documento) throws SQLException {
        Validador.validarDocumento(documento);
        return entrenadorRepository.findByDocumento(documento);
    }

    // Obtiene todos los entrenadores
    public List<Entrenadores> obtenerTodosLosEntrenadores() throws SQLException {
        return entrenadorRepository.findAll();
    }

    // Busca entrenadores por especialidad
    public List<Entrenadores> buscarPorEspecialidad(String especialidad) throws SQLException {
        if (especialidad == null || especialidad.trim().isEmpty()) {
            throw new IllegalArgumentException("La especialidad no puede estar vacía");
        }
        return entrenadorRepository.findByEspecialidad(especialidad);
    }

    // Asigna un entrenador a un cliente
    public void asignarEntrenadorACliente(String documentoEntrenador,
                                          String documentoCliente) throws SQLException {
        Validador.validarDocumento(documentoEntrenador);
        Validador.validarDocumento(documentoCliente);

        // Verificar que existe el entrenador
        Optional<Entrenadores> entrenadorOpt = entrenadorRepository.findByDocumento(documentoEntrenador);
        if (!entrenadorOpt.isPresent()) {
            throw new IllegalArgumentException("No existe el entrenador con documento: " + documentoEntrenador);
        }

        // Verificar que existe el cliente
        Optional<Clientes> clienteOpt = clienteRepository.findByDocumento(documentoCliente);
        if (!clienteOpt.isPresent()) {
            throw new IllegalArgumentException("No existe el cliente con documento: " + documentoCliente);
        }

        // Verificar que el cliente no tenga un entrenador activo
        Optional<AsignacionEntrenadores> asignacionActivaOpt =
                asignacionRepository.findAsignacionActivaByCliente(documentoCliente);

        if (asignacionActivaOpt.isPresent()) {
            throw new IllegalArgumentException("El cliente ya tiene un entrenador asignado");
        }

        // Crear asignación
        AsignacionEntrenadores asignacion = new AsignacionEntrenadores();
        asignacion.setEntrenador(entrenadorOpt.get());
        asignacion.setCliente(clienteOpt.get());
        asignacion.setFechaAsignacion(LocalDate.now());

        try {
            if (clienteOpt.get().getChatId() != null && !clienteOpt.get().getChatId().trim().isEmpty()) {
                notificacionService.enviarNotificacion(
                        "NUEVO_ENTRENADOR",
                        clienteOpt.get(),
                        null,
                        asignacion
                );
            }
        } catch (Exception e) {
            System.err.println("⚠Error al enviar notificación de entrenador: " + e.getMessage());
        }

        asignacionRepository.save(asignacion);
    }

    // Finaliza la asignación de un entrenador a un cliente
    public void finalizarAsignacion(String documentoCliente) throws SQLException {
        Validador.validarDocumento(documentoCliente);

        Optional<AsignacionEntrenadores> asignacionOpt =
                asignacionRepository.findAsignacionActivaByCliente(documentoCliente);

        if (!asignacionOpt.isPresent()) {
            throw new IllegalArgumentException("El cliente no tiene un entrenador asignado");
        }

        AsignacionEntrenadores asignacion = asignacionOpt.get();
        asignacion.setFechaFinalizacion(LocalDate.now());

        asignacionRepository.update(asignacion);
    }

    // Obtiene los clientes activos asignados a un entrenador
    public List<AsignacionEntrenadores> obtenerClientesActivos(String documentoEntrenador) throws SQLException {
        Validador.validarDocumento(documentoEntrenador);
        return asignacionRepository.findClientesActivosByEntrenador(documentoEntrenador);
    }

    // Obtiene el historial de clientes asignados a un entrenador
    public List<AsignacionEntrenadores> obtenerHistorialClientes(String documentoEntrenador) throws SQLException {
        Validador.validarDocumento(documentoEntrenador);
        return asignacionRepository.findByEntrenador(documentoEntrenador);
    }

    // Obtiene el entrenador asignado a un cliente
    public Optional<AsignacionEntrenadores> obtenerEntrenadorDeCliente(String documentoCliente) throws SQLException {
        Validador.validarDocumento(documentoCliente);
        return asignacionRepository.findAsignacionActivaByCliente(documentoCliente);
    }

    // Elimina un entrenador del sistema
    public void eliminarEntrenador(String documento) throws SQLException {
        //Validador.validarDocumento(documento);

        // Verificar que no tenga clientes activos
        List<AsignacionEntrenadores> clientesActivos =
                asignacionRepository.findClientesActivosByEntrenador(documento);

        if (!clientesActivos.isEmpty()) {
            throw new IllegalArgumentException(
                    "No se puede eliminar. El entrenador tiene " + clientesActivos.size() + " cliente(s) activo(s)"
            );
        }

        entrenadorRepository.delete(documento);
    }

    // Valida los datos de un entrenador
    private void validarDatosEntrenador(Entrenadores entrenador) {
        if (entrenador == null) {
            throw new IllegalArgumentException("El entrenador no puede ser nulo");
        }

        Validador.validarDocumento(entrenador.getDocuEntrenador());
        Validador.validarNombre(entrenador.getNombres(), "Nombres");
        Validador.validarNombre(entrenador.getApellidos(), "Apellidos");
        Validador.validarTelefono(entrenador.getTelefono());
        Validador.validarCorreo(entrenador.getCorreo());
        Validador.validarUsuarioSistema(entrenador.getUsuarioSistema());
        Validador.validarContrasena(entrenador.getContrasena());
        Validador.validarTexto(entrenador.getEspecialidad(), "Especialidad", 20, true);
        Validador.validarNumeroPositivo(entrenador.getExperiencia(), "Experiencia");
    }
}
