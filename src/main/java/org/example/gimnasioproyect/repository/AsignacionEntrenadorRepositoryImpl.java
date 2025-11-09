package org.example.gimnasioproyect.repository;

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
        String sql = "{call PKG_ENTRENADORCLIENTES.PR_INSERTAR_ASIGNACION_ENTRENADOR(?, ?, ?, ?, ?)}";

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
            System.out.println("✅ Entrenador asignado exitosamente al cliente: " +
                    entity.getCliente().getDocumento());

        } catch (SQLException e) {
            System.err.println("❌ Error al guardar asignación entrenador: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<AsignacionEntrenadores> findById(Integer id) throws SQLException {
        String sql = "SELECT ec.ID_ENTRENADOR_CLIENTE, ec.FECHA_ASIGNACION, ec.FECHA_FINALIZACION, " +
                "e.DOCUENTRENADOR, e.ESPECIALIDAD, e.EXPERIENCIA, " +
                "p.ID_PERSONAL, p.NOMBRES AS ENT_NOMBRES, p.APELLIDOS AS ENT_APELLIDOS, " +
                "p.TELEFONO AS ENT_TELEFONO, p.CORREO AS ENT_CORREO, p.USUARIO_SISTEMA, " +
                "p.CONTRASENA, p.TIPO_PERSONAL, p.FECHA_CONTRATACION, " +
                "c.DOCUMENTO, c.NOMBRES AS CLI_NOMBRES, c.APELLIDOS AS CLI_APELLIDOS, " +
                "c.FECHA_NACIMIENTO, c.GENERO, c.TELEFONO AS CLI_TELEFONO, " +
                "c.CORREO AS CLI_CORREO, c.DIRECCION, c.FECHA_REGISTRO, " +
                "c.ID_BARRIO, b.NOM_BARRIO " +
                "FROM ENTRENADORCLIENTES ec " +
                "JOIN ENTRENADORES e ON ec.DOCUENTRENADOR = e.DOCUENTRENADOR " +
                "JOIN PERSONAL p ON e.ID_PERSONAL = p.ID_PERSONAL " +
                "JOIN CLIENTES c ON ec.DOCUMENTO = c.DOCUMENTO " +
                "LEFT JOIN BARRIOS b ON c.ID_BARRIO = b.ID_BARRIO " +
                "WHERE ec.ID_ENTRENADOR_CLIENTE = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

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
        String sql = "SELECT ec.ID_ENTRENADOR_CLIENTE, ec.FECHA_ASIGNACION, ec.FECHA_FINALIZACION, " +
                "e.DOCUENTRENADOR, e.ESPECIALIDAD, e.EXPERIENCIA, " +
                "p.ID_PERSONAL, p.NOMBRES AS ENT_NOMBRES, p.APELLIDOS AS ENT_APELLIDOS, " +
                "p.TELEFONO AS ENT_TELEFONO, p.CORREO AS ENT_CORREO, p.USUARIO_SISTEMA, " +
                "p.CONTRASENA, p.TIPO_PERSONAL, p.FECHA_CONTRATACION, " +
                "c.DOCUMENTO, c.NOMBRES AS CLI_NOMBRES, c.APELLIDOS AS CLI_APELLIDOS, " +
                "c.FECHA_NACIMIENTO, c.GENERO, c.TELEFONO AS CLI_TELEFONO, " +
                "c.CORREO AS CLI_CORREO, c.DIRECCION, c.FECHA_REGISTRO, " +
                "c.ID_BARRIO, b.NOM_BARRIO " +
                "FROM ENTRENADORCLIENTES ec " +
                "JOIN ENTRENADORES e ON ec.DOCUENTRENADOR = e.DOCUENTRENADOR " +
                "JOIN PERSONAL p ON e.ID_PERSONAL = p.ID_PERSONAL " +
                "JOIN CLIENTES c ON ec.DOCUMENTO = c.DOCUMENTO " +
                "LEFT JOIN BARRIOS b ON c.ID_BARRIO = b.ID_BARRIO " +
                "WHERE ec.DOCUMENTO = ? " +
                "AND (ec.FECHA_FINALIZACION IS NULL OR ec.FECHA_FINALIZACION >= SYSDATE) " +
                "ORDER BY ec.FECHA_ASIGNACION DESC " +
                "FETCH FIRST 1 ROW ONLY";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, documentoCliente);
            ResultSet rs = ps.executeQuery();

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
        String sql = "SELECT ec.ID_ENTRENADOR_CLIENTE, ec.FECHA_ASIGNACION, ec.FECHA_FINALIZACION, " +
                "e.DOCUENTRENADOR, e.ESPECIALIDAD, e.EXPERIENCIA, " +
                "p.ID_PERSONAL, p.NOMBRES AS ENT_NOMBRES, p.APELLIDOS AS ENT_APELLIDOS, " +
                "p.TELEFONO AS ENT_TELEFONO, p.CORREO AS ENT_CORREO, p.USUARIO_SISTEMA, " +
                "p.CONTRASENA, p.TIPO_PERSONAL, p.FECHA_CONTRATACION, " +
                "c.DOCUMENTO, c.NOMBRES AS CLI_NOMBRES, c.APELLIDOS AS CLI_APELLIDOS, " +
                "c.FECHA_NACIMIENTO, c.GENERO, c.TELEFONO AS CLI_TELEFONO, " +
                "c.CORREO AS CLI_CORREO, c.DIRECCION, c.FECHA_REGISTRO, " +
                "c.ID_BARRIO, b.NOM_BARRIO " +
                "FROM ENTRENADORCLIENTES ec " +
                "JOIN ENTRENADORES e ON ec.DOCUENTRENADOR = e.DOCUENTRENADOR " +
                "JOIN PERSONAL p ON e.ID_PERSONAL = p.ID_PERSONAL " +
                "JOIN CLIENTES c ON ec.DOCUMENTO = c.DOCUMENTO " +
                "LEFT JOIN BARRIOS b ON c.ID_BARRIO = b.ID_BARRIO " +
                "WHERE ec.DOCUMENTO = ? " +
                "ORDER BY ec.FECHA_ASIGNACION DESC";

        List<AsignacionEntrenadores> asignaciones = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, documentoCliente);
            ResultSet rs = ps.executeQuery();

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
        String sql = "SELECT ec.ID_ENTRENADOR_CLIENTE, ec.FECHA_ASIGNACION, ec.FECHA_FINALIZACION, " +
                "e.DOCUENTRENADOR, e.ESPECIALIDAD, e.EXPERIENCIA, " +
                "p.ID_PERSONAL, p.NOMBRES AS ENT_NOMBRES, p.APELLIDOS AS ENT_APELLIDOS, " +
                "p.TELEFONO AS ENT_TELEFONO, p.CORREO AS ENT_CORREO, p.USUARIO_SISTEMA, " +
                "p.CONTRASENA, p.TIPO_PERSONAL, p.FECHA_CONTRATACION, " +
                "c.DOCUMENTO, c.NOMBRES AS CLI_NOMBRES, c.APELLIDOS AS CLI_APELLIDOS, " +
                "c.FECHA_NACIMIENTO, c.GENERO, c.TELEFONO AS CLI_TELEFONO, " +
                "c.CORREO AS CLI_CORREO, c.DIRECCION, c.FECHA_REGISTRO, " +
                "c.ID_BARRIO, b.NOM_BARRIO " +
                "FROM ENTRENADORCLIENTES ec " +
                "JOIN ENTRENADORES e ON ec.DOCUENTRENADOR = e.DOCUENTRENADOR " +
                "JOIN PERSONAL p ON e.ID_PERSONAL = p.ID_PERSONAL " +
                "JOIN CLIENTES c ON ec.DOCUMENTO = c.DOCUMENTO " +
                "LEFT JOIN BARRIOS b ON c.ID_BARRIO = b.ID_BARRIO " +
                "WHERE ec.DOCUENTRENADOR = ? " +
                "ORDER BY ec.FECHA_ASIGNACION DESC";

        List<AsignacionEntrenadores> asignaciones = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, documentoEntrenador);
            ResultSet rs = ps.executeQuery();

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
        String sql = "SELECT ec.ID_ENTRENADOR_CLIENTE, ec.FECHA_ASIGNACION, ec.FECHA_FINALIZACION, " +
                "e.DOCUENTRENADOR, e.ESPECIALIDAD, e.EXPERIENCIA, " +
                "p.ID_PERSONAL, p.NOMBRES AS ENT_NOMBRES, p.APELLIDOS AS ENT_APELLIDOS, " +
                "p.TELEFONO AS ENT_TELEFONO, p.CORREO AS ENT_CORREO, p.USUARIO_SISTEMA, " +
                "p.CONTRASENA, p.TIPO_PERSONAL, p.FECHA_CONTRATACION, " +
                "c.DOCUMENTO, c.NOMBRES AS CLI_NOMBRES, c.APELLIDOS AS CLI_APELLIDOS, " +
                "c.FECHA_NACIMIENTO, c.GENERO, c.TELEFONO AS CLI_TELEFONO, " +
                "c.CORREO AS CLI_CORREO, c.DIRECCION, c.FECHA_REGISTRO, " +
                "c.ID_BARRIO, b.NOM_BARRIO " +
                "FROM ENTRENADORCLIENTES ec " +
                "INNER JOIN ENTRENADORES e ON ec.DOCUENTRENADOR = e.DOCUENTRENADOR " +
                "INNER JOIN PERSONAL p ON e.ID_PERSONAL = p.ID_PERSONAL " +
                "INNER JOIN CLIENTES c ON ec.DOCUMENTO = c.DOCUMENTO " +
                "LEFT JOIN BARRIOS b ON c.ID_BARRIO = b.ID_BARRIO " +
                "WHERE ec.DOCUENTRENADOR = ? " +
                "AND (ec.FECHA_FINALIZACION IS NULL OR ec.FECHA_FINALIZACION >= SYSDATE) " +
                "ORDER BY c.NOMBRES, c.APELLIDOS";

        List<AsignacionEntrenadores> asignaciones = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, documentoEntrenador);
            ResultSet rs = ps.executeQuery();

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
        String sql = "SELECT ec.ID_ENTRENADOR_CLIENTE, ec.FECHA_ASIGNACION, ec.FECHA_FINALIZACION, " +
                "e.DOCUENTRENADOR, e.ESPECIALIDAD, e.EXPERIENCIA, " +
                "p.ID_PERSONAL, p.NOMBRES AS ENT_NOMBRES, p.APELLIDOS AS ENT_APELLIDOS, " +
                "p.TELEFONO AS ENT_TELEFONO, p.CORREO AS ENT_CORREO, p.USUARIO_SISTEMA, " +
                "p.CONTRASENA, p.TIPO_PERSONAL, p.FECHA_CONTRATACION, " +
                "c.DOCUMENTO, c.NOMBRES AS CLI_NOMBRES, c.APELLIDOS AS CLI_APELLIDOS, " +
                "c.FECHA_NACIMIENTO, c.GENERO, c.TELEFONO AS CLI_TELEFONO, " +
                "c.CORREO AS CLI_CORREO, c.DIRECCION, c.FECHA_REGISTRO, " +
                "c.ID_BARRIO, b.NOM_BARRIO " +
                "FROM ENTRENADORCLIENTES ec " +
                "INNER JOIN ENTRENADORES e ON ec.DOCUENTRENADOR = e.DOCUENTRENADOR " +
                "INNER JOIN PERSONAL p ON e.ID_PERSONAL = p.ID_PERSONAL " +
                "INNER JOIN CLIENTES c ON ec.DOCUMENTO = c.DOCUMENTO " +
                "LEFT JOIN BARRIOS b ON c.ID_BARRIO = b.ID_BARRIO " +
                "ORDER BY ec.ID_ENTRENADOR_CLIENTE DESC";

        List<AsignacionEntrenadores> asignaciones = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

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
        String sql = "UPDATE ENTRENADORCLIENTES SET DOCUENTRENADOR = ?, DOCUMENTO = ?, " +
                "FECHA_ASIGNACION = ?, FECHA_FINALIZACION = ? " +
                "WHERE ID_ENTRENADOR_CLIENTE = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, entity.getEntrenador().getDocuEntrenador());
            ps.setString(2, entity.getCliente().getDocumento());
            ps.setDate(3, entity.getFechaAsignacion() != null ?
                    Date.valueOf(entity.getFechaAsignacion()) : null);
            ps.setDate(4, entity.getFechaFinalizacion() != null ?
                    Date.valueOf(entity.getFechaFinalizacion()) : null);
            ps.setInt(5, entity.getIdEntrenadorCliente());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Asignación entrenador actualizada: " + entity.getIdEntrenadorCliente());
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar asignación entrenador: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM ENTRENADORCLIENTES WHERE ID_ENTRENADOR_CLIENTE = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Asignación entrenador eliminada: " + id);
            }

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
