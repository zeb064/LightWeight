package org.example.gimnasioproyect.Utilidades;

public enum EstadoRutina {
    ACTIVA(false),
    COMPLETADA(true),
    PAUSADA(false),
    CANCELADA(true);

    private final boolean requiereFechaFinalizacion;

    EstadoRutina(boolean requiereFechaFinalizacion) {
        this.requiereFechaFinalizacion = requiereFechaFinalizacion;
    }

    public boolean requiereFechaFinalizacion() {
        return this.requiereFechaFinalizacion;
    }

    public static EstadoRutina from(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado no puede ser nulo o vacío");
        }
        try {
            return EstadoRutina.valueOf(estado.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado inválido: " + estado +
                    ". Estados válidos: ACTIVA, COMPLETADA, PAUSADA, CANCELADA");
        }
    }

    // Método útil para validaciones en la UI
    public boolean puedeTransicionarA(EstadoRutina nuevoEstado) {
        switch (this) {
            case ACTIVA:
                return true; // Puede ir a cualquier estado
            case PAUSADA:
                return nuevoEstado == ACTIVA || nuevoEstado == CANCELADA;
            case COMPLETADA:
            case CANCELADA:
                return false; // Estados finales, no pueden cambiar
            default:
                return false;
        }
    }
}
