package com.ttcsn.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.ttcsn.model.Edge;
import com.ttcsn.model.Graph;
import com.ttcsn.model.Node;
import com.ttcsn.model.Route;

/**
 * Thực hiện các nghiệp vụ của thuật toán: 1. Tìm đường đi ngẫu
 * nhiên(GenerateRandomRoute) 2. Tính độ sáng (CalculateBrightness) 3. Tính
 * khoảng cách (JaccardDistance) 4. Tính độ hấp dẫn (CalculateAttractiveness) 5.
 * Lai ghép (Crossover) 6. Đột biến (Mutate)
 */
public class RoutingService {
	private final Random random = new Random();
	private Graph graph;

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public Node getNode(String name) {
		return graph.getNodeByName(name);
	}

	public Route generateRandomRoute(Node start, Node end) {
		List<Edge> path = new ArrayList<>();
		Set<Node> visited = new HashSet<>();
		boolean found = findPathDFS(start, end, path, visited);
		if (!found) {
			System.err.println("Cảnh báo: Không tìm thấy đường từ " + start.getName() + " đến " + end.getName());
			return null;
		}
		return buildRouteFromPath(start, path);
	}

	// Overload dùng cho hàm mutate
	public Route generateRandomRoute(Node start, Node end, Set<Node> existingNode) {
		List<Edge> path = new ArrayList<>();
		// Copy các node đã tồn tại để tránh đi lặp lại
		Set<Node> visited = new HashSet<>(existingNode);
		boolean found = findPathDFS(start, end, path, visited);

		if (!found) {
			return null;
		}

		return buildRouteFromPath(start, path);
	}

	// convert List<Edge> thành Route Object
	private Route buildRouteFromPath(Node start, List<Edge> path) {
		Route newRoute = new Route();
		newRoute.addStep(start, null); // Thêm điểm đầu
		for (Edge edge : path) {
			newRoute.addStep(edge.getTo(), edge);
		}
		return newRoute;
	}

	public Route crossover(Route r1, Route r2) {
		List<Node> n1 = new ArrayList<>(r1.getNodes()); // r1 sáng hơn
		List<Node> n2 = new ArrayList<>(r2.getNodes()); // r2 kém hơn

		// Tìm các điểm chung (trừ đầu và cuối)
		List<Node> commonNodes = new ArrayList<>();
		for (int i = 1; i < n1.size() - 1; i++) {
			Node n = n1.get(i);
			if (n2.contains(n)) {
				commonNodes.add(n);
			}
		}

		// Không có điểm chung => không lai ghép, r2 giữ nguyên
		if (commonNodes.isEmpty()) {
			return new Route(r2);
		}

		// Chọn ngẫu nhiên 1 điểm chung làm điểm cắt
		Node cutPoint = commonNodes.get(random.nextInt(commonNodes.size()));
		//System.out.println("Điểm chung: " + cutPoint);

		int index1 = n1.indexOf(cutPoint); // r1
		int index2 = n2.indexOf(cutPoint); // r2

		// Tạo route mới: đầu từ r1 đến cutPoint, cuối từ r2 sau cutPoint
        List<Node> newRouteNodes = new ArrayList<>(n1.subList(0, index1 + 1)); // phần đầu r1
        if (index2 + 1 < n2.size()) {
			newRouteNodes.addAll(n2.subList(index2 + 1, n2.size())); // phần sau r2
		}

		Route newRoute = new Route();

		for (int i = 0; i < newRouteNodes.size(); i++) {
			Node currentNode = newRouteNodes.get(i);
			Edge edge = null;

			// Nếu có node tiếp theo
			if (i < newRouteNodes.size() - 1) {
				Node nextNode = newRouteNodes.get(i + 1);

				// Tìm edge ưu tiên trong r1, fallback r2
				edge = findEdgeBetween(r1, currentNode, nextNode);
				if (edge == null) {
					edge = findEdgeBetween(r2, currentNode, nextNode);
				}

				// Nếu vẫn null => không hợp lệ, r2 giữ nguyên
				if (edge == null) {
					System.out.println("Edge null giữa " + currentNode + " -> " + nextNode + ", fallback r2");
					return mutate(r1);
				}
			}

			newRoute.addStep(currentNode, edge);
		}

		return newRoute;
	}

	private Edge findEdgeBetween(Route route, Node from, Node to) {
		List<Node> nodes = route.getNodes();
		List<Edge> edges = route.getEdges();

		for (int i = 0; i < nodes.size() - 1; i++) {
			if (nodes.get(i).equals(from) && nodes.get(i + 1).equals(to)) {
				if (i < edges.size()) {
					return edges.get(i);
				}
			}
		}
		return null;
	}

	// Tìm lộ trình
	public boolean findPathDFS(Node current, Node end, List<Edge> path, Set<Node> visited) {
		visited.add(current);
		if (current.equals(end)) {
			return true;
		}
		List<Edge> neighbors = new ArrayList<>(graph.getNeighbors(current));
		Collections.shuffle(neighbors, random);

		for (Edge edge : neighbors) {
			Node nextNode = edge.getTo();
			if (!visited.contains(nextNode)) {
				path.add(edge);
				if (findPathDFS(nextNode, end, path, visited)) {
					return true;
				}
				path.remove(path.size() - 1);
			}
		}
		return false;
	}

	// Tính khoảng cách r
	public double jaccardDistance(Route r1, Route r2) {
		// Lấy tập hợp các cạnh (Edge Set) từ mỗi lộ trình
		Set<Edge> set1 = r1.getEdgeSet();
		Set<Edge> set2 = r2.getEdgeSet();

		// 1. Tính giao
		Set<Edge> intersection = new HashSet<>(set1);
		intersection.retainAll(set2); // Giữ lại các phần tử chung
		double intersectionSize = intersection.size();

		// 2. Tính hợp
		double unionSize = set1.size() + set2.size() - intersectionSize;

		if (unionSize == 0) {
			return 0.0;
		}

		// Jaccard Similarity = Giao / Hợp
		double jaccardSimilarity = intersectionSize / unionSize;

		// Jaccard Distance = 1 - Similarity
		return 1.0 - jaccardSimilarity;
	}

	// Tính độ hấp dẫn
	public double calculateAttractiveness(double beta0, double gamma, double distance) {
		return beta0 * Math.exp(-gamma * distance * distance);
	}

	// Đột biến
	public Route mutate(Route route) {
//		System.out.println("\n[MUTATE] Lộ trình GỐC: " + route);
		List<Node> nodes = route.getNodes();
		if (nodes.size() < 2)
			return route;

		int u_index = random.nextInt(nodes.size() - 1);
		int v_index = u_index + 1 + random.nextInt(nodes.size() - 1 - u_index);
		if (v_index >= nodes.size())
			v_index = nodes.size() - 1;

		Node u = nodes.get(u_index);
		Node v = nodes.get(v_index);
        /* System.out.println("[MUTATE] --- Đột biến đoạn: [" + u + "] (index " + u_index + ") TỚI [" + v + "] (index " + v_index + ")"); */

		Set<Node> existingNodes = new HashSet<>();
		for (int i = 0; i < u_index; i++) {
			existingNodes.add(nodes.get(i));
		}
		for (int i = v_index + 1; i < nodes.size(); i++) {
			existingNodes.add(nodes.get(i));
		}

		List<Edge> newEdges = new ArrayList<>();
		for (int i = 0; i < u_index; i++) {
			newEdges.add(route.getEdges().get(i));
		}
		Route middleRoute = generateRandomRoute(u, v, existingNodes);
		if (middleRoute == null || middleRoute.getEdges().isEmpty()) {
            //	System.out.println("[MUTATE] -> Thất bại (Không tìm được đường thay thế). Giữ nguyên.");
			return route;
		}
//		System.out.println("[MUTATE] --- Đoạn thay thế: " + middleRoute);
		newEdges.addAll(middleRoute.getEdges());
		for (int i = v_index; i < nodes.size() - 1; i++) {
			newEdges.add(route.getEdges().get(i));
		}

		Route newRoute = new Route();
		newRoute.addStep(nodes.get(0), null);
		for (Edge edge : newEdges) {
			newRoute.addStep(edge.getTo(), edge);
		}
//		System.out.println("[MUTATE] Lộ trình MỚI: " + newRoute);

		return newRoute;
	}

}
