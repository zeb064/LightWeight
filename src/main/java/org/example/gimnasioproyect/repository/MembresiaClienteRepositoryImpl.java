package org.example.gimnasioproyect.repository;

import org.example.gimnasioproyect.model.Barrios;
import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.model.MembresiaClientes;
import org.example.gimnasioproyect.model.Membresias;
import org.example.gimnasioproyect.confi.OracleDatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MembresiaClienteRepositoryImpl implements MembresiaClienteRepository{
    private final OracleDatabaseConnection connection;

    public MembresiaClienteRepositoryImpl(OracleDatabaseConnection connection) throws SQLException {
        this.connection = connection;
        try (Connection conn = this.connection.connect()) {
            System.out.println("🎯 Conexión a BD probada exitosamente - MembresiaClienteRepository");
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void save(MembresiaClientes entity) throws SQLException {
        String sql = "{call PKG_MEMBRESIAS_CLIENTES.PR_INSERTAR_MEMBRESIA_CLIENTE(?, ?, ?, ?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            //ps.setInt(1, entity.getIdMembresiaCliente());
            cs.setInt(1, entity.getMembresia().getIdMembresia());
            cs.setString(2, entity.getCliente().getDocumento());
            cs.setDate(3, entity.getFechaAsignacion() != null ?
                    Date.valueOf(entity.getFechaAsignacion()) : null);
            cs.setDate(4, entity.getFechaFinalizacion() != null ?
                    Date.valueOf(entity.getFechaFinalizacion()) : null);

            cs.execute();
            System.out.println("✅ Membresía asignada exitosamente al cliente: " +
                    entity.getCliente().getDocumento());

        } catch (SQLException e) {
            System.err.println("❌ Error al guardar membresía cliente: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<MembresiaClientes> findById(Integer id) throws SQLException {
        String sql = "SELECT mc.ID_MEMBRESIA_CLIENTE, mc.FECHA_ASIGNACION, mc.FECHA_FINALIZACION, " +
                "m.ID_MEMBRESIA, m.TIPO, m.PRECIO_MEMBRESIA, " +
                "c.DOCUMENTO, c.NOMBRES, c.APELLIDOS, c.FECHA_NACIMIENTO, c.GENERO, " +
                "c.TELEFONO, c.CORREO, c.DIRECCION, c.FECHA_REGISTRO, " +
                "c.ID_BARRIO, b.NOM_BARRIO " +
                "FROM MEMBRESIASCLIENTES mc " +
                "JOIN MEMBRESIAS m ON mc.ID_MEMBRESIA = m.ID_MEMBRESIA " +
                "JOIN CLIENTES c ON mc.DOCUMENTO = c.DOCUMENTO " +
                "LEFT JOIN BARRIOS b ON c.ID_BARRIO = b.ID_BARRIO " +
                "WHERE mc.ID_MEMBRESIA_CLIENTE = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                MembresiaClientes membresiaCliente = mapResultSetToMembresiaCliente(rs);
                return Optional.of(membresiaCliente);
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar membresía cliente: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<MembresiaClientes> findMembresiaActivaByCliente(String documentoCliente) throws SQLException {
        String sql = "SELECT mc.ID_MEMBRESIA_CLIENTE, mc.FECHA_ASIGNACION, mc.FECHA_FINALIZACION, " +
                "m.ID_MEMBRESIA, m.TIPO, m.PRECIO_MEMBRESIA, " +
                "c.DOCUMENTO, c.NOMBRES, c.APELLIDOS, c.FECHA_NACIMIENTO, c.GENERO, " +
                "c.TELEFONO, c.CORREO, c.DIRECCION, c.FECHA_REGISTRO, " +
                "c.ID_BARRIO, b.NOM_BARRIO " +
                "FROM MEMBRESIASCLIENTES mc " +
                "JOIN MEMBRESIAS m ON mc.ID_MEMBRESIA = m.ID_MEMBRESIA " +
                "JOIN CLIENTES c ON mc.DOCUMENTO = c.DOCUMENTO " +
                "LEFT JOIN BARRIOS b ON c.ID_BARRIO = b.ID_BARRIO " +
                "WHERE mc.DOCUMENTO = ? " +
                "AND (mc.FECHA_FINALIZACION IS NULL OR mc.FECHA_FINALIZACION >= SYSDATE) " +
                "ORDER BY mc.FECHA_ASIGNACION DESC " +
                "FETCH FIRST 1 ROW ONLY";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, documentoCliente);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                MembresiaClientes membresiaCliente = mapResultSetToMembresiaCliente(rs);
                return Optional.of(membresiaCliente);
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar membresía activa: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<MembresiaClientes> findByCliente(String documentoCliente) throws SQLException {
        String sql = "SELECT mc.ID_MEMBRESIA_CLIENTE, mc.FECHA_ASIGNACION, mc.FECHA_FINALIZACION, " +
                "m.ID_MEMBRESIA, m.TIPO, m.PRECIO_MEMBRESIA, " +
                "c.DOCUMENTO, c.NOMBRES, c.APELLIDOS, c.FECHA_NACIMIENTO, c.GENERO, " +
                "c.TELEFONO, c.CORREO, c.DIRECCION, c.FECHA_REGISTRO, " +
                "c.ID_BARRIO, b.NOM_BARRIO " +
                "FROM MEMBRESIASCLIENTES mc " +
                "JOIN MEMBRESIAS m ON mc.ID_MEMBRESIA = m.ID_MEMBRESIA " +
                "JOIN CLIENTES c ON mc.DOCUMENTO = c.DOCUMENTO " +
                "LEFT JOIN BARRIOS b ON c.ID_BARRIO = b.ID_BARRIO " +
                "WHERE mc.DOCUMENTO = ? " +
                "ORDER BY mc.FECHA_ASIGNACION DESC";

        List<MembresiaClientes> membresiasCliente = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, documentoCliente);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                membresiasCliente.add(mapResultSetToMembresiaCliente(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar membresías del cliente: " + e.getMessage());
            throw e;
        }

        return membresiasCliente;
    }

    @Override
    public List<MembresiaClientes> findMembresiasProximasAVencer(int dias) throws SQLException {
        String sql = "SELECT mc.ID_MEMBRESIA_CLIENTE, mc.FECHA_ASIGNACION, mc.FECHA_FINALIZACION, " +
                "m.ID_MEMBRESIA, m.TIPO, m.PRECIO_MEMBRESIA, " +
                "c.DOCUMENTO, c.NOMBRES, c.APELLIDOS, c.FECHA_NACIMIENTO, c.GENERO, " +
                "c.TELEFONO, c.CORREO, c.DIRECCION, c.FECHA_REGISTRO, " +
                "c.ID_BARRIO, b.NOM_BARRIO " +
                "FROM MEMBRESIASCLIENTES mc " +
                "JOIN MEMBRESIAS m ON mc.ID_MEMBRESIA = m.ID_MEMBRESIA " +
                "JOIN CLIENTES c ON mc.DOCUMENTO = c.DOCUMENTO " +
                "LEFT JOIN BARRIOS b ON c.ID_BARRIO = b.ID_BARRIO " +
                "WHERE mc.FECHA_FINALIZACION BETWEEN SYSDATE AND SYSDATE + ? " +
                "ORDER BY mc.FECHA_FINALIZACION ASC";

        List<MembresiaClientes> membresiasCliente = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dias);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                membresiasCliente.add(mapResultSetToMembresiaCliente(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar membresías próximas a vencer: " + e.getMessage());
            throw e;
        }

        return membresiasCliente;
    }

    @Override
    public List<MembresiaClientes> findMembresiasVencidas() throws SQLException {
        String sql = "SELECT mc.ID_MEMBRESIA_CLIENTE, mc.FECHA_ASIGNACION, mc.FECHA_FINALIZACION, " +
                "m.ID_MEMBRESIA, m.TIPO, m.PRECIO_MEMBRESIA, " +
                "c.DOCUMENTO, c.NOMBRES, c.APELLIDOS, c.FECHA_NACIMIENTO, c.GENERO, " +
                "c.TELEFONO, c.CORREO, c.DIRECCION, c.FECHA_REGISTRO, " +
                "c.ID_BARRIO, b.NOM_BARRIO " +
                "FROM MEMBRESIASCLIENTES mc " +
                "JOIN MEMBRESIAS m ON mc.ID_MEMBRESIA = m.ID_MEMBRESIA " +
                "JOIN CLIENTES c ON mc.DOCUMENTO = c.DOCUMENTO " +
                "LEFT JOIN BARRIOS b ON c.ID_BARRIO = b.ID_BARRIO " +
                "WHERE mc.FECHA_FINALIZACION < SYSDATE " +
                "ORDER BY mc.FECHA_FINALIZACION DESC";

        List<MembresiaClientes> membresiasCliente = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                membresiasCliente.add(mapResultSetToMembresiaCliente(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar membresías vencidas: " + e.getMessage());
            throw e;
        }

        return membresiasCliente;
    }

    @Override
    public List<MembresiaClientes> findAll() throws SQLException {
        String sql = "SELECT mc.ID_MEMBRESIA_CLIENTE, mc.FECHA_ASIGNACION, mc.FECHA_FINALIZACION, " +
                "m.ID_MEMBRESIA, m.TIPO, m.PRECIO_MEMBRESIA, " +
                "c.DOCUMENTO, c.NOMBRES, c.APELLIDOS, c.FECHA_NACIMIENTO, c.GENERO, " +
                "c.TELEFONO, c.CORREO, c.DIRECCION, c.FECHA_REGISTRO, " +
                "c.ID_BARRIO, b.NOM_BARRIO " +
                "FROM MEMBRESIASCLIENTES mc " +
                "JOIN MEMBRESIAS m ON mc.ID_MEMBRESIA = m.ID_MEMBRESIA " +
                "JOIN CLIENTES c ON mc.DOCUMENTO = c.DOCUMENTO " +
                "LEFT JOIN BARRIOS b ON c.ID_BARRIO = b.ID_BARRIO " +
                "ORDER BY mc.ID_MEMBRESIA_CLIENTE DESC";

        List<MembresiaClientes> membresiasCliente = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                membresiasCliente.add(mapResultSetToMembresiaCliente(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al listar membresías cliente: " + e.getMessage());
            throw e;
        }

        return membresiasCliente;
    }

    @Override
    public void update(MembresiaClientes entity) throws SQLException {
        String sql = "UPDATE MEMBRESIASCLIENTES SET ID_MEMBRESIA = ?, DOCUMENTO = ?, " +
                "FECHA_ASIGNACION = ?, FECHA_FINALIZACION = ? " +
                "WHERE ID_MEMBRESIA_CLIENTE = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, entity.getMembresia().getIdMembresia());
            ps.setString(2, entity.getCliente().getDocumento());
            ps.setDate(3, entity.getFechaAsignacion() != null ?
                    Date.valueOf(entity.getFechaAsignacion()) : null);
            ps.setDate(4, entity.getFechaFinalizacion() != null ?
                    Date.valueOf(entity.getFechaFinalizacion()) : null);
            ps.setInt(5, entity.getIdMembresiaCliente());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Membresía cliente actualizada: " + entity.getIdMembresiaCliente());
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar membresía cliente: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM MEMBRESIASCLIENTES WHERE ID_MEMBRESIA_CLIENTE = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Membresía cliente eliminada: " + id);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar membresía cliente: " + e.getMessage());
            throw e;
        }
    }

    private MembresiaClientes mapResultSetToMembresiaCliente(ResultSet rs) throws SQLException {
        MembresiaClientes membresiaCliente = new MembresiaClientes();

        // Datos de MembresiaCliente
        membresiaCliente.setIdMembresiaCliente(rs.getInt("ID_MEMBRESIA_CLIENTE"));

        if (rs.getDate("FECHA_ASIGNACION") != null) {
            membresiaCliente.setFechaAsignacion(rs.getDate("FECHA_ASIGNACION").toLocalDate());
        }
        if (rs.getDate("FECHA_FINALIZACION") != null) {
            membresiaCliente.setFechaFinalizacion(rs.getDate("FECHA_FINALIZACION").toLocalDate());
        }

        // Mapear Membresia
        Membresias membresia = new Membresias();
        membresia.setIdMembresia(rs.getInt("ID_MEMBRESIA"));
        membresia.setTipoMembresia(rs.getString("TIPO"));
        membresia.setPrecioMembresia(rs.getDouble("PRECIO_MEMBRESIA"));
        membresiaCliente.setMembresia(membresia);

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

        membresiaCliente.setCliente(cliente);

        return membresiaCliente;
    }
}
