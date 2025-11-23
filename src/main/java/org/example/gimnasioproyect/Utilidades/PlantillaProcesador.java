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

    //Verifica si una plantilla tiene todas las variables reemplazadas
    public static boolean tieneVariablesSinReemplazar(String texto) {
        return texto.contains("{") && texto.contains("}");
    }
}