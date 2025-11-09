package org.example.gimnasioproyect.repository;

import org.example.gimnasioproyect.model.Membresias;
import org.example.gimnasioproyect.confi.OracleDatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MembresiaRepositoryImpl implements MembresiaRepository{
    private final OracleDatabaseConnection connection;

    public MembresiaRepositoryImpl(OracleDatabaseConnection connection) throws SQLException {
        this.connection = connection;
        try (Connection conn = this.connection.connect()) {
            System.out.println("🎯 Conexión a BD probada exitosamente - MembresiaRepository");
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void save(Membresias entity) throws SQLException {
        String sql = "{call PKG_MEMBRESIAS.PR_INSERTAR_MEMBRESIA(?, ?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            //ps.setInt(1, entity.getIdMembresia());
            cs.setString(1, entity.getTipoMembresia());
            cs.setDouble(2, entity.getPrecioMembresia());

            cs.execute();
            System.out.println("✅ Membresía guardada exitosamente: " + entity.getIdMembresia());

        } catch (SQLException e) {
            System.err.println("❌ Error al guardar membresía: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Membresias> findById(Integer id) throws SQLException {
        String sql = "SELECT ID_MEMBRESIA, TIPO, PRECIO_MEMBRESIA FROM MEMBRESIAS WHERE ID_MEMBRESIA = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Membresias membresia = mapResultSetToMembresia(rs);
                return Optional.of(membresia);
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar membresía: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Membresias> findAll() throws SQLException {
        String sql = "SELECT ID_MEMBRESIA, TIPO, PRECIO_MEMBRESIA FROM MEMBRESIAS ORDER BY ID_MEMBRESIA";

        List<Membresias> membresias = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                membresias.add(mapResultSetToMembresia(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al listar membresías: " + e.getMessage());
            throw e;
        }

        return membresias;
    }

    @Override
    public List<Membresias> findByTipo(String tipo) throws SQLException {
        String sql = "SELECT ID_MEMBRESIA, TIPO, PRECIO_MEMBRESIA FROM MEMBRESIAS " +
                "WHERE UPPER(TIPO) = ? ORDER BY ID_MEMBRESIA";

        List<Membresias> membresias = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tipo.toUpperCase());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                membresias.add(mapResultSetToMembresia(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar membresías por tipo: " + e.getMessage());
            throw e;
        }

        return membresias;
    }

    @Override
    public void update(Membresias entity) throws SQLException {
        String sql = "UPDATE MEMBRESIAS SET TIPO = ?, PRECIO_MEMBRESIA = ? WHERE ID_MEMBRESIA = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, entity.getTipoMembresia());
            ps.setDouble(2, entity.getPrecioMembresia());
            ps.setInt(3, entity.getIdMembresia());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Membresía actualizada: " + entity.getIdMembresia());
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar membresía: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM MEMBRESIAS WHERE ID_MEMBRESIA = ?";

        try (Connection conn = this.connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Membresía eliminada: " + id);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar membresía: " + e.getMessage());
            throw e;
        }
    }

    private Membresias mapResultSetToMembresia(ResultSet rs) throws SQLException {
        Membresias membresia = new Membresias();
        membresia.setIdMembresia(rs.getInt("ID_MEMBRESIA"));
        membresia.setTipoMembresia(rs.getString("TIPO"));
        membresia.setPrecioMembresia(rs.getDouble("PRECIO_MEMBRESIA"));
        return membresia;
    }
}
