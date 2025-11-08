package org.example.gimnasioproyect.repository;

import org.example.gimnasioproyect.model.Personal;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PersonalRepository {
    Optional<Personal> findByUsuario(String usuario) throws SQLException;

    Optional<Personal> autenticar(String usuario, String contrasena) throws SQLException;

    List<Personal> findAll() throws SQLException;

    List<Personal> findByTipo(String tipoPersonal) throws SQLException;

    boolean existeUsuario(String usuario) throws SQLException;
}
