package com.ttcsn.algorithm;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
		Set<Firefly> exitsFirefly = new HashSet<>();
		DecimalFormat df = new DecimalFormat("#.###");
		do {
			population.clear();
			exitsFirefly.clear();
			System.out.println("Khởi tạo");
			for (int i = 0; i < Constant.POPULATION_SIZE; i++) {
				Node start = graph.getNodeByName(Constant.START_POINT);
				Node end = graph.getNodeByName(Constant.END_POINT);
				Route route = routingService.generateRandomRoute(start, end);
				Firefly firefly = new Firefly(route);
				firefly.calculateBrightness();
				population.add(firefly);
				exitsFirefly.add(firefly);	
			}
		}while(exitsFirefly.size() == 1);
		
		//Log khởi tạo
		for (int i = 0; i < population.size(); i++) {
		    Firefly f = population.get(i);
//		    String brightnessStr = df.format(f.getBrightness());
		    System.out.println((i + 1) + ". [" + f.getBrightness() + "] " + f.getRoute().toString());
		}
		// End Log khởi tạo

		Firefly best = population.get(0);
		int g = 0;
		boolean test = false;
		while (g < Constant.MAX_GENERATION) {
			if(!test) System.out.println("GEN = " + g);
			for (int i = 0; i < population.size(); i++) {
				for (int j = 0; j < population.size(); j++) {
					Firefly fi = population.get(i);
					Firefly fj = population.get(j);
					if(!test){
						System.out.println("i = " + i + "," + "j = " + j);
						System.out.println("f[i]" + ". [" + df.format(fi.getBrightness()) + "] " + fi.getRoute().toString());
						System.out.println("f[j]" + ". [" + df.format(fj.getBrightness()) + "] " + fj.getRoute().toString());
					}
					if (fj.getBrightness() > fi.getBrightness()) {
						double r = routingService.jaccardDistance(fi.getRoute(), fj.getRoute());
						double beta = routingService.calculateAttractiveness(Constant.BETA_0, Constant.GAMMA, r);
						if(!test) System.out.println("f[i] < f[j] = True");
						double randomBeta  = random.nextDouble();
						if ( randomBeta < beta) {
								
							Route newRoute = routingService.crossover(fj.getRoute(), fi.getRoute());
							fi.setRoute(newRoute);
							if(!test) {
								System.out.println("random = " + df.format(randomBeta) +" < beta = "+df.format(beta) + " => True");
								System.out.println("[CROSSOVER] " + newRoute.toString());
							}
						}else {
							if(!test) {
								System.out.println("random = " + df.format(randomBeta) +" < beta = "+df.format(beta) + " => False");
							}
						}
						Route mutated = routingService.mutate(fi.getRoute());
						fi.setRoute(mutated);
						if(!test) System.out.println("[MUTATED] " + mutated.toString());
						fi.calculateBrightness();
					}
				}
			}

			Collections.sort(population);
			if (population.get(0).getBrightness() > best.getBrightness()) {
				best = population.get(0);
			}
			g = g + 1;
			test = true;
			System.out.println("\n");
		}

		return best.getRoute();
	}

	// --- HÀM PHỤ sẽ được để trong class RoutingService ---
	// Tạo 1 lộ trình ngẫu nhiên

}
