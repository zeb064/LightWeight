package org.example.gimnasioproyect.repository;

import org.example.gimnasioproyect.Utilidades.TipoPersonal;
import org.example.gimnasioproyect.model.Administradores;
import org.example.gimnasioproyect.model.Entrenadores;
import org.example.gimnasioproyect.model.Personal;
import org.example.gimnasioproyect.model.Recepcionistas;
import org.example.gimnasioproyect.confi.OracleDatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PersonalRepositoryImpl implements PersonalRepository {
    private final OracleDatabaseConnection connection;

    public PersonalRepositoryImpl(OracleDatabaseConnection connection) throws SQLException {
        this.connection = connection;
        try (Connection conn = this.connection.connect()) {
            System.out.println("🎯 Conexión a BD probada exitosamente - PersonalRepository");
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Personal> findByUsuario(String usuario) throws SQLException {
        String sql = "SELECT p.ID_PERSONAL, p.NOMBRES, p.APELLIDOS, p.TELEFONO, p.CORREO, " +
                "p.USUARIO_SISTEMA, p.CONTRASENA, p.TIPO_PERSONAL, p.FECHA_CONTRATACION, " +
                "e.DOCUENTRENADOR, e.ESPECIALIDAD, e.EXPERIENCIA, " +
                "a.DOCUADMINISTRADOR, a.CARGO, " +
                "r.DOCURECEPCIONISTA, r.HORARIO_TURNO " +
                "FROM PERSONAL p " +
                "LEFT JOIN ENTRENADORES e ON p.ID_PERSONAL = e.ID_PERSONAL " +
                "LEFT JOIN ADMINISTRADORES a ON p.ID_PERSONAL = a.ID_PERSONAL " +
                "LEFT JOIN RECEPCIONISTAS r ON p.ID_PERSONAL = r.ID_PERSONAL " +
                "WHERE p.USUARIO_SISTEMA = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Personal personal = mapResultSetToPersonal(rs);
                return Optional.of(personal);
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar personal por usuario: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Personal> autenticar(String usuario, String contrasena) throws SQLException {
        String sql = "SELECT p.ID_PERSONAL, p.NOMBRES, p.APELLIDOS, p.TELEFONO, p.CORREO, " +
                "p.USUARIO_SISTEMA, p.CONTRASENA, p.TIPO_PERSONAL, p.FECHA_CONTRATACION, " +
                "e.DOCUENTRENADOR, e.ESPECIALIDAD, e.EXPERIENCIA, " +
                "a.DOCUADMINISTRADOR, a.CARGO, " +
                "r.DOCURECEPCIONISTA, r.HORARIO_TURNO " +
                "FROM PERSONAL p " +
                "LEFT JOIN ENTRENADORES e ON p.ID_PERSONAL = e.ID_PERSONAL " +
                "LEFT JOIN ADMINISTRADORES a ON p.ID_PERSONAL = a.ID_PERSONAL " +
                "LEFT JOIN RECEPCIONISTAS r ON p.ID_PERSONAL = r.ID_PERSONAL " +
                "WHERE p.USUARIO_SISTEMA = ? AND p.CONTRASENA = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, contrasena);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Personal personal = mapResultSetToPersonal(rs);
                System.out.println("✅ Autenticación exitosa: " + usuario + " - Tipo: " + personal.getTipoPersonal());
                return Optional.of(personal);
            }

            System.out.println("❌ Autenticación fallida para: " + usuario);
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("❌ Error al autenticar: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Personal> findAll() throws SQLException {
        String sql = "SELECT p.ID_PERSONAL, p.NOMBRES, p.APELLIDOS, p.TELEFONO, p.CORREO, " +
                "p.USUARIO_SISTEMA, p.CONTRASENA, p.TIPO_PERSONAL, p.FECHA_CONTRATACION, " +
                "e.DOCUENTRENADOR, e.ESPECIALIDAD, e.EXPERIENCIA, " +
                "a.DOCUADMINISTRADOR, a.CARGO, " +
                "r.DOCURECEPCIONISTA, r.HORARIO_TURNO " +
                "FROM PERSONAL p " +
                "LEFT JOIN ENTRENADORES e ON p.ID_PERSONAL = e.ID_PERSONAL " +
                "LEFT JOIN ADMINISTRADORES a ON p.ID_PERSONAL = a.ID_PERSONAL " +
                "LEFT JOIN RECEPCIONISTAS r ON p.ID_PERSONAL = r.ID_PERSONAL " +
                "ORDER BY p.TIPO_PERSONAL, p.NOMBRES, p.APELLIDOS";

        List<Personal> personalList = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                personalList.add(mapResultSetToPersonal(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al listar personal: " + e.getMessage());
            throw e;
        }

        return personalList;
    }

    @Override
    public List<Personal> findByTipo(String tipoPersonal) throws SQLException {
        String sql = "SELECT p.ID_PERSONAL, p.NOMBRES, p.APELLIDOS, p.TELEFONO, p.CORREO, " +
                "p.USUARIO_SISTEMA, p.CONTRASENA, p.TIPO_PERSONAL, p.FECHA_CONTRATACION, " +
                "e.DOCUENTRENADOR, e.ESPECIALIDAD, e.EXPERIENCIA, " +
                "a.DOCUADMINISTRADOR, a.CARGO, " +
                "r.DOCURECEPCIONISTA, r.HORARIO_TURNO " +
                "FROM PERSONAL p " +
                "LEFT JOIN ENTRENADORES e ON p.ID_PERSONAL = e.ID_PERSONAL " +
                "LEFT JOIN ADMINISTRADORES a ON p.ID_PERSONAL = a.ID_PERSONAL " +
                "LEFT JOIN RECEPCIONISTAS r ON p.ID_PERSONAL = r.ID_PERSONAL " +
                "WHERE UPPER(p.TIPO_PERSONAL) = ? " +
                "ORDER BY p.NOMBRES, p.APELLIDOS";

        List<Personal> personalList = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tipoPersonal.toUpperCase());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                personalList.add(mapResultSetToPersonal(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar personal por tipo: " + e.getMessage());
            throw e;
        }

        return personalList;
    }

    @Override
    public boolean existeUsuario(String usuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM PERSONAL WHERE USUARIO_SISTEMA = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("❌ Error al verificar existencia de usuario: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Mapea el ResultSet a la instancia correcta de Personal según el tipo
     * Usa polimorfismo para retornar Entrenador, Administrador o Recepcionista
     */
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
