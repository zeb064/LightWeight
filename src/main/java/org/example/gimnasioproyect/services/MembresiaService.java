package org.example.gimnasioproyect.services;

import org.example.gimnasioproyect.Utilidades.Validador;
import org.example.gimnasioproyect.model.Membresias;
import org.example.gimnasioproyect.repository.MembresiaRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MembresiaService {
    private final MembresiaRepository membresiaRepository;

    public MembresiaService(MembresiaRepository membresiaRepository) {
        this.membresiaRepository = membresiaRepository;
    }

    // Registra una nueva membresía
    public void registrarMembresia(Membresias membresia) throws SQLException {
        validarDatosMembresia(membresia);

        // Verificar que no exista el tipo
        List<Membresias> existentes = membresiaRepository.findByTipo(membresia.getTipoMembresia());
        if (!existentes.isEmpty()) {
            throw new IllegalArgumentException("Ya existe una membresía del tipo: " + membresia.getTipoMembresia());
        }

        membresiaRepository.save(membresia);
    }

    // Actualiza los datos de una membresía existente
    public void actualizarMembresia(Membresias membresia) throws SQLException {
        validarDatosMembresia(membresia);

        // Verificar que existe
        if (!membresiaRepository.findById(membresia.getIdMembresia()).isPresent()) {
            throw new IllegalArgumentException("No existe la membresía con ID: " + membresia.getIdMembresia());
        }

        membresiaRepository.update(membresia);
    }

    // Busca una membresía por ID
    public Optional<Membresias> buscarMembresiaPorId(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El ID es obligatorio");
        }
        return membresiaRepository.findById(id);
    }

    // Busca membresías por tipo
    public List<Membresias> buscarPorTipo(String tipo) throws SQLException {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo es obligatorio");
        }
        return membresiaRepository.findByTipo(tipo);
    }

    // Obtiene todas las membresías
    public List<Membresias> obtenerTodasLasMembresias() throws SQLException {
        return membresiaRepository.findAll();
    }

    // Actualiza el precio de una membresía
    public void actualizarPrecio(Integer idMembresia, Double nuevoPrecio) throws SQLException {
        if (idMembresia == null) {
            throw new IllegalArgumentException("El ID es obligatorio");
        }

        Validador.validarPrecio(nuevoPrecio, "Precio");

        Optional<Membresias> membresiaOpt = membresiaRepository.findById(idMembresia);
        if (!membresiaOpt.isPresent()) {
            throw new IllegalArgumentException("No existe la membresía con ID: " + idMembresia);
        }

        Membresias membresia = membresiaOpt.get();
        membresia.setPrecioMembresia(nuevoPrecio);

        membresiaRepository.update(membresia);
    }

    // Elimina una membresía por ID
    public void eliminarMembresia(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El ID es obligatorio");
        }

        // Verificar que existe
        if (!membresiaRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("No existe la membresía con ID: " + id);
        }


        // Nota: Idealmente deberías verificar que no esté asignada a clientes
        // Pero eso requeriría inyectar MembresiaClienteRepository
        // Por ahora la BD debería tener restricción de FK

        membresiaRepository.delete(id);
    }

    // Valida los datos de una membresía
    private void validarDatosMembresia(Membresias membresia) {
        if (membresia == null) {
            throw new IllegalArgumentException("La membresía no puede ser nula");
        }

        Validador.validarTexto(membresia.getTipoMembresia(), "Tipo de membresía", 15, true);
        Validador.validarPrecio(membresia.getPrecioMembresia(), "Precio");
    }
}
