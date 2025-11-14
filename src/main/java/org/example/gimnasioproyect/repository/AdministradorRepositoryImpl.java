package org.example.gimnasioproyect.repository;

import oracle.jdbc.internal.OracleTypes;
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
        String sql = "{? = call PKG_ADMINISTRADORES.FN_OBTENER_ADMINISTRADOR_POR_DOCUMENTO(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, documento);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
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
        String sql = "{? = call PKG_ADMINISTRADORES.FN_OBTENER_ADMINISTRADOR_POR_USUARIO(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, usuario);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
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
        String sql = "{? = call PKG_ADMINISTRADORES.FN_LISTAR_ADMINISTRADORES()}";
        List<Administradores> administradores = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
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
        String sql = "{CALL PKG_ADMINISTRADORES.PR_ACTUALIZAR_ADMINISTRADOR(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, entity.getIdPersonal());
            cs.setString(2, entity.getNombres());
            cs.setString(3, entity.getApellidos());
            cs.setString(4, entity.getTelefono());
            cs.setString(5, entity.getCorreo());
            cs.setString(6, entity.getUsuarioSistema());
            cs.setString(7, entity.getContrasena());

            if (entity.getFechaContratacion() != null) {
                cs.setDate(8, Date.valueOf(entity.getFechaContratacion()));
            } else {
                cs.setNull(8, Types.DATE);
            }

            cs.setString(9, entity.getDocuAdministrador());
            cs.setString(10, entity.getCargo());

            cs.execute();
            System.out.println("✅ Administrador actualizado: " + entity.getDocuAdministrador());

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar administrador: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(String documento) throws SQLException {
        String sql = "{CALL PKG_ADMINISTRADORES.PR_ELIMINAR_ADMINISTRADOR(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, documento);
            cs.execute();
            System.out.println("✅ Administrador eliminado: " + documento);

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar administrador: " + e.getMessage());
            throw e;
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
