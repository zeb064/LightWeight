package org.example.gimnasioproyect.repository;

import org.example.gimnasioproyect.confi.OracleDatabaseConnection;
import org.example.gimnasioproyect.model.HistorialMensajeTelegram;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HistorialMensajeTelegramRepositoryImpl implements HistorialMensajeTelegramRepository {
    private final OracleDatabaseConnection connection;
    private final ClienteRepository clienteRepository;
    private final MensajeTelegramRepository mensajeTelegramRepository;

    public HistorialMensajeTelegramRepositoryImpl(OracleDatabaseConnection connection,
                                                  ClienteRepository clienteRepository,
                                                  MensajeTelegramRepository mensajeTelegramRepository) throws SQLException {
        this.connection = connection;
        this.clienteRepository = clienteRepository;
        this.mensajeTelegramRepository = mensajeTelegramRepository;

        try (Connection conn = this.connection.connect()) {
            System.out.println("🎯 Conexión a BD probada exitosamente - HistorialMensajeTelegramRepository");
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void save(HistorialMensajeTelegram entity) throws SQLException {
        String sql = "INSERT INTO HISTORIAL_MENSAJES_TELEGRAM " +
                "(id_mensaje, documento, mensaje_final, fecha_envio, estado, chat_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, entity.getMensaje().getIdMensaje());
            ps.setString(2, entity.getClientes().getDocumento());
            ps.setString(3, entity.getMensajeFinal());
            ps.setTimestamp(4, entity.getFechaEnvio() != null ?
                    Timestamp.valueOf(entity.getFechaEnvio().toLocalDateTime()) :
                    Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setString(5, entity.getEstado());
            ps.setString(6, entity.getChatId());

            ps.executeUpdate();
            System.out.println("✅ Historial guardado para cliente: " + entity.getClientes().getDocumento());

        } catch (SQLException e) {
            System.err.println("❌ Error al guardar historial: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<HistorialMensajeTelegram> findById(Integer id) throws SQLException {
        String sql = "SELECT h.id_historial, h.id_mensaje, h.documento, h.mensaje_final, " +
                "h.fecha_envio, h.estado, h.chat_id " +
                "FROM HISTORIAL_MENSAJES_TELEGRAM h WHERE h.id_historial = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSet(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar historial: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<HistorialMensajeTelegram> findAll() throws SQLException {
        String sql = "SELECT h.id_historial, h.id_mensaje, h.documento, h.mensaje_final, " +
                "h.fecha_envio, h.estado, h.chat_id " +
                "FROM HISTORIAL_MENSAJES_TELEGRAM h ORDER BY h.fecha_envio DESC";

        List<HistorialMensajeTelegram> historial = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                historial.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al listar historial: " + e.getMessage());
            throw e;
        }

        return historial;
    }

    @Override
    public void update(HistorialMensajeTelegram entity) throws SQLException {

    }

    @Override
    public void delete(Integer integer) throws SQLException {

    }

    @Override
    public List<HistorialMensajeTelegram> findByCliente(String documento) throws SQLException {
        String sql = "SELECT h.id_historial, h.id_mensaje, h.documento, h.mensaje_final, " +
                "h.fecha_envio, h.estado, h.chat_id " +
                "FROM HISTORIAL_MENSAJES_TELEGRAM h WHERE h.documento = ? ORDER BY h.fecha_envio DESC";

        List<HistorialMensajeTelegram> historial = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, documento);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                historial.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar historial por cliente: " + e.getMessage());
            throw e;
        }

        return historial;
    }

    @Override
    public List<HistorialMensajeTelegram> findByFecha(LocalDate fecha) throws SQLException {
        String sql = "SELECT h.id_historial, h.id_mensaje, h.documento, h.mensaje_final, " +
                "h.fecha_envio, h.estado, h.chat_id " +
                "FROM HISTORIAL_MENSAJES_TELEGRAM h " +
                "WHERE TRUNC(h.fecha_envio) = ? ORDER BY h.fecha_envio DESC";

        List<HistorialMensajeTelegram> historial = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(fecha));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                historial.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar historial por fecha: " + e.getMessage());
            throw e;
        }

        return historial;
    }

    @Override
    public List<HistorialMensajeTelegram> findByEstado(String estado) throws SQLException {
        String sql = "SELECT h.id_historial, h.id_mensaje, h.documento, h.mensaje_final, " +
                "h.fecha_envio, h.estado, h.chat_id " +
                "FROM HISTORIAL_MENSAJES_TELEGRAM h WHERE UPPER(h.estado) = ? ORDER BY h.fecha_envio DESC";

        List<HistorialMensajeTelegram> historial = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, estado.toUpperCase());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                historial.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar historial por estado: " + e.getMessage());
            throw e;
        }

        return historial;
    }

    @Override
    public List<HistorialMensajeTelegram> findByTipoMensaje(String tipo) throws SQLException {
        String sql = "SELECT h.id_historial, h.id_mensaje, h.documento, h.mensaje_final, " +
                "h.fecha_envio, h.estado, h.chat_id " +
                "FROM HISTORIAL_MENSAJES_TELEGRAM h " +
                "JOIN MENSAJES_TELEGRAM m ON h.id_mensaje = m.id_mensaje " +
                "WHERE UPPER(m.tipo_mensaje) = ? ORDER BY h.fecha_envio DESC";

        List<HistorialMensajeTelegram> historial = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tipo.toUpperCase());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                historial.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar historial por tipo: " + e.getMessage());
            throw e;
        }

        return historial;
    }

    @Override
    public void updateEstado(Integer id, String nuevoEstado) throws SQLException {
        String sql = "UPDATE HISTORIAL_MENSAJES_TELEGRAM SET estado = ? WHERE id_historial = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);
            ps.executeUpdate();

            System.out.println("✅ Estado actualizado para historial: " + id);

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar estado: " + e.getMessage());
            throw e;
        }
    }

    private HistorialMensajeTelegram mapResultSet(ResultSet rs) throws SQLException {
        HistorialMensajeTelegram historial = new HistorialMensajeTelegram();

        historial.setIdHistorial(rs.getInt("id_historial"));
        historial.setMensajeFinal(rs.getString("mensaje_final"));
        historial.setFechaEnvio(Timestamp.valueOf(rs.getTimestamp("fecha_envio").toLocalDateTime()));
        historial.setEstado(rs.getString("estado"));
        historial.setChatId(rs.getString("chat_id"));

        // Cargar mensaje
        Integer idMensaje = rs.getInt("id_mensaje");
        mensajeTelegramRepository.findById(idMensaje).ifPresent(historial::setMensaje);

        // Cargar cliente
        String documento = rs.getString("documento");
        clienteRepository.findByDocumento(documento).ifPresent(historial::setClientes);

        return historial;
    }
}