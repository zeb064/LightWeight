package org.example.gimnasioproyect.confi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OracleDatabaseConnection implements IDatabaseConnection{
    private final DatabaseConfig config;

    //Cargar driver una sola vez
    static {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("✅ Driver JDBC de Oracle cargado correctamente");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("❌ No se encontró el driver JDBC de Oracle");
        }
    }

    public OracleDatabaseConnection(DatabaseConfig config) {
        this.config = config;
        config.validate(); //Validar configuración al crear la instancia
    }

    @Override
    public Connection connect() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(
                    config.getJdbcUrl(),
                    config.getUser(),
                    config.getPassword()
            );
            System.out.println("✅ Conexión establecida con Oracle");
            return conn;
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar con Oracle: " + e.getMessage());
            throw e;
        }
    }

//    @Override
//    public void disconnect() throws SQLException {//
//        System.out.println("✅ Desconexión de Oracle no implementada en esta versión");
//    }
}
