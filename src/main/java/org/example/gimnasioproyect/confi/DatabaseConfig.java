package org.example.gimnasioproyect.confi;

import lombok.*;
import org.example.gimnasioproyect.Utilidades.Validador;

@RequiredArgsConstructor
@Data
@Builder

public class DatabaseConfig {
    private final String host;
    private final String port;
    private final String service;
    private final String user;
    private final String password;

    public String getJdbcUrl(){
        return "jdbc:oracle:thin:@" + host + ":" + port + "/" + service;
    }

    public void validate() {
        Validador.validarConfiguracionBD(host, port, service, user, password);
    }
}
