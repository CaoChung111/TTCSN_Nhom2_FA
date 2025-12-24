package com.ttcsn;

import com.ttcsn.algorithm.FireflyAlgorithm;
import com.ttcsn.config.Constant;
import com.ttcsn.model.Graph;
import com.ttcsn.model.Node;
import com.ttcsn.model.Route;
import com.ttcsn.service.GraphService;
import com.ttcsn.service.RoutingService;

public class RunComparison {

	public static void main(String[] args) {
		System.out.println("==========================================================");
		System.out.println("   KỊCH BẢN KIỂM THỬ: SO SÁNH DIJKSTRA VS FIREFLY");
		System.out.println("==========================================================\n");

		try {
			// --- 1. THIẾT LẬP CẤU HÌNH CỐ ĐỊNH ---
			Constant.GRAPH_FILE_PATH = "src/main/resources/I_100N_800E_Tight.json";
			Constant.START_POINT = "Trạm 1";
			Constant.END_POINT = "Trạm 100";
			Constant.MAX_COST = 80000;
			Constant.TIME_START = 17.0;
			Constant.POPULATION_SIZE = 80;
			Constant.MAX_GENERATION = 150;
			Constant.ALPHA = 0.7;
			Constant.BETA_0 = 1.0;
			Constant.GAMMA = 1;

			// --- 2. LOAD DỮ LIỆU ---
			System.out.println(">> Đang tải dữ liệu từ: " + Constant.GRAPH_FILE_PATH);
			GraphService graphService = new GraphService();
			Graph graph = graphService.loadGraphFromJson(Constant.GRAPH_FILE_PATH);

			if (graph == null) {
				System.err.println("[LỖI] Không load được đồ thị. Kiểm tra đường dẫn file!");
				return;
			}

			RoutingService routingService = new RoutingService();
			routingService.setGraph(graph);

			Node startNode = routingService.getNode(Constant.START_POINT);
			Node endNode = routingService.getNode(Constant.END_POINT);

			if (startNode == null || endNode == null) {
				System.err.println(
						"[LỖI] Không tìm thấy điểm Start/End: " + Constant.START_POINT + " -> " + Constant.END_POINT);
				return;
			}

			System.out
					.println(">> Bài toán: Tìm đường từ [" + startNode.getName() + "] đến [" + endNode.getName() + "]");
			System.out.println(">> Ràng buộc ngân sách: " + String.format("%,.0f", Constant.MAX_COST) + " VNĐ\n");

			// =================================================================
			// 3. CHẠY THUẬT TOÁN 1: DIJKSTRA
			// =================================================================
			System.out.println("--- [1] Đang chạy DIJKSTRA (Truyền thống)... ---");
			long t1 = System.currentTimeMillis();
			Route dijkstraRoute = routingService.runDijkstra(startNode, endNode, Constant.TIME_START);

			long t2 = System.currentTimeMillis();
			printResult("Dijkstra", dijkstraRoute, (t2 - t1));

			// =================================================================
			// 4. CHẠY THUẬT TOÁN 2: FIREFLY (ĐA MỤC TIÊU CÓ RÀNG BUỘC)
			// =================================================================
			System.out.println("\n--- [2] Đang chạy FIREFLY ALGORITHM (Đề xuất)... ---");
			FireflyAlgorithm fa = new FireflyAlgorithm(routingService);

			long t3 = System.currentTimeMillis();
			Route fireflyRoute = fa.run();

			long t4 = System.currentTimeMillis();
			printResult("Firefly Algorithm", fireflyRoute, (t4 - t3));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void printResult(String algoName, Route route, long timeMs) {
		if (route == null) {
			System.out.println("   [KẾT QUẢ " + algoName + "]: KHÔNG TÌM THẤY ĐƯỜNG!");
			return;
		}

		double cost = route.getTotalCost();
		double time = route.getTotalTime();
		double distance = route.getTotalDistance();
		double budget = Constant.MAX_COST;
		boolean isValid = cost <= budget;

		System.out.println("   [KẾT QUẢ " + algoName.toUpperCase() + "]");
		System.out.println("   + Thời gian chạy code:  " + timeMs + " ms");
		System.out.println("   + Thời gian di chuyển:  " + String.format("%.2f", time) + " giờ");
		System.out.println("   + Tổng chi phí:         " + String.format("%,.0f", cost) + " VNĐ");
		System.out.println("   + Tổng quãng đường:         " + String.format("%,.0f", distance) + " km");

		if (isValid) {
			System.out.println("   => ĐÁNH GIÁ: [HỢP LỆ] (Thỏa mãn ngân sách)");
		} else {
			System.out.println(
					"   => ĐÁNH GIÁ: [VI PHẠM] (Vượt ngân sách " + String.format("%,.0f", (cost - budget)) + " VNĐ)");
		}
	}
}