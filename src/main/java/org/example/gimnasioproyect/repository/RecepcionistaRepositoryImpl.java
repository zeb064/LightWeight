package org.example.gimnasioproyect.repository;

import org.example.gimnasioproyect.Utilidades.TipoPersonal;
import org.example.gimnasioproyect.model.Recepcionistas;
import org.example.gimnasioproyect.confi.OracleDatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RecepcionistaRepositoryImpl implements RecepcionistaRepository{
    private final OracleDatabaseConnection connection;

    public RecepcionistaRepositoryImpl(OracleDatabaseConnection connection) throws SQLException {
        this.connection = connection;
        try (Connection conn = this.connection.connect()) {
            System.out.println("🎯 Conexión a BD probada exitosamente - RecepcionistaRepository");
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void save(Recepcionistas entity) throws SQLException {
        String sql = "{ call PKG_RECEPCIONISTAS.PR_INSERTAR_RECEPCIONISTAS(?, ?, ?, ?, ?, ?, ?, ?, ?) }";
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
            cs.setString(8, entity.getDocuRecepcionista());
            cs.setString(9, entity.getHorarioTurno());

            cs.execute();
            System.out.println("✅ Recepcionista registrado con paquete PL/SQL: " + entity.getDocuRecepcionista());

        } catch (SQLException e) {
            System.err.println("❌ Error al insertar recepcionista con paquete: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Recepcionistas> findById(String documento) throws SQLException {
        return findByDocumento(documento);
    }

    @Override
    public Optional<Recepcionistas> findByDocumento(String documento) throws SQLException {
        String sql = "SELECT r.DOCURECEPCIONISTA, r.HORARIO_TURNO, " +
                "p.ID_PERSONAL, p.NOMBRES, p.APELLIDOS, p.TELEFONO, p.CORREO, " +
                "p.USUARIO_SISTEMA, p.CONTRASENA, p.TIPO_PERSONAL, p.FECHA_CONTRATACION " +
                "FROM RECEPCIONISTAS r " +
                "INNER JOIN PERSONAL p ON r.ID_PERSONAL = p.ID_PERSONAL " +
                "WHERE r.DOCURECEPCIONISTA = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, documento);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Recepcionistas recepcionista = mapResultSetToRecepcionista(rs);
                return Optional.of(recepcionista);
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar recepcionista: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Recepcionistas> findByUsuario(String usuario) throws SQLException {
        String sql = "SELECT r.DOCURECEPCIONISTA, r.HORARIO_TURNO, " +
                "p.ID_PERSONAL, p.NOMBRES, p.APELLIDOS, p.TELEFONO, p.CORREO, " +
                "p.USUARIO_SISTEMA, p.CONTRASENA, p.TIPO_PERSONAL, p.FECHA_CONTRATACION " +
                "FROM RECEPCIONISTAS r " +
                "INNER JOIN PERSONAL p ON r.ID_PERSONAL = p.ID_PERSONAL " +
                "WHERE p.USUARIO_SISTEMA = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Recepcionistas recepcionista = mapResultSetToRecepcionista(rs);
                return Optional.of(recepcionista);
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar recepcionista por usuario: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Recepcionistas> findByTurno(String turno) throws SQLException {
        String sql = "SELECT r.DOCURECEPCIONISTA, r.HORARIO_TURNO, " +
                "p.ID_PERSONAL, p.NOMBRES, p.APELLIDOS, p.TELEFONO, p.CORREO, " +
                "p.USUARIO_SISTEMA, p.CONTRASENA, p.TIPO_PERSONAL, p.FECHA_CONTRATACION " +
                "FROM RECEPCIONISTAS r " +
                "INNER JOIN PERSONAL p ON r.ID_PERSONAL = p.ID_PERSONAL " +
                "WHERE UPPER(r.HORARIO_TURNO) = ? " +
                "ORDER BY p.NOMBRES, p.APELLIDOS";

        List<Recepcionistas> recepcionistas = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, turno.toUpperCase());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                recepcionistas.add(mapResultSetToRecepcionista(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar recepcionistas por turno: " + e.getMessage());
            throw e;
        }

        return recepcionistas;
    }

    @Override
    public List<Recepcionistas> findAll() throws SQLException {
        String sql = "SELECT r.DOCURECEPCIONISTA, r.HORARIO_TURNO, " +
                "p.ID_PERSONAL, p.NOMBRES, p.APELLIDOS, p.TELEFONO, p.CORREO, " +
                "p.USUARIO_SISTEMA, p.CONTRASENA, p.TIPO_PERSONAL, p.FECHA_CONTRATACION " +
                "FROM RECEPCIONISTAS r " +
                "INNER JOIN PERSONAL p ON r.ID_PERSONAL = p.ID_PERSONAL " +
                "ORDER BY p.NOMBRES, p.APELLIDOS";

        List<Recepcionistas> recepcionistas = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                recepcionistas.add(mapResultSetToRecepcionista(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al listar recepcionistas: " + e.getMessage());
            throw e;
        }

        return recepcionistas;
    }

    @Override
    public void update(Recepcionistas entity) throws SQLException {
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

            // 2. Actualizar RECEPCIONISTAS
            String sqlRecep = "UPDATE RECEPCIONISTAS SET HORARIO_TURNO = ? WHERE DOCURECEPCIONISTA = ?";

            try (PreparedStatement ps = conn.prepareStatement(sqlRecep)) {
                ps.setString(1, entity.getHorarioTurno());
                ps.setString(2, entity.getDocuRecepcionista());
                ps.executeUpdate();
            }

            conn.commit();
            System.out.println("✅ Recepcionista actualizado: " + entity.getDocuRecepcionista());

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("❌ Error en rollback: " + ex.getMessage());
                }
            }
            System.err.println("❌ Error al actualizar recepcionista: " + e.getMessage());
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
            String sqlGetId = "SELECT ID_PERSONAL FROM RECEPCIONISTAS WHERE DOCURECEPCIONISTA = ?";
            Integer idPersonal = null;

            try (PreparedStatement ps = conn.prepareStatement(sqlGetId)) {
                ps.setString(1, documento);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    idPersonal = rs.getInt("ID_PERSONAL");
                }
            }

            if (idPersonal == null) {
                throw new SQLException("No se encontró el recepcionista");
            }

            // 1. Eliminar de RECEPCIONISTAS
            String sqlRecep = "DELETE FROM RECEPCIONISTAS WHERE DOCURECEPCIONISTA = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlRecep)) {
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
            System.out.println("✅ Recepcionista eliminado: " + documento);

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("❌ Error en rollback: " + ex.getMessage());
                }
            }
            System.err.println("❌ Error al eliminar recepcionista: " + e.getMessage());
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

    private Recepcionistas mapResultSetToRecepcionista(ResultSet rs) throws SQLException {
        Recepcionistas recepcionista = new Recepcionistas();

        // Datos de RECEPCIONISTAS
        recepcionista.setDocuRecepcionista(rs.getString("DOCURECEPCIONISTA"));
        recepcionista.setHorarioTurno(rs.getString("HORARIO_TURNO"));

        // Datos de PERSONAL
        recepcionista.setIdPersonal(rs.getInt("ID_PERSONAL"));
        recepcionista.setNombres(rs.getString("NOMBRES"));
        recepcionista.setApellidos(rs.getString("APELLIDOS"));
        recepcionista.setTelefono(rs.getString("TELEFONO"));
        recepcionista.setCorreo(rs.getString("CORREO"));
        recepcionista.setUsuarioSistema(rs.getString("USUARIO_SISTEMA"));
        recepcionista.setContrasena(rs.getString("CONTRASENA"));
        String tipoStr = rs.getString("TIPO_PERSONAL");
        if (tipoStr != null) {
            try {
                recepcionista.setTipoPersonal(TipoPersonal.valueOf(tipoStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new SQLException("Tipo de personal inválido en la base de datos: " + tipoStr);
            }
        }

        if (rs.getDate("FECHA_CONTRATACION") != null) {
            recepcionista.setFechaContratacion(rs.getDate("FECHA_CONTRATACION").toLocalDate());
        }

        return recepcionista;
    }
}
