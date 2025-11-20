package com.ttcsn.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Route {
	private List<Node> nodes = new ArrayList<>();
	private List<Edge> edges = new ArrayList<>();
	private double totalTime = 0;
	private double totalCost = 0;

	public Route() {
	}

	// Constructor sao chép để tránh tham chiếu
	public Route(Route other) {
		this.nodes = new ArrayList<>(other.nodes);
		this.edges = new ArrayList<>(other.edges);
		this.totalTime = other.totalTime;
		this.totalCost = other.totalCost;
	}

	// Thêm 1 bước di chuyển vào lộ trình
	public void addStep(Node node, Edge edge) {
		nodes.add(node);
		if (edge != null) {
			edges.add(edge);
			totalTime += edge.calculateTravelTime();
			totalCost += edge.getCost();
		}
	}

	// Tính lại sau khi thay đổi lộ trình
	public void recalculate() {
		totalTime = 0;
		totalCost = 0;
		for (Edge e : edges) {
			totalTime += e.calculateTravelTime();
			totalCost += e.getCost();
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
