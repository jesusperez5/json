package com.odeene;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class H2Controller {
    private final String url = "jdbc:h2:./database/mydb";
    private final String user = "sa";
    private final String password = "";

    public void createTable(String name) {
        try (Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement()) {
            // Crear una tabla de ejemplo
            String sql = "CREATE TABLE IF NOT EXISTS " + name + " (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "fecha VARCHAR(20), " +
                    "cielo VARCHAR(20), " +
                    "temperatura DECIMAL(3,1), " +
                    "lluvia INT, " +
                    "velocidad_viento DOUBLE, " +
                    "direccion_viento DOUBLE, " +
                    "humedad DOUBLE, " +
                    "niebla DOUBLE, ";
            stmt.execute(sql);

            System.out.println("Base de datos creada exitosamente");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
