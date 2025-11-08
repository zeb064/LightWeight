package org.example.gimnasioproyect.repository;

import org.example.gimnasioproyect.Utilidades.TipoPersonal;
import org.example.gimnasioproyect.model.Administradores;
import org.example.gimnasioproyect.confi.OracleDatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdministradorRepositoryImpl implements AdministradorRepository{
    private final OracleDatabaseConnection connection;

    public AdministradorRepositoryImpl(OracleDatabaseConnection connection) throws SQLException {
        this.connection = connection;
        try (Connection conn = this.connection.connect()) {
            System.out.println("🎯 Conexión a BD probada exitosamente - AdministradorRepository");
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void save(Administradores entity) throws SQLException {
        String sql = "{ call PKG_ADMINISTRADORES.PR_INSERTAR_ADMINISTRADORES(?, ?, ?, ?, ?, ?, ?, ?, ?) }";
        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, entity.getNombres());
            cs.setString(2, entity.getApellidos());
            cs.setString(3, entity.getTelefono());
            cs.setString(4, entity.getCorreo());
            cs.setString(5, entity.getUsuarioSistema());
            cs.setString(6, entity.getContrasena());
            if (entity.getFechaContratacion() != null)
                cs.setDate(7, Date.valueOf(entity.getFechaContratacion()));
            else
                cs.setNull(7, Types.DATE);
            cs.setString(8, entity.getDocuAdministrador());
            cs.setString(9, entity.getCargo());

            cs.execute();
            System.out.println("✅ Administrador registrado con paquete PL/SQL: " + entity.getDocuAdministrador());

        } catch (SQLException e) {
            System.err.println("❌ Error al insertar administrador con paquete: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Administradores> findById(String documento) throws SQLException {
        return findByDocumento(documento);
    }

    @Override
    public Optional<Administradores> findByDocumento(String documento) throws SQLException {
        String sql = "SELECT a.DOCUADMINISTRADOR, a.CARGO, " +
                "p.ID_PERSONAL, p.NOMBRES, p.APELLIDOS, p.TELEFONO, p.CORREO, " +
                "p.USUARIO_SISTEMA, p.CONTRASENA, p.TIPO_PERSONAL, p.FECHA_CONTRATACION " +
                "FROM ADMINISTRADORES a " +
                "INNER JOIN PERSONAL p ON a.ID_PERSONAL = p.ID_PERSONAL " +
                "WHERE a.DOCUADMINISTRADOR = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, documento);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Administradores administrador = mapResultSetToAdministrador(rs);
                return Optional.of(administrador);
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar administrador: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Administradores> findByUsuario(String usuario) throws SQLException {
        String sql = "SELECT a.DOCUADMINISTRADOR, a.CARGO, " +
                "p.ID_PERSONAL, p.NOMBRES, p.APELLIDOS, p.TELEFONO, p.CORREO, " +
                "p.USUARIO_SISTEMA, p.CONTRASENA, p.TIPO_PERSONAL, p.FECHA_CONTRATACION " +
                "FROM ADMINISTRADORES a " +
                "INNER JOIN PERSONAL p ON a.ID_PERSONAL = p.ID_PERSONAL " +
                "WHERE p.USUARIO_SISTEMA = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Administradores administrador = mapResultSetToAdministrador(rs);
                return Optional.of(administrador);
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar administrador por usuario: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Administradores> findAll() throws SQLException {
        String sql = "SELECT a.DOCUADMINISTRADOR, a.CARGO, " +
                "p.ID_PERSONAL, p.NOMBRES, p.APELLIDOS, p.TELEFONO, p.CORREO, " +
                "p.USUARIO_SISTEMA, p.CONTRASENA, p.TIPO_PERSONAL, p.FECHA_CONTRATACION " +
                "FROM ADMINISTRADORES a " +
                "INNER JOIN PERSONAL p ON a.ID_PERSONAL = p.ID_PERSONAL " +
                "ORDER BY p.NOMBRES, p.APELLIDOS";

        List<Administradores> administradores = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                administradores.add(mapResultSetToAdministrador(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al listar administradores: " + e.getMessage());
            throw e;
        }

        return administradores;
    }

    @Override
    public void update(Administradores entity) throws SQLException {
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

            // 2. Actualizar ADMINISTRADORES
            String sqlAdmin = "UPDATE ADMINISTRADORES SET CARGO = ? WHERE DOCUADMINISTRADOR = ?";

            try (PreparedStatement ps = conn.prepareStatement(sqlAdmin)) {
                ps.setString(1, entity.getCargo());
                ps.setString(2, entity.getDocuAdministrador());
                ps.executeUpdate();
            }

            conn.commit();
            System.out.println("✅ Administrador actualizado: " + entity.getDocuAdministrador());

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("❌ Error en rollback: " + ex.getMessage());
                }
            }
            System.err.println("❌ Error al actualizar administrador: " + e.getMessage());
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
            String sqlGetId = "SELECT ID_PERSONAL FROM ADMINISTRADORES WHERE DOCUADMINISTRADOR = ?";
            Integer idPersonal = null;

            try (PreparedStatement ps = conn.prepareStatement(sqlGetId)) {
                ps.setString(1, documento);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    idPersonal = rs.getInt("ID_PERSONAL");
                }
            }

            if (idPersonal == null) {
                throw new SQLException("No se encontró el administrador");
            }

            // 1. Eliminar de ADMINISTRADORES
            String sqlAdmin = "DELETE FROM ADMINISTRADORES WHERE DOCUADMINISTRADOR = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlAdmin)) {
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
            System.out.println("✅ Administrador eliminado: " + documento);

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("❌ Error en rollback: " + ex.getMessage());
                }
            }
            System.err.println("❌ Error al eliminar administrador: " + e.getMessage());
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

    private Administradores mapResultSetToAdministrador(ResultSet rs) throws SQLException {
        Administradores administrador = new Administradores();

        // Datos de ADMINISTRADORES
        administrador.setDocuAdministrador(rs.getString("DOCUADMINISTRADOR"));
        administrador.setCargo(rs.getString("CARGO"));

        // Datos de PERSONAL
        administrador.setIdPersonal(rs.getInt("ID_PERSONAL"));
        administrador.setNombres(rs.getString("NOMBRES"));
        administrador.setApellidos(rs.getString("APELLIDOS"));
        administrador.setTelefono(rs.getString("TELEFONO"));
        administrador.setCorreo(rs.getString("CORREO"));
        administrador.setUsuarioSistema(rs.getString("USUARIO_SISTEMA"));
        administrador.setContrasena(rs.getString("CONTRASENA"));
        String tipoStr = rs.getString("TIPO_PERSONAL");
        if (tipoStr != null) {
            try {
                administrador.setTipoPersonal(TipoPersonal.valueOf(tipoStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new SQLException("Tipo de personal inválido en la base de datos: " + tipoStr);
            }
        }

        if (rs.getDate("FECHA_CONTRATACION") != null) {
            administrador.setFechaContratacion(rs.getDate("FECHA_CONTRATACION").toLocalDate());
        }

        return administrador;
    }
}
