package org.example.gimnasioproyect.repository;

import org.example.gimnasioproyect.model.Rutinas;
import org.example.gimnasioproyect.confi.OracleDatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        String sql = "INSERT INTO RUTINAS (OBJETIVO) VALUES (?)";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            //ps.setInt(1, entity.getIdRutina());
            ps.setString(1, entity.getObjetivo());

            ps.executeUpdate();
            System.out.println("✅ Rutina guardada exitosamente: " + entity.getIdRutina());

        } catch (SQLException e) {
            System.err.println("❌ Error al guardar rutina: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Rutinas> findById(Integer id) throws SQLException {
        String sql = "SELECT ID_RUTINA, OBJETIVO FROM RUTINAS WHERE ID_RUTINA = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

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
        String sql = "SELECT ID_RUTINA, OBJETIVO FROM RUTINAS ORDER BY ID_RUTINA";

        List<Rutinas> rutinas = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

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
        String sql = "SELECT ID_RUTINA, OBJETIVO FROM RUTINAS " +
                "WHERE UPPER(OBJETIVO) LIKE ? ORDER BY ID_RUTINA";

        List<Rutinas> rutinas = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + objetivo.toUpperCase() + "%");
            ResultSet rs = ps.executeQuery();

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
        String sql = "UPDATE RUTINAS SET OBJETIVO = ? WHERE ID_RUTINA = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, entity.getObjetivo());
            ps.setInt(2, entity.getIdRutina());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Rutina actualizada: " + entity.getIdRutina());
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar rutina: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM RUTINAS WHERE ID_RUTINA = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Rutina eliminada: " + id);
            }

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
