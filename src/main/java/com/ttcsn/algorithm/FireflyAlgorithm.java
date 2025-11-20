package com.ttcsn.algorithm;

import java.util.*;

import com.ttcsn.config.Constant;
import com.ttcsn.model.Edge;
import com.ttcsn.model.Graph;
import com.ttcsn.model.Node;
import com.ttcsn.model.Route;

public class FireflyAlgorithm {
	private Random random = new Random();
	private Graph graph;

	public FireflyAlgorithm(Graph graph) {
		this.graph = graph; // Nhận graph đầu vào
	}

	// Hàm chạy thuật toán
	public Route run() {
		List<Firefly> population = new ArrayList<>();

		for (int i = 0; i < Constant.POPULATION_SIZE; i++) {
//			Route route = generateRandomRoute(Constant.START_POINT, Constant.END_POINT);
//			Firefly firefly = new Firefly(route);
//			firefly.calculateBrightness();
//			population.add(firefly);
		}

		Firefly best = population.get(0);
		int g = 0;
		while (g < Constant.MAX_GENERATION) {
			for (int i = 0; i < population.size(); i++) {
				for (int j = 0; j < population.size(); j++) {
					Firefly fi = population.get(i);
					Firefly fj = population.get(j);

					if (fj.getBrightness() > fi.getBrightness()) {
						double r = jaccardDistance(fi.getRoute(), fj.getRoute());
						double beta = calculateAttractiveness(Constant.BETA_0, Constant.GAMMA, r);

						if (random.nextDouble() < beta) {
							Route newRoute = crossover(fi.getRoute(), fj.getRoute());
							fi.setRoute(newRoute);
						}

						Route mutated = mutate(fi.getRoute());
						fi.setRoute(mutated);
						fi.calculateBrightness();
					}
				}
			}

			Collections.sort(population);
			if (population.get(0).getBrightness() > best.getBrightness()) {
				best = population.get(0);
			}
			g = g + 1;
		}

		return best.getRoute();
	}

	// --- HÀM PHỤ sẽ được để trong class RoutingService ---
	// Tạo 1 lộ trình ngẫu nhiên
	public Route generateRandomRoute(Node start, Node end) {
		Route route = new Route();
		List<Edge> path = new ArrayList<Edge>();
		Set<Node> visited = new HashSet<>();
		findPathDFS(start, end, path, visited);
		Route newRoute = new Route();
		newRoute.addStep(start, null);
		for (Edge edge : path) {
			newRoute.addStep(edge.getTo(), edge);
		}
		return newRoute;
	}

	// Ghi đè dùng cho hàm mutate
	public Route generateRandomRoute(Node start, Node end, Set<Node> existingNode) {
		List<Edge> path = new ArrayList<Edge>();
		Set<Node> visited = new HashSet<>(existingNode);
		findPathDFS(start, end, path, visited);
		Route newRoute = new Route();
		newRoute.addStep(start, null);
		for (Edge edge : path) {
			newRoute.addStep(edge.getTo(), edge);
		}
		return newRoute;
	}

	private Route crossover(Route r1, Route r2) {
		List<Node> n1 = new ArrayList<>(r1.getNodes());
		List<Node> n2 = new ArrayList<>(r2.getNodes());

		// Tìm danh sách điểm chung
		List<Node> commonNodes = new ArrayList<>();
		for (int i = 1; i < n1.size() - 1; i++) {
			Node n = n1.get(i);
			if (n2.contains(n)) {
				commonNodes.add(n);
			}
		}

		// Nếu có điểm chung thực hiện lai ghép
		if (commonNodes.size() > 0) {
			// Chọn ngẫu nhiên 1 điểm chung làm điểm cắt
			Node cutPoint = commonNodes.get(random.nextInt(commonNodes.size()));

			int index1 = n1.indexOf(cutPoint);
			int index2 = n2.indexOf(cutPoint);

			List<Node> newRouteNodes = new ArrayList<>();
			newRouteNodes.addAll(n1.subList(0, index1 + 1));
			newRouteNodes.addAll(n2.subList(index2 + 1, n2.size()));

			Route newRoute = new Route();

			for (int i = 0; i < newRouteNodes.size(); i++) {
				Node currentNode = newRouteNodes.get(i);
				Edge edge = null;

				// Tìm edge giữa node hiện tại và node tiếp theo
				if (i < newRouteNodes.size() - 1) {
					Node nextNode = newRouteNodes.get(i + 1);

					if (i <= index1) {
						// Phần này lấy từ r1
						edge = findEdgeBetween(r1, currentNode, nextNode);
					} else {
						// Phần này lấy từ r2
						edge = findEdgeBetween(r2, currentNode, nextNode);
					}
				}

				newRoute.addStep(currentNode, edge);
			}

			return newRoute;
		}

		return new Route(r2);
	}

	private Edge findEdgeBetween(Route route, Node from, Node to) {
		List<Node> nodes = route.getNodes();
		List<Edge> edges = route.getEdges();

		for (int i = 0; i < nodes.size() - 1; i++) {
			if (nodes.get(i).equals(from) && nodes.get(i + 1).equals(to)) {
				return edges.get(i);
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
		// TODO: tính khoảng cách giữa 2 lộ trình
    	// Lấy tập hợp các cạnh (Edge Set) từ mỗi lộ trình
        Set<Edge> set1 = r1.getEdgeSet();
        Set<Edge> set2 = r2.getEdgeSet();
        
        // 1. Tính Intersection (Giao): |E₁ ∩ E₂|
        Set<Edge> intersection = new HashSet<>(set1);
        intersection.retainAll(set2); // Giữ lại các phần tử chung
        double intersectionSize = intersection.size();
        
        // 2. Tính Union (Hợp): |E₁ ∪ E₂| = |E₁| + |E₂| - |E₁ ∩ E₂|
        double unionSize = set1.size() + set2.size() - intersectionSize;
        
        if (unionSize == 0) {
            return 0.0;
        }

        // Jaccard Similarity (Độ tương đồng) = |Intersection| / |Union|
        double jaccardSimilarity = intersectionSize / unionSize;
        
        // Jaccard Distance (Khoảng cách) = 1 - Similarity
        return 1.0 - jaccardSimilarity;
	}

	// Tính độ hấp dẫn
	public double calculateAttractiveness(double beta0, double gamma, double distance) {
		// TODO: công thức β
		return beta0 * Math.exp(-gamma * distance * distance);
	}

	// Đột biến
	public Route mutate(Route route) {
		System.out.println("\n[MUTATE] Lộ trình GỐC: " + route);
		List<Node> nodes = route.getNodes();
		if (nodes.size() < 2)
			return route;

		int u_index = random.nextInt(nodes.size() - 1);
		int v_index = u_index + 1 + random.nextInt(nodes.size() - 1 - u_index);

		Node u = nodes.get(u_index);
		Node v = nodes.get(v_index);
		System.out.println("[MUTATE] --- Đột biến đoạn: [" + u + "] (index " + u_index + ") TỚI [" + v + "] (index "
				+ v_index + ")");

		Set<Node> existingNodes = new HashSet<>();

		for (int i = 0; i < u_index; i++) {
			existingNodes.add(nodes.get(i));
		}

		for (int i = v_index + 1; i < nodes.size(); i++) {
			existingNodes.add(nodes.get(i));
		}

		List<Edge> newEdges = new ArrayList<Edge>();
		for (int i = 0; i < u_index; i++) {
			newEdges.add(route.getEdges().get(i));
		}
		Route middleRoute = generateRandomRoute(u, v, existingNodes);
		System.out.println("[MUTATE] --- Đoạn thay thế: " + middleRoute);
		newEdges.addAll(middleRoute.getEdges());
		for (int i = v_index; i < nodes.size() - 1; i++) {
			newEdges.add(route.getEdges().get(i));
		}

		Route newRoute = new Route();
		newRoute.addStep(nodes.get(0), null);
		for (Edge edge : newEdges) {
			newRoute.addStep(edge.getTo(), edge);
		}
		System.out.println("[MUTATE] Lộ trình MỚI: " + newRoute);

		return newRoute;
	}

}
