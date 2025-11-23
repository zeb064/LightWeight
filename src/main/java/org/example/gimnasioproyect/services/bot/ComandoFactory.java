package org.example.gimnasioproyect.services.bot;

import org.example.gimnasioproyect.services.bot.comandos.*;

import java.util.*;

/**
 * Factory para crear y gestionar comandos del bot de Telegram.
 *
 * PATRÓN FACTORY: Centraliza la creación de comandos.
 * PATRÓN REGISTRY: Mantiene un registro de todos los comandos disponibles.
 *
 * Ventajas:
 * - Punto único de registro de comandos
 * - Búsqueda eficiente por nombre
 * - Fácil agregar nuevos comandos
 * - Permite listar todos los comandos disponibles (útil para /ayuda dinámico)
 *
 * Uso:
 * <pre>
 * ComandoBot comando = ComandoFactory.obtenerComando("start");
 * List<ComandoBot> todos = ComandoFactory.getTodosLosComandos();
 * </pre>
 */
public class ComandoFactory {

    // Registro de comandos: nombre -> instancia del comando
    // Usamos LinkedHashMap para mantener el orden de registro
    private static final Map<String, ComandoBot> COMANDOS = new LinkedHashMap<>();

    // Registro de alias: alias -> nombre del comando
    // Ejemplo: "help" -> "ayuda"
    private static final Map<String, String> ALIAS = new HashMap<>();

    // Bloque estático: se ejecuta una sola vez al cargar la clase
    static {
        registrarComando(new ComandoStart());
        registrarComando(new ComandoRegistrar());
        registrarComando(new ComandoAyuda());
        registrarComando(new ComandoMiMembresia());
        registrarComando(new ComandoMiRutina());
        registrarComando(new ComandoMiEntrenador());
        registrarComando(new ComandoMisAsistencias());

        // Registrar alias
        registrarAlias("help", "ayuda");

        System.out.println("✅ ComandoFactory inicializado con " + COMANDOS.size() + " comandos");
    }

    /**
     * Registra un comando en el factory.
     *
     * @param comando El comando a registrar
     */
    private static void registrarComando(ComandoBot comando) {
        String nombre = comando.getNombreComando().toLowerCase();
        COMANDOS.put(nombre, comando);
        System.out.println("  📝 Comando registrado: /" + nombre);
    }

    /**
     * Registra un alias para un comando existente.
     *
     * @param alias El alias (ej: "help")
     * @param comandoReal El nombre del comando real (ej: "ayuda")
     */
    private static void registrarAlias(String alias, String comandoReal) {
        ALIAS.put(alias.toLowerCase(), comandoReal.toLowerCase());
        System.out.println("  🔗 Alias registrado: /" + alias + " → /" + comandoReal);
    }

    /**
     * Obtiene un comando por su nombre o alias.
     *
     * @param nombre Nombre del comando (sin la barra diagonal)
     * @return El comando correspondiente, o null si no existe
     */
    public static ComandoBot obtenerComando(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return null;
        }

        String nombreNormalizado = nombre.trim().toLowerCase();

        // Eliminar la barra diagonal si viene incluida
        if (nombreNormalizado.startsWith("/")) {
            nombreNormalizado = nombreNormalizado.substring(1);
        }

        // Verificar si es un alias
        if (ALIAS.containsKey(nombreNormalizado)) {
            nombreNormalizado = ALIAS.get(nombreNormalizado);
        }

        // Buscar el comando
        return COMANDOS.get(nombreNormalizado);
    }

    /**
     * Verifica si existe un comando con el nombre especificado.
     *
     * @param nombre Nombre del comando
     * @return true si existe, false en caso contrario
     */
    public static boolean existeComando(String nombre) {
        return obtenerComando(nombre) != null;
    }

    /**
     * Obtiene todos los comandos registrados.
     *
     * Útil para generar menús de ayuda dinámicos o listados.
     *
     * @return Lista inmutable de todos los comandos
     */
    public static List<ComandoBot> getTodosLosComandos() {
        return Collections.unmodifiableList(new ArrayList<>(COMANDOS.values()));
    }

    /**
     * Obtiene solo los comandos públicos (no requieren permisos especiales).
     *
     * @return Lista de comandos públicos
     */
    public static List<ComandoBot> getComandosPublicos() {
        List<ComandoBot> publicos = new ArrayList<>();
        for (ComandoBot comando : COMANDOS.values()) {
            if (comando.esPublico()) {
                publicos.add(comando);
            }
        }
        return Collections.unmodifiableList(publicos);
    }

    /**
     * Obtiene los nombres de todos los comandos registrados.
     *
     * @return Array con los nombres de comandos
     */
    public static String[] getNombresComandos() {
        return COMANDOS.keySet().toArray(new String[0]);
    }

    /**
     * Obtiene información de un comando específico.
     *
     * @param nombre Nombre del comando
     * @return Descripción del comando, o mensaje de error si no existe
     */
    public static String getInfoComando(String nombre) {
        ComandoBot comando = obtenerComando(nombre);
        if (comando == null) {
            return "Comando no encontrado: " + nombre;
        }

        return String.format("/%s - %s\n" +
                        "Requiere registro: %s\n" +
                        "Público: %s",
                comando.getNombreComando(),
                comando.getDescripcion(),
                comando.requiereRegistro() ? "Sí" : "No",
                comando.esPublico() ? "Sí" : "No");
    }

    // Constructor privado para prevenir instanciación
    private ComandoFactory() {
        throw new UnsupportedOperationException("Esta es una clase utilitaria y no debe ser instanciada");
    }
}