package com.odeene;

public class WeatherData {
    private City city;
    private String date;
    private String sky_state;
    private Double temperature;
    private Integer precipitation_amount;
    private Wind wind;
    private Double relative_humidity;
    private Double cloud_area_fraction;
    public WeatherData(City city, String date, String sky_state, Double temperature, Integer precipitation_amount,
            Wind wind, Double relative_humidity, Double cloud_area_fraction) {
        this.city = city;
        this.date = date;
        this.sky_state = sky_state;
        this.temperature = temperature;
        this.precipitation_amount = precipitation_amount;
        this.wind = wind;
        this.relative_humidity = relative_humidity;
        this.cloud_area_fraction = cloud_area_fraction;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSky_state() {
        return sky_state;
    }

    public void setSky_state(String sky_state) {
        this.sky_state = sky_state;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getPrecipitation_amount() {
        return precipitation_amount;
    }

    public void setPrecipitation_amount(Integer precipitation_amount) {
        this.precipitation_amount = precipitation_amount;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Double getRelative_humidity() {
        return relative_humidity;
    }

    public void setRelative_humidity(Double relative_humidity) {
        this.relative_humidity = relative_humidity;
    }

    public Double getCloud_area_fraction() {
        return cloud_area_fraction;
    }

    public void setCloud_area_fraction(Double cloud_area_fraction) {
        this.cloud_area_fraction = cloud_area_fraction;
    }
    
    
}