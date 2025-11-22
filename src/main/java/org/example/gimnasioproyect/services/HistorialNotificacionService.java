package org.example.gimnasioproyect.services;

import org.example.gimnasioproyect.Utilidades.EstadisticasHistorial;
import org.example.gimnasioproyect.model.HistorialMensajeTelegram;
import org.example.gimnasioproyect.repository.HistorialMensajeTelegramRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class HistorialNotificacionService {
    private final HistorialMensajeTelegramRepository historialRepository;

    public HistorialNotificacionService(HistorialMensajeTelegramRepository historialRepository) {
        this.historialRepository = historialRepository;
    }

    //Obtiene todo el historial de mensajes
    public List<HistorialMensajeTelegram> obtenerTodoElHistorial() throws SQLException {
        return historialRepository.findAll();
    }

    //Obtiene el historial de un cliente específico
    public List<HistorialMensajeTelegram> obtenerHistorialPorCliente(String documento) throws SQLException {
        if (documento == null || documento.trim().isEmpty()) {
            throw new IllegalArgumentException("El documento es obligatorio");
        }
        return historialRepository.findByCliente(documento);
    }

    //Obtiene mensajes por tipo
    public List<HistorialMensajeTelegram> obtenerPorTipoMensaje(String tipo) throws SQLException {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de mensaje es obligatorio");
        }
        return historialRepository.findByTipoMensaje(tipo);
    }

    //Obtiene mensajes por estado

    public List<HistorialMensajeTelegram> obtenerPorEstado(String estado) throws SQLException {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }
        return historialRepository.findByEstado(estado);
    }

    //Obtiene mensajes de una fecha específica

    public List<HistorialMensajeTelegram> obtenerPorFecha(LocalDate fecha) throws SQLException {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        return historialRepository.findByFecha(fecha);
    }

    //Cuenta total de mensajes enviados
    public int contarTotalMensajes() throws SQLException {
        return historialRepository.findAll().size();
    }

    //Cuenta mensajes exitosos
    public int contarMensajesExitosos() throws SQLException {
        return historialRepository.findByEstado("ENVIADO").size();
    }

    //Cuenta mensajes fallidos
    public int contarMensajesFallidos() throws SQLException {
        return historialRepository.findByEstado("FALLIDO").size();
    }

    //Cuenta mensajes enviados hoy

    public int contarMensajesHoy() throws SQLException {
        LocalDate hoy = LocalDate.now();
        return historialRepository.findByFecha(hoy).size();
    }

    //Obtiene mensajes fallidos para reenviar

    public List<HistorialMensajeTelegram> obtenerMensajesFallidos() throws SQLException {
        return historialRepository.findByEstado("FALLIDO");
    }

    //Actualiza el estado de un mensaje (para marcar como reenviado)

    public void actualizarEstado(Integer idHistorial, String nuevoEstado) throws SQLException {
        if (idHistorial == null) {
            throw new IllegalArgumentException("El ID del historial es obligatorio");
        }
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            throw new IllegalArgumentException("El nuevo estado es obligatorio");
        }

        // Validar que el estado sea válido
        if (!nuevoEstado.equals("ENVIADO") && !nuevoEstado.equals("FALLIDO")) {
            throw new IllegalArgumentException("Estado inválido. Debe ser ENVIADO o FALLIDO");
        }

        historialRepository.updateEstado(idHistorial, nuevoEstado);
    }

    //Filtra historial por rango de fechas
    public List<HistorialMensajeTelegram> filtrarPorRangoFechas(
            List<HistorialMensajeTelegram> historial,
            LocalDate desde,
            LocalDate hasta) {

        if (historial == null) {
            throw new IllegalArgumentException("El historial no puede ser nulo");
        }

        return historial.stream()
                .filter(h -> {
                    LocalDate fechaMensaje = h.getFechaEnvio().toLocalDateTime().toLocalDate();

                    if (desde != null && hasta != null) {
                        return !fechaMensaje.isBefore(desde) && !fechaMensaje.isAfter(hasta);
                    } else if (desde != null) {
                        return !fechaMensaje.isBefore(desde);
                    } else if (hasta != null) {
                        return !fechaMensaje.isAfter(hasta);
                    } else {
                        return true;
                    }
                })
                .collect(Collectors.toList());
    }

    //Busca mensajes por nombre o documento de cliente
    public List<HistorialMensajeTelegram> buscarPorCliente(
            List<HistorialMensajeTelegram> historial,
            String busqueda) {

        if (historial == null) {
            throw new IllegalArgumentException("El historial no puede ser nulo");
        }

        if (busqueda == null || busqueda.trim().isEmpty()) {
            return historial;
        }

        String busquedaLower = busqueda.toLowerCase();

        return historial.stream()
                .filter(h -> {
                    String nombre = h.getClientes().getNombreCompleto().toLowerCase();
                    String documento = h.getClientes().getDocumento().toLowerCase();
                    return nombre.contains(busquedaLower) || documento.contains(busquedaLower);
                })
                .collect(Collectors.toList());
    }

    //Obtiene estadísticas generales del historial
    public EstadisticasHistorial obtenerEstadisticas() throws SQLException {
        List<HistorialMensajeTelegram> todo = historialRepository.findAll();

        int total = todo.size();
        int exitosos = (int) todo.stream()
                .filter(h -> h.getEstado().equals("ENVIADO"))
                .count();
        int fallidos = total - exitosos;

        LocalDate hoy = LocalDate.now();
        int hoyCount = (int) todo.stream()
                .filter(h -> h.getFechaEnvio().toLocalDateTime().toLocalDate().equals(hoy))
                .count();

        return new EstadisticasHistorial(total, exitosos, fallidos, hoyCount);
    }
}