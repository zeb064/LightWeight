package org.example.gimnasioproyect.repository;

import org.example.gimnasioproyect.model.RutinaAsignadas;

import java.sql.SQLException;
import java.util.List;

public interface RutinaAsignadaRepository extends Repository<RutinaAsignadas, Integer>{
    List<RutinaAsignadas> findByCliente(String documentoCliente) throws SQLException;

    List<RutinaAsignadas> findRutinasActivasByCliente(String documentoCliente) throws SQLException;

    List<RutinaAsignadas> findByRutina(Integer idRutina) throws SQLException;

    List<RutinaAsignadas> findByEstado(String estado) throws SQLException;
}
