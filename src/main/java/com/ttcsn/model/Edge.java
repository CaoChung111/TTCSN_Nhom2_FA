package com.ttcsn.model;
import com.ttcsn.config.Constant;

public class Edge {
	private final Node from; // điểm bắt đầu
	private final Node to; // điểm đến
	private final double distance; // khoảng cách (km)
	private final double speedLimit; // giới hạn tốc độ (km/h)
	private final double cost; // chi phí (VNĐ)
	private final int trafficLights; // số đèn đỏ
	private final double avgWaitTime; // thời gian chờ trung bình (giây)
	private final double rushHourFactor;// hệ số giờ

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
        return baseTravelTime * currentFactor + totalWaitTime;
	}

	public Node getFrom() {
		return from;
	}

	public Node getTo() {
		return to;
	}

    public double getDistance() {
        return distance;
    }
	public double getCost() {
		
		return cost;
	}

	@Override
	public String toString() {
		return from.getName() + "->" + to.getName();
	}
}
