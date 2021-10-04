package org.runhare.theappthatneverlies.Models;

import android.location.Location;

import java.util.ArrayList;
import java.util.Locale;

public class CaptureModel {

    public double getDistance() {
        return distance;
    }

    public String getRouteName() {
        return routeName;
    }

    public CaptureModel setDistance(double distance) {
        this.distance = distance;
        return this;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    private double distance;
    private String routeName;
    private float gpsAccuracy;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public int getVehicleCapacity() {
        return vehicleCapacity;
    }

    public void setVehicleCapacity(int vehicleCapacity) {
        this.vehicleCapacity = vehicleCapacity;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getPassengers() {
        return passengers;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }

    public ArrayList<Location> getStops() {
        return stops;
    }

    public void setStops(ArrayList<Location> stops) {
        this.stops = stops;
    }

    private String description;
    private String vehicleType;
    private int vehicleCapacity;
    private long duration;
    private int passengers;
    private ArrayList<Location> stops;

    public float getGpsAccuracy() {
        return gpsAccuracy;
    }

    public void setGpsAccuracy(float gpsAccuracy) {
        this.gpsAccuracy = gpsAccuracy;
    }
}
