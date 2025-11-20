package com.ttcsn.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
		return 0.0;
	}

	// Tính độ hấp dẫn
	public double calculateAttractiveness(double beta0, double gamma, double distance) {
		// TODO: công thức β
		return 0.0;
	}

	// Lai ghép
	public Route crossover(Route r1, Route r2) {
		// TODO: lai ghép
		return null;
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
