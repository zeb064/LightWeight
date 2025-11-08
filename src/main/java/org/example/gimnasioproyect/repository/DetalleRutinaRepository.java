package org.example.gimnasioproyect.repository;

import org.example.gimnasioproyect.model.DetalleRutinas;

import java.sql.SQLException;
import java.util.List;

public interface DetalleRutinaRepository extends Repository<DetalleRutinas, Integer>{
    List<DetalleRutinas> findByRutina(Integer idRutina) throws SQLException;

    List<DetalleRutinas> findByRutinaAndDia(Integer idRutina, String diaSemana) throws SQLException;

    void deleteByRutina(Integer idRutina) throws SQLException;
}
