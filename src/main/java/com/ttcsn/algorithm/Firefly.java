package com.ttcsn.algorithm;

import com.ttcsn.config.Constant;
import com.ttcsn.model.Route;

public class Firefly implements Comparable<Firefly> {
	private Route route;
	private double brightness;

	public Firefly(Route route) {
		this.route = route;
	}

	// Đánh giá độ sáng dựa trên hàm mục tiêu và hàm phạt
	public void calculateBrightness() {
		double time = route.getTotalTime();
		double cost = route.getTotalCost();

		// Nếu vi phạm ngân sách, áp dụng hàm phạt
		if (cost > Constant.MAX_COST) {
			// Công thức phạt đơn giản: tăng thời gian ảo lên
			double penalty = Constant.PENALTY_FACTOR * (cost - Constant.MAX_COST);
			this.brightness = 1.0 / (time + penalty);
		} else {
			this.brightness = 1.0 / time;
		}
	}

	public Route getRoute() {
		return route;
	}

	public double getBrightness() {
		return brightness;
	}

	public void setRoute(Route newRoute) {
		this.route = newRoute;
	}

	@Override
	public int compareTo(Firefly other) {
		// Sắp xếp giảm dần theo độ sáng (sáng hơn đứng trước)
		return Double.compare(other.brightness, this.brightness);
	}

	@Override
	public String toString() {
		return route.toString() + "  [" + String.format("%.5f", brightness) + "]";
	}

}