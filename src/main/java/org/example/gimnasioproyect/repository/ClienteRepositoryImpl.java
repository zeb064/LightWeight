package org.example.gimnasioproyect.repository;

import org.example.gimnasioproyect.model.Barrios;
import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.confi.OracleDatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteRepositoryImpl implements ClienteRepository{
    private final OracleDatabaseConnection connection;

    public ClienteRepositoryImpl(OracleDatabaseConnection connection) throws SQLException {
        this.connection = connection;
        try (Connection conn = this.connection.connect()) {
            System.out.println("🎯 Conexión a BD probada exitosamente - ClienteRepository");
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void save(Clientes entity) throws SQLException {
        String sql = "INSERT INTO CLIENTES (DOCUMENTO, NOMBRES, APELLIDOS, FECHA_NACIMIENTO, " +
                "GENERO, TELEFONO, CORREO, DIRECCION, FECHA_REGISTRO, ID_BARRIO) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, entity.getDocumento());
            ps.setString(2, entity.getNombres());
            ps.setString(3, entity.getApellidos());
            ps.setDate(4, entity.getFechaNacimiento() != null ?
                    java.sql.Date.valueOf(entity.getFechaNacimiento()) : null);
            ps.setString(5, entity.getGenero());
            ps.setString(6, entity.getTelefono());
            ps.setString(7, entity.getCorreo());
            ps.setString(8, entity.getDireccion());
            ps.setDate(9, entity.getFechaRegistro() != null ?
                    java.sql.Date.valueOf(entity.getFechaRegistro()) : null);
            ps.setInt(10, entity.getBarrio() != null ? entity.getBarrio().getIdBarrio() : null);

            ps.executeUpdate();
            System.out.println("✅ Cliente guardado exitosamente: " + entity.getDocumento());

        } catch (SQLException e) {
            System.err.println("❌ Error al guardar cliente: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Clientes> findById(String documento) throws SQLException {
        return findByDocumento(documento);
    }

    @Override
    public Optional<Clientes> findByDocumento(String documento) throws SQLException {
        String sql = "SELECT c.DOCUMENTO, c.NOMBRES, c.APELLIDOS, c.FECHA_NACIMIENTO, " +
                "c.GENERO, c.TELEFONO, c.CORREO, c.DIRECCION, c.FECHA_REGISTRO, " +
                "c.ID_BARRIO, b.NOM_BARRIO " +
                "FROM CLIENTES c " +
                "LEFT JOIN BARRIOS b ON c.ID_BARRIO = b.ID_BARRIO " +
                "WHERE c.DOCUMENTO = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, documento);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Clientes cliente = mapResultSetToCliente(rs);
                return Optional.of(cliente);
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar cliente: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Clientes> findAll() throws SQLException {
        String sql = "SELECT c.DOCUMENTO, c.NOMBRES, c.APELLIDOS, c.FECHA_NACIMIENTO, " +
                "c.GENERO, c.TELEFONO, c.CORREO, c.DIRECCION, c.FECHA_REGISTRO, " +
                "c.ID_BARRIO, b.NOM_BARRIO " +
                "FROM CLIENTES c " +
                "LEFT JOIN BARRIOS b ON c.ID_BARRIO = b.ID_BARRIO " +
                "ORDER BY c.NOMBRES, c.APELLIDOS";

        List<Clientes> clientes = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapResultSetToCliente(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al listar clientes: " + e.getMessage());
            throw e;
        }

        return clientes;
    }

    @Override
    public List<Clientes> findByNombre(String nombre) throws SQLException {
        String sql = "SELECT c.DOCUMENTO, c.NOMBRES, c.APELLIDOS, c.FECHA_NACIMIENTO, " +
                "c.GENERO, c.TELEFONO, c.CORREO, c.DIRECCION, c.FECHA_REGISTRO, " +
                "c.ID_BARRIO, b.NOM_BARRIO " +
                "FROM CLIENTES c " +
                "LEFT JOIN BARRIOS b ON c.ID_BARRIO = b.ID_BARRIO " +
                "WHERE UPPER(c.NOMBRES) LIKE ? OR UPPER(c.APELLIDOS) LIKE ? " +
                "ORDER BY c.NOMBRES, c.APELLIDOS";

        List<Clientes> clientes = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String parametro = "%" + nombre.toUpperCase() + "%";
            ps.setString(1, parametro);
            ps.setString(2, parametro);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                clientes.add(mapResultSetToCliente(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar clientes por nombre: " + e.getMessage());
            throw e;
        }

        return clientes;
    }

    @Override
    public List<Clientes> findByBarrio(Integer idBarrio) throws SQLException {
        String sql = "SELECT c.DOCUMENTO, c.NOMBRES, c.APELLIDOS, c.FECHA_NACIMIENTO, " +
                "c.GENERO, c.TELEFONO, c.CORREO, c.DIRECCION, c.FECHA_REGISTRO, " +
                "c.ID_BARRIO, b.NOM_BARRIO " +
                "FROM CLIENTES c " +
                "LEFT JOIN BARRIOS b ON c.ID_BARRIO = b.ID_BARRIO " +
                "WHERE c.ID_BARRIO = ? " +
                "ORDER BY c.NOMBRES, c.APELLIDOS";

        List<Clientes> clientes = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idBarrio);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                clientes.add(mapResultSetToCliente(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar clientes por barrio: " + e.getMessage());
            throw e;
        }

        return clientes;
    }

    @Override
    public void update(Clientes entity) throws SQLException {
        String sql = "UPDATE CLIENTES SET NOMBRES = ?, APELLIDOS = ?, FECHA_NACIMIENTO = ?, " +
                "GENERO = ?, TELEFONO = ?, CORREO = ?, DIRECCION = ?, ID_BARRIO = ? " +
                "WHERE DOCUMENTO = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, entity.getNombres());
            ps.setString(2, entity.getApellidos());
            ps.setDate(3, entity.getFechaNacimiento() != null ?
                    java.sql.Date.valueOf(entity.getFechaNacimiento()) : null);
            ps.setString(4, entity.getGenero());
            ps.setString(5, entity.getTelefono());
            ps.setString(6, entity.getCorreo());
            ps.setString(7, entity.getDireccion());
            ps.setInt(8, entity.getBarrio() != null ? entity.getBarrio().getIdBarrio() : null);
            ps.setString(9, entity.getDocumento());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Cliente actualizado: " + entity.getDocumento());
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar cliente: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(String documento) throws SQLException {
        String sql = "DELETE FROM CLIENTES WHERE DOCUMENTO = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, documento);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Cliente eliminado: " + documento);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar cliente: " + e.getMessage());
            throw e;
        }
    }

    private Clientes mapResultSetToCliente(ResultSet rs) throws SQLException {
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

        // Mapear barrio
        int idBarrio = rs.getInt("ID_BARRIO");
        if (!rs.wasNull()) {
            Barrios barrio = new Barrios();
            barrio.setIdBarrio(idBarrio);
            barrio.setNombreBarrio(rs.getString("NOM_BARRIO"));
            cliente.setBarrio(barrio);
        }

        return cliente;
    }
}
