package org.example.gimnasioproyect.services;

import org.example.gimnasioproyect.Utilidades.TipoPersonal;
import org.example.gimnasioproyect.model.Personal;
import org.example.gimnasioproyect.repository.PersonalRepository;

import java.sql.SQLException;
import java.util.Optional;

public class LoginService {
    private final PersonalRepository personalRepository;
    private Personal usuarioActual;

    public LoginService(PersonalRepository personalRepository) {
        this.personalRepository = personalRepository;
        this.usuarioActual = null;
    }

    // Realiza el login de un usuario
    public Personal login(String usuario, String contrasena) throws SQLException {
        if (usuario == null || usuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }

        if (contrasena == null || contrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }

        Optional<Personal> personalOpt = personalRepository.autenticar(usuario, contrasena);

        if (!personalOpt.isPresent()) {
            throw new IllegalArgumentException("Usuario o contraseña incorrectos");
        }

        usuarioActual = personalOpt.get();
        return usuarioActual;
    }

    // Cierra la sesión actual
    public void logout() {
        usuarioActual = null;
    }

    // Obtiene el usuario autenticado
    public Personal getUsuarioActual() {
        return usuarioActual;
    }

    // Verifica si hay sesión activa
    public boolean hayUsuarioAutenticado() {
        return usuarioActual != null;
    }

    // Verifica si el usuario actual tiene un rol específico
    public boolean tieneRol(TipoPersonal rolEsperado) {
        return usuarioActual != null && usuarioActual.getTipoPersonal() == rolEsperado;
    }

    // Métodos específicos para roles (más legibles)
    public boolean esAdministrador() {
        return tieneRol(TipoPersonal.ADMINISTRADOR);
    }

    public boolean esEntrenador() {
        return tieneRol(TipoPersonal.ENTRENADOR);
    }

    public boolean esRecepcionista() {
        return tieneRol(TipoPersonal.RECEPCIONISTA);
    }

    // Obtiene el tipo de usuario actual
    public TipoPersonal getTipoUsuarioActual() {
        return (usuarioActual != null) ? usuarioActual.getTipoPersonal() : null;
    }

    // Verificaciones de permisos
    public void validarSesionActiva() {
        if (!hayUsuarioAutenticado()) {
            throw new SecurityException("No hay una sesión activa. Por favor, inicie sesión.");
        }
    }

    public void validarPermisoAdministrador() {
        if (!esAdministrador()) {
            throw new SecurityException("Se requieren permisos de administrador para esta operación.");
        }
    }

    public void validarPermisoEntrenador() {
        if (!esEntrenador()) {
            throw new SecurityException("Se requieren permisos de entrenador para esta operación.");
        }
    }

    // Obtiene el nombre completo del usuario actual
    public String getNombreUsuarioActual() {
        return (usuarioActual != null) ? usuarioActual.getNombreCompleto() : null;
    }

    // Obtiene el documento del usuario actual (gracias al polimorfismo)
    public String getDocumentoUsuarioActual() {
        return (usuarioActual != null) ? usuarioActual.getDocumento() : null;
    }
}
