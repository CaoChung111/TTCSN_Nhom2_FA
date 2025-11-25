package com.ttcsn.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.ttcsn.config.Constant;

public class Route {
	private List<Node> nodes = new ArrayList<>();
	private List<Edge> edges = new ArrayList<>();
	private double totalTime = 0;
	private double totalCost = 0;
	private double totalDistance = 0;

	public Route() {
	}

	// Constructor sao chép dữ liệu từ List Node
	public Route(List<Node> other) {
		this.nodes = new ArrayList<>(other);
	}

	// Constructor sao chép từ Route khác
	public Route(Route other) {
		this.nodes = new ArrayList<>(other.nodes);
		this.edges = new ArrayList<>(other.edges);
		this.totalTime = other.totalTime;
		this.totalCost = other.totalCost;
        this.totalDistance = other.totalDistance;
	}

	// Thêm 1 bước di chuyển vào lộ trình
	public void addStep(Node node, Edge edge) {
		nodes.add(node);
		if (edge != null) {
			double startTime = Constant.TIME_START + totalTime;
			double currentEdgeTime = edge.calculateTravelTime(startTime);
			edges.add(edge);
			totalTime += currentEdgeTime;
			totalDistance += edge.getDistance();
			totalCost += edge.getCost();
		}
	}

	// Tính lại sau khi thay đổi lộ trình
	public void recalculate() {
		totalTime = 0;
		totalCost = 0;
        totalDistance = 0;
		double currentTime = Constant.TIME_START;
		for (Edge e : edges) {
			double currentEdgeTime = e.calculateTravelTime(currentTime);
			totalTime += currentEdgeTime;
			currentTime += currentEdgeTime;
			totalCost += e.getCost();
            totalDistance += e.getDistance();
		}
	}

	// Hỗ trợ tính Jaccard nhanh
	public Set<Edge> getEdgeSet() {
		return new HashSet<>(edges);
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public double getTotalTime() {
		return totalTime;
	}

	public double getTotalCost() {
		return totalCost;
	}

    public double getTotalDistance() {return  totalDistance;}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Route route = (Route) o;
		// Hai lộ trình bằng nhau nếu danh sách Node và Edge giống hệt nhau
		return Objects.equals(nodes, route.nodes) && Objects.equals(edges, route.edges);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nodes, edges);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < nodes.size(); i++) {
			sb.append(nodes.get(i).getName());
			if (i < nodes.size() - 1)
				sb.append(" -> ");
		}
		return sb.toString();
	}
}
