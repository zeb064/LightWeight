package org.example.gimnasioproyect.repository;


import oracle.jdbc.internal.OracleTypes;
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
            System.out.println("Conexión a BD probada exitosamente - RutinaAsignadaRepository");
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void save(RutinaAsignadas entity) throws SQLException {
        String sql = "{call PKG_RUTINASCLIENTES.PR_INSERTAR_RUTINA_ASIGNADA(?, ?, ?, ?, ?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            //ps.setInt(1, entity.getIdRutinaCliente());
            cs.setInt(1, entity.getRutina().getIdRutina());
            cs.setString(2, entity.getCliente().getDocumento());
            cs.setDate(3, entity.getFechaAsignacion() != null ?
                    Date.valueOf(entity.getFechaAsignacion()) : null);
            cs.setDate(4, entity.getFechaFinalizacion() != null ?
                    Date.valueOf(entity.getFechaFinalizacion()) : null);
            cs.setString(5, entity.getEstado());

            cs.execute();
            System.out.println("✅ Rutina asignada exitosamente al cliente: " +
                    entity.getCliente().getDocumento());

        } catch (SQLException e) {
            System.err.println("❌ Error al guardar rutina asignada: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<RutinaAsignadas> findById(Integer id) throws SQLException {
        String sql = "{? = call PKG_RUTINASCLIENTES.FN_OBTENER_RUTINA_ASIGNADA_POR_ID(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setInt(2, id);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
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
        String sql = "{? = call PKG_RUTINASCLIENTES.FN_BUSCAR_RUTINAS_POR_CLIENTE(?)}";
        List<RutinaAsignadas> rutinasAsignadas = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, documentoCliente);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
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
        String sql = "{? = call PKG_RUTINASCLIENTES.FN_BUSCAR_RUTINAS_ACTIVAS_CLIENTE(?)}";
        List<RutinaAsignadas> rutinasAsignadas = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, documentoCliente);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
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
        String sql = "{? = call PKG_RUTINASCLIENTES.FN_BUSCAR_ASIGNACIONES_POR_RUTINA(?)}";
        List<RutinaAsignadas> rutinasAsignadas = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setInt(2, idRutina);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
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
        String sql = "{? = call PKG_RUTINASCLIENTES.FN_BUSCAR_RUTINAS_POR_ESTADO(?)}";
        List<RutinaAsignadas> rutinasAsignadas = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, estado);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
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
        String sql = "{? = call PKG_RUTINASCLIENTES.FN_LISTAR_RUTINAS_ASIGNADAS()}";
        List<RutinaAsignadas> rutinasAsignadas = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
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
        String sql = "{call PKG_RUTINASCLIENTES.PR_ACTUALIZAR_RUTINA_ASIGNADA(?, ?, ?, ?, ?, ?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, entity.getIdRutinaCliente());
            cs.setInt(2, entity.getRutina().getIdRutina());
            cs.setString(3, entity.getCliente().getDocumento());
            cs.setDate(4, entity.getFechaAsignacion() != null ?
                    Date.valueOf(entity.getFechaAsignacion()) : null);
            cs.setDate(5, entity.getFechaFinalizacion() != null ?
                    Date.valueOf(entity.getFechaFinalizacion()) : null);
            cs.setString(6, entity.getEstado());

            cs.execute();
            System.out.println("✅ Rutina asignada actualizada: " + entity.getIdRutinaCliente());

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar rutina asignada: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "{call PKG_RUTINASCLIENTES.PR_ELIMINAR_RUTINA_ASIGNADA(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.execute();
            System.out.println("✅ Rutina asignada eliminada: " + id);

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
