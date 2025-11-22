package org.example.gimnasioproyect.repository;

import oracle.jdbc.internal.OracleTypes;
import org.example.gimnasioproyect.model.DetalleRutinas;
import org.example.gimnasioproyect.model.Rutinas;
import org.example.gimnasioproyect.confi.OracleDatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DetalleRutinaRepositoryImpl implements DetalleRutinaRepository {
    private final OracleDatabaseConnection connection;

    public DetalleRutinaRepositoryImpl(OracleDatabaseConnection connection) throws SQLException {
        this.connection = connection;
        try (Connection conn = this.connection.connect()) {
            System.out.println("🎯 Conexión a BD probada exitosamente - DetalleRutinaRepository");
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void save(DetalleRutinas entity) throws SQLException {
        String sql = "{call PKG_DETALLERUTINAS.PR_INSERTAR_DETALLE_RUTINA(?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

           // ps.setInt(1, entity.getIdDetalle());
            cs.setString(1, entity.getDiaSemana());
            cs.setInt(2, entity.getOrden() != null ? entity.getOrden() : 0);
            cs.setString(3, entity.getEjercicio());
            cs.setInt(4, entity.getSeries() != null ? entity.getSeries() : 0);
            cs.setInt(5, entity.getRepeticiones() != null ? entity.getRepeticiones() : 0);

            if (entity.getPeso() != null) {
                cs.setDouble(6, entity.getPeso());
            } else {
                cs.setNull(6, Types.DOUBLE);
            }

            cs.setString(7, entity.getNotas());
            cs.setInt(8, entity.getRutina().getIdRutina());

            cs.execute();
            System.out.println("Detalle de rutina guardado exitosamente");

        } catch (SQLException e) {
            System.err.println("Error al guardar detalle rutina: " + e.getMessage());
            if (e.getErrorCode() == 20013) {
                throw new IllegalArgumentException("No se puede poner el mismo orden para el mismo día en una rutina.");
            }
            throw e;
        }
    }

    @Override
    public Optional<DetalleRutinas> findById(Integer id) throws SQLException {
        String sql = "{? = call PKG_DETALLERUTINAS.FN_OBTENER_DETALLE_POR_ID(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setInt(2, id);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            if (rs.next()) {
                DetalleRutinas detalle = mapResultSetToDetalleRutina(rs);
                return Optional.of(detalle);
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar detalle rutina: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<DetalleRutinas> findByRutina(Integer idRutina) throws SQLException {
        String sql = "{? = call PKG_DETALLERUTINAS.FN_BUSCAR_DETALLES_POR_RUTINA(?)}";
        List<DetalleRutinas> detalles = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setInt(2, idRutina);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                detalles.add(mapResultSetToDetalleRutina(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar detalles de rutina: " + e.getMessage());
            throw e;
        }

        return detalles;
    }

    @Override
    public List<DetalleRutinas> findByRutinaAndDia(Integer idRutina, String diaSemana) throws SQLException {
        String sql = "{? = call PKG_DETALLERUTINAS.FN_BUSCAR_DETALLES_POR_RUTINA_DIA(?, ?)}";
        List<DetalleRutinas> detalles = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setInt(2, idRutina);
            cs.setString(3, diaSemana);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                detalles.add(mapResultSetToDetalleRutina(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar detalles por día: " + e.getMessage());
            throw e;
        }

        return detalles;
    }

    @Override
    public List<DetalleRutinas> findAll() throws SQLException {
        String sql = "{? = call PKG_DETALLERUTINAS.FN_LISTAR_DETALLES_RUTINAS()}";
        List<DetalleRutinas> detalles = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                detalles.add(mapResultSetToDetalleRutina(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al listar detalles rutina: " + e.getMessage());
            throw e;
        }

        return detalles;
    }

    @Override
    public void update(DetalleRutinas entity) throws SQLException {
        String sql = "{call PKG_DETALLERUTINAS.PR_ACTUALIZAR_DETALLE_RUTINA(?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, entity.getIdDetalle());
            cs.setString(2, entity.getDiaSemana());
            cs.setInt(3, entity.getOrden() != null ? entity.getOrden() : 0);
            cs.setString(4, entity.getEjercicio());
            cs.setInt(5, entity.getSeries() != null ? entity.getSeries() : 0);
            cs.setInt(6, entity.getRepeticiones() != null ? entity.getRepeticiones() : 0);

            if (entity.getPeso() != null) {
                cs.setDouble(7, entity.getPeso());
            } else {
                cs.setNull(7, Types.DOUBLE);
            }

            cs.setString(8, entity.getNotas());
            cs.setInt(9, entity.getRutina().getIdRutina());

            cs.execute();
            System.out.println("✅ Detalle rutina actualizado: " + entity.getIdDetalle());

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar detalle rutina: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "{call PKG_DETALLERUTINAS.PR_ELIMINAR_DETALLE_RUTINA(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.execute();
            System.out.println("✅ Detalle rutina eliminado: " + id);

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar detalle rutina: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void deleteByRutina(Integer idRutina) throws SQLException {
        String sql = "{call PKG_DETALLERUTINAS.PR_ELIMINAR_DETALLES_POR_RUTINA(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, idRutina);
            cs.execute();
            System.out.println("✅ Detalles de rutina eliminados para ID_RUTINA: " + idRutina);

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar detalles por rutina: " + e.getMessage());
            throw e;
        }
    }

    private DetalleRutinas mapResultSetToDetalleRutina(ResultSet rs) throws SQLException {
        DetalleRutinas detalle = new DetalleRutinas();

        detalle.setIdDetalle(rs.getInt("ID_DETALLE"));
        detalle.setDiaSemana(rs.getString("DIA_SEMANA"));
        detalle.setOrden(rs.getInt("ORDEN"));
        detalle.setEjercicio(rs.getString("EJERCICIO"));
        detalle.setSeries(rs.getInt("SERIES"));
        detalle.setRepeticiones(rs.getInt("REPETICIONES"));

        double peso = rs.getDouble("PESO");
        if (!rs.wasNull()) {
            detalle.setPeso(peso);
        }

        detalle.setNotas(rs.getString("NOTAS"));

        // Mapear Rutina
        Rutinas rutina = new Rutinas();
        rutina.setIdRutina(rs.getInt("ID_RUTINA"));
        rutina.setObjetivo(rs.getString("OBJETIVO"));
        detalle.setRutina(rutina);

        return detalle;
    }
}
