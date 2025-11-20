package com.ttcsn.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
	private Map<Integer, Node> nodes = new HashMap<>(); // tìm đối tượng Node
	private Map<Integer, List<Edge>> edges = new HashMap<>(); // Danh sách kề

	// thêm đỉnh
	public void addNode(Node node) {
		nodes.put(node.getId(), node);
		edges.put(node.getId(), new ArrayList<>());
	}

	// thêm cạnh
	public void addEdge(Edge edge) {
		edges.get(edge.getFrom().getId()).add(edge);
	}

	// Overload thêm hàm lấy theo tên nếu cần (nhưng sẽ chậm hơn vì phải duyệt)
	public Node getNodeByName(String name) {
		for (Node node : nodes.values()) {
			if (node.getName().equals(name)) {
				return node;
			}
		}
		return null;
	}

	public Node getNode(int id) {
		return nodes.get(id);
	}

	public List<Edge> getNeighbors(Node node) {
		List<Edge> list = edges.get(node.getId());
		// Nếu null (không tìm thấy hoặc chưa init) -> trả về list rỗng
		if (list == null) {
			return new ArrayList<>();
		}
		return list;
	}

	public Collection<Node> getAllNodes() {
		return nodes.values();
	}
}