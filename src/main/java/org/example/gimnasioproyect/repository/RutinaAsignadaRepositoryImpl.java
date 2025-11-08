package org.example.gimnasioproyect.repository;


import org.example.gimnasioproyect.model.Barrios;
import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.model.RutinaAsignadas;
import org.example.gimnasioproyect.model.Rutinas;
import org.example.gimnasioproyect.confi.OracleDatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RutinaAsignadaRepositoryImpl implements RutinaAsignadaRepository{
    private final OracleDatabaseConnection connection;

    public RutinaAsignadaRepositoryImpl(OracleDatabaseConnection connection) throws SQLException {
        this.connection = connection;
        try (Connection conn = this.connection.connect()) {
            System.out.println("🎯 Conexión a BD probada exitosamente - RutinaAsignadaRepository");
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void save(RutinaAsignadas entity) throws SQLException {
        String sql = "INSERT INTO RUTINASCLIENTES (ID_RUTINA, DOCUMENTO, " +
                "FECHA_ASIGNACION, FECHA_FINALIZACION, ESTADO) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            //ps.setInt(1, entity.getIdRutinaCliente());
            ps.setInt(1, entity.getRutina().getIdRutina());
            ps.setString(2, entity.getCliente().getDocumento());
            ps.setDate(3, entity.getFechaAsignacion() != null ?
                    Date.valueOf(entity.getFechaAsignacion()) : null);
            ps.setDate(4, entity.getFechaFinalizacion() != null ?
                    Date.valueOf(entity.getFechaFinalizacion()) : null);
            ps.setString(5, entity.getEstado());

            ps.executeUpdate();
            System.out.println("✅ Rutina asignada exitosamente al cliente: " +
                    entity.getCliente().getDocumento());

        } catch (SQLException e) {
            System.err.println("❌ Error al guardar rutina asignada: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<RutinaAsignadas> findById(Integer id) throws SQLException {
        String sql = "SELECT rc.ID_RUTINA_CLIENTE, rc.FECHA_ASIGNACION, rc.FECHA_FINALIZACION, rc.ESTADO, " +
                "r.ID_RUTINA, r.OBJETIVO, " +
                "c.DOCUMENTO, c.NOMBRES, c.APELLIDOS, c.FECHA_NACIMIENTO, c.GENERO, " +
                "c.TELEFONO, c.CORREO, c.DIRECCION, c.FECHA_REGISTRO, " +
                "c.ID_BARRIO, b.NOM_BARRIO " +
                "FROM RUTINASCLIENTES rc " +
                "INNER JOIN RUTINAS r ON rc.ID_RUTINA = r.ID_RUTINA " +
                "INNER JOIN CLIENTES c ON rc.DOCUMENTO = c.DOCUMENTO " +
                "LEFT JOIN BARRIOS b ON c.ID_BARRIO = b.ID_BARRIO " +
                "WHERE rc.ID_RUTINA_CLIENTE = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                RutinaAsignadas rutinaAsignada = mapResultSetToRutinaAsignada(rs);
                return Optional.of(rutinaAsignada);
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar rutina asignada: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<RutinaAsignadas> findByCliente(String documentoCliente) throws SQLException {
        String sql = "SELECT rc.ID_RUTINA_CLIENTE, rc.FECHA_ASIGNACION, rc.FECHA_FINALIZACION, rc.ESTADO, " +
                "r.ID_RUTINA, r.OBJETIVO, " +
                "c.DOCUMENTO, c.NOMBRES, c.APELLIDOS, c.FECHA_NACIMIENTO, c.GENERO, " +
                "c.TELEFONO, c.CORREO, c.DIRECCION, c.FECHA_REGISTRO, " +
                "c.ID_BARRIO, b.NOM_BARRIO " +
                "FROM RUTINASCLIENTES rc " +
                "INNER JOIN RUTINAS r ON rc.ID_RUTINA = r.ID_RUTINA " +
                "INNER JOIN CLIENTES c ON rc.DOCUMENTO = c.DOCUMENTO " +
                "LEFT JOIN BARRIOS b ON c.ID_BARRIO = b.ID_BARRIO " +
                "WHERE rc.DOCUMENTO = ? " +
                "ORDER BY rc.FECHA_ASIGNACION DESC";

        List<RutinaAsignadas> rutinasAsignadas = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, documentoCliente);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                rutinasAsignadas.add(mapResultSetToRutinaAsignada(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar rutinas del cliente: " + e.getMessage());
            throw e;
        }

        return rutinasAsignadas;
    }

    @Override
    public List<RutinaAsignadas> findRutinasActivasByCliente(String documentoCliente) throws SQLException {
        String sql = "SELECT rc.ID_RUTINA_CLIENTE, rc.FECHA_ASIGNACION, rc.FECHA_FINALIZACION, rc.ESTADO, " +
                "r.ID_RUTINA, r.OBJETIVO, " +
                "c.DOCUMENTO, c.NOMBRES, c.APELLIDOS, c.FECHA_NACIMIENTO, c.GENERO, " +
                "c.TELEFONO, c.CORREO, c.DIRECCION, c.FECHA_REGISTRO, " +
                "c.ID_BARRIO, b.NOM_BARRIO " +
                "FROM RUTINASCLIENTES rc " +
                "INNER JOIN RUTINAS r ON rc.ID_RUTINA = r.ID_RUTINA " +
                "INNER JOIN CLIENTES c ON rc.DOCUMENTO = c.DOCUMENTO " +
                "LEFT JOIN BARRIOS b ON c.ID_BARRIO = b.ID_BARRIO " +
                "WHERE rc.DOCUMENTO = ? AND rc.ESTADO = 'ACTIVA' " +
                "ORDER BY rc.FECHA_ASIGNACION DESC";

        List<RutinaAsignadas> rutinasAsignadas = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, documentoCliente);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                rutinasAsignadas.add(mapResultSetToRutinaAsignada(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar rutinas activas del cliente: " + e.getMessage());
            throw e;
        }

        return rutinasAsignadas;
    }

    @Override
    public List<RutinaAsignadas> findByRutina(Integer idRutina) throws SQLException {
        String sql = "SELECT rc.ID_RUTINA_CLIENTE, rc.FECHA_ASIGNACION, rc.FECHA_FINALIZACION, rc.ESTADO, " +
                "r.ID_RUTINA, r.OBJETIVO, " +
                "c.DOCUMENTO, c.NOMBRES, c.APELLIDOS, c.FECHA_NACIMIENTO, c.GENERO, " +
                "c.TELEFONO, c.CORREO, c.DIRECCION, c.FECHA_REGISTRO, " +
                "c.ID_BARRIO, b.NOM_BARRIO " +
                "FROM RUTINASCLIENTES rc " +
                "INNER JOIN RUTINAS r ON rc.ID_RUTINA = r.ID_RUTINA " +
                "INNER JOIN CLIENTES c ON rc.DOCUMENTO = c.DOCUMENTO " +
                "LEFT JOIN BARRIOS b ON c.ID_BARRIO = b.ID_BARRIO " +
                "WHERE rc.ID_RUTINA = ? " +
                "ORDER BY rc.FECHA_ASIGNACION DESC";

        List<RutinaAsignadas> rutinasAsignadas = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idRutina);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                rutinasAsignadas.add(mapResultSetToRutinaAsignada(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar asignaciones de rutina: " + e.getMessage());
            throw e;
        }

        return rutinasAsignadas;
    }

    @Override
    public List<RutinaAsignadas> findByEstado(String estado) throws SQLException {
        String sql = "SELECT rc.ID_RUTINA_CLIENTE, rc.FECHA_ASIGNACION, rc.FECHA_FINALIZACION, rc.ESTADO, " +
                "r.ID_RUTINA, r.OBJETIVO, " +
                "c.DOCUMENTO, c.NOMBRES, c.APELLIDOS, c.FECHA_NACIMIENTO, c.GENERO, " +
                "c.TELEFONO, c.CORREO, c.DIRECCION, c.FECHA_REGISTRO, " +
                "c.ID_BARRIO, b.NOM_BARRIO " +
                "FROM RUTINASCLIENTES rc " +
                "INNER JOIN RUTINAS r ON rc.ID_RUTINA = r.ID_RUTINA " +
                "INNER JOIN CLIENTES c ON rc.DOCUMENTO = c.DOCUMENTO " +
                "LEFT JOIN BARRIOS b ON c.ID_BARRIO = b.ID_BARRIO " +
                "WHERE UPPER(rc.ESTADO) = ? " +
                "ORDER BY rc.FECHA_ASIGNACION DESC";

        List<RutinaAsignadas> rutinasAsignadas = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, estado.toUpperCase());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                rutinasAsignadas.add(mapResultSetToRutinaAsignada(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar rutinas por estado: " + e.getMessage());
            throw e;
        }

        return rutinasAsignadas;
    }

    @Override
    public List<RutinaAsignadas> findAll() throws SQLException {
        String sql = "SELECT rc.ID_RUTINA_CLIENTE, rc.FECHA_ASIGNACION, rc.FECHA_FINALIZACION, rc.ESTADO, " +
                "r.ID_RUTINA, r.OBJETIVO, " +
                "c.DOCUMENTO, c.NOMBRES, c.APELLIDOS, c.FECHA_NACIMIENTO, c.GENERO, " +
                "c.TELEFONO, c.CORREO, c.DIRECCION, c.FECHA_REGISTRO, " +
                "c.ID_BARRIO, b.NOM_BARRIO " +
                "FROM RUTINASCLIENTES rc " +
                "INNER JOIN RUTINAS r ON rc.ID_RUTINA = r.ID_RUTINA " +
                "INNER JOIN CLIENTES c ON rc.DOCUMENTO = c.DOCUMENTO " +
                "LEFT JOIN BARRIOS b ON c.ID_BARRIO = b.ID_BARRIO " +
                "ORDER BY rc.ID_RUTINA_CLIENTE DESC";

        List<RutinaAsignadas> rutinasAsignadas = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rutinasAsignadas.add(mapResultSetToRutinaAsignada(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al listar rutinas asignadas: " + e.getMessage());
            throw e;
        }

        return rutinasAsignadas;
    }

    @Override
    public void update(RutinaAsignadas entity) throws SQLException {
        String sql = "UPDATE RUTINASCLIENTES SET ID_RUTINA = ?, DOCUMENTO = ?, " +
                "FECHA_ASIGNACION = ?, FECHA_FINALIZACION = ?, ESTADO = ? " +
                "WHERE ID_RUTINA_CLIENTE = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, entity.getRutina().getIdRutina());
            ps.setString(2, entity.getCliente().getDocumento());
            ps.setDate(3, entity.getFechaAsignacion() != null ?
                    Date.valueOf(entity.getFechaAsignacion()) : null);
            ps.setDate(4, entity.getFechaFinalizacion() != null ?
                    Date.valueOf(entity.getFechaFinalizacion()) : null);
            ps.setString(5, entity.getEstado());
            ps.setInt(6, entity.getIdRutinaCliente());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Rutina asignada actualizada: " + entity.getIdRutinaCliente());
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar rutina asignada: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM RUTINASCLIENTES WHERE ID_RUTINA_CLIENTE = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Rutina asignada eliminada: " + id);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar rutina asignada: " + e.getMessage());
            throw e;
        }
    }

    private RutinaAsignadas mapResultSetToRutinaAsignada(ResultSet rs) throws SQLException {
        RutinaAsignadas rutinaAsignada = new RutinaAsignadas();

        // Datos de RutinaAsignada
        rutinaAsignada.setIdRutinaCliente(rs.getInt("ID_RUTINA_CLIENTE"));

        if (rs.getDate("FECHA_ASIGNACION") != null) {
            rutinaAsignada.setFechaAsignacion(rs.getDate("FECHA_ASIGNACION").toLocalDate());
        }
        if (rs.getDate("FECHA_FINALIZACION") != null) {
            rutinaAsignada.setFechaFinalizacion(rs.getDate("FECHA_FINALIZACION").toLocalDate());
        }

        rutinaAsignada.setEstado(rs.getString("ESTADO"));

        // Mapear Rutina
        Rutinas rutina = new Rutinas();
        rutina.setIdRutina(rs.getInt("ID_RUTINA"));
        rutina.setObjetivo(rs.getString("OBJETIVO"));
        rutinaAsignada.setRutina(rutina);

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

        rutinaAsignada.setCliente(cliente);

        return rutinaAsignada;
    }
}
