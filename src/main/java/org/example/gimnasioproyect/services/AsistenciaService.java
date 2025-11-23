package org.example.gimnasioproyect.services;

import org.example.gimnasioproyect.Utilidades.Validador;
import org.example.gimnasioproyect.model.Asistencias;
import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.repository.AsistenciaRepository;
import org.example.gimnasioproyect.repository.ClienteRepository;
import org.example.gimnasioproyect.repository.MembresiaClienteRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class AsistenciaService {
    private final AsistenciaRepository asistenciaRepository;
    private final ClienteRepository clienteRepository;
    private final MembresiaClienteRepository membresiaClienteRepository;

    public AsistenciaService(AsistenciaRepository asistenciaRepository,
                             ClienteRepository clienteRepository,
                             MembresiaClienteRepository membresiaClienteRepository) {
        this.asistenciaRepository = asistenciaRepository;
        this.clienteRepository = clienteRepository;
        this.membresiaClienteRepository = membresiaClienteRepository;
    }

    //Registra la asistencia de un cliente
    public void registrarAsistencia(String documentoCliente) throws SQLException {
        Validador.validarDocumento(documentoCliente);

        // Verificar que el cliente existe
        Optional<Clientes> clienteOpt = clienteRepository.findByDocumento(documentoCliente);
        if (!clienteOpt.isPresent()) {
            throw new IllegalArgumentException("No existe un cliente con el documento: " + documentoCliente);
        }

        // Verificar que tiene membresía activa
        if (!membresiaClienteRepository.findMembresiaActivaByCliente(documentoCliente).isPresent()) {
            throw new IllegalArgumentException("El cliente no tiene una membresía activa");
        }

        // Crear asistencia
        Asistencias asistencia = new Asistencias();
        asistencia.setFecha(LocalDate.now());
        asistencia.setCliente(clienteOpt.get());

        asistenciaRepository.save(asistencia);
    }

    //Registra asistencia con una fecha específica
    public void registrarAsistenciaConFecha(String documentoCliente, LocalDate fecha) throws SQLException {
        Validador.validarDocumento(documentoCliente);

        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }

        if (fecha.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("No se puede registrar asistencia en el futuro");
        }

        Optional<Clientes> clienteOpt = clienteRepository.findByDocumento(documentoCliente);
        if (!clienteOpt.isPresent()) {
            throw new IllegalArgumentException("No existe un cliente con el documento: " + documentoCliente);
        }

        Asistencias asistencia = new Asistencias();
        asistencia.setFecha(fecha);
        asistencia.setCliente(clienteOpt.get());

        asistenciaRepository.save(asistencia);
    }

    // Obtiene las asistencias de un cliente
    public List<Asistencias> obtenerHistorialCliente(String documentoCliente) throws SQLException {
        Validador.validarDocumento(documentoCliente);
        return asistenciaRepository.findByCliente(documentoCliente);
    }

    //Elimina una asistencia del sistema
    public void eliminarAsistencia(Integer idAsistencia) throws SQLException {
        if (idAsistencia == null) {
            throw new IllegalArgumentException("El ID de asistencia es obligatorio");
        }

        Optional<Asistencias> asistenciaOpt = asistenciaRepository.findById(idAsistencia);
        if (!asistenciaOpt.isPresent()) {
            throw new IllegalArgumentException("No existe la asistencia");
        }

        asistenciaRepository.delete(idAsistencia);
    }
}
