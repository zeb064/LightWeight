package org.example.gimnasioproyect.Utilidades;

public class Constantes {
    // Estados de rutinas
    public static final String RUTINA_ACTIVA = EstadoRutina.ACTIVA.name();
    public static final String RUTINA_COMPLETADA = EstadoRutina.COMPLETADA.name();
    public static final String RUTINA_PAUSADA = EstadoRutina.PAUSADA.name();
    public static final String RUTINA_CANCELADA = EstadoRutina.CANCELADA.name();

    // Tipos de personal
    public static final String TIPO_RECEPCIONISTA = "RECEPCIONISTA";
    public static final String TIPO_ADMINISTRADOR = "ADMINISTRADOR";
    public static final String TIPO_ENTRENADOR = "ENTRENADOR";

    // Géneros
    public static final String GENERO_MASCULINO = "M";
    public static final String GENERO_FEMENINO = "F";

    // Tipos de membresía
    public static final String MEMBRESIA_MENSUAL = "MENSUAL";
    public static final String MEMBRESIA_TRIMESTRAL = "TRIMESTRAL";
    public static final String MEMBRESIA_SEMESTRAL = "SEMESTRAL";
    public static final String MEMBRESIA_ANUAL = "ANUAL";

    // Días de la semana
    public static final String[] DIAS_SEMANA = {
            "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO"
    };

    // Límites de caracteres
    public static final int MAX_NOMBRES = 15;
    public static final int MAX_APELLIDOS = 15;
    public static final int MAX_TELEFONO = 10;
    public static final int MAX_CORREO = 40;
    public static final int MAX_USUARIO = 15;
    public static final int MAX_CONTRASENA = 15;

    private Constantes() {
        // Evitar instanciación
    }
}
