package org.example.gimnasioproyect.repository;

import org.example.gimnasioproyect.confi.OracleDatabaseConnection;
import org.example.gimnasioproyect.model.MensajesTelegram;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MensajeTelegramRepositoryImpl implements MensajeTelegramRepository {
    private final OracleDatabaseConnection connection;

    public MensajeTelegramRepositoryImpl(OracleDatabaseConnection connection) throws SQLException {
        this.connection = connection;
        try (Connection conn = this.connection.connect()) {
            System.out.println("🎯 Conexión a BD probada exitosamente - MensajeTelegramRepository");
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void save(MensajesTelegram mensaje) throws SQLException {
        String sql = "INSERT INTO MENSAJES_TELEGRAM (tipo_mensaje, contenido, activo) " +
                "VALUES (?, ?, ?)";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, mensaje.getTipoMensaje());
            ps.setString(2, mensaje.getContenido());
            ps.setInt(3, mensaje.isActivo() ? 1 : 0);

            ps.executeUpdate();
            System.out.println("✅ Mensaje Telegram guardado: " + mensaje.getTipoMensaje());

        } catch (SQLException e) {
            System.err.println("❌ Error al guardar mensaje: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<MensajesTelegram> findById(Integer id) throws SQLException {
        String sql = "SELECT id_mensaje, tipo_mensaje, contenido, activo FROM MENSAJES_TELEGRAM WHERE id_mensaje = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSet(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar mensaje: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<MensajesTelegram> findByTipo(String tipo) throws SQLException {
        String sql = "SELECT id_mensaje, tipo_mensaje, contenido, activo FROM MENSAJES_TELEGRAM " +
                "WHERE UPPER(tipo_mensaje) = ? AND activo = 1";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tipo.toUpperCase());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSet(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar mensaje por tipo: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<MensajesTelegram> findAll() throws SQLException {
        String sql = "SELECT id_mensaje, tipo_mensaje, contenido, activo FROM MENSAJES_TELEGRAM ORDER BY tipo_mensaje";
        List<MensajesTelegram> mensajes = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                mensajes.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al listar mensajes: " + e.getMessage());
            throw e;
        }

        return mensajes;
    }

    @Override
    public List<MensajesTelegram> findActivos() throws SQLException {
        String sql = "SELECT id_mensaje, tipo_mensaje, contenido, activo FROM MENSAJES_TELEGRAM " +
                "WHERE activo = 1 ORDER BY tipo_mensaje";
        List<MensajesTelegram> mensajes = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                mensajes.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al listar mensajes activos: " + e.getMessage());
            throw e;
        }

        return mensajes;
    }

    @Override
    public void update(MensajesTelegram mensaje) throws SQLException {
        String sql = "UPDATE MENSAJES_TELEGRAM SET tipo_mensaje = ?, contenido = ?, activo = ? WHERE id_mensaje = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, mensaje.getTipoMensaje());
            ps.setString(2, mensaje.getContenido());
            ps.setInt(3, mensaje.isActivo() ? 1 : 0);
            ps.setInt(4, mensaje.getIdMensaje());

            int filasActualizadas = ps.executeUpdate();
            if (filasActualizadas > 0) {
                System.out.println("✅ Mensaje actualizado: " + mensaje.getTipoMensaje());
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar mensaje: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM MENSAJES_TELEGRAM WHERE id_mensaje = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("✅ Mensaje eliminado: " + id);

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar mensaje: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void activar(Integer id) throws SQLException {
        String sql = "UPDATE MENSAJES_TELEGRAM SET activo = 1 WHERE id_mensaje = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("✅ Mensaje activado: " + id);

        } catch (SQLException e) {
            System.err.println("❌ Error al activar mensaje: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void desactivar(Integer id) throws SQLException {
        String sql = "UPDATE MENSAJES_TELEGRAM SET activo = 0 WHERE id_mensaje = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("✅ Mensaje desactivado: " + id);

        } catch (SQLException e) {
            System.err.println("❌ Error al desactivar mensaje: " + e.getMessage());
            throw e;
        }
    }

    private MensajesTelegram mapResultSet(ResultSet rs) throws SQLException {
        MensajesTelegram mensaje = new MensajesTelegram();
        mensaje.setIdMensaje(rs.getInt("id_mensaje"));
        mensaje.setTipoMensaje(rs.getString("tipo_mensaje"));
        mensaje.setContenido(rs.getString("contenido"));
        mensaje.setActivo(rs.getInt("activo") == 1);
        return mensaje;
    }
}