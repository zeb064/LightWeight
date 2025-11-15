package org.example.gimnasioproyect.repository;

import oracle.jdbc.internal.OracleTypes;
import org.example.gimnasioproyect.model.Rutinas;
import org.example.gimnasioproyect.confi.OracleDatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RutinaRepositoryImpl implements  RutinaRepository{
    private final OracleDatabaseConnection connection;

    public RutinaRepositoryImpl(OracleDatabaseConnection connection) throws SQLException {
        this.connection = connection;
        try (Connection conn = this.connection.connect()) {
            System.out.println("🎯 Conexión a BD probada exitosamente - RutinaRepository");
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void save(Rutinas entity) throws SQLException {
        String sql = "{call PKG_RUTINAS.PR_INSERTAR_RUTINA(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            //ps.setInt(1, entity.getIdRutina());
            cs.setString(1, entity.getObjetivo());

            cs.execute();
            System.out.println("✅ Rutina guardada exitosamente: " + entity.getIdRutina());

        } catch (SQLException e) {
            System.err.println("❌ Error al guardar rutina: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Rutinas> findById(Integer id) throws SQLException {
        String sql = "{? = call PKG_RUTINAS.FN_OBTENER_RUTINA_POR_ID(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setInt(2, id);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            if (rs.next()) {
                Rutinas rutina = mapResultSetToRutina(rs);
                return Optional.of(rutina);
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar rutina: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Rutinas> findAll() throws SQLException {
        String sql = "{? = call PKG_RUTINAS.FN_LISTAR_RUTINAS()}";
        List<Rutinas> rutinas = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                rutinas.add(mapResultSetToRutina(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al listar rutinas: " + e.getMessage());
            throw e;
        }

        return rutinas;
    }

    @Override
    public List<Rutinas> findByObjetivo(String objetivo) throws SQLException {
        String sql = "{? = call PKG_RUTINAS.FN_BUSCAR_RUTINAS_POR_OBJETIVO(?)}";
        List<Rutinas> rutinas = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, objetivo);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                rutinas.add(mapResultSetToRutina(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar rutinas por objetivo: " + e.getMessage());
            throw e;
        }

        return rutinas;
    }

    @Override
    public void update(Rutinas entity) throws SQLException {
        String sql = "{call PKG_RUTINAS.PR_ACTUALIZAR_RUTINA(?, ?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, entity.getIdRutina());
            cs.setString(2, entity.getObjetivo());

            cs.execute();
            System.out.println("✅ Rutina actualizada: " + entity.getIdRutina());

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar rutina: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "{call PKG_RUTINAS.PR_ELIMINAR_RUTINA(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.execute();
            System.out.println("✅ Rutina eliminada: " + id);

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar rutina: " + e.getMessage());
            throw e;
        }
    }

    private Rutinas mapResultSetToRutina(ResultSet rs) throws SQLException {
        Rutinas rutina = new Rutinas();
        rutina.setIdRutina(rs.getInt("ID_RUTINA"));
        rutina.setObjetivo(rs.getString("OBJETIVO"));
        return rutina;
    }
}
