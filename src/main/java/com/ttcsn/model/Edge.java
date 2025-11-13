package com.ttcsn.model;

public class Edge {
	private Node from; // điểm bắt đầu
	private Node to; // điểm đến
	private double distance; // khoảng cách (km)
	private double speedLimit; // giới hạn tốc độ (km/h)
	private double cost; // chi phí (VNĐ)
	private int trafficLights; // số đèn đỏ
	private double avgWaitTime; // thời gian chờ trung bình (giây)
	private double rushHourFactor;// hệ số giờ

	public Edge(Node from, Node to, double distance, double speedLimit, double cost, int trafficLights,
			double avgWaitTime, double rushHourFactor) {
		this.from = from;
		this.to = to;
		this.distance = distance;
		this.speedLimit = speedLimit;
		this.cost = cost;
		this.trafficLights = trafficLights;
		this.avgWaitTime = avgWaitTime;
		this.rushHourFactor = rushHourFactor;
	}

	// Phương thức tính thời gian di chuyển thực tế (giờ)
	public double calculateTravelTime() {

		return 0;
	}

	public Node getFrom() {
		return from;
	}

	public Node getTo() {
		return to;
	}

	public double getCost() {
		return cost;
	}

	@Override
	public String toString() {
		return from.getName() + "->" + to.getName();
	}
}
