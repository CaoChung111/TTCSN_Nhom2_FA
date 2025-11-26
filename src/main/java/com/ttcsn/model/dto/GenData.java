package com.ttcsn.model.dto;

public class GenData {
    private int gen;
    private double brightness;
    private double cost;
    private double time;
    private double distance;
    private String route;

    // constructor
    public GenData(int gen, double brightness, double cost, double time, double distance, String route) {
        this.gen = gen;
        this.brightness = brightness;
        this.cost = cost;
        this.time = time;
        this.distance = distance;
        this.route = route;
    }

    // getters (quan trọng cho Gson)
    public int getGen() { return gen; }
    public double getBrightness() { return brightness; }
    public double getCost() { return cost; }
    public double getTime() { return time; }
    public double getDistance() { return distance; }
    public String getRoute() { return route; }
}
