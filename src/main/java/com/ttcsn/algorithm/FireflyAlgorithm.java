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
	        List<Node> n1 = new ArrayList<>(r1.getNodes());
	        List<Node> n2 = new ArrayList<>(r2.getNodes());

	        //Tìm danh sách điểm chung
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

	    private Route mutate(Route route) {
	        // TODO: đột biến lộ trình (đảo vị trí, thêm cạnh, v.v.)
	        return route;
	    }
	    
}
