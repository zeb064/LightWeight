package org.example.gimnasioproyect.repository;

import org.example.gimnasioproyect.model.AsignacionEntrenadores;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface AsignacionEntrenadorRepository extends Repository<AsignacionEntrenadores, Integer> {
    Optional<AsignacionEntrenadores> findAsignacionActivaByCliente(String documentoCliente) throws SQLException;

    List<AsignacionEntrenadores> findByCliente(String documentoCliente) throws SQLException;

    List<AsignacionEntrenadores> findByEntrenador(String documentoEntrenador) throws SQLException;

    List<AsignacionEntrenadores> findClientesActivosByEntrenador(String documentoEntrenador) throws SQLException;
}
