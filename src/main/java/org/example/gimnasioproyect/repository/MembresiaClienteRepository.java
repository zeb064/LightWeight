package org.example.gimnasioproyect.repository;

import org.example.gimnasioproyect.model.MembresiaClientes;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface MembresiaClienteRepository extends Repository<MembresiaClientes, Integer> {
    Optional<MembresiaClientes> findMembresiaActivaByCliente(String documentoCliente) throws SQLException;

    List<MembresiaClientes> findByCliente(String documentoCliente) throws SQLException;

    List<MembresiaClientes> findMembresiasProximasAVencer(int dias) throws SQLException;

    List<MembresiaClientes> findMembresiasVencidas() throws SQLException;
}
