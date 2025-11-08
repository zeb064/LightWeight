package org.example.gimnasioproyect.repository;

import org.example.gimnasioproyect.model.Asistencias;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface AsistenciaRepository extends Repository<Asistencias, Integer> {
    List<Asistencias> findByCliente(String documentoCliente) throws SQLException;

    List<Asistencias> findByFecha(LocalDate fecha) throws SQLException;

    List<Asistencias> findByClienteAndFechaRange(String documentoCliente, LocalDate fechaInicio,
                                                 LocalDate fechaFin) throws SQLException;

    int countAsistenciasByCliente(String documentoCliente) throws SQLException;

    int countAsistenciasByClienteAndMonth(String documentoCliente, int mes, int anio) throws SQLException;
}
