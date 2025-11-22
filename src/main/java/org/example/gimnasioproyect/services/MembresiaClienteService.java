package org.example.gimnasioproyect.services;

import org.example.gimnasioproyect.Utilidades.CalculadoraFechas;
import org.example.gimnasioproyect.Utilidades.Validador;
import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.model.MembresiaClientes;
import org.example.gimnasioproyect.model.Membresias;
import org.example.gimnasioproyect.repository.ClienteRepository;
import org.example.gimnasioproyect.repository.MembresiaClienteRepository;
import org.example.gimnasioproyect.repository.MembresiaRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MembresiaClienteService {
    private final MembresiaClienteRepository membresiaClienteRepository;
    private final ClienteRepository clienteRepository;
    private final MembresiaRepository membresiaRepository;

    public MembresiaClienteService(MembresiaClienteRepository membresiaClienteRepository,
                                   ClienteRepository clienteRepository,
                                   MembresiaRepository membresiaRepository) {
        this.membresiaClienteRepository = membresiaClienteRepository;
        this.clienteRepository = clienteRepository;
        this.membresiaRepository = membresiaRepository;
    }



    //Asignamos una membresía a un cliente
    public void asignarMembresiaACliente(String documento, Integer idMembresia, LocalDate fechaInicio) throws SQLException {
        Validador.validarDocumento(documento);
        if(idMembresia == null) {
            throw new IllegalArgumentException("El ID de la membresía no puede ser nulo.");
        }
        if(fechaInicio == null) {
            fechaInicio = LocalDate.now();
        }

        // Verificar que el cliente exista
        Optional<Clientes> clienteOpt = clienteRepository.findByDocumento(documento);
        if (!clienteOpt.isPresent()) {
            throw new IllegalArgumentException("No existe un cliente con el documento: " + documento);
        }
        // Verificar que la membresía exista
        Optional<Membresias> membresiaOpt = membresiaRepository.findById(idMembresia);
        if (!membresiaOpt.isPresent()) {
            throw new IllegalArgumentException("No existe la membresia con el id: " + idMembresia);
        }

        //verificar que el cliente no tenga una membresía activa
        Optional<MembresiaClientes> membresiaClieneteOpt = membresiaClienteRepository.findMembresiaActivaByCliente(documento);
        if (membresiaClieneteOpt.isPresent()) {
            throw new IllegalArgumentException("El cliente ya tiene una membresía activa.");
        }

        //Calcular fecha de finalización segun el tipo de membresía
        Membresias membresia = membresiaOpt.get();
        LocalDate fechaFin = CalculadoraFechas.calcularFechaFinalizacion(fechaInicio, membresia.getTipoMembresia());

        // Crear y guardar la membresía del cliente
        MembresiaClientes membresiaCliente = new MembresiaClientes();
        membresiaCliente.setCliente(clienteOpt.get());
        membresiaCliente.setMembresia(membresia);
        membresiaCliente.setFechaAsignacion(fechaInicio);
        membresiaCliente.setFechaFinalizacion(fechaFin);

        membresiaClienteRepository.save(membresiaCliente);
    }

    //Renovar la membresía de un cliente
    public void renovarMembresia(String documento, Integer idNuevaMembresia) throws SQLException {
        Validador.validarDocumento(documento);

        //Obtener la membresía activa del cliente
        Optional<MembresiaClientes> membresiaActivaOpt = membresiaClienteRepository.findMembresiaActivaByCliente(documento);
        if (!membresiaActivaOpt.isPresent()) {
            throw new IllegalArgumentException("El cliente no tiene una membresía activa para renovar");
        }

        // Finalizar la membresía actual
        MembresiaClientes membresiaActiva = membresiaActivaOpt.get();
        membresiaActiva.setFechaFinalizacion(LocalDate.now());
        membresiaClienteRepository.update(membresiaActiva);

        // Asignar la nueva membresía
        Integer idMembresia = (idNuevaMembresia != null) ? idNuevaMembresia : membresiaActiva.getMembresia().getIdMembresia();

        asignarMembresiaACliente(documento, idMembresia, LocalDate.now());
    }

    //Cancelar la membresía de un cliente
    public void cancelarMembresia(String documento) throws SQLException {
        Validador.validarDocumento(documento);

        //Obtener la membresía activa del cliente
        Optional<MembresiaClientes> membresiaActivaOpt = membresiaClienteRepository.findMembresiaActivaByCliente(documento);
        if (!membresiaActivaOpt.isPresent()) {
            throw new IllegalArgumentException("El cliente no tiene una membresía activa para cancelar");
        }

        // Finalizar la membresía actual
        MembresiaClientes membresiaActiva = membresiaActivaOpt.get();
        membresiaActiva.setFechaFinalizacion(LocalDate.now());
        membresiaClienteRepository.update(membresiaActiva);
    }

    //Obtiene la membresía activa de un cliente
    public Optional<MembresiaClientes> obtenerMembresiaActiva(String documentoCliente) throws SQLException {
        Validador.validarDocumento(documentoCliente);
        return membresiaClienteRepository.findMembresiaActivaByCliente(documentoCliente);
    }

    //Obtiene el estado de la membresía de un cliente: ACTIVA, VENCIDA, SIN_MEMBRESIA
    public String obtenerEstadoMembresia(String documento) throws SQLException {
        Validador.validarDocumento(documento);

        // Obtener todas las membresías del cliente
        List<MembresiaClientes> membresias = membresiaClienteRepository.findByCliente(documento);

        if (membresias.isEmpty()) {
            return "SIN_MEMBRESIA";
        }

        // Obtener la membresía más reciente
        MembresiaClientes membresiaReciente = membresias.stream()
                .max((m1, m2) -> m1.getFechaAsignacion().compareTo(m2.getFechaAsignacion()))
                .orElse(null);

        if (membresiaReciente == null) {
            return "SIN_MEMBRESIA";
        }

        if (membresiaReciente.estaActiva()) {
            return "ACTIVA";
        } else if (membresiaReciente.estaVencida()) {
            return "VENCIDA";
        }

        return "SIN_MEMBRESIA";
    }

    //Obtiene el historial de membresías de un cliente
    public List<MembresiaClientes> obtenerHistorialMembresias(String documento) throws SQLException {
        Validador.validarDocumento(documento);
        return membresiaClienteRepository.findByCliente(documento);
    }

    //verifica si un cliente tiene una membresía activa
    public boolean tieneMembresiaActiva(String documento) throws SQLException {
        Validador.validarDocumento(documento);
        Optional<MembresiaClientes> membresiaActivaOpt = membresiaClienteRepository.findMembresiaActivaByCliente(documento);
        return membresiaActivaOpt.isPresent();
    }

//    //Membresías próximas a vencer
//    public List<MembresiaClientes> obtenerMembresiasProximasAVencer() throws SQLException {
//        return membresiaClienteRepository.findMembresiasProximasAVencer(7);
//    }
//
//    //Obtiene clientes con membresías vencidas
//    public List<MembresiaClientes> obtenerMembresiasVencidas() throws SQLException {
//        return membresiaClienteRepository.findMembresiasVencidas();
//    }
//
//    //Calcula días restantes de membresía
//    public long calcularDiasRestantes(String documentoCliente) throws SQLException {
//        Optional<MembresiaClientes> membresiaOpt = obtenerMembresiaActiva(documentoCliente);
//
//        if (!membresiaOpt.isPresent()) {
//            return 0;
//        }
//
//        return CalculadoraFechas.calcularDiasRestantes(membresiaOpt.get().getFechaFinalizacion());
//    }
}
