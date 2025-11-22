package org.example.gimnasioproyect.services;

import org.example.gimnasioproyect.Utilidades.CalculadoraFechas;
import org.example.gimnasioproyect.Utilidades.Validador;
import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.repository.ClienteRepository;
import org.example.gimnasioproyect.repository.MembresiaClienteRepository;
import org.example.gimnasioproyect.repository.MembresiaClienteRepositoryImpl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ClienteServices {
    private final ClienteRepository clienteRepository;
    private final MembresiaClienteRepository membresiasClientes;

    public ClienteServices(ClienteRepository clienteRepository, MembresiaClienteRepository membresiaClienteRepository) {
        this.membresiasClientes = membresiaClienteRepository;
        this.clienteRepository = clienteRepository;
    }


    //Registra un nuevo cliente en el sistema

    public void registrarCliente(Clientes cliente) throws SQLException {
        // Validaciones
        validarDatosCliente(cliente);

        // Verificar que no exista
        if (clienteRepository.findByDocumento(cliente.getDocumento()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un cliente con el documento: " + cliente.getDocumento());
        }

        // Establecer fecha de registro si no tiene
        if (cliente.getFechaRegistro() == null) {
            cliente.setFechaRegistro(LocalDate.now());
        }

        clienteRepository.save(cliente);
    }


     //Actualiza los datos de un cliente existente

    public void actualizarCliente(Clientes cliente) throws SQLException {
        // Validaciones
        validarDatosCliente(cliente);

        // Verificar que exista
        if (!clienteRepository.findByDocumento(cliente.getDocumento()).isPresent()) {
            throw new IllegalArgumentException("No existe un cliente con el documento: " + cliente.getDocumento());
        }

        clienteRepository.update(cliente);
    }

    //Busca un cliente por su documento

    public Optional<Clientes> buscarClientePorDocumento(String documento) throws SQLException {
        Validador.validarDocumento(documento);
        return clienteRepository.findByDocumento(documento);
    }

    //Obtiene todos los clientes registrados

    public List<Clientes> obtenerTodosLosClientes() throws SQLException {
        return clienteRepository.findAll();
    }

    //Elimina un cliente del sistema

    public void eliminarCliente(String documento) throws SQLException {
        Validador.validarDocumento(documento);

        // Verificar que exista
        if (!clienteRepository.findByDocumento(documento).isPresent()) {
            throw new IllegalArgumentException("No existe un cliente con el documento: " + documento);
        }

        if(membresiasClientes.findMembresiaActivaByCliente(documento).isPresent()) {
            throw new IllegalArgumentException("El cliente con documento " + documento + " tiene membresías activas y no puede ser eliminado.");
        }



        clienteRepository.delete(documento);
    }

    //Busca clientes por nombre (búsqueda parcial)

    public List<Clientes> buscarClientesPorNombre(String nombre) throws SQLException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de búsqueda no puede estar vacío");
        }
        return clienteRepository.findByNombre(nombre);
    }

    //Obtiene clientes por barrio

    public List<Clientes> obtenerClientesPorBarrio(Integer idBarrio) throws SQLException {
        if (idBarrio == null) {
            throw new IllegalArgumentException("El ID del barrio es obligatorio");
        }
        return clienteRepository.findByBarrio(idBarrio);
    }

    //Calcula la edad de un cliente

    public int calcularEdadCliente(String documento) throws SQLException {
        Optional<Clientes> clienteOpt = buscarClientePorDocumento(documento);
        if (!clienteOpt.isPresent()) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }

        Clientes cliente = clienteOpt.get();
        return CalculadoraFechas.calcularEdad(cliente.getFechaNacimiento());
    }

    //Valida todos los datos requeridos de un cliente

    private void validarDatosCliente(Clientes cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo");
        }

        Validador.validarDocumento(cliente.getDocumento());
        Validador.validarNombre(cliente.getNombres(), "Nombres");
        Validador.validarNombre(cliente.getApellidos(), "Apellidos");
        Validador.validarFechaNacimiento(cliente.getFechaNacimiento());
        Validador.validarGenero(cliente.getGenero());
        Validador.validarTelefono(cliente.getTelefono());
        Validador.validarCorreo(cliente.getCorreo());

        if (cliente.getBarrio() == null) {
            throw new IllegalArgumentException("El barrio es obligatorio");
        }
    }
}
