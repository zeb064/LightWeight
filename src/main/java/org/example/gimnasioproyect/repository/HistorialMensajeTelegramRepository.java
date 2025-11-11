package org.example.gimnasioproyect.repository;

import org.example.gimnasioproyect.model.HistorialMensajeTelegram;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface HistorialMensajeTelegramRepository extends Repository<HistorialMensajeTelegram, Integer>{
    List<HistorialMensajeTelegram> findByCliente(String documento) throws SQLException;

    List<HistorialMensajeTelegram> findByFecha(LocalDate fecha) throws SQLException;

    List<HistorialMensajeTelegram> findByEstado(String estado) throws SQLException;

    List<HistorialMensajeTelegram> findByTipoMensaje(String tipo) throws SQLException;

    void updateEstado(Integer id, String nuevoEstado) throws SQLException;
}
