package org.example.gimnasioproyect.repository;

import org.example.gimnasioproyect.model.MensajesTelegram;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface MensajeTelegramRepository extends Repository<MensajesTelegram, Integer> {
    Optional<MensajesTelegram> findByTipo(String tipo) throws SQLException;

    List<MensajesTelegram> findActivos() throws SQLException;

    void activar(Integer id) throws SQLException;

    void desactivar(Integer id) throws SQLException;
}
