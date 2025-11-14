package org.example.gimnasioproyect.repository;

import oracle.jdbc.internal.OracleTypes;
import org.example.gimnasioproyect.Utilidades.TipoPersonal;
import org.example.gimnasioproyect.model.Entrenadores;
import org.example.gimnasioproyect.confi.OracleDatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EntrenadorRepositoryImpl implements EntrenadorRepository{
    private final OracleDatabaseConnection connection;

    public EntrenadorRepositoryImpl(OracleDatabaseConnection connection) throws SQLException {
        this.connection = connection;
        try (Connection conn = this.connection.connect()) {
            System.out.println("🎯 Conexión a BD probada exitosamente - EntrenadorRepository");
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void save(Entrenadores entity) throws SQLException {
        String sql = "{ call PKG_ENTRENADORES.PR_INSERTAR_ENTRENADOR(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, entity.getNombres());
            cs.setString(2, entity.getApellidos());
            cs.setString(3, entity.getTelefono());
            cs.setString(4, entity.getCorreo());
            cs.setString(5, entity.getUsuarioSistema());
            cs.setString(6, entity.getContrasena());
            cs.setDate(7, entity.getFechaContratacion() != null ? Date.valueOf(entity.getFechaContratacion()) : null);
            cs.setString(8, entity.getDocuEntrenador());
            cs.setString(9, entity.getEspecialidad());
            cs.setInt(10, entity.getExperiencia() != null ? entity.getExperiencia() : 0);

            // Parámetro OUT para capturar ID_PERSONAL generado
            cs.registerOutParameter(11, java.sql.Types.INTEGER);

            cs.executeUpdate();

            // Obtener ID_PERSONAL generado
            int idPersonal = cs.getInt(11);
            entity.setIdPersonal(idPersonal);

            System.out.println("✅ Entrenador guardado con ID_PERSONAL = " + idPersonal);
        }
    }

    @Override
    public Optional<Entrenadores> findById(String documento) throws SQLException {
        return findByDocumento(documento);
    }

    @Override
    public Optional<Entrenadores> findByDocumento(String documento) throws SQLException {
        String sql = "{? = call PKG_ENTRENADORES.FN_OBTENER_ENTRENADOR_POR_DOCUMENTO(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, documento);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            if (rs.next()) {
                Entrenadores entrenador = mapResultSetToEntrenador(rs);
                return Optional.of(entrenador);
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar entrenador: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Entrenadores> findByEspecialidad(String especialidad) throws SQLException {
        String sql = "{? = call PKG_ENTRENADORES.FN_BUSCAR_ENTRENADORES_POR_ESPECIALIDAD(?)}";
        List<Entrenadores> entrenadores = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, especialidad);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                entrenadores.add(mapResultSetToEntrenador(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar entrenadores por especialidad: " + e.getMessage());
            throw e;
        }

        return entrenadores;
    }

    @Override
    public Optional<Entrenadores> findByUsuario(String usuario) throws SQLException {
        String sql = "{? = call PKG_ENTRENADORES.FN_OBTENER_ENTRENADOR_POR_USUARIO(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, usuario);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            if (rs.next()) {
                Entrenadores entrenador = mapResultSetToEntrenador(rs);
                return Optional.of(entrenador);
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar entrenador por usuario: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Entrenadores> findAll() throws SQLException {
        String sql = "{? = call PKG_ENTRENADORES.FN_LISTAR_ENTRENADORES()}";
        List<Entrenadores> entrenadores = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                entrenadores.add(mapResultSetToEntrenador(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al listar entrenadores: " + e.getMessage());
            throw e;
        }

        return entrenadores;
    }

    @Override
    public void update(Entrenadores entity) throws SQLException {
        String sql = "{call PKG_ENTRENADORES.PR_ACTUALIZAR_ENTRENADOR(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, entity.getIdPersonal());
            cs.setString(2, entity.getNombres());
            cs.setString(3, entity.getApellidos());
            cs.setString(4, entity.getTelefono());
            cs.setString(5, entity.getCorreo());
            cs.setString(6, entity.getUsuarioSistema());
            cs.setString(7, entity.getContrasena());
            cs.setDate(8, entity.getFechaContratacion() != null ?
                    Date.valueOf(entity.getFechaContratacion()) : null);
            cs.setString(9, entity.getDocuEntrenador());
            cs.setString(10, entity.getEspecialidad());
            cs.setInt(11, entity.getExperiencia() != null ? entity.getExperiencia() : 0);

            cs.execute();
            System.out.println("✅ Entrenador actualizado: " + entity.getDocuEntrenador());

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar entrenador: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(String documento) throws SQLException {
        String sql = "{call PKG_ENTRENADORES.PR_ELIMINAR_ENTRENADOR(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, documento);
            cs.execute();
            System.out.println("✅ Entrenador eliminado: " + documento);

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar entrenador: " + e.getMessage());
            throw e;
        }
    }

    private Entrenadores mapResultSetToEntrenador(ResultSet rs) throws SQLException {
        Entrenadores entrenador = new Entrenadores();

        // Datos de ENTRENADORES
        entrenador.setDocuEntrenador(rs.getString("DOCUENTRENADOR"));
        entrenador.setEspecialidad(rs.getString("ESPECIALIDAD"));
        entrenador.setExperiencia(rs.getInt("EXPERIENCIA"));

        // Datos de PERSONAL
        entrenador.setIdPersonal(rs.getInt("ID_PERSONAL"));
        entrenador.setNombres(rs.getString("NOMBRES"));
        entrenador.setApellidos(rs.getString("APELLIDOS"));
        entrenador.setTelefono(rs.getString("TELEFONO"));
        entrenador.setCorreo(rs.getString("CORREO"));
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

        return entrenador;
    }
}
