package com.odeene;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Main {
    private static Scanner sc = new Scanner(System.in);
    private static City[] cities = {
            new City("A Coruna", 43.3623, -8.4115),
            new City("Lugo", 43.0121, -7.5558),
            new City("Ourense", 42.335, -7.8639),
            new City("Pontevedra", 42.4333, -8.6443),
            new City("Vigo", 42.2406, -8.7207),
            new City("Santiago de Compostela", 42.8805, -8.5457),
            new City("Ferrol", 43.4831, -8.2369)
    };

    public static void main(String[] args) {
        // Array de datos
        List<WeatherData> weatherDatas = new ArrayList<>();

        // URL de la API
        String url = "https://servizos.meteogalicia.gal/apiv4/getNumericForecastInfo?coords=";
        String API_KEY = "Asf85bkF56Ae0PJlCCOLS1Ci47mY4CTWjafPV4erU3MIoLzM1gD38O8FunARHg4U";
        // Cliente HTTP
        OkHttpClient client = new OkHttpClient();
        // Array de ciudades con su localizacion
        System.out.println("Cargando datos...");
        for (City city : cities) {
            // Crear y ejecutar la petición HTTP
            Request request = new Request.Builder()
                    .url(url + city.getLongitude() + "," + city.getLatitude()
                            + "&variables=sky_state,temperature,precipitation_amount,wind,relative_humidity,cloud_area_fraction&API_KEY="
                            + API_KEY)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) { // si hay respuesta
                    // Procesar la respuesta JSON

                    String jsonResponse = response.body().string();
                    weatherDatas.addAll(processForecastData(jsonResponse, city)); // Llamamos a este metodo para obtener
                                                                                  // los datos pasandole el body y la
                                                                                  // ciudad
                } else {
                    System.err.println("Error en la petición HTTP: " + response.code());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        new H2Controller().guardarBase(weatherDatas);
        start();
    }

    // Metodo para porcesar el json
    private static List<WeatherData> processForecastData(String jsonResponse, City city) {
        city.setName(city.getName().replace(" ", ""));
        List<WeatherData> weatherDatas = new ArrayList<>();
        try {
            // Listas para los atributos del WeatherData ya que dan varios datos

            String fecha = "";
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
            JsonNode firstDay = root.at("/features/0/properties/days/1");
            if (firstDay.isMissingNode()) {
                System.out.println("No se encontró información del primer día.");
                return weatherDatas;
            }
            fecha = firstDay.path("timePeriod").findValue("timeInstant").asText().split("T")[0];
            String[] fechas = { fecha + " Mañana", fecha + " Tarde", fecha + " Noche" };
            // Obtener los valores de estado del cielo (sky_state)
            List<JsonNode> skyStateNode = firstDay.path("variables").get(0).findValues("value");
            if (skyStateNode != null) {
                sky_state.add(skyStateNode.get(7).asText());
                sky_state.add(skyStateNode.get(15).asText());
                sky_state.add(skyStateNode.get(23).asText());
            } else {
                System.out.println("No se encontró información sobre el estado del cielo.");
            }

            // Obtener los valores de temperatura
            List<JsonNode> temperatureNode = firstDay.path("variables").get(1).findValues("value");
            if (temperatureNode != null) {
                temperature.add(temperatureNode.get(7).asDouble());
                temperature.add(temperatureNode.get(15).asDouble());
                temperature.add(temperatureNode.get(23).asDouble());
            } else {
                System.out.println("No se encontró información sobre la temperatura.");
            }

            // Obtener los valores de lluvias
            List<JsonNode> precipitationAmountNode = firstDay.path("variables").get(2).findValues("value");
            if (precipitationAmountNode != null) {
                precipitation_amount.add(precipitationAmountNode.get(7).asInt());
                precipitation_amount.add(precipitationAmountNode.get(15).asInt());
                precipitation_amount.add(precipitationAmountNode.get(23).asInt());
            } else {
                System.out.println("No se encontró información sobre las probablidades de lluvias.");
            }

            // Obtener los valores del viento
            List<JsonNode> windNode = firstDay.path("variables").get(3).findValues("moduleValue");
            List<JsonNode> windDirectionNode = firstDay.path("variables").get(3).findValues("directionValue");
            if (windNode != null && windDirectionNode != null) {

                wind.add(new Wind(windNode.get(7).asDouble(), windDirectionNode.get(7).asDouble()));
                wind.add(new Wind(windNode.get(15).asDouble(), windDirectionNode.get(15).asDouble()));
                wind.add(new Wind(windNode.get(23).asDouble(), windDirectionNode.get(23).asDouble()));
                // System.out.println("Viento:");
                // for (int i = 0; i < windNode.size(); i++) {
                // System.out.println(windNode.get(i).asDouble() + "kmh");
                // System.out.println(windDirectionNode.get(i).asDouble() + "->");
                // wind.add(new Wind(windNode.get(i).asDouble(),
                // windDirectionNode.get(i).asDouble()));
                // }
            } else {
                System.out.println("No se encontró información sobre el viento.");
            }

            // Obtener los valores de humedad
            List<JsonNode> relativeHumidityNode = firstDay.path("variables").get(4).findValues("value");
            if (relativeHumidityNode != null) {
                relative_humidity.add(relativeHumidityNode.get(7).asDouble());
                relative_humidity.add(relativeHumidityNode.get(15).asDouble());
                relative_humidity.add(relativeHumidityNode.get(23).asDouble());
                // System.out.println("Humedad:");
                // for (JsonNode valueNode : relativeHumidityNode) {
                // System.out.println(valueNode.asDouble() + "%");
                // relative_humidity.add(valueNode.asDouble());
                // }
            } else {
                System.out.println("No se encontró información sobre la humedad de la zona.");
            }

            // Obtener los valores de cobertura nubosa
            List<JsonNode> cloudAreaFractionNode = firstDay.path("variables").get(5).findValues("value");
            if (cloudAreaFractionNode != null) {
                cloud_area_fraction.add(cloudAreaFractionNode.get(7).asDouble());
                cloud_area_fraction.add(cloudAreaFractionNode.get(15).asDouble());
                cloud_area_fraction.add(cloudAreaFractionNode.get(23).asDouble());
                // System.out.println("Cobertura nubosa:");
                // for (JsonNode valueNode : cloudAreaFractionNode) {
                // System.out.println(valueNode.asDouble() + "%");
                // cloud_area_fraction.add(valueNode.asDouble());
                // }
            } else {
                System.out.println("No se encontró información sobre la cobertura nubosa de la zona.");
            }
            // System.out.println("==================================================================\n");
            // Añadimos al array de datos los datos obtenidos
            // weatherDatas.add(new WeatherData(sky_state, temperature,
            // precipitation_amount, wind, relative_humidity, cloud_area_fraction));
            for (int i = 0; i < cloud_area_fraction.size(); i++) {
                WeatherData wd = new WeatherData(city, fechas[i], sky_state.get(i), temperature.get(i),
                        precipitation_amount.get(i), wind.get(i), relative_humidity.get(i), cloud_area_fraction.get(i));
                weatherDatas.add(wd);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return weatherDatas;
    }

    public static void start() {
        int opcion = -1;
        do {
            System.out.println("Que quieres hacer?\n1. Ver Registros\n2. Modificar Registros\n3. Salir");
            try {
                opcion = Integer.parseInt(sc.nextLine());
                switch (opcion) {
                    case 1:
                        verRegistros();
                        break;
                    case 2:
                        modificarRegistros();
                        break;
                    case 3:
                        break;
                    default:
                        System.out.println("Opción equivocada.");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Solo se permiten numeros");
            } catch (Exception e) {
                e.printStackTrace();
            }

        } while (opcion != 3);
    }

    public static void verRegistros() {
        int opcion = -1;
        City selectedCity = null;
        do {
            System.out.println("Para que ciudad quieres ver los registros:");
            for (int i = 0; i < cities.length; i++) {
                if (cities[i].getName().equalsIgnoreCase("ACoruna")) {
                    System.out.println((i + 1) + ". A Coruña");
                } else {
                    System.out.println((i + 1) + ". " + cities[i].getName());
                }
            }
            try {
                opcion = Integer.parseInt(sc.nextLine()) - 1;
                selectedCity = cities[opcion];
                verRegistrosCiudad(selectedCity.getName().replace(" ", ""));
            } catch (NumberFormatException e) {
                System.out.println("No se permiten numeros");
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Indice incorrecto");
            } catch (Exception e) {
                e.printStackTrace();
            }

        } while (selectedCity == null);
    }

    public static void verRegistrosCiudad(String city) {
        H2Controller h2 = new H2Controller();
        List<String> fechas = h2.traerFechasDeCiudad(city);
        String fecha = null;
        do {
            System.out.println("¿Que registro quieres ver?");
            for (int i = 0; i < fechas.size(); i++) {
                System.out.println((i + 1) + ". " + fechas.get(i));
            }
            fecha = fechas.get(Integer.parseInt(sc.nextLine()) - 1);
            WeatherData weatherData = h2.traerDatosDeCiudad(city, fecha);
            System.out.println(city + ": cielo -> " + weatherData.getSky_state() + ", temperatura -> "
                    + weatherData.getTemperature() + "º, probabilidad de lluvia -> "
                    + weatherData.getPrecipitation_amount() +
                    "%, viento -> " + weatherData.getWind().getValue() + "km/h, dirección del viento -> "
                    + weatherData.getWind().getDirection() + "º, humedad -> " + weatherData.getRelative_humidity()
                    + "%, porcentaje de nubes -> " + weatherData.getCloud_area_fraction() + "%.");
        } while (fecha == null);

    }

    public static void modificarRegistrosCiudad(String city) {
        H2Controller h2 = new H2Controller();
        List<String> fechas = h2.traerFechasDeCiudad(city);
        String fecha = null;
        do {
            System.out.println("¿Que registro quieres modificar?");
            for (int i = 0; i < fechas.size(); i++) {
                System.out.println((i + 1) + ". " + fechas.get(i));
            }
            fecha = fechas.get(Integer.parseInt(sc.nextLine()) - 1);
            WeatherData weatherData = h2.traerDatosDeCiudad(city, fecha);
            weatherData.getCity().setName(city);
            modificarRegistro(weatherData);

        } while (fecha == null);

    }

    public static void modificarRegistros() {
        int opcion = -1;
        City selectedCity = null;
        do {
            System.out.println("Para que ciudad quieres modificar los registros:");
            for (int i = 0; i < cities.length; i++) {
                if (cities[i].getName().equalsIgnoreCase("ACoruna")) {
                    System.out.println((i + 1) + ". A Coruña");
                } else {
                    System.out.println((i + 1) + ". " + cities[i].getName());
                }
            }
            try {
                opcion = Integer.parseInt(sc.nextLine()) - 1;
                selectedCity = cities[opcion];
                modificarRegistrosCiudad(selectedCity.getName().replace(" ", ""));
            } catch (NumberFormatException e) {
                System.out.println("No se permiten numeros");
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Indice incorrecto");
            } catch (Exception e) {
                e.printStackTrace();
            }

        } while (selectedCity == null);
    }

    public static void modificarRegistro(WeatherData weatherData) {
        int opcion = -1;
        H2Controller h2 = new H2Controller();
        do {
            System.out.println(weatherData.getCity().getName() + ": cielo -> " + weatherData.getSky_state()
                    + ", temperatura -> "
                    + weatherData.getTemperature() + "º, probabilidad de lluvia -> "
                    + weatherData.getPrecipitation_amount() +
                    "%, viento -> " + weatherData.getWind().getValue() + "km/h, dirección del viento -> "
                    + weatherData.getWind().getDirection() + "º, humedad -> " + weatherData.getRelative_humidity()
                    + "%, porcentaje de nubes -> " + weatherData.getCloud_area_fraction() + "%.");
            System.out.println(
                    "Que valor quieres cambiar:\n1. Cielo\n2. Temperatura\n3. Probabilidad de lluvia\n4. Viento\n5. Dirección del viento\n6. Humedad\n7. Porcentaje de nubes\n8. salir");
            try {
                opcion = Integer.parseInt(sc.nextLine());
                switch (opcion) {
                    case 1:
                        System.out.print("Introduce el valor que quieras establecer:");
                        try {
                            String cielo = sc.nextLine();
                            if (!validarSQLInjection(cielo)) {
                                System.out.println("Texto con caracteres inválidos!");
                                break;
                            }
                            weatherData.setSky_state(cielo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        System.out.print(
                                "Introduce el valor que quieras establecer(solo números, recomendable con un decimal):");
                        try {
                            double temperatura = Math.round(Double.parseDouble(sc.nextLine()) * 10) / 10;
                            weatherData.setTemperature(temperatura);
                        } catch (NumberFormatException e) {
                            System.out.println("Caracteres inválidos!");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        System.out.print("Introduce el valor que quieras establecer(solo números enteros):");
                        try {
                            weatherData.setPrecipitation_amount(Integer.parseInt(sc.nextLine()));
                        } catch (NumberFormatException e) {
                            System.out.println("Caracteres inválidos!");
                        } catch (Exception e) {
                            System.out.println("Valor inválido!");
                        }
                        break;
                    case 4:
                        System.out.print(
                                "Introduce el valor que quieras establecer(solo números, recomendable con un decimal):");
                        try {
                            double viento = Math.round(Double.parseDouble(sc.nextLine()) * 10) / 10;
                            weatherData.getWind().setValue(viento);
                        } catch (NumberFormatException e) {
                            System.out.println("Caracteres inválidos!");
                        } catch (Exception e) {
                            System.out.println("Valor inválido!");
                        }
                        break;
                    case 5:
                        System.out.print(
                                "Introduce el valor que quieras establecer(solo números, recomendable con dos decimales):");
                        try {
                            double viento = Math.round(Double.parseDouble(sc.nextLine()) * 100) / 100;
                            weatherData.getWind().setDirection(viento);
                        } catch (NumberFormatException e) {
                            System.out.println("Caracteres inválidos!");
                        } catch (Exception e) {
                            System.out.println("Valor inválido!");
                        }
                        break;
                    case 6:
                        System.out.print(
                                "Introduce el valor que quieras establecer(solo números, recomendable con dos decimales):");
                        try {
                            double humedad = Math.round(Double.parseDouble(sc.nextLine()) * 100) / 100;
                            weatherData.setRelative_humidity(humedad);
                        } catch (NumberFormatException e) {
                            System.out.println("Caracteres inválidos!");
                        } catch (Exception e) {
                            System.out.println("Valor inválido!");
                        }
                        break;
                    case 7:
                        System.out.print(
                                "Introduce el valor que quieras establecer(solo números, recomendable con un decimal):");
                        try {
                            double humedad = Math.round(Double.parseDouble(sc.nextLine()) * 10) / 10;
                            weatherData.setRelative_humidity(humedad);
                        } catch (NumberFormatException e) {
                            System.out.println("Caracteres inválidos!");
                        } catch (Exception e) {
                            System.out.println("Valor inválido!");
                        }
                        break;
                    case 8:
                        break;
                    default:
                        System.out.println("Valor inválido!");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido!");
            } catch (Exception e) {
                e.printStackTrace();
            }

        } while (opcion != 8);
        System.out.println("Actualizando datos...");
        if (h2.updateWeatherData(weatherData) == 1) {
            System.out.println("Datos actualizados con exito");
        } else {
            System.out.println("Ha habido un error");
        }

    }

    public static boolean validarSQLInjection(String string) {
        string = string.toLowerCase();
        if (string.contains("update") || string.contains("insert") || string.contains("delete") || string.contains("\"")
                || string.contains("\'"))
            return false;
        return true;
    }
}