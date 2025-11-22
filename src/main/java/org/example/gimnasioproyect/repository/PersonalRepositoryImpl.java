package org.example.gimnasioproyect.repository;

import oracle.jdbc.internal.OracleTypes;
import org.example.gimnasioproyect.Utilidades.TipoPersonal;
import org.example.gimnasioproyect.model.Administradores;
import org.example.gimnasioproyect.model.Entrenadores;
import org.example.gimnasioproyect.model.Personal;
import org.example.gimnasioproyect.model.Recepcionistas;
import org.example.gimnasioproyect.confi.OracleDatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PersonalRepositoryImpl implements PersonalRepository {
    private final OracleDatabaseConnection connection;

    public PersonalRepositoryImpl(OracleDatabaseConnection connection) throws SQLException {
        this.connection = connection;
        try (Connection conn = this.connection.connect()) {
            System.out.println("Conexión a BD probada exitosamente - PersonalRepository");
        } catch (SQLException e) {
            System.err.println("Error al conectar: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Personal> findByUsuario(String usuario) throws SQLException {
        String sql = "{? = call PKG_PERSONAL.FN_OBTENER_PERSONAL_POR_USUARIO(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, usuario);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            if (rs.next()) {
                Personal personal = mapResultSetToPersonal(rs);
                return Optional.of(personal);
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("Error al buscar personal por usuario: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Personal> autenticar(String usuario, String contrasena) throws SQLException {
        String sql = "{? = call PKG_PERSONAL.FN_AUTENTICAR_PERSONAL(?, ?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, usuario);
            cs.setString(3, contrasena);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            if (rs.next()) {
                Personal personal = mapResultSetToPersonal(rs);
                System.out.println("Autenticación exitosa: " + usuario + " - Tipo: " + personal.getTipoPersonal());
                return Optional.of(personal);
            }

            System.out.println("Autenticación fallida para: " + usuario);
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("Error al autenticar: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Personal> findAll() throws SQLException {
        String sql = "{? = call PKG_PERSONAL.FN_LISTAR_PERSONAL()}";
        List<Personal> personalList = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                personalList.add(mapResultSetToPersonal(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar personal: " + e.getMessage());
            throw e;
        }

        return personalList;
    }

    @Override
    public List<Personal> findByTipo(String tipoPersonal) throws SQLException {
        String sql = "{? = call PKG_PERSONAL.FN_BUSCAR_PERSONAL_POR_TIPO(?)}";
        List<Personal> personalList = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, tipoPersonal);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                personalList.add(mapResultSetToPersonal(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar personal por tipo: " + e.getMessage());
            throw e;
        }

        return personalList;
    }

    @Override
    public boolean existeUsuario(String usuario) throws SQLException {
        String sql = "{? = call PKG_PERSONAL.FN_EXISTE_USUARIO(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setString(2, usuario);
            cs.execute();

            int count = cs.getInt(1);
            return count > 0;

        } catch (SQLException e) {
            System.err.println("Error al verificar existencia de usuario: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void update(Personal entity) throws SQLException {
        String sql = "{call PKG_PERSONAL.PR_ACTUALIZAR_PERSONAL(?, ?, ?, ?, ?, ?, ?, ?)}";

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

            cs.execute();
            System.out.println("Personal actualizado: " + entity.getUsuarioSistema());

        } catch (SQLException e) {
            System.err.println("Error al actualizar personal: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void save(Personal entity) throws SQLException {
        // No implementado - Personal se crea a través de Entrenadores, Administradores o Recepcionistas
    }

    @Override
    public Optional<Personal> findById(Integer integer) throws SQLException {
        // No implementado - Se usa findByUsuario para buscar
        return Optional.empty();
    }

    @Override
    public void delete(Integer integer) throws SQLException {
        // No implementado - Personal se elimina a través de las tablas específicas
    }

    // Mapea un ResultSet a una instancia de Personal (o sus subclases)
    private Personal mapResultSetToPersonal(ResultSet rs) throws SQLException {
        String tipoPersonal = rs.getString("TIPO_PERSONAL");

        Personal personal;

        // Crear la instancia específica según el tipo
        switch (tipoPersonal) {
            case "ENTRENADOR":
                Entrenadores entrenador = new Entrenadores();
                entrenador.setDocuEntrenador(rs.getString("DOCUENTRENADOR"));
                entrenador.setEspecialidad(rs.getString("ESPECIALIDAD"));

                int experiencia = rs.getInt("EXPERIENCIA");
                if (!rs.wasNull()) {
                    entrenador.setExperiencia(experiencia);
                }

                personal = entrenador;
                break;

            case "ADMINISTRADOR":
                Administradores administrador = new Administradores();
                administrador.setDocuAdministrador(rs.getString("DOCUADMINISTRADOR"));
                administrador.setCargo(rs.getString("CARGO"));
                personal = administrador;
                break;

            case "RECEPCIONISTA":
                Recepcionistas recepcionista = new Recepcionistas();
                recepcionista.setDocuRecepcionista(rs.getString("DOCURECEPCIONISTA"));
                recepcionista.setHorarioTurno(rs.getString("HORARIO_TURNO"));
                personal = recepcionista;
                break;

            default:
                throw new SQLException("Tipo de personal desconocido: " + tipoPersonal);
        }

        // Mapear datos comunes de PERSONAL
        personal.setIdPersonal(rs.getInt("ID_PERSONAL"));
        personal.setNombres(rs.getString("NOMBRES"));
        personal.setApellidos(rs.getString("APELLIDOS"));
        personal.setTelefono(rs.getString("TELEFONO"));
        personal.setCorreo(rs.getString("CORREO"));
        personal.setUsuarioSistema(rs.getString("USUARIO_SISTEMA"));
        personal.setContrasena(rs.getString("CONTRASENA"));
        String tipoStr = rs.getString("TIPO_PERSONAL");
        if (tipoStr != null) {
            try {
                personal.setTipoPersonal(TipoPersonal.valueOf(tipoStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new SQLException("Tipo de personal inválido en la base de datos: " + tipoStr);
            }
        }
        if (rs.getDate("FECHA_CONTRATACION") != null) {
            personal.setFechaContratacion(rs.getDate("FECHA_CONTRATACION").toLocalDate());
        }

        return personal;
    }

}
