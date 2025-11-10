package org.example.gimnasioproyect.services;

import org.example.gimnasioproyect.Utilidades.Validador;
import org.example.gimnasioproyect.model.Administradores;
import org.example.gimnasioproyect.model.Entrenadores;
import org.example.gimnasioproyect.model.Personal;
import org.example.gimnasioproyect.model.Recepcionistas;
import org.example.gimnasioproyect.repository.AdministradorRepository;
import org.example.gimnasioproyect.repository.EntrenadorRepository;
import org.example.gimnasioproyect.repository.PersonalRepository;
import org.example.gimnasioproyect.repository.RecepcionistaRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PersonalService {

    private final PersonalRepository personalRepository;
    private final AdministradorRepository administradorRepository;
    private final EntrenadorRepository entrenadorRepository;
    private final RecepcionistaRepository recepcionistaRepository;

    public PersonalService(PersonalRepository personalRepository,
                           AdministradorRepository administradorRepository,
                           EntrenadorRepository entrenadorRepository,
                           RecepcionistaRepository recepcionistaRepository) {
        this.personalRepository = personalRepository;
        this.administradorRepository = administradorRepository;
        this.entrenadorRepository = entrenadorRepository;
        this.recepcionistaRepository = recepcionistaRepository;
    }

    // Cambia la contraseña de un usuario
    public void cambiarContrasena(String usuario, String contrasenaActual,
                                  String nuevaContrasena) throws SQLException {
        if (usuario == null || usuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }

        Validador.validarContrasena(nuevaContrasena);

        // Buscar el personal
        Optional<Personal> personalOpt = personalRepository.findByUsuario(usuario);
        if (!personalOpt.isPresent()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        Personal personal = personalOpt.get();

        // Verificar contraseña actual
        if (!personal.getContrasena().equals(contrasenaActual)) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        // Actualizar contraseña
        personal.setContrasena(nuevaContrasena);

        // Guardar según el tipo de personal
        actualizarPersonalSegunTipo(personal);
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

        actualizarPersonalSegunTipo(personal);
    }

    // Verifica si un usuario existe
    public boolean existeUsuario(String usuario) throws SQLException {
        if (usuario == null || usuario.trim().isEmpty()) {
            return false;
        }
        return personalRepository.existeUsuario(usuario);
    }

    // Busca personal por usuario
    public Optional<Personal> buscarPorUsuario(String usuario) throws SQLException {
        if (usuario == null || usuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }
        return personalRepository.findByUsuario(usuario);
    }

    // Obtiene todo el personal
    public List<Personal> obtenerTodoElPersonal() throws SQLException {
        return personalRepository.findAll();
    }

    // Obtiene personal por tipo
    public List<Personal> obtenerPersonalPorTipo(String tipoPersonal) throws SQLException {
        if (tipoPersonal == null || tipoPersonal.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de personal es obligatorio");
        }
        return personalRepository.findByTipo(tipoPersonal);
    }

    // Valida las credenciales de un usuario
    public Optional<Personal> validarCredenciales(String usuario, String contrasena) throws SQLException {
        if (usuario == null || usuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }

        if (contrasena == null || contrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }

        return personalRepository.autenticar(usuario, contrasena);
    }

    // Resetea la contraseña de un usuario (olvidó su contraseña)
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

        actualizarPersonalSegunTipo(personal);
    }

    // Cuenta el personal por tipo
    public long contarPersonalPorTipo(String tipoPersonal) throws SQLException {
        return personalRepository.findByTipo(tipoPersonal).size();
    }

    // Cuenta el total de personal
    public int obtenerTotalPersonal() throws SQLException {
        return personalRepository.findAll().size();
    }

    // Actualiza el personal según su tipo
    private void actualizarPersonalSegunTipo(Personal personal) throws SQLException {
        if (personal instanceof Administradores) {
            administradorRepository.update((Administradores) personal);
        } else if (personal instanceof Entrenadores) {
            entrenadorRepository.update((Entrenadores) personal);
        } else if (personal instanceof Recepcionistas) {
            recepcionistaRepository.update((Recepcionistas) personal);
        } else {
            throw new IllegalArgumentException("Tipo de personal desconocido");
        }
    }

    // Valida que un usuario sea único en el sistema
    public void validarUsuarioUnico(String usuario) throws SQLException {
        Validador.validarUsuarioSistema(usuario);

        if (personalRepository.existeUsuario(usuario)) {
            throw new IllegalArgumentException("El usuario ya existe: " + usuario);
        }
    }
}
