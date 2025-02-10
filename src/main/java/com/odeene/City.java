package com.odeene;

public class City {
    private String name;
    private double latitude;
    private double longitude;
    private String fecha;
    private String sky_state;
    private double temperature;
    private int precipitation_amount;
    private Wind wind;
    private double relative_humidity;
    private double cloud_area_fraction;

    public City(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
