package org.example.gimnasioproyect.repository;

import org.example.gimnasioproyect.model.Recepcionistas;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface RecepcionistaRepository extends Repository<Recepcionistas, String>{
    Optional<Recepcionistas> findByDocumento(String documento) throws SQLException;

    Optional<Recepcionistas> findByUsuario(String usuario) throws SQLException;

    List<Recepcionistas> findByTurno(String turno) throws SQLException;
}
