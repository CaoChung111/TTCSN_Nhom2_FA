package com.ttcsn.algorithm;

import java.util.*;

import com.ttcsn.model.Route;
import com.ttcsn.config.*;

public class FireflyAlgorithm {
	  private Random random = new Random();

	    public Route run() {
	        List<Firefly> population = new ArrayList<>();

	        for (int i = 0; i < Constant.POPULATION_SIZE; i++) {
	            Route route = generateRandomRoute(Constant.START_POINT, Constant.END_POINT);
	            Firefly firefly = new Firefly(route);
	            firefly.calculateBrightness();
	            population.add(firefly);
	        }

	        Firefly best = population.get(0);
	        int g = 0;
	        while ( g < Constant.MAX_GENERATION) {
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
	    
	    // --- HÀM PHỤ (bạn sẽ tự triển khai sau) ---

	    private Route generateRandomRoute(String start, String end) {
	        // TODO: sinh ngẫu nhiên lộ trình từ A -> B
	        return null;
	    }

	    private double jaccardDistance(Route r1, Route r2) {
	        // TODO: tính khoảng cách giữa 2 lộ trình
	        return 0.0;
	    }

	    private double calculateAttractiveness(double beta0, double gamma, double distance) {
	        // TODO: công thức β = β0 * e^(-γ * r^2)
	        return 0.0;
	    }

	    private Route crossover(Route r1, Route r2) {
	        // TODO: lai ghép 2 lộ trình
	        return null;
	    }

	    private Route mutate(Route route) {
	        // TODO: đột biến lộ trình (đảo vị trí, thêm cạnh, v.v.)
	        return route;
	    }
}
