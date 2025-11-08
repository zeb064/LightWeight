package org.example.gimnasioproyect.Utilidades;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

public class CalculadoraFechas {

    // Calcular edad exacta
    public static int calcularEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) return 0;
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    // Calcular días restantes de membresía
    public static long calcularDiasRestantes(LocalDate fechaFinalizacion) {
        if (fechaFinalizacion == null) return 0;
        return ChronoUnit.DAYS.between(LocalDate.now(), fechaFinalizacion);
    }

    // Verificar si está próximo a vencer (7 días)
    public static boolean estaProximoAVencer(LocalDate fechaFinalizacion) {
        if (fechaFinalizacion == null) return false;
        long diasRestantes = calcularDiasRestantes(fechaFinalizacion);
        return diasRestantes > 0 && diasRestantes <= 7;
    }

    // Verificar si ya venció
    public static boolean estaVencido(LocalDate fechaFinalizacion) {
        if (fechaFinalizacion == null) return false;
        return fechaFinalizacion.isBefore(LocalDate.now());
    }

    // Calcular fecha de finalización según tipo de membresía
    public static LocalDate calcularFechaFinalizacion(LocalDate fechaInicio, String tipoMembresia) {
        if (fechaInicio == null) return null;

        switch (tipoMembresia.toUpperCase()) {
            case "DIARIA":
                return fechaInicio.plusDays(1);
            case "SEMANAL":
                return fechaInicio.plusWeeks(1);
            case "MENSUAL":
                return fechaInicio.plusMonths(1);
            case "TRIMESTRAL":
                return fechaInicio.plusMonths(3);
            case "SEMESTRAL":
                return fechaInicio.plusMonths(6);
            case "ANUAL":
                return fechaInicio.plusYears(1);
            default:
                return fechaInicio.plusMonths(1);
        }
    }
}
