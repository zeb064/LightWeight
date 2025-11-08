package org.example.gimnasioproyect.Utilidades;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class Validador {
    // Patrones reutilizables
    private static final Pattern PATTERN_EMAIL = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern PATTERN_TELEFONO = Pattern.compile("^[0-9]{7,10}$");
    private static final Pattern PATTERN_DOCUMENTO = Pattern.compile("^[0-9]{6,10}$");

    // Validar documento
    public static void validarDocumento(String documento) {
        if (documento == null || documento.trim().isEmpty()) {
            throw new IllegalArgumentException("El documento es obligatorio");
        }
        if (!PATTERN_DOCUMENTO.matcher(documento).matches()) {
            throw new IllegalArgumentException("Documento inválido. Debe contener entre 6 y 10 dígitos");
        }
    }

    // Validar correo
    public static void validarCorreo(String correo) {
        if (correo != null && !correo.trim().isEmpty()) {
            if (!PATTERN_EMAIL.matcher(correo).matches()) {
                throw new IllegalArgumentException("Correo electrónico inválido");
            }
            if (correo.length() > 40) {
                throw new IllegalArgumentException("El correo no puede exceder 40 caracteres");
            }
        }
    }

    // Validar teléfono
    public static void validarTelefono(String telefono) {
        if (telefono != null && !telefono.trim().isEmpty()) {
            if (!PATTERN_TELEFONO.matcher(telefono).matches()) {
                throw new IllegalArgumentException("Teléfono inválido. Debe contener entre 7 y 10 dígitos");
            }
        }
    }

    // Validar nombres
    public static void validarNombre(String nombre, String campo) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " es obligatorio");
        }
        if (nombre.length() > 15) {
            throw new IllegalArgumentException(campo + " no puede exceder 15 caracteres");
        }
        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            throw new IllegalArgumentException(campo + " solo puede contener letras");
        }
    }

    // Validar fecha de nacimiento
    public static void validarFechaNacimiento(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria");
        }
        if (fechaNacimiento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
        }

        int edad = LocalDate.now().getYear() - fechaNacimiento.getYear();
        if (edad < 12) {
            throw new IllegalArgumentException("El cliente debe tener al menos 12 años");
        }
        if (edad > 100) {
            throw new IllegalArgumentException("Fecha de nacimiento inválida");
        }
    }

    // Validar género
    public static void validarGenero(String genero) {
        if (genero != null && !genero.trim().isEmpty()) {
            if (!genero.equals("M") && !genero.equals("F")) {
                throw new IllegalArgumentException("Género inválido. Debe ser 'M' o 'F'");
            }
        }
    }

    // Validar usuario de sistema
    public static void validarUsuarioSistema(String usuario) {
        if (usuario == null || usuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario del sistema es obligatorio");
        }
        if (usuario.length() > 15) {
            throw new IllegalArgumentException("El usuario no puede exceder 15 caracteres");
        }
        if (!usuario.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("El usuario solo puede contener letras, números y guión bajo");
        }
    }

    // Validar contraseña
    public static void validarContrasena(String contrasena) {
        if (contrasena == null || contrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        if (contrasena.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
        if (contrasena.length() > 15) {
            throw new IllegalArgumentException("La contraseña no puede exceder 15 caracteres");
        }
    }

    // Validar texto genérico con límite
    public static void validarTexto(String texto, String campo, int maxLength, boolean obligatorio) {
        if (obligatorio && (texto == null || texto.trim().isEmpty())) {
            throw new IllegalArgumentException(campo + " es obligatorio");
        }
        if (texto != null && texto.length() > maxLength) {
            throw new IllegalArgumentException(campo + " no puede exceder " + maxLength + " caracteres");
        }
    }

    // Validar número positivo
    public static void validarNumeroPositivo(Number numero, String campo) {
        if (numero != null && numero.doubleValue() < 0) {
            throw new IllegalArgumentException(campo + " no puede ser negativo");
        }
    }

    // Validar rango de fechas
    public static void validarRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio != null && fechaFin != null) {
            if (fechaFin.isBefore(fechaInicio)) {
                throw new IllegalArgumentException("La fecha de finalización no puede ser anterior a la fecha de inicio");
            }
        }
    }

    //validar precio de la membresia
    public static void validarPrecio(Double precio, String campo) {
        if (precio == null) {
            throw new IllegalArgumentException(campo + " es obligatorio");
        }
        if (precio <= 0) {
            throw new IllegalArgumentException(campo + " debe ser mayor a cero");
        }

    }

    //Valida parámetros de configuración de base de datos
    public static void validarConfiguracionBD(String host, String port, String service,
                                              String user, String password) {
        validarTexto(host, "Host de base de datos", 100, true);
        validarTexto(port, "Port de base de datos", 10, true);
        validarTexto(service, "Service de base de datos", 100, true);
        validarTexto(user, "Usuario de base de datos", 50, true);

        if (password == null) {
            throw new IllegalArgumentException("La contraseña de base de datos no puede ser nula");
        }

        // Validar que el puerto sea numérico
        try {
            int portNum = Integer.parseInt(port);
            if (portNum < 1 || portNum > 65535) {
                throw new IllegalArgumentException("El puerto debe estar entre 1 y 65535");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El puerto debe ser un número válido");
        }
    }

    //Valida un valor individual de configuración
    public static void validarParametroConfig(String valor, String nombreParametro) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(nombreParametro + " no puede ser nulo o vacío");
        }
    }
}
