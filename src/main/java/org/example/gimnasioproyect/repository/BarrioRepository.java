package org.example.gimnasioproyect.repository;

import org.example.gimnasioproyect.model.Barrios;

import java.sql.SQLException;
import java.util.Optional;

public interface BarrioRepository extends Repository<Barrios, Integer>{
    Optional<Barrios> findByNombre(String nombre) throws SQLException;
}
