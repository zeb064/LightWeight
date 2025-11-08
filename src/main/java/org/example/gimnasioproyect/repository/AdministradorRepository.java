package org.example.gimnasioproyect.repository;

import org.example.gimnasioproyect.model.Administradores;

import java.sql.SQLException;
import java.util.Optional;

public interface AdministradorRepository extends Repository<Administradores, String>{
    Optional<Administradores> findByDocumento(String documento) throws SQLException;

    Optional<Administradores> findByUsuario(String usuario) throws SQLException;
}
