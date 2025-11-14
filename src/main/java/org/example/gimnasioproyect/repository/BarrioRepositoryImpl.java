package org.example.gimnasioproyect.repository;

import oracle.jdbc.internal.OracleTypes;
import org.example.gimnasioproyect.model.Barrios;
import org.example.gimnasioproyect.confi.OracleDatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BarrioRepositoryImpl implements  BarrioRepository{
    private final OracleDatabaseConnection connection;

    public BarrioRepositoryImpl(OracleDatabaseConnection connection) throws SQLException {
        this.connection = connection;
        try (Connection conn = this.connection.connect()) {
            System.out.println("🎯 Conexión a BD probada exitosamente - BarrioRepository");
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void save(Barrios entity) throws SQLException {
        String sql = "{call PKG_BARRIOS.PR_INSERTAR_BARRIO(?, ?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, entity.getIdBarrio());
            cs.setString(2, entity.getNombreBarrio());

            cs.execute();
            System.out.println("✅ Barrio guardado exitosamente: " + entity.getNombreBarrio());

        } catch (SQLException e) {
            System.err.println("❌ Error al guardar barrio: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Barrios> findById(Integer id) throws SQLException {
        String sql = "{? = call PKG_BARRIOS.FN_OBTENER_BARRIO_POR_ID(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setInt(2, id);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            if (rs.next()) {
                Barrios barrio = mapResultSetToBarrio(rs);
                return Optional.of(barrio);
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar barrio: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Barrios> findByNombre(String nombre) throws SQLException {
        String sql = "{? = call PKG_BARRIOS.FN_OBTENER_BARRIO_POR_NOMBRE(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, nombre);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            if (rs.next()) {
                Barrios barrio = mapResultSetToBarrio(rs);
                return Optional.of(barrio);
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar barrio por nombre: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Barrios> findAll() throws SQLException {
        String sql = "{? = call PKG_BARRIOS.FN_LISTAR_BARRIOS()}";
        List<Barrios> barrios = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                barrios.add(mapResultSetToBarrio(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al listar barrios: " + e.getMessage());
            throw e;
        }

        return barrios;
    }

    @Override
    public void update(Barrios entity) throws SQLException {
        String sql = "{call PKG_BARRIOS.PR_ACTUALIZAR_BARRIO(?, ?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, entity.getIdBarrio());
            cs.setString(2, entity.getNombreBarrio());

            cs.execute();
            System.out.println("✅ Barrio actualizado: " + entity.getIdBarrio());

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar barrio: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "{call PKG_BARRIOS.PR_ELIMINAR_BARRIO(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.execute();
            System.out.println("✅ Barrio eliminado: " + id);

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar barrio: " + e.getMessage());
            throw e;
        }
    }

    private Barrios mapResultSetToBarrio(ResultSet rs) throws SQLException {
        Barrios barrio = new Barrios();
        barrio.setIdBarrio(rs.getInt("ID_BARRIO"));
        barrio.setNombreBarrio(rs.getString("NOM_BARRIO"));
        return barrio;
    }
}
