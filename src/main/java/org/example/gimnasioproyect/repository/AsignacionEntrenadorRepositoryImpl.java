package org.example.gimnasioproyect.repository;

import oracle.jdbc.internal.OracleTypes;
import org.example.gimnasioproyect.Utilidades.TipoPersonal;
import org.example.gimnasioproyect.model.AsignacionEntrenadores;
import org.example.gimnasioproyect.model.Barrios;
import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.model.Entrenadores;
import org.example.gimnasioproyect.confi.OracleDatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AsignacionEntrenadorRepositoryImpl implements AsignacionEntrenadorRepository {
    private final OracleDatabaseConnection connection;

    public AsignacionEntrenadorRepositoryImpl(OracleDatabaseConnection connection) throws SQLException {
        this.connection = connection;
        try (Connection conn = this.connection.connect()) {
            System.out.println("🎯 Conexión a BD probada exitosamente - AsignacionEntrenadorRepository");
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void save(AsignacionEntrenadores entity) throws SQLException {
        String sql = "{call PKG_ASIGNACION_ENTRENADORES.PR_INSERTAR_ASIGNACION_ENTRENADOR(?, ?, ?, ?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            //cs.setInt(1, entity.getIdEntrenadorCliente());
            cs.setString(1, entity.getEntrenador().getDocuEntrenador());
            cs.setString(2, entity.getCliente().getDocumento());
            cs.setDate(3, entity.getFechaAsignacion() != null ?
                    Date.valueOf(entity.getFechaAsignacion()) : null);
            cs.setDate(4, entity.getFechaFinalizacion() != null ?
                    Date.valueOf(entity.getFechaFinalizacion()) : null);

            cs.execute();
            System.out.println("✅ Entrenador asignado exitosamente al cliente: " +
                    entity.getCliente().getDocumento());

        } catch (SQLException e) {
            System.err.println("❌ Error al guardar asignación entrenador: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<AsignacionEntrenadores> findById(Integer id) throws SQLException {
        String sql = "{? = call PKG_ASIGNACION_ENTRENADORES.FN_OBTENER_ASIGNACION_POR_ID(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setInt(2, id);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            if (rs.next()) {
                AsignacionEntrenadores asignacion = mapResultSetToAsignacionEntrenador(rs);
                return Optional.of(asignacion);
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar asignación entrenador: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<AsignacionEntrenadores> findAsignacionActivaByCliente(String documentoCliente) throws SQLException {
        String sql = "{? = call PKG_ASIGNACION_ENTRENADORES.FN_OBTENER_ASIGNACION_ACTIVA_CLIENTE(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, documentoCliente);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            if (rs.next()) {
                AsignacionEntrenadores asignacion = mapResultSetToAsignacionEntrenador(rs);
                return Optional.of(asignacion);
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar asignación activa: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<AsignacionEntrenadores> findByCliente(String documentoCliente) throws SQLException {
        String sql = "{? = call PKG_ASIGNACION_ENTRENADORES.FN_BUSCAR_ASIGNACIONES_POR_CLIENTE(?)}";
        List<AsignacionEntrenadores> asignaciones = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, documentoCliente);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                asignaciones.add(mapResultSetToAsignacionEntrenador(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar asignaciones del cliente: " + e.getMessage());
            throw e;
        }

        return asignaciones;
    }

    @Override
    public List<AsignacionEntrenadores> findByEntrenador(String documentoEntrenador) throws SQLException {
        String sql = "{? = call PKG_ASIGNACION_ENTRENADORES.FN_BUSCAR_ASIGNACIONES_POR_ENTRENADOR(?)}";
        List<AsignacionEntrenadores> asignaciones = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, documentoEntrenador);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                asignaciones.add(mapResultSetToAsignacionEntrenador(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar clientes del entrenador: " + e.getMessage());
            throw e;
        }

        return asignaciones;
    }

    @Override
    public List<AsignacionEntrenadores> findClientesActivosByEntrenador(String documentoEntrenador) throws SQLException {
        String sql = "{? = call PKG_ASIGNACION_ENTRENADORES.FN_BUSCAR_CLIENTES_ACTIVOS_ENTRENADOR(?)}";
        List<AsignacionEntrenadores> asignaciones = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, documentoEntrenador);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                asignaciones.add(mapResultSetToAsignacionEntrenador(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar clientes activos del entrenador: " + e.getMessage());
            throw e;
        }

        return asignaciones;
    }

    @Override
    public List<AsignacionEntrenadores> findAll() throws SQLException {
        String sql = "{? = call PKG_ASIGNACION_ENTRENADORES.FN_LISTAR_ASIGNACIONES()}";
        List<AsignacionEntrenadores> asignaciones = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                asignaciones.add(mapResultSetToAsignacionEntrenador(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al listar asignaciones entrenador: " + e.getMessage());
            throw e;
        }

        return asignaciones;
    }

    @Override
    public void update(AsignacionEntrenadores entity) throws SQLException {
        String sql = "{call PKG_ASIGNACION_ENTRENADORES.PR_ACTUALIZAR_ASIGNACION(?, ?, ?, ?, ?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, entity.getIdEntrenadorCliente());
            cs.setString(2, entity.getEntrenador().getDocuEntrenador());
            cs.setString(3, entity.getCliente().getDocumento());
            cs.setDate(4, entity.getFechaAsignacion() != null ?
                    Date.valueOf(entity.getFechaAsignacion()) : null);
            cs.setDate(5, entity.getFechaFinalizacion() != null ?
                    Date.valueOf(entity.getFechaFinalizacion()) : null);

            cs.execute();
            System.out.println("✅ Asignación entrenador actualizada: " + entity.getIdEntrenadorCliente());

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar asignación entrenador: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "{call PKG_ASIGNACION_ENTRENADORES.PR_ELIMINAR_ASIGNACION(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.execute();
            System.out.println("✅ Asignación entrenador eliminada: " + id);

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar asignación entrenador: " + e.getMessage());
            throw e;
        }
    }

    private AsignacionEntrenadores mapResultSetToAsignacionEntrenador(ResultSet rs) throws SQLException {
        AsignacionEntrenadores asignacion = new AsignacionEntrenadores();

        // Datos de AsignacionEntrenador
        asignacion.setIdEntrenadorCliente(rs.getInt("ID_ENTRENADOR_CLIENTE"));

        if (rs.getDate("FECHA_ASIGNACION") != null) {
            asignacion.setFechaAsignacion(rs.getDate("FECHA_ASIGNACION").toLocalDate());
        }
        if (rs.getDate("FECHA_FINALIZACION") != null) {
            asignacion.setFechaFinalizacion(rs.getDate("FECHA_FINALIZACION").toLocalDate());
        }

        // Mapear Entrenador
        Entrenadores entrenador = new Entrenadores();
        entrenador.setDocuEntrenador(rs.getString("DOCUENTRENADOR"));
        entrenador.setEspecialidad(rs.getString("ESPECIALIDAD"));
        entrenador.setExperiencia(rs.getInt("EXPERIENCIA"));
        entrenador.setIdPersonal(rs.getInt("ID_PERSONAL"));
        entrenador.setNombres(rs.getString("ENT_NOMBRES"));
        entrenador.setApellidos(rs.getString("ENT_APELLIDOS"));
        entrenador.setTelefono(rs.getString("ENT_TELEFONO"));
        entrenador.setCorreo(rs.getString("ENT_CORREO"));
        entrenador.setUsuarioSistema(rs.getString("USUARIO_SISTEMA"));
        entrenador.setContrasena(rs.getString("CONTRASENA"));
        String tipoStr = rs.getString("TIPO_PERSONAL");
        if (tipoStr != null) {
            try {
                entrenador.setTipoPersonal(TipoPersonal.valueOf(tipoStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new SQLException("Tipo de personal inválido en la base de datos: " + tipoStr);
            }
        }

        if (rs.getDate("FECHA_CONTRATACION") != null) {
            entrenador.setFechaContratacion(rs.getDate("FECHA_CONTRATACION").toLocalDate());
        }

        asignacion.setEntrenador(entrenador);

        // Mapear Cliente
        Clientes cliente = new Clientes();
        cliente.setDocumento(rs.getString("DOCUMENTO"));
        cliente.setNombres(rs.getString("CLI_NOMBRES"));
        cliente.setApellidos(rs.getString("CLI_APELLIDOS"));

        if (rs.getDate("FECHA_NACIMIENTO") != null) {
            cliente.setFechaNacimiento(rs.getDate("FECHA_NACIMIENTO").toLocalDate());
        }

        cliente.setGenero(rs.getString("GENERO"));
        cliente.setTelefono(rs.getString("CLI_TELEFONO"));
        cliente.setCorreo(rs.getString("CLI_CORREO"));
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

        asignacion.setCliente(cliente);

        return asignacion;
    }
}
