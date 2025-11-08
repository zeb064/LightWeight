package org.example.gimnasioproyect.repository;

import org.example.gimnasioproyect.model.Rutinas;

import java.sql.SQLException;
import java.util.List;

public interface RutinaRepository extends Repository<Rutinas, Integer> {
    List<Rutinas> findByObjetivo(String objetivo) throws SQLException;
}
