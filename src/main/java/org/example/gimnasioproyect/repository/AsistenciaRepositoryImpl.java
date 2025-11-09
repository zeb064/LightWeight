package org.example.gimnasioproyect.repository;

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
        String sql = "SELECT a.ID_ASISTENCIA, a.FECHA, " +
                "c.DOCUMENTO, c.NOMBRES, c.APELLIDOS, c.FECHA_NACIMIENTO, c.GENERO, " +
                "c.TELEFONO, c.CORREO, c.DIRECCION, c.FECHA_REGISTRO, " +
                "c.ID_BARRIO, b.NOM_BARRIO " +
                "FROM ASISTENCIAS a " +
                "INNER JOIN CLIENTES c ON a.DOCUMENTO = c.DOCUMENTO " +
                "LEFT JOIN BARRIOS b ON c.ID_BARRIO = b.ID_BARRIO " +
                "WHERE a.ID_ASISTENCIA = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

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
        String sql = "SELECT a.ID_ASISTENCIA, a.FECHA, " +
                "c.DOCUMENTO, c.NOMBRES, c.APELLIDOS, c.FECHA_NACIMIENTO, c.GENERO, " +
                "c.TELEFONO, c.CORREO, c.DIRECCION, c.FECHA_REGISTRO, " +
                "c.ID_BARRIO, b.NOM_BARRIO " +
                "FROM ASISTENCIAS a " +
                "JOIN CLIENTES c ON a.DOCUMENTO = c.DOCUMENTO " +
                "LEFT JOIN BARRIOS b ON c.ID_BARRIO = b.ID_BARRIO " +
                "WHERE a.DOCUMENTO = ? " +
                "ORDER BY a.FECHA DESC";

        List<Asistencias> asistencias = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, documentoCliente);
            ResultSet rs = ps.executeQuery();

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
        String sql = "SELECT a.ID_ASISTENCIA, a.FECHA, " +
                "c.DOCUMENTO, c.NOMBRES, c.APELLIDOS, c.FECHA_NACIMIENTO, c.GENERO, " +
                "c.TELEFONO, c.CORREO, c.DIRECCION, c.FECHA_REGISTRO, " +
                "c.ID_BARRIO, b.NOM_BARRIO " +
                "FROM ASISTENCIAS a " +
                "INNER JOIN CLIENTES c ON a.DOCUMENTO = c.DOCUMENTO " +
                "LEFT JOIN BARRIOS b ON c.ID_BARRIO = b.ID_BARRIO " +
                "WHERE TRUNC(a.FECHA) = ? " +
                "ORDER BY a.FECHA DESC";

        List<Asistencias> asistencias = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(fecha));
            ResultSet rs = ps.executeQuery();

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
        String sql = "SELECT a.ID_ASISTENCIA, a.FECHA, " +
                "c.DOCUMENTO, c.NOMBRES, c.APELLIDOS, c.FECHA_NACIMIENTO, c.GENERO, " +
                "c.TELEFONO, c.CORREO, c.DIRECCION, c.FECHA_REGISTRO, " +
                "c.ID_BARRIO, b.NOM_BARRIO " +
                "FROM ASISTENCIAS a " +
                "INNER JOIN CLIENTES c ON a.DOCUMENTO = c.DOCUMENTO " +
                "LEFT JOIN BARRIOS b ON c.ID_BARRIO = b.ID_BARRIO " +
                "WHERE a.DOCUMENTO = ? AND a.FECHA BETWEEN ? AND ? " +
                "ORDER BY a.FECHA DESC";

        List<Asistencias> asistencias = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, documentoCliente);
            ps.setDate(2, Date.valueOf(fechaInicio));
            ps.setDate(3, Date.valueOf(fechaFin));
            ResultSet rs = ps.executeQuery();

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
        String sql = "SELECT COUNT(*) FROM ASISTENCIAS WHERE DOCUMENTO = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, documentoCliente);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al contar asistencias: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public int countAsistenciasByClienteAndMonth(String documentoCliente, int mes, int anio) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ASISTENCIAS " +
                "WHERE DOCUMENTO = ? AND EXTRACT(MONTH FROM FECHA) = ? AND EXTRACT(YEAR FROM FECHA) = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, documentoCliente);
            ps.setInt(2, mes);
            ps.setInt(3, anio);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al contar asistencias del mes: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Asistencias> findAll() throws SQLException {
        String sql = "SELECT a.ID_ASISTENCIA, a.FECHA, " +
                "c.DOCUMENTO, c.NOMBRES, c.APELLIDOS, c.FECHA_NACIMIENTO, c.GENERO, " +
                "c.TELEFONO, c.CORREO, c.DIRECCION, c.FECHA_REGISTRO, " +
                "c.ID_BARRIO, b.NOM_BARRIO " +
                "FROM ASISTENCIAS a " +
                "INNER JOIN CLIENTES c ON a.DOCUMENTO = c.DOCUMENTO " +
                "LEFT JOIN BARRIOS b ON c.ID_BARRIO = b.ID_BARRIO " +
                "ORDER BY a.FECHA DESC";

        List<Asistencias> asistencias = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

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
        String sql = "UPDATE ASISTENCIAS SET FECHA = ?, DOCUMENTO = ? WHERE ID_ASISTENCIA = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, entity.getFecha() != null ? Date.valueOf(entity.getFecha()) : null);
            ps.setString(2, entity.getCliente().getDocumento());
            ps.setInt(3, entity.getIdAsistencia());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Asistencia actualizada: " + entity.getIdAsistencia());
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar asistencia: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM ASISTENCIAS WHERE ID_ASISTENCIA = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Asistencia eliminada: " + id);
            }

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
