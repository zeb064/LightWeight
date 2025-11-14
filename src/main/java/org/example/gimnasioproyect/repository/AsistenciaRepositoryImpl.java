package org.example.gimnasioproyect.repository;

import oracle.jdbc.internal.OracleTypes;
import org.example.gimnasioproyect.model.Asistencias;
import org.example.gimnasioproyect.model.Barrios;
import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.confi.OracleDatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AsistenciaRepositoryImpl implements  AsistenciaRepository {
    private final OracleDatabaseConnection connection;

    public AsistenciaRepositoryImpl(OracleDatabaseConnection connection) throws SQLException {
        this.connection = connection;
        try (Connection conn = this.connection.connect()) {
            System.out.println("🎯 Conexión a BD probada exitosamente - AsistenciaRepository");
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void save(Asistencias entity) throws SQLException {
        String sql = "{call PKG_ASISTENCIAS.PR_INSERTAR_ASISTENCIA(?, ?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            //ps.setInt(1, entity.getIdAsistencia());
            cs.setDate(1, entity.getFecha() != null ? Date.valueOf(entity.getFecha()) : null);
            cs.setString(2, entity.getCliente().getDocumento());

            cs.execute();
            System.out.println("✅ Asistencia registrada exitosamente para: " +
                    entity.getCliente().getDocumento());

        } catch (SQLException e) {
            System.err.println("❌ Error al guardar asistencia: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Asistencias> findById(Integer id) throws SQLException {
        String sql = "{? = call PKG_ASISTENCIAS.FN_OBTENER_ASISTENCIA_POR_ID(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setInt(2, id);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            if (rs.next()) {
                Asistencias asistencia = mapResultSetToAsistencia(rs);
                return Optional.of(asistencia);
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar asistencia: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Asistencias> findByCliente(String documentoCliente) throws SQLException {
        String sql = "{? = call PKG_ASISTENCIAS.FN_BUSCAR_ASISTENCIAS_POR_CLIENTE(?)}";
        List<Asistencias> asistencias = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, documentoCliente);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                asistencias.add(mapResultSetToAsistencia(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar asistencias del cliente: " + e.getMessage());
            throw e;
        }

        return asistencias;
    }

    @Override
    public List<Asistencias> findByFecha(LocalDate fecha) throws SQLException {
        String sql = "{? = call PKG_ASISTENCIAS.FN_BUSCAR_ASISTENCIAS_POR_FECHA(?)}";
        List<Asistencias> asistencias = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setDate(2, Date.valueOf(fecha));
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                asistencias.add(mapResultSetToAsistencia(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar asistencias por fecha: " + e.getMessage());
            throw e;
        }

        return asistencias;
    }

    @Override
    public List<Asistencias> findByClienteAndFechaRange(String documentoCliente, LocalDate fechaInicio,
                                                        LocalDate fechaFin) throws SQLException {
        String sql = "{? = call PKG_ASISTENCIAS.FN_BUSCAR_ASISTENCIAS_POR_RANGO(?, ?, ?)}";
        List<Asistencias> asistencias = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, documentoCliente);
            cs.setDate(3, Date.valueOf(fechaInicio));
            cs.setDate(4, Date.valueOf(fechaFin));
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                asistencias.add(mapResultSetToAsistencia(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar asistencias por rango: " + e.getMessage());
            throw e;
        }

        return asistencias;
    }

    @Override
    public int countAsistenciasByCliente(String documentoCliente) throws SQLException {
        String sql = "{? = call PKG_ASISTENCIAS.FN_CONTAR_ASISTENCIAS_CLIENTE(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setString(2, documentoCliente);
            cs.execute();

            return cs.getInt(1);

        } catch (SQLException e) {
            System.err.println("❌ Error al contar asistencias: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public int countAsistenciasByClienteAndMonth(String documentoCliente, int mes, int anio) throws SQLException {
        String sql = "{? = call PKG_ASISTENCIAS.FN_CONTAR_ASISTENCIAS_CLIENTE_MES(?, ?, ?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setString(2, documentoCliente);
            cs.setInt(3, mes);
            cs.setInt(4, anio);
            cs.execute();

            return cs.getInt(1);

        } catch (SQLException e) {
            System.err.println("❌ Error al contar asistencias del mes: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Asistencias> findAll() throws SQLException {
        String sql = "{? = call PKG_ASISTENCIAS.FN_LISTAR_ASISTENCIAS()}";
        List<Asistencias> asistencias = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                asistencias.add(mapResultSetToAsistencia(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al listar asistencias: " + e.getMessage());
            throw e;
        }

        return asistencias;
    }

    @Override
    public void update(Asistencias entity) throws SQLException {
        String sql = "{call PKG_ASISTENCIAS.PR_ACTUALIZAR_ASISTENCIA(?, ?, ?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, entity.getIdAsistencia());
            cs.setDate(2, entity.getFecha() != null ? Date.valueOf(entity.getFecha()) : null);
            cs.setString(3, entity.getCliente().getDocumento());

            cs.execute();
            System.out.println("✅ Asistencia actualizada: " + entity.getIdAsistencia());

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar asistencia: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "{call PKG_ASISTENCIAS.PR_ELIMINAR_ASISTENCIA(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.execute();
            System.out.println("✅ Asistencia eliminada: " + id);

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar asistencia: " + e.getMessage());
            throw e;
        }
    }

    private Asistencias mapResultSetToAsistencia(ResultSet rs) throws SQLException {
        Asistencias asistencia = new Asistencias();

        // Datos de Asistencia
        asistencia.setIdAsistencia(rs.getInt("ID_ASISTENCIA"));

        if (rs.getDate("FECHA") != null) {
            asistencia.setFecha(rs.getDate("FECHA").toLocalDate());
        }

        // Mapear Cliente
        Clientes cliente = new Clientes();
        cliente.setDocumento(rs.getString("DOCUMENTO"));
        cliente.setNombres(rs.getString("NOMBRES"));
        cliente.setApellidos(rs.getString("APELLIDOS"));

        if (rs.getDate("FECHA_NACIMIENTO") != null) {
            cliente.setFechaNacimiento(rs.getDate("FECHA_NACIMIENTO").toLocalDate());
        }

        cliente.setGenero(rs.getString("GENERO"));
        cliente.setTelefono(rs.getString("TELEFONO"));
        cliente.setCorreo(rs.getString("CORREO"));
        cliente.setDireccion(rs.getString("DIRECCION"));

        if (rs.getDate("FECHA_REGISTRO") != null) {
            cliente.setFechaRegistro(rs.getDate("FECHA_REGISTRO").toLocalDate());
        }

        // Mapear Barrio del cliente
        int idBarrio = rs.getInt("ID_BARRIO");
        if (!rs.wasNull()) {
            Barrios barrio = new Barrios();
            barrio.setIdBarrio(idBarrio);
            barrio.setNombreBarrio(rs.getString("NOM_BARRIO"));
            cliente.setBarrio(barrio);
        }

        asistencia.setCliente(cliente);

        return asistencia;
    }
}
