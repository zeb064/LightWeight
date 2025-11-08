package org.example.gimnasioproyect.repository;

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
        String sql = "INSERT INTO DETALLERUTINAS (DIA_SEMANA, ORDEN, EJERCICIO, " +
                "SERIES, REPETICIONES, PESO, NOTAS, ID_RUTINA) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

           // ps.setInt(1, entity.getIdDetalle());
            ps.setString(1, entity.getDiaSemana());
            ps.setInt(2, entity.getOrden() != null ? entity.getOrden() : 0);
            ps.setString(3, entity.getEjercicio());
            ps.setInt(4, entity.getSeries() != null ? entity.getSeries() : 0);
            ps.setInt(5, entity.getRepeticiones() != null ? entity.getRepeticiones() : 0);

            if (entity.getPeso() != null) {
                ps.setDouble(6, entity.getPeso());
            } else {
                ps.setNull(6, Types.DOUBLE);
            }

            ps.setString(7, entity.getNotas());
            ps.setInt(8, entity.getRutina().getIdRutina());

            ps.executeUpdate();
            System.out.println("✅ Detalle de rutina guardado exitosamente");

        } catch (SQLException e) {
            System.err.println("❌ Error al guardar detalle rutina: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<DetalleRutinas> findById(Integer id) throws SQLException {
        String sql = "SELECT dr.ID_DETALLE, dr.DIA_SEMANA, dr.ORDEN, dr.EJERCICIO, " +
                "dr.SERIES, dr.REPETICIONES, dr.PESO, dr.NOTAS, " +
                "dr.ID_RUTINA, r.OBJETIVO " +
                "FROM DETALLERUTINAS dr " +
                "INNER JOIN RUTINAS r ON dr.ID_RUTINA = r.ID_RUTINA " +
                "WHERE dr.ID_DETALLE = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

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
        String sql = "SELECT dr.ID_DETALLE, dr.DIA_SEMANA, dr.ORDEN, dr.EJERCICIO, " +
                "dr.SERIES, dr.REPETICIONES, dr.PESO, dr.NOTAS, " +
                "dr.ID_RUTINA, r.OBJETIVO " +
                "FROM DETALLERUTINAS dr " +
                "INNER JOIN RUTINAS r ON dr.ID_RUTINA = r.ID_RUTINA " +
                "WHERE dr.ID_RUTINA = ? " +
                "ORDER BY dr.DIA_SEMANA, dr.ORDEN";

        List<DetalleRutinas> detalles = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idRutina);
            ResultSet rs = ps.executeQuery();

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
        String sql = "SELECT dr.ID_DETALLE, dr.DIA_SEMANA, dr.ORDEN, dr.EJERCICIO, " +
                "dr.SERIES, dr.REPETICIONES, dr.PESO, dr.NOTAS, " +
                "dr.ID_RUTINA, r.OBJETIVO " +
                "FROM DETALLERUTINAS dr " +
                "INNER JOIN RUTINAS r ON dr.ID_RUTINA = r.ID_RUTINA " +
                "WHERE dr.ID_RUTINA = ? AND UPPER(dr.DIA_SEMANA) = ? " +
                "ORDER BY dr.ORDEN";

        List<DetalleRutinas> detalles = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idRutina);
            ps.setString(2, diaSemana.toUpperCase());
            ResultSet rs = ps.executeQuery();

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
        String sql = "SELECT dr.ID_DETALLE, dr.DIA_SEMANA, dr.ORDEN, dr.EJERCICIO, " +
                "dr.SERIES, dr.REPETICIONES, dr.PESO, dr.NOTAS, " +
                "dr.ID_RUTINA, r.OBJETIVO " +
                "FROM DETALLERUTINAS dr " +
                "INNER JOIN RUTINAS r ON dr.ID_RUTINA = r.ID_RUTINA " +
                "ORDER BY dr.ID_RUTINA, dr.DIA_SEMANA, dr.ORDEN";

        List<DetalleRutinas> detalles = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

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
        String sql = "UPDATE DETALLERUTINAS SET DIA_SEMANA = ?, ORDEN = ?, EJERCICIO = ?, " +
                "SERIES = ?, REPETICIONES = ?, PESO = ?, NOTAS = ?, ID_RUTINA = ? " +
                "WHERE ID_DETALLE = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, entity.getDiaSemana());
            ps.setInt(2, entity.getOrden() != null ? entity.getOrden() : 0);
            ps.setString(3, entity.getEjercicio());
            ps.setInt(4, entity.getSeries() != null ? entity.getSeries() : 0);
            ps.setInt(5, entity.getRepeticiones() != null ? entity.getRepeticiones() : 0);

            if (entity.getPeso() != null) {
                ps.setDouble(6, entity.getPeso());
            } else {
                ps.setNull(6, Types.DOUBLE);
            }

            ps.setString(7, entity.getNotas());
            ps.setInt(8, entity.getRutina().getIdRutina());
            ps.setInt(9, entity.getIdDetalle());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Detalle rutina actualizado: " + entity.getIdDetalle());
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar detalle rutina: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM DETALLERUTINAS WHERE ID_DETALLE = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Detalle rutina eliminado: " + id);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar detalle rutina: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void deleteByRutina(Integer idRutina) throws SQLException {
        String sql = "DELETE FROM DETALLERUTINAS WHERE ID_RUTINA = ?";
        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idRutina);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Detalles de rutina eliminados: " + rowsAffected + " registros");
            }
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
