package com.ttcsn.model.dto;

public class FireflyPoint {
    private double cost;
    private double time;
    private boolean isValid; // True nếu <= Budget

    public FireflyPoint(double cost, double time, double budget) {
        this.cost = cost;
        this.time = time;
        this.isValid = (cost <= budget);
    }

    // Getter (Gson cần cái này để serialize)
    public double getCost() { return cost; }
    public double getTime() { return time; }
    public boolean isValid() { return isValid; }
}