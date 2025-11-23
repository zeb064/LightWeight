package org.example.gimnasioproyect.repository;

import oracle.jdbc.internal.OracleTypes;
import org.example.gimnasioproyect.model.Barrios;
import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.confi.OracleDatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteRepositoryImpl implements ClienteRepository {
    private final OracleDatabaseConnection connection;

    public ClienteRepositoryImpl(OracleDatabaseConnection connection) throws SQLException {
        this.connection = connection;
        try (Connection conn = this.connection.connect()) {
            System.out.println("Conexión a BD probada exitosamente - ClienteRepository");
        } catch (SQLException e) {
            System.err.println("Error al conectar: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void save(Clientes entity) throws SQLException {
        String sql = "{call PKG_CLIENTES.PR_INSERTAR_CLIENTE(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, entity.getDocumento());
            cs.setString(2, entity.getNombres());
            cs.setString(3, entity.getApellidos());
            cs.setDate(4, entity.getFechaNacimiento() != null ?
                    java.sql.Date.valueOf(entity.getFechaNacimiento()) : null);
            cs.setString(5, entity.getGenero());
            cs.setString(6, entity.getTelefono());
            cs.setString(7, entity.getCorreo());
            cs.setString(8, entity.getDireccion());
            cs.setDate(9, entity.getFechaRegistro() != null ?
                    java.sql.Date.valueOf(entity.getFechaRegistro()) : null);
            cs.setInt(10, entity.getBarrio() != null ? entity.getBarrio().getIdBarrio() : null);

            cs.execute();
            System.out.println("Cliente guardado exitosamente: " + entity.getDocumento());

        } catch (SQLException e) {
            System.err.println("Error al guardar cliente: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Clientes> findById(String documento) throws SQLException {
        return findByDocumento(documento);
    }

    @Override
    public Optional<Clientes> findByDocumento(String documento) throws SQLException {
        String sql = "{ ? = call PKG_CLIENTES.FN_OBTENER_CLIENTE_POR_DOCUMENTO(?) }";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, documento);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            if (rs.next()) {
                Clientes cliente = mapResultSetToCliente(rs);
                return Optional.of(cliente);
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("Error al buscar cliente: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Clientes> findAll() throws SQLException {
        String sql = "{ ? = call PKG_CLIENTES.FN_LISTAR_CLIENTES() }";
        List<Clientes> clientes = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                clientes.add(mapResultSetToCliente(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar clientes: " + e.getMessage());
            throw e;
        }

        return clientes;
    }

    @Override
    public List<Clientes> findByNombre(String nombre) throws SQLException {
        String sql = "{ ? = call PKG_CLIENTES.FN_BUSCAR_CLIENTES_POR_NOMBRE(?) }";
        List<Clientes> clientes = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, nombre);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                clientes.add(mapResultSetToCliente(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar clientes por nombre: " + e.getMessage());
            throw e;
        }

        return clientes;
    }

    @Override
    public List<Clientes> findByBarrio(Integer idBarrio) throws SQLException {
        String sql = "{ ? = call PKG_CLIENTES.FN_BUSCAR_CLIENTES_POR_BARRIO(?) }";
        List<Clientes> clientes = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setInt(2, idBarrio);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                clientes.add(mapResultSetToCliente(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar clientes por barrio: " + e.getMessage());
            throw e;
        }

        return clientes;
    }

    @Override
    public Optional<Clientes> findByChatId(String chatId) throws SQLException {
        String sql = "{? = call PKG_CLIENTES.FN_BUSCAR_POR_CHAT_ID(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, chatId);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            if (rs.next()) {
                return Optional.of(mapResultSetToCliente(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("Error al buscar cliente por chatId: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void update(Clientes entity) throws SQLException {
        String sql = "{call PKG_CLIENTES.PR_ACTUALIZAR_CLIENTE(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, entity.getDocumento());
            cs.setString(2, entity.getNombres());
            cs.setString(3, entity.getApellidos());
            cs.setDate(4, entity.getFechaNacimiento() != null ?
                    java.sql.Date.valueOf(entity.getFechaNacimiento()) : null);
            cs.setString(5, entity.getGenero());
            cs.setString(6, entity.getTelefono());
            cs.setString(7, entity.getCorreo());
            cs.setString(8, entity.getDireccion());
            cs.setInt(9, entity.getBarrio() != null ? entity.getBarrio().getIdBarrio() : null);
            cs.setString(10, entity.getChatId());

            cs.execute();
            System.out.println("Cliente actualizado: " + entity.getDocumento());

        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(String documento) throws SQLException {
        String sql = "{call PKG_CLIENTES.PR_ELIMINAR_CLIENTE(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, documento);
            cs.execute();
            System.out.println("Cliente eliminado: " + documento);

        } catch (SQLException e) {
            System.err.println("Error al eliminar cliente con documento " + documento );
            if(e.getErrorCode() == 20015){
                throw new IllegalArgumentException("No se puede eliminar el cliente porque tiene registros de asistencias.");
            } else if (e.getErrorCode() == 20016) {
                throw new IllegalArgumentException("No se puede eliminar el cliente porque tiene membresías activas o vencidas.");
            } else if (e.getErrorCode() == 20017) {
                throw new IllegalArgumentException("No se puede eliminar el cliente porque tiene rutinas asignadas.");
            } else if (e.getErrorCode() == 20018) {
                throw new IllegalArgumentException("No se puede eliminar el cliente porque tiene entrenadores asignados.");
            }
            throw e;
        }
    }

    // Metodo auxiliar para mapear ResultSet a Cliente
    private Clientes mapResultSetToCliente(ResultSet rs) throws SQLException {
        Clientes cliente = new Clientes();
        cliente.setDocumento(rs.getString("DOCUMENTO"));
        cliente.setNombres(rs.getString("NOMBRES"));
        cliente.setApellidos(rs.getString("APELLIDOS"));

        java.sql.Date fechaNac = rs.getDate("FECHA_NACIMIENTO");
        if (fechaNac != null) {
            cliente.setFechaNacimiento(fechaNac.toLocalDate());
        }

        cliente.setGenero(rs.getString("GENERO"));
        cliente.setTelefono(rs.getString("TELEFONO"));
        cliente.setCorreo(rs.getString("CORREO"));
        cliente.setDireccion(rs.getString("DIRECCION"));

        java.sql.Date fechaReg = rs.getDate("FECHA_REGISTRO");
        if (fechaReg != null) {
            cliente.setFechaRegistro(fechaReg.toLocalDate());
        }

        int idBarrio = rs.getInt("ID_BARRIO");
        if (!rs.wasNull()) {
            Barrios barrio = new Barrios();
            barrio.setIdBarrio(idBarrio);

            String nomBarrio = rs.getString("NOM_BARRIO");
            barrio.setNombreBarrio(nomBarrio);

            cliente.setBarrio(barrio);
        }
        cliente.setChatId(rs.getString("CHAT_ID"));


        return cliente;
    }
}
