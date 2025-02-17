package com.odeene;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.h2.jdbc.JdbcSQLSyntaxErrorException;

public class H2Controller {
    private final String url = "jdbc:h2:./database/mydb";
    private final String user = "sa";
    private final String password = "";

    public void createTable(String name) {
        try (Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement()) {
            // Crear una tabla de ejemplo
            String sql = "CREATE TABLE IF NOT EXISTS \"" + name + "\" (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "fecha VARCHAR(40), " +
                    "cielo VARCHAR(30), " +
                    "temperatura DECIMAL(3,1), " +
                    "lluvia INT, " +
                    "velocidad_viento DOUBLE, " +
                    "direccion_viento DOUBLE, " +
                    "humedad DOUBLE, " +
                    "niebla DOUBLE)";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void guardarBase(List<WeatherData> weatherDatas) {
        for (WeatherData weatherData : weatherDatas) {
            createTable(weatherData.getCity().getName());
            if (!comprobarRegistro(weatherData)) {
                guardarDatos(weatherData);
            }
        }
    }

    public boolean comprobarRegistro(WeatherData weatherData) {
        String query = "SELECT * FROM \"" + weatherData.getCity().getName() + "\" WHERE fecha LIKE ?";
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
        } catch (JdbcSQLSyntaxErrorException e) {
            e.printStackTrace();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void guardarDatos(WeatherData weatherData) {
        String sql = "INSERT INTO \"" + weatherData.getCity().getName()
                + "\" (fecha, cielo, temperatura, lluvia, velocidad_viento, direccion_viento, humedad, niebla) "
                +
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
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public WeatherData traerDatosDeCiudad(String city, String fecha) {
        String query = "SELECT * FROM \"" + city + "\" WHERE fecha LIKE ?";
        WeatherData weatherData = null;
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // Crear el PreparedStatement con la consulta SQL
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                // Establecer el parámetro en la consulta (nombre como patrón)
                stmt.setString(1, fecha);
                // Ejecutar la consulta
                try (ResultSet rs = stmt.executeQuery()) {
                    // Iterar sobre los resultados
                    if (rs.next()) {
                        weatherData = new WeatherData(new City(city, 0, 0), fecha,
                                rs.getString(3), rs.getDouble(4), rs.getInt(5),
                                new Wind(rs.getDouble(6), rs.getDouble(7)),
                                rs.getDouble(8), rs.getDouble(9));
                    }
                }
            }
        } catch (JdbcSQLSyntaxErrorException e) {
            e.printStackTrace();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return weatherData;
    }

    public List<String> traerFechasDeCiudad(String city) {
        String query = "SELECT fecha FROM \"" + city + "\"";
        List<String> fechas = new ArrayList();
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // Crear el PreparedStatement con la consulta SQL
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                // Ejecutar la consulta
                try (ResultSet rs = stmt.executeQuery()) {
                    // Iterar sobre los resultados
                    while (rs.next()) {
                        fechas.add(rs.getString(1));
                    }
                }
            }
        } catch (JdbcSQLSyntaxErrorException e) {
            e.printStackTrace();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fechas;
    }

    public int updateWeatherData(WeatherData weatherData) {
        String query = "UPDATE \"" + weatherData.getCity().getName().replace(" ", "")
                + "\" SET cielo = ?, temperatura = ?, lluvia = ?, velocidad_viento = ?," +
                "direccion_viento = ?, humedad = ?, niebla = ? WHERE fecha = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // Crear el PreparedStatement con la consulta SQL
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, weatherData.getSky_state());
                stmt.setDouble(2, weatherData.getTemperature());
                stmt.setInt(3, weatherData.getPrecipitation_amount());
                stmt.setDouble(4, weatherData.getWind().getValue());
                stmt.setDouble(5, weatherData.getWind().getDirection());
                stmt.setDouble(6, weatherData.getRelative_humidity());
                stmt.setDouble(7, weatherData.getCloud_area_fraction());
                stmt.setString(8, weatherData.getDate());
                // Ejecutar la consulta
                try {
                    return stmt.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (JdbcSQLSyntaxErrorException e) {
            e.printStackTrace();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
