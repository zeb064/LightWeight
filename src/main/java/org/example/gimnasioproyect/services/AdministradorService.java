package org.example.gimnasioproyect.services;

import org.example.gimnasioproyect.Utilidades.Validador;
import org.example.gimnasioproyect.model.Administradores;
import org.example.gimnasioproyect.repository.AdministradorRepository;
import org.example.gimnasioproyect.repository.PersonalRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class AdministradorService {
    private final AdministradorRepository administradorRepository;
    private final PersonalRepository personalRepository;

    public AdministradorService(AdministradorRepository administradorRepository,
                                PersonalRepository personalRepository) {
        this.administradorRepository = administradorRepository;
        this.personalRepository = personalRepository;
    }

    /**
     * Registra un nuevo administrador
     */
    public void registrarAdministrador(Administradores administrador) throws SQLException {
        validarDatosAdministrador(administrador);

        // Verificar que no exista el documento
        if (administradorRepository.findByDocumento(administrador.getDocuAdministrador()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un administrador con el documento: " +
                    administrador.getDocuAdministrador());
        }

        // Verificar que el usuario no exista
        if (personalRepository.existeUsuario(administrador.getUsuarioSistema())) {
            throw new IllegalArgumentException("El usuario ya existe: " + administrador.getUsuarioSistema());
        }

        // Establecer fecha de contratación si no tiene
        if (administrador.getFechaContratacion() == null) {
            administrador.setFechaContratacion(LocalDate.now());
        }

        administradorRepository.save(administrador);
    }

    /**
     * Actualiza los datos de un administrador
     */
    public void actualizarAdministrador(Administradores administrador) throws SQLException {
        validarDatosAdministrador(administrador);

        // Verificar que exista
        if (!administradorRepository.findByDocumento(administrador.getDocuAdministrador()).isPresent()) {
            throw new IllegalArgumentException("No existe el administrador con documento: " +
                    administrador.getDocuAdministrador());
        }

        administradorRepository.update(administrador);
    }

    // Busca un administrador por documento
    public Optional<Administradores> buscarAdministradorPorDocumento(String documento) throws SQLException {
        Validador.validarDocumento(documento);
        return administradorRepository.findByDocumento(documento);
    }

    // Busca un administrador por usuario
    public Optional<Administradores> buscarAdministradorPorUsuario(String usuario) throws SQLException {
        if (usuario == null || usuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }
        return administradorRepository.findByUsuario(usuario);
    }

    // Obtiene todos los administradores
    public List<Administradores> obtenerTodosLosAdministradores() throws SQLException {
        return administradorRepository.findAll();
    }

    // Actualiza el cargo de un administrador
    public void actualizarCargo(String documentoAdministrador, String nuevoCargo) throws SQLException {
        Validador.validarDocumento(documentoAdministrador);
        Validador.validarTexto(nuevoCargo, "Cargo", 20, true);

        Optional<Administradores> administradorOpt =
                administradorRepository.findByDocumento(documentoAdministrador);

        if (!administradorOpt.isPresent()) {
            throw new IllegalArgumentException("No existe el administrador");
        }

        Administradores administrador = administradorOpt.get();
        administrador.setCargo(nuevoCargo);

        administradorRepository.update(administrador);
    }

    // Elimina un administrador por documento
    public void eliminarAdministrador(String documento) throws SQLException {
        Validador.validarDocumento(documento);

        // Verificar que existe
        if (!administradorRepository.findByDocumento(documento).isPresent()) {
            throw new IllegalArgumentException("No existe el administrador con documento: " + documento);
        }

        // Verificar que no sea el único administrador
        List<Administradores> administradores = administradorRepository.findAll();
        if (administradores.size() <= 1) {
            throw new IllegalArgumentException("No se puede eliminar el único administrador del sistema");
        }

        administradorRepository.delete(documento);
    }

    //valida los datos de un administrador
    private void validarDatosAdministrador(Administradores administrador) {
        if (administrador == null) {
            throw new IllegalArgumentException("El administrador no puede ser nulo");
        }

        Validador.validarDocumento(administrador.getDocuAdministrador());
        Validador.validarNombre(administrador.getNombres(), "Nombres");
        Validador.validarNombre(administrador.getApellidos(), "Apellidos");
        Validador.validarTelefono(administrador.getTelefono());
        Validador.validarCorreo(administrador.getCorreo());
        Validador.validarUsuarioSistema(administrador.getUsuarioSistema());
        Validador.validarContrasena(administrador.getContrasena());
        Validador.validarTexto(administrador.getCargo(), "Cargo", 20, true);
    }
}
