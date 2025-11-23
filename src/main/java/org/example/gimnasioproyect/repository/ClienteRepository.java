package org.example.gimnasioproyect.repository;

import org.example.gimnasioproyect.model.Clientes;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends Repository<Clientes, String>{
    Optional<Clientes> findByDocumento(String documento) throws SQLException;

    List<Clientes> findByNombre(String nombre) throws SQLException;

    List<Clientes> findByBarrio(Integer idBarrio) throws SQLException;

    Optional<Clientes> findByChatId(String chatId) throws SQLException;
}
