package com.ttcsn.model.dto;

public class ComparisonPoint {
    private double cost;
    private double time;
    private boolean isValid;
    
    public ComparisonPoint(double cost, double time, double budget) {
        this.cost = cost;
        this.time = time;
        this.isValid = (cost <= budget);
    }
    
    // Getters
    public double getCost() { return cost; }
    public double getTime() { return time; }
    public boolean isValid() { return isValid; }
}