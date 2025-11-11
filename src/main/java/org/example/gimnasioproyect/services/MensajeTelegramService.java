package org.example.gimnasioproyect.services;

import org.example.gimnasioproyect.model.MensajesTelegram;
import org.example.gimnasioproyect.repository.MensajeTelegramRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MensajeTelegramService {
    private final MensajeTelegramRepository mensajeTelegramRepository;

    public MensajeTelegramService(MensajeTelegramRepository mensajeTelegramRepository) {
        this.mensajeTelegramRepository = mensajeTelegramRepository;
    }

    // Crear un nuevo mensaje/plantilla
    public void crearMensaje(MensajesTelegram mensaje) throws SQLException {
        validarMensaje(mensaje);

        // Verificar que no exista el tipo
        Optional<MensajesTelegram> existente = mensajeTelegramRepository.findByTipo(mensaje.getTipoMensaje());
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Ya existe un mensaje del tipo: " + mensaje.getTipoMensaje());
        }

        mensajeTelegramRepository.save(mensaje);
    }

    // Actualizar un mensaje/plantilla
    public void actualizarMensaje(MensajesTelegram mensaje) throws SQLException {
        validarMensaje(mensaje);

        if (mensaje.getIdMensaje() == null) {
            throw new IllegalArgumentException("El ID del mensaje es obligatorio");
        }

        // Verificar que exista
        if (!mensajeTelegramRepository.findById(mensaje.getIdMensaje()).isPresent()) {
            throw new IllegalArgumentException("No existe el mensaje con ID: " + mensaje.getIdMensaje());
        }

        mensajeTelegramRepository.update(mensaje);
    }

    // Obtener mensaje por ID
    public Optional<MensajesTelegram> obtenerMensajePorId(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El ID es obligatorio");
        }
        return mensajeTelegramRepository.findById(id);
    }

    // Obtener mensaje por tipo
    public Optional<MensajesTelegram> obtenerMensajePorTipo(String tipo) throws SQLException {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de mensaje es obligatorio");
        }
        return mensajeTelegramRepository.findByTipo(tipo);
    }

    // Obtener todos los mensajes
    public List<MensajesTelegram> obtenerTodosLosMensajes() throws SQLException {
        return mensajeTelegramRepository.findAll();
    }

    // Obtener solo mensajes activos
    public List<MensajesTelegram> obtenerMensajesActivos() throws SQLException {
        return mensajeTelegramRepository.findActivos();
    }

    // Activar un mensaje
    public void activarMensaje(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El ID es obligatorio");
        }

        if (!mensajeTelegramRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("No existe el mensaje con ID: " + id);
        }

        mensajeTelegramRepository.activar(id);
    }

    // Desactivar un mensaje
    public void desactivarMensaje(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El ID es obligatorio");
        }

        if (!mensajeTelegramRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("No existe el mensaje con ID: " + id);
        }

        mensajeTelegramRepository.desactivar(id);
    }

    // Eliminar un mensaje
    public void eliminarMensaje(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El ID es obligatorio");
        }

        if (!mensajeTelegramRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("No existe el mensaje con ID: " + id);
        }

        mensajeTelegramRepository.delete(id);
    }

    // Validaciones
    private void validarMensaje(MensajesTelegram mensaje) {
        if (mensaje == null) {
            throw new IllegalArgumentException("El mensaje no puede ser nulo");
        }

        if (mensaje.getTipoMensaje() == null || mensaje.getTipoMensaje().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de mensaje es obligatorio");
        }

        if (mensaje.getContenido() == null || mensaje.getContenido().trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido del mensaje es obligatorio");
        }

        // Validar que el tipo sea uno de los permitidos
        String tipo = mensaje.getTipoMensaje().toUpperCase();
        if (!tipo.equals("BIENVENIDA") && !tipo.equals("VENCE_PRONTO") && !tipo.equals("VENCIDO")) {
            throw new IllegalArgumentException("Tipo de mensaje inválido. Debe ser: BIENVENIDA, VENCE_PRONTO o VENCIDO");
        }
    }
}