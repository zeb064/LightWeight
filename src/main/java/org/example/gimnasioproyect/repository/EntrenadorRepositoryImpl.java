package org.example.gimnasioproyect.repository;

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
        String sql = "SELECT e.DOCUENTRENADOR, e.ESPECIALIDAD, e.EXPERIENCIA, " +
                "p.ID_PERSONAL, p.NOMBRES, p.APELLIDOS, p.TELEFONO, p.CORREO, " +
                "p.USUARIO_SISTEMA, p.CONTRASENA, p.TIPO_PERSONAL, p.FECHA_CONTRATACION " +
                "FROM ENTRENADORES e " +
                "JOIN PERSONAL p ON e.ID_PERSONAL = p.ID_PERSONAL " +
                "WHERE e.DOCUENTRENADOR = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, documento);
            ResultSet rs = ps.executeQuery();

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
        String sql = "SELECT e.DOCUENTRENADOR, e.ESPECIALIDAD, e.EXPERIENCIA, " +
                "p.ID_PERSONAL, p.NOMBRES, p.APELLIDOS, p.TELEFONO, p.CORREO, " +
                "p.USUARIO_SISTEMA, p.CONTRASENA, p.TIPO_PERSONAL, p.FECHA_CONTRATACION " +
                "FROM ENTRENADORES e " +
                "INNER JOIN PERSONAL p ON e.ID_PERSONAL = p.ID_PERSONAL " +
                "WHERE UPPER(e.ESPECIALIDAD) LIKE ? " +
                "ORDER BY p.NOMBRES, p.APELLIDOS";

        List<Entrenadores> entrenadores = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + especialidad.toUpperCase() + "%");
            ResultSet rs = ps.executeQuery();

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
        String sql = "SELECT e.DOCUENTRENADOR, e.ESPECIALIDAD, e.EXPERIENCIA, " +
                "p.ID_PERSONAL, p.NOMBRES, p.APELLIDOS, p.TELEFONO, p.CORREO, " +
                "p.USUARIO_SISTEMA, p.CONTRASENA, p.TIPO_PERSONAL, p.FECHA_CONTRATACION " +
                "FROM ENTRENADORES e " +
                "INNER JOIN PERSONAL p ON e.ID_PERSONAL = p.ID_PERSONAL " +
                "WHERE p.USUARIO_SISTEMA = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();

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
        String sql = "SELECT e.DOCUENTRENADOR, e.ESPECIALIDAD, e.EXPERIENCIA, " +
                "p.ID_PERSONAL, p.NOMBRES, p.APELLIDOS, p.TELEFONO, p.CORREO, " +
                "p.USUARIO_SISTEMA, p.CONTRASENA, p.TIPO_PERSONAL, p.FECHA_CONTRATACION " +
                "FROM ENTRENADORES e " +
                "INNER JOIN PERSONAL p ON e.ID_PERSONAL = p.ID_PERSONAL " +
                "ORDER BY p.NOMBRES, p.APELLIDOS";

        List<Entrenadores> entrenadores = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

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
        Connection conn = null;
        try {
            conn = this.connection.connect();
            conn.setAutoCommit(false);

            // 1. Actualizar PERSONAL
            String sqlPersonal = "UPDATE PERSONAL SET NOMBRES = ?, APELLIDOS = ?, TELEFONO = ?, " +
                    "CORREO = ?, USUARIO_SISTEMA = ?, CONTRASENA = ?, FECHA_CONTRATACION = ? " +
                    "WHERE ID_PERSONAL = ?";

            try (PreparedStatement ps = conn.prepareStatement(sqlPersonal)) {
                ps.setString(1, entity.getNombres());
                ps.setString(2, entity.getApellidos());
                ps.setString(3, entity.getTelefono());
                ps.setString(4, entity.getCorreo());
                ps.setString(5, entity.getUsuarioSistema());
                ps.setString(6, entity.getContrasena());
                ps.setDate(7, entity.getFechaContratacion() != null ?
                        Date.valueOf(entity.getFechaContratacion()) : null);
                ps.setInt(8, entity.getIdPersonal());
                ps.executeUpdate();
            }

            // 2. Actualizar ENTRENADORES
            String sqlEntrenador = "UPDATE ENTRENADORES SET ESPECIALIDAD = ?, EXPERIENCIA = ? " +
                    "WHERE DOCUENTRENADOR = ?";

            try (PreparedStatement ps = conn.prepareStatement(sqlEntrenador)) {
                ps.setString(1, entity.getEspecialidad());
                ps.setInt(2, entity.getExperiencia() != null ? entity.getExperiencia() : 0);
                ps.setString(3, entity.getDocuEntrenador());
                ps.executeUpdate();
            }

            conn.commit();
            System.out.println("✅ Entrenador actualizado: " + entity.getDocuEntrenador());

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("❌ Error en rollback: " + ex.getMessage());
                }
            }
            System.err.println("❌ Error al actualizar entrenador: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("❌ Error al cerrar conexión: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void delete(String documento) throws SQLException {
        Connection conn = null;
        try {
            conn = this.connection.connect();
            conn.setAutoCommit(false);

            // Primero obtener ID_PERSONAL
            String sqlGetId = "SELECT ID_PERSONAL FROM ENTRENADORES WHERE DOCUENTRENADOR = ?";
            Integer idPersonal = null;

            try (PreparedStatement ps = conn.prepareStatement(sqlGetId)) {
                ps.setString(1, documento);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    idPersonal = rs.getInt("ID_PERSONAL");
                }
            }

            if (idPersonal == null) {
                throw new SQLException("No se encontró el entrenador");
            }

            // 1. Eliminar de ENTRENADORES
            String sqlEntrenador = "DELETE FROM ENTRENADORES WHERE DOCUENTRENADOR = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlEntrenador)) {
                ps.setString(1, documento);
                ps.executeUpdate();
            }

            // 2. Eliminar de PERSONAL
            String sqlPersonal = "DELETE FROM PERSONAL WHERE ID_PERSONAL = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlPersonal)) {
                ps.setInt(1, idPersonal);
                ps.executeUpdate();
            }

            conn.commit();
            System.out.println("✅ Entrenador eliminado: " + documento);

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("❌ Error en rollback: " + ex.getMessage());
                }
            }
            System.err.println("❌ Error al eliminar entrenador: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("❌ Error al cerrar conexión: " + e.getMessage());
                }
            }
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
