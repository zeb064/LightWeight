package org.example.gimnasioproyect.repository;

import oracle.jdbc.internal.OracleTypes;
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
            System.out.println("Conexión a BD probada exitosamente - MensajeTelegramRepository");
        } catch (SQLException e) {
            System.err.println("Error al conectar: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void save(MensajesTelegram mensaje) throws SQLException {
        String sql = "{call PKG_MENSAJES_TELEGRAM.PR_INSERTAR_MENSAJE(?, ?, ?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, mensaje.getTipoMensaje());
            cs.setString(2, mensaje.getContenido());
            cs.setInt(3, mensaje.isActivo() ? 1 : 0);

            cs.execute();
            System.out.println("Mensaje Telegram guardado: " + mensaje.getTipoMensaje());

        } catch (SQLException e) {
            System.err.println("Error al guardar mensaje: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<MensajesTelegram> findById(Integer id) throws SQLException {
        String sql = "{? = call PKG_MENSAJES_TELEGRAM.FN_OBTENER_MENSAJE_POR_ID(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setInt(2, id);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            if (rs.next()) {
                return Optional.of(mapResultSet(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("Error al buscar mensaje: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<MensajesTelegram> findByTipo(String tipo) throws SQLException {
        String sql = "{? = call PKG_MENSAJES_TELEGRAM.FN_OBTENER_MENSAJE_POR_TIPO(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, tipo);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            if (rs.next()) {
                return Optional.of(mapResultSet(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("Error al buscar mensaje por tipo: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<MensajesTelegram> findAll() throws SQLException {
        String sql = "{? = call PKG_MENSAJES_TELEGRAM.FN_LISTAR_MENSAJES()}";
        List<MensajesTelegram> mensajes = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                mensajes.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar mensajes: " + e.getMessage());
            throw e;
        }

        return mensajes;
    }

    @Override
    public List<MensajesTelegram> findActivos() throws SQLException {
        String sql = "{? = call PKG_MENSAJES_TELEGRAM.FN_LISTAR_MENSAJES_ACTIVOS()}";
        List<MensajesTelegram> mensajes = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                mensajes.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar mensajes activos: " + e.getMessage());
            throw e;
        }

        return mensajes;
    }

    @Override
    public void update(MensajesTelegram mensaje) throws SQLException {
        String sql = "{call PKG_MENSAJES_TELEGRAM.PR_ACTUALIZAR_MENSAJE(?, ?, ?, ?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, mensaje.getIdMensaje());
            cs.setString(2, mensaje.getTipoMensaje());
            cs.setString(3, mensaje.getContenido());
            cs.setInt(4, mensaje.isActivo() ? 1 : 0);

            cs.execute();
            System.out.println("Mensaje actualizado: " + mensaje.getTipoMensaje());

        } catch (SQLException e) {
            System.err.println("Error al actualizar mensaje: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "{call PKG_MENSAJES_TELEGRAM.PR_ELIMINAR_MENSAJE(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.execute();
            System.out.println("Mensaje eliminado: " + id);

        } catch (SQLException e) {
            System.err.println("Error al eliminar mensaje: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void activar(Integer id) throws SQLException {
        String sql = "{call PKG_MENSAJES_TELEGRAM.PR_ACTIVAR_MENSAJE(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.execute();
            System.out.println("Mensaje activado: " + id);

        } catch (SQLException e) {
            System.err.println("Error al activar mensaje: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void desactivar(Integer id) throws SQLException {
        String sql = "{call PKG_MENSAJES_TELEGRAM.PR_DESACTIVAR_MENSAJE(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.execute();
            System.out.println("Mensaje desactivado: " + id);

        } catch (SQLException e) {
            System.err.println("Error al desactivar mensaje: " + e.getMessage());
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