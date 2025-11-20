package com.ttcsn.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.ttcsn.config.Constant;
import com.ttcsn.model.Graph;
import com.ttcsn.model.Node;
import com.ttcsn.model.Route;
import com.ttcsn.service.RoutingService;

public class FireflyAlgorithm {
	private Random random = new Random();
	private RoutingService routingService;
	private Graph graph;

	public FireflyAlgorithm(Graph graph, RoutingService routingService) {
		this.graph = graph;
		this.routingService = routingService;
	}

	// Hàm chạy thuật toán
	public Route run() {
		List<Firefly> population = new ArrayList<>();

		for (int i = 0; i < Constant.POPULATION_SIZE; i++) {
			Node start = graph.getNodeByName(Constant.START_POINT);
			Node end = graph.getNodeByName(Constant.END_POINT);
			Route route = routingService.generateRandomRoute(start, end);
			Firefly firefly = new Firefly(route);
			firefly.calculateBrightness();
			population.add(firefly);
		}

		Firefly best = population.get(0);
		int g = 0;
		while (g < Constant.MAX_GENERATION) {
			for (int i = 0; i < population.size(); i++) {
				for (int j = 0; j < population.size(); j++) {
					Firefly fi = population.get(i);
					Firefly fj = population.get(j);

					if (fj.getBrightness() > fi.getBrightness()) {
						double r = routingService.jaccardDistance(fi.getRoute(), fj.getRoute());
						double beta = routingService.calculateAttractiveness(Constant.BETA_0, Constant.GAMMA, r);

						if (random.nextDouble() < beta) {
							Route newRoute = routingService.crossover(fi.getRoute(), fj.getRoute());
							fi.setRoute(newRoute);
						}

						Route mutated = routingService.mutate(fi.getRoute());
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

}
