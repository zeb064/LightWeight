package org.example.gimnasioproyect.services;

import org.example.gimnasioproyect.Utilidades.Validador;
import org.example.gimnasioproyect.model.Barrios;
import org.example.gimnasioproyect.repository.BarrioRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class BarrioService {
    private final BarrioRepository barrioRepository;

    public BarrioService(BarrioRepository barrioRepository) {
        this.barrioRepository = barrioRepository;
    }

    // Registra un nuevo barrio
    public void registrarBarrio(Barrios barrio) throws SQLException {
        validarDatosBarrio(barrio);

        // Verificar que no exista el nombre
        if (barrioRepository.findByNombre(barrio.getNombreBarrio()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un barrio con el nombre: " + barrio.getNombreBarrio());
        }

        barrioRepository.save(barrio);
    }

    // Actualiza los datos de un barrio existente
    public void actualizarBarrio(Barrios barrio) throws SQLException {
        validarDatosBarrio(barrio);

        // Verificar que existe
        if (!barrioRepository.findById(barrio.getIdBarrio()).isPresent()) {
            throw new IllegalArgumentException("No existe el barrio con ID: " + barrio.getIdBarrio());
        }

        barrioRepository.update(barrio);
    }

    // Busca un barrio por ID
    public Optional<Barrios> buscarBarrioPorId(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El ID es obligatorio");
        }
        return barrioRepository.findById(id);
    }

    // Busca un barrio por nombre
    public Optional<Barrios> buscarBarrioPorNombre(String nombre) throws SQLException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        return barrioRepository.findByNombre(nombre);
    }

    // Obtiene todos los barrios
    public List<Barrios> obtenerTodosLosBarrios() throws SQLException {
        return barrioRepository.findAll();
    }

    // Elimina un barrio por ID
    public void eliminarBarrio(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El ID es obligatorio");
        }

        // Verificar que existe
        if (!barrioRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("No existe el barrio con ID: " + id);
        }

        barrioRepository.delete(id);
    }

    // Valida los datos de un barrio
    private void validarDatosBarrio(Barrios barrio) {
        if (barrio == null) {
            throw new IllegalArgumentException("El barrio no puede ser nulo");
        }

        Validador.validarTexto(barrio.getNombreBarrio(), "Nombre del barrio", 20, true);
    }
}
