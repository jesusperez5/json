package com.odeene;



// import java.io.IOException;
// import java.net.URI;
// import java.net.http.HttpClient;
// import java.net.http.HttpRequest;
// import java.net.http.HttpResponse;
// import java.util.ArrayList;
// import java.util.List;

// public class MeteoSIXApiClient {
//     private static final String API_URL = "https://servizos.meteogalicia.gal/apiv4/getNumericForecastInfo";
//     private static final String TOKEN = "Asf85bkF56Ae0PJlCCOLS1Ci47mY4CTWjafPV4erU3MIoLzM1gD38O8FunARHg4U"; // Reemplaza con tu token válido

//     public List<WeatherData> getWeatherDataForCities(City[] cities) throws IOException, InterruptedException {
//         List<WeatherData> weatherDataList = new ArrayList<>();
//         HttpClient client = HttpClient.newHttpClient();

//         for (City city : cities) {
//             HttpRequest request = HttpRequest.newBuilder()
//                     .uri(URI.create(API_URL + "?coords=" + city.getLatitude() + "," + city.getLongitude()+ "=sky_state,temperature,precipitation_amount,wind,relative_humidity,cloud_area_fraction" + "&API_TOKEN=" + TOKEN))
//                     .GET()
//                     .build();

//             HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

//             if (response.statusCode() == 200) {
//                 // Parsear la respuesta JSON
//                 Gson gson = new Gson();
//                 JsonObject jsonObject = gson.fromJson(response.body(), JsonObject.class);

//                 // Extraer datos relevantes del JSON
//                 JsonObject dataNode = jsonObject.getAsJsonObject("variables");

//                 WeatherData weatherData = new WeatherData();
//                 weatherData.setCity(city.getName());
//                 weatherData.setTemperature(dataNode.getAsJsonArray("temperature").get(0).getAsDouble());
//                 weatherData.setSkyCondition(dataNode.getAsJsonArray("sky_state").get(0).getAsString());
//                 weatherData.setHumidity(dataNode.getAsJsonArray("relative_humidity").get(0).getAsInt());
//                 weatherData.setWind(dataNode.getAsJsonArray("wind").get(0).getAsDouble());
//                 weatherData.setPrecipitation(dataNode.getAsJsonArray("precipitation_amount").get(0).getAsDouble());
//                 weatherData.setCloudCoverage(dataNode.getAsJsonArray("cloud_area_fraction").get(0).getAsInt());

//                 weatherDataList.add(weatherData);
//             } else {
//                 System.err.println("Error al obtener datos para " + city.getName() + ": " + response.body());
//             }
//         }

//         return weatherDataList;
//     }
// }
