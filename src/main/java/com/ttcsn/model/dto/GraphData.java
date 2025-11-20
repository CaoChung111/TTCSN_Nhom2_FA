package com.ttcsn.model.dto;

import java.util.List;

import com.ttcsn.model.Node;

// Lớp DTO đại diện cho toàn bộ file JSON
public class GraphData {
	private List<Node> nodes;
	private List<EdgeData> edges;

	public List<Node> getNodes() {
		return nodes;
	}

	public List<EdgeData> getEdges() {
		return edges;
	}
}