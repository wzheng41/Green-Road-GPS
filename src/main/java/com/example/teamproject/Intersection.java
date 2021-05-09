package com.example.teamproject;

public class Intersection {
    private double latitude;
    private double longitude;
    private double light_timing;

    public Intersection(double latitude, double longitude, double light_timing){
        this.latitude = latitude;
        this.longitude = longitude;
        this.light_timing = light_timing;             // seconds
    }
    public void setLatitude(double new_latitude){
        this.latitude = new_latitude;
    }
    public void setLongitude(double new_longitude){
        this.longitude = new_longitude;
    }
    public void setLight_timing(double new_light_timing){
        this.light_timing = new_light_timing;
    }

    public double getLatitude(){
        return this.latitude;
    }
    public double getLongitude(){
        return this.longitude;
    }
    public double getLight_timing(){
        return this.light_timing;
    }


}