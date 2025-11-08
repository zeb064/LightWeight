package org.example.gimnasioproyect.services;

import org.example.gimnasioproyect.Utilidades.Validador;
import org.example.gimnasioproyect.model.Recepcionistas;
import org.example.gimnasioproyect.repository.PersonalRepository;
import org.example.gimnasioproyect.repository.RecepcionistaRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class RecepcionistaService {
    private final RecepcionistaRepository recepcionistaRepository;
    private final PersonalRepository personalRepository;

    public RecepcionistaService(RecepcionistaRepository recepcionistaRepository,
                                PersonalRepository personalRepository) {
        this.recepcionistaRepository = recepcionistaRepository;
        this.personalRepository = personalRepository;
    }

    // Registra un nuevo recepcionista en el sistema
    public void registrarRecepcionista(Recepcionistas recepcionista) throws SQLException {
        validarDatosRecepcionista(recepcionista);

        // Verificar que no exista el documento
        if (recepcionistaRepository.findByDocumento(recepcionista.getDocuRecepcionista()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un recepcionista con el documento: " +
                    recepcionista.getDocuRecepcionista());
        }

        // Verificar que el usuario no exista
        if (personalRepository.existeUsuario(recepcionista.getUsuarioSistema())) {
            throw new IllegalArgumentException("El usuario ya existe: " + recepcionista.getUsuarioSistema());
        }

        // Establecer fecha de contratación si no tiene
        if (recepcionista.getFechaContratacion() == null) {
            recepcionista.setFechaContratacion(LocalDate.now());
        }

        recepcionistaRepository.save(recepcionista);
    }

    // Actualiza los datos de un recepcionista
    public void actualizarRecepcionista(Recepcionistas recepcionista) throws SQLException {
        validarDatosRecepcionista(recepcionista);

        // Verificar que exista
        if (!recepcionistaRepository.findByDocumento(recepcionista.getDocuRecepcionista()).isPresent()) {
            throw new IllegalArgumentException("No existe el recepcionista con documento: " +
                    recepcionista.getDocuRecepcionista());
        }

        recepcionistaRepository.update(recepcionista);
    }

    // Busca un recepcionista por documento
    public Optional<Recepcionistas> buscarRecepcionistaPorDocumento(String documento) throws SQLException {
        Validador.validarDocumento(documento);
        return recepcionistaRepository.findByDocumento(documento);
    }

    // Busca un recepcionista por usuario
    public Optional<Recepcionistas> buscarRecepcionistaPorUsuario(String usuario) throws SQLException {
        if (usuario == null || usuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }
        return recepcionistaRepository.findByUsuario(usuario);
    }

    // Obtiene todos los recepcionistas
    public List<Recepcionistas> obtenerTodosLosRecepcionistas() throws SQLException {
        return recepcionistaRepository.findAll();
    }

    // Busca recepcionistas por turno
    public List<Recepcionistas> buscarPorTurno(String turno) throws SQLException {
        if (turno == null || turno.trim().isEmpty()) {
            throw new IllegalArgumentException("El turno es obligatorio");
        }
        return recepcionistaRepository.findByTurno(turno);
    }

    // Actualiza el turno de un recepcionista
    public void actualizarTurno(String documentoRecepcionista, String nuevoTurno) throws SQLException {
        Validador.validarDocumento(documentoRecepcionista);
        Validador.validarTexto(nuevoTurno, "Turno", 10, true);

        Optional<Recepcionistas> recepcionistaOpt =
                recepcionistaRepository.findByDocumento(documentoRecepcionista);

        if (!recepcionistaOpt.isPresent()) {
            throw new IllegalArgumentException("No existe el recepcionista");
        }

        Recepcionistas recepcionista = recepcionistaOpt.get();
        recepcionista.setHorarioTurno(nuevoTurno);

        recepcionistaRepository.update(recepcionista);
    }

    // Elimina un recepcionista por documento
    public void eliminarRecepcionista(String documento) throws SQLException {
        Validador.validarDocumento(documento);

        // Verificar que existe
        if (!recepcionistaRepository.findByDocumento(documento).isPresent()) {
            throw new IllegalArgumentException("No existe el recepcionista con documento: " + documento);
        }

        recepcionistaRepository.delete(documento);
    }

    // Obtiene los recepcionistas disponibles por turno
    public List<Recepcionistas> obtenerRecepcionistasDisponiblesPorTurno(String turno) throws SQLException {
        return buscarPorTurno(turno);
    }

    // Valida los datos de un recepcionista
    private void validarDatosRecepcionista(Recepcionistas recepcionista) {
        if (recepcionista == null) {
            throw new IllegalArgumentException("El recepcionista no puede ser nulo");
        }

        Validador.validarDocumento(recepcionista.getDocuRecepcionista());
        Validador.validarNombre(recepcionista.getNombres(), "Nombres");
        Validador.validarNombre(recepcionista.getApellidos(), "Apellidos");
        Validador.validarTelefono(recepcionista.getTelefono());
        Validador.validarCorreo(recepcionista.getCorreo());
        Validador.validarUsuarioSistema(recepcionista.getUsuarioSistema());
        Validador.validarContrasena(recepcionista.getContrasena());
        Validador.validarTexto(recepcionista.getHorarioTurno(), "Turno", 10, false);
    }
}
