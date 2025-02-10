package com.odeene;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.opencsv.CSVWriter;
public class Main {
    public static void main(String[] args) {
        // Array de datos
        List<WeatherData> weatherDatas = new ArrayList<>();

        // URL de la API
        String url = "https://servizos.meteogalicia.gal/apiv4/getNumericForecastInfo?coords=";
        String API_KEY = "Asf85bkF56Ae0PJlCCOLS1Ci47mY4CTWjafPV4erU3MIoLzM1gD38O8FunARHg4U";
        // Cliente HTTP
        OkHttpClient client = new OkHttpClient();
        // Array de ciudades con su localizacion
        City[] cities = {
            new City("A Coruna", 43.3623, -8.4115),
            new City("Lugo", 43.0121, -7.5558),
            new City("Ourense", 42.335, -7.8639),
            new City("Pontevedra", 42.4333, -8.6443),
            new City("Vigo", 42.2406, -8.7207),
            new City("Santiago de Compostela", 42.8805, -8.5457),
            new City("Ferrol", 43.4831, -8.2369)
        }; 
        for (City city : cities) {
            // Crear y ejecutar la petición HTTP
            Request request = new Request.Builder()
            .url(url +city.getLongitude()+ "," + city.getLatitude() + "&variables=sky_state,temperature,precipitation_amount,wind,relative_humidity,cloud_area_fraction&API_KEY=" + API_KEY)
            .build();
            System.out.println(city.getName() + ":");

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) { // si hay respuesta
                    // Procesar la respuesta JSON
                    String jsonResponse = response.body().string();
                    
                    processForecastData(jsonResponse, weatherDatas); // Llamamos a este metodo para obtener los datos pasandole el body y el array
                } else {
                    System.err.println("Error en la petición HTTP: " + response.code());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Método para escribir en el csv
        writeWeatherDataToCsv(weatherDatas, cities); 
    }
    // Metodo para porcesar el json
    private static void processForecastData(String jsonResponse, List<WeatherData> weatherDatas) {
        try {
            // Listas para los atributos del WeatherData ya que dan varios datos
            List<String> sky_state = new ArrayList<>();
            List<Double> temperature = new ArrayList<>();
            List<Integer> precipitation_amount = new ArrayList<>();
            List<Wind> wind = new ArrayList<>();
            List<Double> relative_humidity = new ArrayList<>();
            List<Double> cloud_area_fraction = new ArrayList<>();
            // Parsear el JSON usando ObjectMapper
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            // Obtener el primer día del pronóstico
            JsonNode firstDay = root.at("/features/0/properties/days/0");
            if (firstDay.isMissingNode()) {
                System.out.println("No se encontró información del primer día.");
                return;
            }
            // Obtener los valores de estado del cielo (sky_state)
            List<JsonNode> skyStateNode = firstDay.path("variables").get(0).findValues("value");
            if (skyStateNode != null) {
                System.out.println("Estado del cielo:");
                for (JsonNode valueNode : skyStateNode) {
                    System.out.println(valueNode.asText());
                    sky_state.add(valueNode.asText());
                }
            } else {
                System.out.println("No se encontró información sobre el estado del cielo.");
            }

            // Obtener los valores de temperatura
            List<JsonNode> temperatureNode = firstDay.path("variables").get(1).findValues("value");
            if (temperatureNode != null) {
                System.out.println("Temperatura:");
                for (JsonNode valueNode : temperatureNode) {
                    System.out.println(valueNode.asDouble() + " C");
                    temperature.add(valueNode.asDouble());
                }
            } else {
                System.out.println("No se encontró información sobre la temperatura.");
            }

            // Obtener los valores de lluvias
            List<JsonNode> precipitationAmountNode = firstDay.path("variables").get(2).findValues("value");
            if (precipitationAmountNode != null) {
                System.out.println("Probabilidad de lluvias:");
                for (JsonNode valueNode : precipitationAmountNode) {
                    System.out.println(valueNode.asInt() + "%");
                    precipitation_amount.add(valueNode.asInt());
                }
            } else {
                System.out.println("No se encontró información sobre las probablidades de lluvias.");
            }

            // Obtener los valores del viento
            List<JsonNode> windNode = firstDay.path("variables").get(3).findValues("moduleValue");
            List<JsonNode> windDirectionNode = firstDay.path("variables").get(3).findValues("directionValue");
            if (windNode != null && windDirectionNode != null) {
                System.out.println("Viento:");
                for (int i = 0; i < windNode.size(); i++) {
                    System.out.println(windNode.get(i).asDouble() + "kmh");
                    System.out.println(windDirectionNode.get(i).asDouble() + "->");
                    wind.add(new Wind(windNode.get(i).asDouble(), windDirectionNode.get(i).asDouble()));
                }
            } else {
                System.out.println("No se encontró información sobre el viento.");
            }

            // Obtener los valores de humedad
            List<JsonNode> relativeHumidityNode = firstDay.path("variables").get(4).findValues("value");
            if (relativeHumidityNode != null) {
                System.out.println("Humedad:");
                for (JsonNode valueNode : relativeHumidityNode) {
                    System.out.println(valueNode.asDouble() + "%");
                    relative_humidity.add(valueNode.asDouble());
                }
            } else {
                System.out.println("No se encontró información sobre la humedad de la zona.");
            }

            // Obtener los valores de cobertura nubosa
            List<JsonNode> cloudAreaFractionNode = firstDay.path("variables").get(5).findValues("value");
            if (cloudAreaFractionNode != null) {
                System.out.println("Cobertura nubosa:");
                for (JsonNode valueNode : cloudAreaFractionNode) {
                    System.out.println(valueNode.asDouble() + "%");
                    cloud_area_fraction.add(valueNode.asDouble());
                }
            } else {
                System.out.println("No se encontró información sobre la cobertura nubosa de la zona.");
            }
            System.out.println("==================================================================\n");
            // Añadimos al array de datos los datos obtenidos
            weatherDatas.add(new WeatherData(sky_state, temperature, precipitation_amount, wind, relative_humidity, cloud_area_fraction));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para escribir en el archivo Csv
    public static void writeWeatherDataToCsv(List<WeatherData> weatherDataArray, City[] cities) {
        String csvFile = java.time.LocalDateTime.now().toString().split("T")[0] + ".csv"; // Obtenemos el dia de hoy
        File file = new File(csvFile);
        try {
            if(file.exists()) // si el archivo ya existe lo borramos y creamos uno nuevo
                file.delete();
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        

        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
            // Escribir la cabecera del CSV
            writer.writeNext(new String[]{"Ciudad", "Estado del cielo", "Temperatura (C)", "Probabilidad de lluvias", "Viento", "Humedad", "Porcentaje de nubosidad"});

            // Escribir los datos de cada ciudad en el archivo CSV
            for (int i = 0; i < cities.length; i++) {
                String cielo = String.join(",", weatherDataArray.get(i).getSky_state()); // Esto de String.join coge un array y te concatena cada valor con una ,
                String temperatura = String.join(",", weatherDataArray.get(i).getTemperature().toString());
                String lluvias = String.join(",", weatherDataArray.get(i).getPrecipitation_amount().toString());
                String viento = String.join(",", weatherDataArray.get(i).getWind().toString());
                String humedad = String.join(",", weatherDataArray.get(i).getRelative_humidity().toString());
                String nubosidad = String.join(",", weatherDataArray.get(i).getCloud_area_fraction().toString());
                writer.writeNext(new String[]{cities[i].getName(), cielo, temperatura, lluvias, viento, humedad, nubosidad}); // Escribimos

            }
            System.out.println("Datos escritos en " + csvFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}