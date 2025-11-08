package org.example.gimnasioproyect.repository;

import org.example.gimnasioproyect.model.Entrenadores;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface EntrenadorRepository extends Repository<Entrenadores, String>{
    Optional<Entrenadores> findByDocumento(String documento) throws SQLException;

    List<Entrenadores> findByEspecialidad(String especialidad) throws SQLException;

    Optional<Entrenadores> findByUsuario(String usuario) throws SQLException;

}
