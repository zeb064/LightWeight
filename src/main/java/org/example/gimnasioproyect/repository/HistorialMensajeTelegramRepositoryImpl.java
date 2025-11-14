package org.example.gimnasioproyect.repository;

import oracle.jdbc.internal.OracleTypes;
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
        String sql = "{call PKG_HISTORIAL_MENSAJES.PR_INSERTAR_HISTORIAL(?, ?, ?, ?, ?, ?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, entity.getMensaje().getIdMensaje());
            cs.setString(2, entity.getClientes().getDocumento());
            cs.setString(3, entity.getMensajeFinal());
            cs.setTimestamp(4, entity.getFechaEnvio() != null ?
                    Timestamp.valueOf(entity.getFechaEnvio().toLocalDateTime()) :
                    Timestamp.valueOf(java.time.LocalDateTime.now()));
            cs.setString(5, entity.getEstado());
            cs.setString(6, entity.getChatId());

            cs.execute();
            System.out.println("✅ Historial guardado para cliente: " + entity.getClientes().getDocumento());

        } catch (SQLException e) {
            System.err.println("❌ Error al guardar historial: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<HistorialMensajeTelegram> findById(Integer id) throws SQLException {
        String sql = "{? = call PKG_HISTORIAL_MENSAJES.FN_OBTENER_HISTORIAL_POR_ID(?)}";

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
            System.err.println("❌ Error al buscar historial: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<HistorialMensajeTelegram> findAll() throws SQLException {
        String sql = "{? = call PKG_HISTORIAL_MENSAJES.FN_LISTAR_HISTORIAL()}";
        List<HistorialMensajeTelegram> historial = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
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
    public List<HistorialMensajeTelegram> findByCliente(String documento) throws SQLException {
        String sql = "{? = call PKG_HISTORIAL_MENSAJES.FN_BUSCAR_HISTORIAL_POR_CLIENTE(?)}";
        List<HistorialMensajeTelegram> historial = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, documento);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
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
        String sql = "{? = call PKG_HISTORIAL_MENSAJES.FN_BUSCAR_HISTORIAL_POR_FECHA(?)}";
        List<HistorialMensajeTelegram> historial = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setDate(2, Date.valueOf(fecha));
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
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
        String sql = "{? = call PKG_HISTORIAL_MENSAJES.FN_BUSCAR_HISTORIAL_POR_ESTADO(?)}";
        List<HistorialMensajeTelegram> historial = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, estado);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
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
        String sql = "{? = call PKG_HISTORIAL_MENSAJES.FN_BUSCAR_HISTORIAL_POR_TIPO(?)}";
        List<HistorialMensajeTelegram> historial = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, tipo);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
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
        String sql = "{call PKG_HISTORIAL_MENSAJES.PR_ACTUALIZAR_ESTADO_HISTORIAL(?, ?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.setString(2, nuevoEstado);
            cs.execute();

            System.out.println("✅ Estado actualizado para historial: " + id);

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar estado: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void update(HistorialMensajeTelegram entity) throws SQLException {
        // Metodo no implementado - El historial generalmente no se actualiza
    }

    @Override
    public void delete(Integer integer) throws SQLException {
        // Metodo no implementado - El historial generalmente no se elimina
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