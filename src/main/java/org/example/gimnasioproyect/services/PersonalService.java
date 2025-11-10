package org.example.gimnasioproyect.services;

import org.example.gimnasioproyect.Utilidades.Validador;
import org.example.gimnasioproyect.model.Personal;
import org.example.gimnasioproyect.repository.PersonalRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PersonalService {

    private final PersonalRepository personalRepository;

    public PersonalService(PersonalRepository personalRepository) {
        this.personalRepository = personalRepository;
    }

    // Cambia la contraseña de un usuario
    public void cambiarContrasena(String usuario, String contrasenaActual,
                                  String nuevaContrasena) throws SQLException {
        if (usuario == null || usuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }

        Validador.validarContrasena(nuevaContrasena);

        Optional<Personal> personalOpt = personalRepository.findByUsuario(usuario);
        if (!personalOpt.isPresent()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        Personal personal = personalOpt.get();

        if (!personal.getContrasena().equals(contrasenaActual)) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        personal.setContrasena(nuevaContrasena);

        // ✅ Actualiza solo la tabla PERSONAL (sin instanceof)
        personalRepository.update(personal);
    }

    // Actualiza datos comunes de cualquier tipo de personal
    public void actualizarDatosComunes(String usuario, String telefono,
                                       String correo) throws SQLException {
        if (usuario == null || usuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }

        Validador.validarTelefono(telefono);
        Validador.validarCorreo(correo);

        Optional<Personal> personalOpt = personalRepository.findByUsuario(usuario);
        if (!personalOpt.isPresent()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        Personal personal = personalOpt.get();
        personal.setTelefono(telefono);
        personal.setCorreo(correo);

        // ✅ Actualiza solo la tabla PERSONAL (sin instanceof)
        personalRepository.update(personal);
    }

    public boolean existeUsuario(String usuario) throws SQLException {
        if (usuario == null || usuario.trim().isEmpty()) {
            return false;
        }
        return personalRepository.existeUsuario(usuario);
    }

    public Optional<Personal> buscarPorUsuario(String usuario) throws SQLException {
        if (usuario == null || usuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }
        return personalRepository.findByUsuario(usuario);
    }

    public List<Personal> obtenerTodoElPersonal() throws SQLException {
        return personalRepository.findAll();
    }

    public List<Personal> obtenerPersonalPorTipo(String tipoPersonal) throws SQLException {
        if (tipoPersonal == null || tipoPersonal.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de personal es obligatorio");
        }
        return personalRepository.findByTipo(tipoPersonal);
    }

    public Optional<Personal> validarCredenciales(String usuario, String contrasena) throws SQLException {
        if (usuario == null || usuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }

        if (contrasena == null || contrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }

        return personalRepository.autenticar(usuario, contrasena);
    }

    public void resetearContrasena(String usuario, String nuevaContrasena) throws SQLException {
        if (usuario == null || usuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }

        Validador.validarContrasena(nuevaContrasena);

        Optional<Personal> personalOpt = personalRepository.findByUsuario(usuario);
        if (!personalOpt.isPresent()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        Personal personal = personalOpt.get();
        personal.setContrasena(nuevaContrasena);

        // ✅ Actualiza solo la tabla PERSONAL (sin instanceof)
        personalRepository.update(personal);
    }

    public long contarPersonalPorTipo(String tipoPersonal) throws SQLException {
        return personalRepository.findByTipo(tipoPersonal).size();
    }

    public int obtenerTotalPersonal() throws SQLException {
        return personalRepository.findAll().size();
    }

    public void validarUsuarioUnico(String usuario) throws SQLException {
        Validador.validarUsuarioSistema(usuario);

        if (personalRepository.existeUsuario(usuario)) {
            throw new IllegalArgumentException("El usuario ya existe: " + usuario);
        }
    }
}