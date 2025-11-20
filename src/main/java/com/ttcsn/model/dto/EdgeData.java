package com.ttcsn.model.dto;

//Lớp DTO để GSON đọc dữ liệu thô của Cạnh
public class EdgeData {
	private int fromId;
	private int toId;
	private double distance;
	private double speedLimit;
	private double cost;
	private int trafficLights;
	private double avgWaitTime;
	private double rushHourFactor;

	public int getFromId() {
		return fromId;
	}

	public int getToId() {
		return toId;
	}

	public double getDistance() {
		return distance;
	}

	public double getSpeedLimit() {
		return speedLimit;
	}

	public double getCost() {
		return cost;
	}

	public int getTrafficLights() {
		return trafficLights;
	}

	public double getAvgWaitTime() {
		return avgWaitTime;
	}

	public double getRushHourFactor() {
		return rushHourFactor;
	}
}