package org.example.gimnasioproyect.repository;

import oracle.jdbc.internal.OracleTypes;
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
            System.out.println("Conexión a BD probada exitosamente - MembresiaRepository");
        } catch (SQLException e) {
            System.err.println("Error al conectar: " + e.getMessage());
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
            System.out.println("Membresía guardada exitosamente: " + entity.getIdMembresia());

        } catch (SQLException e) {
            System.err.println("Error al guardar membresía: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Membresias> findById(Integer id) throws SQLException {
        String sql = "{? = call PKG_MEMBRESIAS.FN_OBTENER_MEMBRESIA_POR_ID(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setInt(2, id);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            if (rs.next()) {
                Membresias membresia = mapResultSetToMembresia(rs);
                return Optional.of(membresia);
            }
            return Optional.empty();

        } catch (SQLException e) {
            System.err.println("Error al buscar membresía: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Membresias> findAll() throws SQLException {
        String sql = "{? = call PKG_MEMBRESIAS.FN_LISTAR_MEMBRESIAS()}";
        List<Membresias> membresias = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                membresias.add(mapResultSetToMembresia(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar membresías: " + e.getMessage());
            throw e;
        }

        return membresias;
    }

    @Override
    public List<Membresias> findByTipo(String tipo) throws SQLException {
        String sql = "{? = call PKG_MEMBRESIAS.FN_BUSCAR_MEMBRESIAS_POR_TIPO(?)}";
        List<Membresias> membresias = new ArrayList<>();

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, tipo);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                membresias.add(mapResultSetToMembresia(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar membresías por tipo: " + e.getMessage());
            throw e;
        }

        return membresias;
    }

    @Override
    public void update(Membresias entity) throws SQLException {
        String sql = "{call PKG_MEMBRESIAS.PR_ACTUALIZAR_MEMBRESIA(?, ?, ?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, entity.getIdMembresia());
            cs.setString(2, entity.getTipoMembresia());
            cs.setDouble(3, entity.getPrecioMembresia());

            cs.execute();
            System.out.println("Membresía actualizada: " + entity.getIdMembresia());

        } catch (SQLException e) {
            System.err.println("Error al actualizar membresía: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "{call PKG_MEMBRESIAS.PR_ELIMINAR_MEMBRESIA(?)}";

        try (Connection conn = this.connection.connect();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.execute();
            System.out.println("Membresía eliminada: " + id);

        } catch (SQLException e) {
            System.err.println("Error al eliminar membresía: " + e.getMessage());
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
