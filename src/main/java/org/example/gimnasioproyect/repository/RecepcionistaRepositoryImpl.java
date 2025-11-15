package org.example.gimnasioproyect.repository;

import oracle.jdbc.internal.OracleTypes;
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
        String sql = "{? = call PKG_RECEPCIONISTAS.FN_OBTENER_RECEPCIONISTA_POR_DOCUMENTO(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, documento);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
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
        String sql = "{? = call PKG_RECEPCIONISTAS.FN_OBTENER_RECEPCIONISTA_POR_USUARIO(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, usuario);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
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
        String sql = "{? = call PKG_RECEPCIONISTAS.FN_BUSCAR_RECEPCIONISTAS_POR_TURNO(?)}";
        List<Recepcionistas> recepcionistas = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, turno);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
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
        String sql = "{? = call PKG_RECEPCIONISTAS.FN_LISTAR_RECEPCIONISTAS()}";
        List<Recepcionistas> recepcionistas = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
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
        String sql = "{call PKG_RECEPCIONISTAS.PR_ACTUALIZAR_RECEPCIONISTA(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

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

            cs.setString(9, entity.getDocuRecepcionista());
            cs.setString(10, entity.getHorarioTurno());

            cs.execute();
            System.out.println("✅ Recepcionista actualizado: " + entity.getDocuRecepcionista());

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar recepcionista: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(String documento) throws SQLException {
        String sql = "{call PKG_RECEPCIONISTAS.PR_ELIMINAR_RECEPCIONISTA(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, documento);
            cs.execute();
            System.out.println("✅ Recepcionista eliminado: " + documento);

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar recepcionista: " + e.getMessage());
            throw e;
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
