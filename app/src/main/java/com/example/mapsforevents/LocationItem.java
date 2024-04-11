package com.example.mapsforevents;

public class LocationItem {
    private String name;
    private double latitude;
    private double longitude;

    public LocationItem(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }
}
