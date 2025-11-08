package org.example.gimnasioproyect.repository;

import org.example.gimnasioproyect.model.Membresias;

import java.sql.SQLException;
import java.util.List;

public interface MembresiaRepository extends Repository<Membresias, Integer>{
    List<Membresias> findByTipo(String tipo) throws SQLException;
}
