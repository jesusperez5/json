package com.odeene;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

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
                    "fecha VARCHAR(40), " +
                    "cielo VARCHAR(30), " +
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

    public void guardarBase(List<WeatherData> weatherDatas){
        if(comprobarRegistro(weatherDatas.get(0))){

            return;
        }
        for (WeatherData weatherData : weatherDatas) {
            //createTable(weatherData.getCity().getName());
            //guardarDatos(weatherData);
        }

    }
    
    public boolean comprobarRegistro(WeatherData weatherData){
        String query = "SELECT * FROM " + weatherData.getCity().getName() + " WHERE fecha LIKE ?";
        String fecha = "%" + weatherData.getDate() + "%";
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // Crear el PreparedStatement con la consulta SQL
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                // Establecer el parámetro en la consulta (nombre como patrón)
                stmt.setString(1, fecha);
                // Ejecutar la consulta
                try (ResultSet rs = stmt.executeQuery()) {
                    // Iterar sobre los resultados
                    if (rs.next()) {
                       return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void guardarDatos(WeatherData weatherData){
        String sql = "INSERT INTO weather_data (fecha, cielo, temperatura, lluvia, velocidad_viento, direccion_viento, humedad, niebla) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, user, password);
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, weatherData.getDate()); // fecha
            stmt.setString(2, weatherData.getSky_state()); // cielo
            stmt.setDouble(3, weatherData.getTemperature()); // temperatura
            stmt.setInt(4, weatherData.getPrecipitation_amount()); // lluvia
            stmt.setDouble(5, weatherData.getWind().getValue()); // velocidad_viento
            stmt.setDouble(6, weatherData.getWind().getDirection()); // direccion_viento
            stmt.setDouble(7, weatherData.getRelative_humidity()); // humedad
            stmt.setDouble(8, weatherData.getCloud_area_fraction()); // niebla
            // Ejecutar el insert
            stmt.executeUpdate();
            System.out.println("Datos meteorológicos insertados correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
