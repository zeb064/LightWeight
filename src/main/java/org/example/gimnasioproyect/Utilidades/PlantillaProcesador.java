package org.example.gimnasioproyect.Utilidades;

import org.example.gimnasioproyect.model.Clientes;
import org.example.gimnasioproyect.model.MembresiaClientes;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class PlantillaProcesador {
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    //Procesa una plantilla reemplazando las variables con sus valores correspondientes
    public static String procesarPlantilla(String plantilla, Map<String, String> variables) {
        String resultado = plantilla;

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String variable = "{" + entry.getKey() + "}";
            resultado = resultado.replace(variable, entry.getValue());
        }

        return resultado;
    }

    //Crea el mapa de variables para el mensaje de BIENVENIDA
    public static Map<String, String> crearVariablesBienvenida(Clientes cliente, MembresiaClientes membresia) {
        Map<String, String> variables = new HashMap<>();

        variables.put("nombre", cliente.getNombreCompleto());

        if (membresia != null) {
            variables.put("fecha_inicio", membresia.getFechaAsignacion() != null ?
                    membresia.getFechaAsignacion().format(FORMATO_FECHA) : "N/A");

            variables.put("fecha_fin", membresia.getFechaFinalizacion() != null ?
                    membresia.getFechaFinalizacion().format(FORMATO_FECHA) : "N/A");

            variables.put("tipo_membresia", membresia.getMembresia() != null ?
                    membresia.getMembresia().getTipoMembresia() : "N/A");
        } else {
            variables.put("fecha_inicio", "N/A");
            variables.put("fecha_fin", "N/A");
            variables.put("tipo_membresia", "N/A");
        }

        return variables;
    }

    //Crea el mapa de variables para el mensaje de VENCE_PRONTO
    public static Map<String, String> crearVariablesVencimientoProximo(Clientes cliente,
                                                                       MembresiaClientes membresia,
                                                                       long diasRestantes) {
        Map<String, String> variables = new HashMap<>();

        variables.put("nombre", cliente.getNombreCompleto());
        variables.put("dias", String.valueOf(diasRestantes));

        if (membresia != null && membresia.getFechaFinalizacion() != null) {
            variables.put("fecha_fin", membresia.getFechaFinalizacion().format(FORMATO_FECHA));
        } else {
            variables.put("fecha_fin", "N/A");
        }

        return variables;
    }

    //Crea el mapa de variables para el mensaje de VENCIDO
    public static Map<String, String> crearVariablesVencido(Clientes cliente, MembresiaClientes membresia) {
        Map<String, String> variables = new HashMap<>();

        variables.put("nombre", cliente.getNombreCompleto());

        if (membresia != null && membresia.getFechaFinalizacion() != null) {
            variables.put("fecha_fin", membresia.getFechaFinalizacion().format(FORMATO_FECHA));
        } else {
            variables.put("fecha_fin", "N/A");
        }

        return variables;
    }

    //Verifica si una plantilla tiene todas las variables reemplazadas
    public static boolean tieneVariablesSinReemplazar(String texto) {
        return texto.contains("{") && texto.contains("}");
    }
}