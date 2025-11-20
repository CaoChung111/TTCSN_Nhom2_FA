package com.ttcsn.model;
import com.ttcsn.config.Constant;

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
	public double calculateTravelTime(double startTime) {
		// Khởi tạo hệ số ảnh hưởng
        double currentFactor = 1.0; 
        
        // Kiểm tra nếu thời điểm bắt đầu rơi vào giờ cao điểm
        if (startTime >= Constant.AM_START && startTime < Constant.AM_END) {
            currentFactor = this.rushHourFactor; 
        }

        else if (startTime >= Constant.PM_START && startTime < Constant.PM_END) {
            currentFactor = this.rushHourFactor; 
        }

		// Tính thời gian di chuyển cơ bản (giờ)
		double baseTravelTime = distance / speedLimit;

		// Tính tổng thời gian chờ đèn đỏ
		double totalWaitTime = (trafficLights * avgWaitTime) / 3600.0;
				
		// Thời gian di chuyển thực tế sau khi áp dụng hệ số giờ cao điểm
		double actualTravelTime = baseTravelTime * currentFactor + totalWaitTime;

		return actualTravelTime;
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
