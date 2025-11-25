package com.ttcsn.model.dto;

import java.util.List;

import com.ttcsn.model.Node;

// Lớp DTO đại diện cho toàn bộ file JSON
public class GraphData {

	// Tên "nodes" và "edges" phải khớp với file JSON
	// Tái sử dụng lớp Node của bạn vì nó đơn giản
	private List<Node> nodes;
	private List<EdgeData> edges;

	// Getters
	public List<Node> getNodes() {
		return nodes;
	}

	public List<EdgeData> getEdges() {
		return edges;
	}
}