package org.example.gimnasioproyect.repository;

import org.example.gimnasioproyect.confi.OracleDatabaseConnection;
import org.example.gimnasioproyect.model.MensajesTelegram;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MensajeTelegramRepositoryImpl implements MensajeTelergramRepository{
    private final OracleDatabaseConnection connection;

    public MensajeTelegramRepositoryImpl(OracleDatabaseConnection connection) throws SQLException {
        this.connection = connection;
        try (Connection conn = this.connection.connect()) {
            System.out.println("🎯 Conexión a BD probada exitosamente - MembresiaClienteRepository");
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar: " + e.getMessage());
            throw e;
        }
    }


    @Override
    public Optional<MensajesTelegram> findByTipo(String tipo) throws SQLException {
        return Optional.empty();
    }

    @Override
    public List<MensajesTelegram> findActivos() throws SQLException {
        return List.of();
    }

    @Override
    public void activar(Integer id) throws SQLException {

    }

    @Override
    public void desactivar(Integer id) throws SQLException {

    }

    @Override
    public void save(MensajesTelegram entity) throws SQLException {

    }

    @Override
    public Optional<MensajesTelegram> findById(Integer integer) throws SQLException {
        return Optional.empty();
    }

    @Override
    public List<MensajesTelegram> findAll() throws SQLException {
        return List.of();
    }

    @Override
    public void update(MensajesTelegram entity) throws SQLException {

    }

    @Override
    public void delete(Integer integer) throws SQLException {

    }
}
