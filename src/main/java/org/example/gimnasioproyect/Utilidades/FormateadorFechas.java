package org.example.gimnasioproyect.Utilidades;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormateadorFechas {
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static String formatearFecha(LocalDate fecha) {
        return fecha != null ? fecha.format(FORMATO_FECHA) : "";
    }

    public static String formatearFechaHora(LocalDateTime fechaHora) {
        return fechaHora != null ? fechaHora.format(FORMATO_FECHA_HORA) : "";
    }

    public static LocalDate parsearFecha(String fecha) {
        try {
            return LocalDate.parse(fecha, FORMATO_FECHA);
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de fecha inválido. Use dd/MM/yyyy");
        }
    }

}
